package com.shyl.msc.webService.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.ICompanyWebService;
import com.shyl.sys.dto.Message;


/**
 * 企业信息下载
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="companyWebService",portName="companyPort", targetNamespace="http://webservice.msc.shyl.com/")
public class CompanyWebService extends BaseWebService implements ICompanyWebService {

	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	
	
	/**
	 * 取得企业列表
	 */
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.company_get, sign, dataType, data);
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
			Hospital hospital = (Hospital) map.get("hospital");
			//第三部验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四部执行主逻辑
			message = getMethod(converData, hospital);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		try {
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
			String yybm = jObject.getString("yybm");//医院编码
			
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			if(!StringUtils.isEmpty(yybm)){
				hospital = hospitalService.findByCode(ptdm, yybm);
				if(hospital == null){
					message.setMsg("医院编码有误");
					return message;
				}
			}
			String qylx = jObject.getString("qylx");//上次更新时间
			if(StringUtils.isEmpty(qylx)){
				message.setMsg("企业类型不能为空");
				return message;
			}
			if(!qylx.equals("1")&&!qylx.equals("2")&&!qylx.equals("3")&&!qylx.equals("4")){
				message.setMsg("企业类型错误");
				return message;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
	
	private Message getMethod(JSONObject jObject, Hospital hospital) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String qylx = jObject.getString("qylx");//企业类型
			
			PageRequest pageable = new PageRequest();
			if(qylx.equals("1")){
				pageable.getQuery().put("t#isProducer_I_EQ", 1);
			}else if(qylx.equals("2")){
				pageable.getQuery().put("t#isVendor_I_EQ", 1);
			}else if(qylx.equals("3")){
				pageable.getQuery().put("t#isSender_I_EQ", 1);
			}else {
				pageable.getQuery().put("t#isGPO_I_EQ", 1);
			}
			List<Company> list = companyService.list(ptdm, pageable);
			
			JSONObject data_js = new JSONObject();
			JSONArray arr = new JSONArray();
			int i=0;
			for(Company company:list){
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh",i+"");							//顺序号
				js.put("qybm",company.getCode()==null?"":company.getCode());				//企业编码
				js.put("qymc",company.getFullName()==null?"":company.getFullName());		//企业名称	
				js.put("qyjc",company.getShortName()==null?"":company.getShortName());		//企业简称
				if(company.getIsDisabled() == 0){
					js.put("sfjy","0");	
				}else{
					js.put("sfjy","1");	
				}
				
				arr.add(js);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			data_js.put("jls", list.size());
			data_js.put("mx", arr);
			message.setData(data_js);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}
	
}
