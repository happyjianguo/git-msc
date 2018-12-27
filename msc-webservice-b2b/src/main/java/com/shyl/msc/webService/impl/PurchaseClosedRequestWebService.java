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
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IPurchaseClosedRequestWebService;
import com.shyl.sys.dto.Message;

@WebService(serviceName="purchaseClosedRequestWebService",portName="purchaseClosedRequestPort", targetNamespace="http://webservice.msc.shyl.com/")
public class PurchaseClosedRequestWebService extends BaseWebService implements IPurchaseClosedRequestWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IPurchaseClosedRequestService purchaseClosedRequestService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaserClosedRequest_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData3(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Hospital hospital = (Hospital) map.get("hospital");
			String ptdm = converData.getString("ptdm");	//平台代码
			//第三步验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.purchaserClosedRequest_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第五步执行主逻辑
			message = sendMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	private Message sendMethod(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(true);
		return message;
	}
	private Message checkData3(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(true);
		return message;
	}
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaserClosedRequest_get, sign, dataType, data);
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
			JSONArray r_jos = purchaseClosedRequestService.getToGPO(ptdm, company, isGPO, isCode, jObject);
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
			String ddjasqdbh = jObject.getString("ddjasqdbh");//订单结案申请单编号
			String cxkssj = jObject.getString("cxkssj");	//查询开始时间
			String cxjssj = jObject.getString("cxjssj");	//查询结束时间
			if(StringUtils.isEmpty(ddjasqdbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或订单结案申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或订单结案申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestService.findByCode(ptdm, ddjasqdbh);
				if(purchaseClosedRequest == null){
					message.setMsg("订单结案申请 单编号有误");
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
	@WebResult(name="fedbackResult")
	public String fedback(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaserClosedRequest_fedback, sign, dataType, data);
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
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.purchaserClosedRequest_fedback);
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
			int zt = jo.getIntValue("zt");//状态
			String df = jo.getString("df");//答复
			String ddjasqdbh = jo.getString("ddjasqdbh");//订单结案申请单编号
			PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestService.findByCode(ptdm, ddjasqdbh);
			purchaseClosedRequest.setReply(df);
			if(zt == 0){
				purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.disagree);
			}else if(zt == 1){
				purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.agree);
			}
			purchaseClosedRequestService.saveRequest(ptdm, purchaseClosedRequest);
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
		PurchaseClosedRequest purchaseClosedRequest = new PurchaseClosedRequest();
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
			
			String ddjasqdbh = jObject.getString("ddjasqdbh");//订单结案申请单编号
			if(StringUtils.isEmpty(ddjasqdbh)){
				message.setMsg("订单结案申请单编号不能为空");
				return message;
			}
			purchaseClosedRequest = purchaseClosedRequestService.findByCode(ptdm, ddjasqdbh);
			if(purchaseClosedRequest == null){
				message.setMsg("订单结案申请单编号有误");
				return message;
			}
			
			if(purchaseClosedRequest.getStatus().equals(PurchaseClosedRequest.Status.agree)
					|| purchaseClosedRequest.getStatus().equals(PurchaseClosedRequest.Status.disagree)){
				message.setMsg("该申请已经反馈");
				return message;
			}
			PurchaseOrder purchaseOrder = purchaseOrderService.findByCode(ptdm, purchaseClosedRequest.getPurchaseOrderCode());
			if(purchaseOrder == null){
				message.setMsg("没有找到相应的订单");
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
		Map<String, Object> map = new HashMap<>();
		map.put("company", company);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
