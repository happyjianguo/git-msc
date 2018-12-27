package com.shyl.msc.webService.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shyl.common.util.HttpRequestUtil;
import com.shyl.common.util.SHA1;
import com.shyl.common.util.XmlConverUtil;
import com.shyl.msc.b2b.order.entity.Datagram;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.service.IDatagramService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IUserService;

public class BaseWebService {
	@Resource
	private WebServiceContext context;
	@Resource
	private IDatagramService datagramService;
	@Resource
	private IUserService userService;
	@Resource
	private IAttributeItemService attributeItemService;
	//private String CAKEY = CommonProperties.CAKEY;
	/**
	 * log日志类
	 */
	protected  Logger logger = LoggerFactory.getLogger(this.getClass().getGenericSuperclass()+"");
	
	public Message checkSign(String ioCode, String data, String sign){
		//String jylx = converData.getString("");
		Message message = new Message();
		boolean r_b = SHA1.checkSign(ioCode, data, sign);
		if(!r_b){
			message.setMsg("无效签名");
			message.setSuccess(false);
			message.setData("");
			return message;
		}
		return message;
	}
	/**
	 * 新增报文资料
	 * @param datagramType
	 * @param senderCode
	 * @param senderName
	 * @param data
	 * @return
	 */
	public Message savaDatagrame(String projectCode, String senderCode, String senderName, String data, String dataType, DatagramType datagramType){
		Message message = new Message();
		try {
			Datagram datagrame = new Datagram();
			datagrame.setDatagramType(datagramType);
			datagrame.setSendDate(new Date());
			datagrame.setSenderCode(senderCode);
			datagrame.setSenderName(senderName);
			datagrame.setIp(getIp());
			datagrame.setDataType(Integer.parseInt(dataType));
			datagrame.setData(data);
			datagrame = datagramService.save(projectCode, datagrame);
			Long datagramId = datagrame.getId();
			message.setData(datagramId);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器端出错！");
			message.setSuccess(false);
			return message;
		}
		return message;
	}

	/**
	 * 检查数据类型
	 * @param datagramType 
	 * @param sign
	 * @param data
	 * @return
	 */
	public Message checkDataType(DatagramType datagramType, String sign, String data_Type, String data) {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		String method = Thread.currentThread().getStackTrace()[2].getMethodName();
		logger = LoggerFactory.getLogger(className+"."+method);
		logger.info("datagramType: "+datagramType.toString());
		logger.info("sign："+sign);
		logger.info("dataType："+data_Type);
		logger.info("data："+data);
		Message message = new Message();
		message.setSuccess(false);
		JSONObject converData = new JSONObject();
		if(StringUtils.isEmpty(data)){
			message.setMsg("数据不能为空");
			return message;
		}
		if(data_Type.equals("1")){
			try {
				converData = JSON.parseObject(data);
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("JSON格式不正确");
				return message;
			}
		}else if(data_Type.equals("2")){
			try {
				data = XmlConverUtil.xmltoJson(data);
				converData = JSON.parseObject(data);
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("XML格式不正确");
				return message;
			}
		}else{
			message.setMsg("数据类型不正确");
			return message;
		}
		String yybm = converData.getString("yybm");//医院编码
		String gysbm = converData.getString("gysbm");//供应商编码
		if(!StringUtils.isEmpty(yybm)){
			logger.info("senderCode："+yybm);
		}
		if(!StringUtils.isEmpty(gysbm)){
			logger.info("senderCode："+gysbm);
		}
		message.setSuccess(true);
		return message;
	}
	
	public JSONObject getConverData(String dataType, String data){
		JSONObject converData = new JSONObject();
		if(dataType.equals("1")){
			converData = JSON.parseObject(data);
		}else if(dataType.equals("2")){
			data = XmlConverUtil.xmltoJson(data).replaceAll("\\[\\]", "null");
			converData = JSON.parseObject(data);
		}
		return converData;
	}
	/**
	 * 结果消息
	 * @param dataType
	 * @param message
	 * @return
	 */
	public String resultMessage(Message message, String dataType){
		String result = "";
		if(dataType.equals("1")){
			result = JSON.toJSONString(message,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty);
		}else if(dataType.equals("2")){
			result =  XmlConverUtil.jsontoXml(JSON.toJSONString(message,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty));
		}
		logger.info("returnData："+result);
		return result;
	}
	/**
	 * 取客户端IP地址
	 * @return
	 */
	public String getIp() {
		try {
			MessageContext ctx = context.getMessageContext();
			HttpServletRequest request = (HttpServletRequest) ctx
			.get(AbstractHTTPDestination.HTTP_REQUEST);
			String ip = request.getRemoteAddr();
			return ip;
		} catch (Exception e) {
			return "获取ip失败";
		}
	}
	
	/**
	 * CA验证
	 * @param empId
	 * @param iocode
	 * @param CACert
	 * @param CASign
	 * @return
	 */
	public Message checkCA(String ptdm, String empId,String iocode,String CACert,String CASign) {
		Message message = new Message();
		message.setSuccess(false);
		System.out.println("empId:"+empId);
		System.out.println("CACert:"+CACert);
		System.out.println("CASign:"+CASign);
		AttributeItem attributeItem1 = attributeItemService.queryByAttrAndItemNo(ptdm,"publicUser", "ISCAUSED");
		String CAflag = attributeItem1.getField3();
		String specialEmpId = attributeItem1.getField4();
		User user = new User();
		if(CAflag.equals("0") || ("1".equals(CAflag) && StringUtils.isNotEmpty(specialEmpId) && specialEmpId.contains(empId))){
			message.setSuccess(true);
			message.setData(user);
			return message;
		}
			
		if(StringUtils.isEmpty(empId)){
			message.setMsg("用户账号不能为空");
			return message;
		}
		/*if(StringUtils.isEmpty(CACert)){
			message.setMsg("用户证书不能为空");
			return message;
		}
		if(StringUtils.isEmpty(CASign)){
			message.setMsg("用户签名不能为空");
			return message;
		}*/
		try{

			user = userService.findByEmpId(ptdm, empId);
			if (user == null) {
				message.setMsg("用户账号错误");
				return message;
			}
			if (StringUtils.isBlank(user.getClientCert())) {
				message.setSuccess(true);
				message.setData(new User());
				return message;
			}
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "SZCA_SIGN_HTTP");
			String result = HttpRequestUtil.verSignData(CACert, iocode, CASign,attributeItem.getField3());
			JSONObject obj = JSON.parseObject(result);
			System.out.println(obj.toString());
			//校验sign结果
			if ("true".equals(obj.get("code"))) {
				/*
				if (user.getClientCert() == null) {
					message.setMsg("CA证书未绑定");
					return message;
				}*/
				String oid = obj.getString("oid");
				//判断用户维护的唯一标准oid从后面匹配是否和cert中维护的一致
				//oid格式可能为SF000000000000，也可能为1@xxxSF0000000000
				if (user!=null&&!StringUtils.isBlank(oid) && !StringUtils.isBlank(oid) 
						&& !StringUtils.isBlank(user.getClientCert()) && oid.endsWith(user.getClientCert())) {
					message.setSuccess(true);
					message.setData(user);
					return message;
				} else {
					message.setMsg("CA验证失败，用户账号（"+empId+"）绑定值（"+oid+"）与上传key不一致");
					return message;
				}
			} else {
				message.setMsg(obj.getString("msg"));
				return message;
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setMsg("服务器程序出错checkCA");
			return message;
		}		
	}
}
