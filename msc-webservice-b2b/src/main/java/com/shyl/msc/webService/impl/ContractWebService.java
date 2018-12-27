package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.GridFSDAO;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IContractWebService;
import com.shyl.sys.dto.Message;

@WebService(serviceName="contractWebService",portName="contractPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ContractWebService extends BaseWebService implements IContractWebService{
	@Resource
	private ICompanyService companyService;
	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IHospitalService hospitalService;
	
	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.contract_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = getCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Boolean isGPO = (Boolean) map.get("isGPO");
			Company company = (Company) map.get("company");
			Boolean isCode = (Boolean) map.get("isCode");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(company, isGPO, isCode, converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message getMethod(Company gpo, boolean isGPO, Boolean isCode, JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			//JSONArray jo_arr_r = contractService.getToGPO(gpo, isGPO);
			String ptdm = jObject.getString("ptdm");	//平台代码
			String htbh = jObject.getString("htbh");//合同编号
			String cxkssj = jObject.getString("cxkssj");//查询开始时间
			String cxjssj = jObject.getString("cxjssj");//查询结束时间
			List<Contract> contracts = new ArrayList<>();

			if(isCode){
				Contract contract = contractService.findByCode(ptdm, htbh);
				contracts.add(contract);
			}else{
				contracts = contractService.listByGPO(ptdm, gpo.getCode(), isGPO, Contract.Status.hospitalSigned, cxkssj, cxjssj);
			}
			JSONArray jo_arr_r = new JSONArray();
			for(Contract contract:contracts){
				JSONObject jo_r = new JSONObject();
				jo_r.put("htbh", contract.getCode());
				jo_r.put("yybm", contract.getHospitalCode());
				jo_r.put("gysbm", contract.getVendorCode());
				jo_r.put("yxqq", contract.getStartValidDate());
				jo_r.put("yxqz", contract.getEndValidDate());
				String fileString = gridFSDAO.findFileByIdToString(contract.getHospitalSealPath(), "contract");
				jo_r.put("htwj", fileString);
				if(contract.getParentId() != null){
					Contract pContract = contractService.getById(ptdm, contract.getParentId());
					jo_r.put("yhtbh", pContract.getCode());
				}else{
					jo_r.put("yhtbh", "");
				}
				jo_r.put("qzym", contract.getPageNum());//签章页码
				jo_r.put("gpoqzxz", contract.getGpoX());//GPO签章X轴
				jo_r.put("gpoqzyz", contract.getGpoY());//GPO签章Y轴
				jo_r.put("gysqzxz", contract.getVendorX());//供应商签章X轴 
				jo_r.put("gysqzyz", contract.getVendorY());//供应商签章Y轴
				List<ContractDetail> contractDetails = contractDetailService.listByContractId(ptdm, contract.getId());
				jo_r.put("jls",contractDetails.size());
				System.out.println("明细数据："+contractDetails.size());
				JSONArray jod_arr_r = new JSONArray();
				for(ContractDetail contractDetail:contractDetails){
					JSONObject jod_r = new JSONObject();
					jod_r.put("htmxbh", contractDetail.getCode());
					jod_r.put("ypbm", contractDetail.getProduct().getCode());
					jod_r.put("dj", contractDetail.getPrice().doubleValue());
					jod_r.put("sl", contractDetail.getContractNum().intValue());
					jod_r.put("je", contractDetail.getContractAmt().doubleValue());
					jod_arr_r.add(jod_r);
				}
				System.out.println("明细数据："+jod_arr_r.toString());
				jo_r.put("mx", jod_arr_r);
				jo_arr_r.add(jo_r);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(jo_arr_r);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message getCheckData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = new Company();
		Boolean isCode = true;
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
			String jglx = jObject.getString("jglx");	//机构类型
			if(StringUtils.isEmpty(jglx)){
				message.setMsg("机构类型不能为空");
				return message;
			}
			try {
				int jglx_i = jObject.getIntValue("jglx");
				if(jglx_i != 1 && jglx_i != 2){
					message.setMsg("机构类型应为1或2");
					return message;
				}
				if(jglx_i == 2){
					isGPO = false;
				} else {
					isGPO = true;
				}
			} catch (Exception e) {
				message.setMsg("机构类型格式有误");
				return message;
			}
			String jgbm = jObject.getString("jgbm");	//机构编码
			if(StringUtils.isEmpty(jgbm)){
				message.setMsg("机构编码不能为空");
				return message;
			}
			if(isGPO){
				company = companyService.findByCode(ptdm, jgbm,"isGPO=1");
			}else{
				company = companyService.findByCode(ptdm, jgbm,"isVendor=1");
			}
			if(company == null){
				message.setMsg("机构编码有误");
				return message;
			}
			String htbh = jObject.getString("htbh");//合同编号
			String cxkssj = jObject.getString("cxkssj");//查询开始时间
			String cxjssj = jObject.getString("cxjssj");//查询结束时间
			if(StringUtils.isEmpty(htbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或合同编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或合同编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				Contract contract = contractService.findByCode(ptdm, htbh);
				if(contract == null){
					message.setMsg("合同编号有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	@Override
	@WebMethod(action="fedback")
	public String fedback(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.contract_fedback, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = fedbackCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Company company = (Company) map.get("company");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = fedbackMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message fedbackMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");//
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			for(JSONObject detail:list){
				String htbh = detail.getString("htbh");	//合同编号
				String wj = detail.getString("wj");	//文件
				String bhyy = detail.getString("bhyy");
				Contract contract = contractService.findByCode(ptdm, htbh);
				int sftg = detail.getIntValue("sftg");
                if(sftg == 0){//驳回时
                	contract.setStatus(Contract.Status.rejected);
                	if(!StringUtils.isEmpty(bhyy)){
                    	contract.setRejectedReason(bhyy);
                	}
    				contractService.update(ptdm, contract);
                	continue;
                } else {
    				//将base64编码的字符串解码成字节数组
                    byte[] bytes = Base64.decodeBase64(wj.getBytes());
                    String path = gridFSDAO.saveFile(bytes, UUID.randomUUID().toString()+".pdf", "contract");
    				contract.setVendorSealPath(path);
    				contract.setFilePath(path);
    				contract.setVendorConfirmDate(new Date());
    				contract.setEffectiveDate(new Date());
    				contract.setStatus(Contract.Status.signed);
        			System.out.println("已签订");
    				contractDetailService.gpoCheck(ptdm, contract);
                }
			}
			
			message.setSuccess(true);
			message.setMsg("反馈成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message fedbackCheckData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = new Company();
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
			String jglx = jObject.getString("jglx");	//机构类型
			if(StringUtils.isEmpty(jglx)){
				message.setMsg("机构类型不能为空");
				return message;
			}
			try {
				int jglx_i = jObject.getIntValue("jglx");
				if(jglx_i != 1 && jglx_i != 2){
					message.setMsg("机构类型应为1或2");
					return message;
				}
				if(jglx_i == 2){
					isGPO = false;
				} else {
					isGPO = true;
				}
			} catch (Exception e) {
				message.setMsg("机构类型格式有误");
				return message;
			}
			String jgbm = jObject.getString("jgbm");	//机构编码
			if(StringUtils.isEmpty(jgbm)){
				message.setMsg("机构编码不能为空");
				return message;
			}
			if(isGPO){
				company = companyService.findByCode(ptdm, jgbm,"isGPO=1");
			}else{
				company = companyService.findByCode(ptdm, jgbm,"isVendor=1");
			}
			if(company == null){
				message.setMsg("机构编码有误");
				return message;
			}
			String mx = jObject.getString("mx");//明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("明细不能为空");
				return message;
			}
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = new ArrayList<>();
			try {
				list = JSON.parseArray(mx, JSONObject.class);
				if (list != null && list.size() == 0) {
					message.setMsg("明细数据不能为空");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("明细数据格式有误");
				return message;
			}
			for(JSONObject detail:list){
				int sxh = 1;
				String sxhString = detail.getString("sxh");		//顺序号
				try {
					sxh = detail.getIntValue("sxh");	//顺序号
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String htbh = detail.getString("htbh");	//合同编号
				if(StringUtils.isEmpty(htbh)){
					message.setMsg("第("+sxh+")笔：合同编号不能为空");
					return message;
				}
				Contract contract = contractService.findByCode(ptdm, htbh);
				if(contract == null){
					message.setMsg("第("+sxh+")笔：合同编号有误");
					return message;
				}
				String wj = detail.getString("wj");	//文件
				if(StringUtils.isEmpty(wj)){
					message.setMsg("第("+sxh+")笔：文件不能为空");
					return message;
				}
				String sftg = detail.getString("sftg");
				if(StringUtils.isEmpty(sftg)){
					message.setMsg("第("+sxh+")笔：是否通过不能为空");
					return message;
				}
				try {
					int sftg_i = jObject.getIntValue("sftg");
					if(sftg_i != 0 && sftg_i != 1){
						message.setMsg("是否通过应为1或2");
						return message;
					}
				} catch (Exception e) {
					message.setMsg("是否通过格式有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("company", company);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	@Override
	@WebMethod(action="rest")
	public String rest(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.product_getToHis, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = restcheckData(converData);
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
			message = restMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message restMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yybm = jObject.getString("yybm");//医院编码
			List<ContractDetail> contractDetails = contractDetailService.listUnClosedByHospital(ptdm, yybm);
			JSONArray array = new JSONArray();
			for(ContractDetail contractDetail:contractDetails){
				JSONObject r_jo = new JSONObject();
				r_jo.put("htbh", contractDetail.getContract().getCode());
				r_jo.put("htmxbh", contractDetail.getCode());
				r_jo.put("gysbm", contractDetail.getContract().getVendorCode());
				r_jo.put("yxqq", contractDetail.getContract().getStartValidDate());
				r_jo.put("yxqz", contractDetail.getContract().getEndValidDate());
				r_jo.put("ypbm", contractDetail.getProduct().getCode());
				r_jo.put("dj", contractDetail.getPrice());
				r_jo.put("sl", contractDetail.getContractNum());
				r_jo.put("je", contractDetail.getContractAmt());
				if((contractDetail.getContractNum()).compareTo(contractDetail.getPurchasePlanNum())==-1){
					r_jo.put("sysl", new BigDecimal(0d));
					r_jo.put("syje", new BigDecimal(0d));
				}else{
					r_jo.put("sysl", contractDetail.getContractNum().subtract(contractDetail.getPurchasePlanNum()));
					r_jo.put("syje", contractDetail.getContractAmt().subtract(contractDetail.getPurchasePlanAmt()));
				}
				array.add(r_jo);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(array);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message restcheckData(JSONObject jObject) {
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
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
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

}
