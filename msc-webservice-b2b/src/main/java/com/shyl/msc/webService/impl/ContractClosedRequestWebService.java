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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IContractClosedRequestWebService;
import com.shyl.sys.dto.Message;

@WebService(serviceName="contractClosedRequestWebService",portName="contractClosedRequestPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ContractClosedRequestWebService extends BaseWebService implements IContractClosedRequestWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IContractClosedRequestService contractClosedRequestService;
	@Resource
	private IContractService contractService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.contractClosedRequest_get, sign, dataType, data);
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
			Boolean isGPO = (Boolean) map.get("isGPO");
			Company company = (Company) map.get("company");
			Boolean isCode = (Boolean) map.get("isCode");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, company, isGPO, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message getMethod(JSONObject jObject, Company company, boolean isGPO, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String cxkssj = jObject.getString("cxkssj");
			String cxjssj = jObject.getString("cxjssj");
			String htjasqdbh = jObject.getString("htjasqdbh");
			List<Map<String, Object>> contractClosedRequests = new ArrayList<>();
			if(isCode){
				contractClosedRequests = contractClosedRequestService.listByCode(ptdm, htjasqdbh);
			}else{
				contractClosedRequests = contractClosedRequestService.listByDate(ptdm, company.getCode(), cxkssj, cxjssj, isGPO);
			}
			JSONArray r_jos = new JSONArray();
			for(Map<String, Object> request:contractClosedRequests){
				JSONObject r_jo = new JSONObject();
				r_jo.put("htjasqdbh", request.get("CODE")==null?"":request.get("CODE"));//结案申请单编号
				r_jo.put("jadx", request.get("CLOSEDOBJECT")==null?"":request.get("CLOSEDOBJECT"));//结案对象 
				r_jo.put("htbh", request.get("CONTRACTCODE")==null?"":request.get("CONTRACTCODE"));//合同明细编号 
				r_jo.put("htmxbh", request.get("CONTRACTDETAILCODE")==null?"":request.get("CONTRACTDETAILCODE"));//合同明细编号 
				r_jo.put("yybm", request.get("HOSPITALCODE")==null?"":request.get("HOSPITALCODE"));//医院编码
				r_jo.put("gpobm", request.get("GPOCODE")==null?"":request.get("GPOCODE"));//GPO编码
				r_jo.put("gysbm", request.get("VENDORCODE")==null?"":request.get("VENDORCODE"));//供应商编码
				r_jo.put("jasqr", request.get("CLOSEDMAN")==null?"":request.get("CLOSEDMAN"));//申请人
				r_jo.put("jasqsj", request.get("REQUESTDATE")==null?"":request.get("REQUESTDATE"));//申请时间
				r_jo.put("yy", request.get("REASON")==null?"":request.get("REASON"));	//原因
				r_jos.add(r_jo);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(r_jos);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}
	private Message checkData(JSONObject jObject) {
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
			String cxkssj = jObject.getString("cxkssj");	//查询开始时间
			String cxjssj = jObject.getString("cxjssj");	//查询结束时间
			String htjasqdh = jObject.getString("htjasqdh");	//合同结案申请单编号
			if(StringUtils.isEmpty(htjasqdh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或合同结案申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或合同结案申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				ContractClosedRequest contractClosedRequest = contractClosedRequestService.findByCode(ptdm, htjasqdh);
				if(contractClosedRequest == null){
					message.setMsg("合同结案申请单编号有误");
					return message;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		return message;
	}
	@Override
	@WebMethod(action="fedback")
	@WebResult(name="fedbackResult")
	public String fedback(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.contractClosedRequest_fedback, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData2(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			
			Company company = (Company) map.get("company");
			String ptdm = converData.getString("ptdm");	//平台代码
			
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.contractClosedRequest_fedback);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = fedbackMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	private Message fedbackMethod(JSONObject jo) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jo.getString("ptdm");	//平台代码
			String htjasqdbh = jo.getString("htjasqdbh");//结案申请单编号
			int zt = jo.getIntValue("zt");//状态
			String df = jo.getString("df");//答复
			
			ContractClosedRequest contractClosedRequest = contractClosedRequestService.findByCode(ptdm, htjasqdbh);
			contractClosedRequest.setReply(df);
			if(zt == 0){
				contractClosedRequest.setStatus(ContractClosedRequest.Status.disagree);
			}else if(zt == 1){
				contractClosedRequest.setStatus(ContractClosedRequest.Status.agree);
			}
			contractClosedRequestService.saveRequest(ptdm, contractClosedRequest);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData("");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}
	private Message checkData2(JSONObject jObject) {
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
			
			String htjasqdbh = jObject.getString("htjasqdbh");//结案申请单编号
			if(StringUtils.isEmpty(htjasqdbh)){
				message.setMsg("合同结案申请单编号不能为空");
				return message;
			}
			ContractClosedRequest contractClosedRequest = contractClosedRequestService.findByCode(ptdm, htjasqdbh);
			if(contractClosedRequest == null){
				message.setMsg("合同结案申请单编号有误");
				return message;
			}
			Contract contract = contractService.findByCode(ptdm, contractClosedRequest.getContractCode());
			if(isGPO){
				if(!contract.getGpoCode().equals(company.getCode())){
					message.setMsg("结案申请单GPOO编码和请求机构编码不匹配");
					return message;
				}
			}else{
				if(!contract.getVendorCode().equals(company.getCode())){
					message.setMsg("结案申请单供应商编码和请求机构编码不匹配");
					return message;
				}
			}
			if(contractClosedRequest.getStatus().equals(PurchaseClosedRequest.Status.agree)
					|| contractClosedRequest.getStatus().equals(PurchaseClosedRequest.Status.disagree)){
				message.setMsg("该申请已经反馈");
				return message;
			}
			String ztString = jObject.getString("zt");
			if(StringUtils.isEmpty(ztString)){
				message.setMsg("状态不能为空");
				return message;
			}
			try {
				int zt = jObject.getIntValue("zt");//状态
				if(zt != 0 && zt != 1){
					message.setMsg("状态应为0或1");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("状态格式有误");
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		message.setData(map);
		return message;
	}
}
