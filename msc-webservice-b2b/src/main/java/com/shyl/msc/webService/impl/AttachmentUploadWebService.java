package com.shyl.msc.webService.impl;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.GridFSDAO;
import com.shyl.msc.b2b.order.entity.Datagram;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.webService.IAttachmentUploadWebService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.service.IFileManagementService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.*;


@WebService(serviceName = "attachmentUploadWebService",portName = "attachmentUploadPort",targetNamespace = "http://webservice.msc.shyl.com/")
public class AttachmentUploadWebService extends BaseWebService implements IAttachmentUploadWebService{

    @Resource
    private ICompanyService companyService;
    @Resource
    private ITradeInvoiceService tradeInvoiceService;
    @Resource
    private GridFSDAO gridFSDAO;
    @Resource
    private IFileManagementService fileManagementService;
    /**
     * 上传单据附件
     * @param sign
     * @param dataType
     * @param data
     * @return
     */
    @Override
    @WebMethod(action = "send")
    @WebResult(name = "sendResult")
    public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
        Message message = new Message();
        try{
            //第一步检查数据类型是否合法
            message = checkDataType(Datagram.DatagramType.attachmentUpload_send, sign, dataType, data);
            if(!message.getSuccess()){
                return resultMessage(message, dataType);
            }
            //第二步验证data数据是否合法
            JSONObject converData = getConverData(dataType, data);
            message = checkData(converData);
            if(!message.getSuccess()){
                return resultMessage(message, dataType);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) message.getData();
            Company company = (Company) map.get("company");
            Boolean isGpo = (Boolean) map.get("isGPO");
            String ptdm = converData.getString("ptdm");	//平台代码

            //第三部验证签名
            message = checkSign(company.getIocode(), data, sign);
            if(!message.getSuccess()){
                return resultMessage(message, dataType);
            }
            //第四步新增报文信息
            message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, Datagram.DatagramType.attachmentUpload_send);
            if(!message.getSuccess()){
                return resultMessage(message, dataType);
            }
            //第五步执行主逻辑
            message = sendMethod(converData, company,isGpo, Long.valueOf(message.getData().toString()));
        }catch (Exception e){
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("服务器出错");
            message.setData("");
        }
        return resultMessage(message, dataType);
    }


    private Message sendMethod(JSONObject jObject, Company company,Boolean isGpo, Long datagramId){
        Message message = new Message();
        message.setSuccess(false);
        try{
            String ptdm = jObject.getString("ptdm");
            String jglx = jObject.getString("jglx");
            String jgbm = jObject.getString("jgbm");
            String djlx = jObject.getString("djlx");
            String djbh = jObject.getString("djbh");
            String wjmc = jObject.getString("wjmc");
            String wj = jObject.getString("wj");
            TradeInvoice tradeInvoice = null;
            Long tradeInvoiceId = null;
            if("1".equals(djlx)){
                tradeInvoice = tradeInvoiceService.getByCode(ptdm,company.getCode(),djbh,isGpo);
                tradeInvoiceId  = tradeInvoice.getId();
            }
            if(StringUtils.isNotEmpty(wj)){
                //将base64编码的字符串解码成字节数组
                byte[] bytes = Base64.decodeBase64(wj.getBytes());
                String path = gridFSDAO.saveFile(bytes,wjmc+".zip","tradeInvoice");
                FileManagement fileManagement = new FileManagement();
                fileManagement.setFileName(wjmc);
                fileManagement.setFileURL(path);
                fileManagement.setKeyFlag("tradeInvoice"+tradeInvoiceId);
                fileManagementService.save(ptdm,fileManagement);
            }

            //返回
            JSONObject data_rtn = new JSONObject();
            message.setSuccess(true);
            message.setMsg("成功返回");
            message.setData(data_rtn);
        }catch (Exception e) {
            e.printStackTrace();
            message.setMsg("服务器端出错！");
            return message;
        }
        return message;
    }

    private Message checkData(JSONObject jObject) {
        Message message = new Message();
        message.setSuccess(false);
        Company company = new Company();
        Boolean isGPO = true;
        try{
            String ptdm = jObject.getString("ptdm");	//平台代码
            if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")){
                if(StringUtils.isEmpty(ptdm)){
                    message.setMsg("平台代码不能为空");
                    return message;
                }
                List<String> projectList = new ArrayList<>(Arrays.asList(CommonProperties.DB_PROJECTCODE.split(",")));
                if(!projectList.contains(ptdm)){
                    message.setMsg("平台代码有误");
                    return message;
                }
            }
            String jglx = jObject.getString("jglx"); // 机构类型
            if (StringUtils.isEmpty(jglx)) {
                message.setMsg("机构类型不能为空");
                return message;
            }
            try {
                int jglx_i = jObject.getIntValue("jglx");
                if (jglx_i != 1 && jglx_i != 2) {
                    message.setMsg("机构类型应为1或2");
                    return message;
                }
                if (jglx_i == 2) {
                    isGPO = false;
                } else {
                    isGPO = true;
                }
            } catch (Exception e) {
                message.setMsg("机构类型格式有误");
                return message;
            }
            String jgbm = jObject.getString("jgbm"); // 机构编码
            if (StringUtils.isEmpty(jgbm)) {
                message.setMsg("机构编码不能为空");
                return message;
            }
            if (isGPO) {
                company = companyService.findByCode(ptdm, jgbm, "isGPO=1");
            } else {
                company = companyService.findByCode(ptdm, jgbm, "isVendor=1");
            }
            if (company == null) {
                message.setMsg("机构编码有误");
                return message;
            }
            String djlx = jObject.getString("djlx");
            if(StringUtils.isEmpty(djlx)){
                message.setMsg("单据类型不能为空");
                return message;
            }
            try {
                int jglx_i = jObject.getIntValue("djlx");
                if (jglx_i != 1) {
                    message.setMsg("单据类型应为1");
                    return message;
                }
            } catch (Exception e) {
                message.setMsg("单据类型格式有误");
                return message;
            }
            String djbh = jObject.getString("djbh");
            TradeInvoice tradeInvoice = tradeInvoiceService.getByCode(ptdm, company.getCode(), djbh, isGPO);
            if(tradeInvoice == null){
                message.setMsg("单据号不存在");
                return message;
            }
            String wjmc = jObject.getString("wjmc");
            if(StringUtils.isEmpty(wjmc)){
                message.setMsg("文件名称不能为空");
                return message;
            }
            String wj = jObject.getString("wj");
            if(StringUtils.isEmpty(wj)){
                message.setMsg("文件不能为空");
                return message;
            }
        }catch (Exception e){
            e.printStackTrace();
            message.setMsg("服务器程序出错");
            return message;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("company",company);
        map.put("isGPO",isGPO);
        message.setData(map);
        message.setSuccess(true);
        return message;
    }
}
