package com.shyl.msc.webService.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.OrderMsg;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.webService.IOrderMsgWebService;
import com.shyl.sys.dto.Message;

/**
 * 订单消息实现类
 * @author a_Q
 *
 */
@WebService(serviceName="orderMsgWebService",portName="orderMsgPort", targetNamespace="http://webservice.msc.shyl.com/")
public class OrderMsgWebService extends BaseWebService implements IOrderMsgWebService {

	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.orderMsg_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = getCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第三步验证签名
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "IOCODE_B2B_TO_PE");
			message = checkSign(attributeItem.getField3(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message getMethod(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "PLATFORM_NO");
			String cgjhmxbh = converData.getString("cgjhmxbh");//采购计划明细编号
			OrderMsg orderMsg = orderMsgService.getByPurchasePlanDetailCode(attributeItem.getField3(), cgjhmxbh);
			
			JSONObject r_jo = new JSONObject();
			r_jo.put("cgjhmxbh", orderMsg.getPurchasePlanDetailCode());
			r_jo.put("clsj", DateUtil.dateToStr(orderMsg.getStatusDate()));
			r_jo.put("zt", orderMsg.getOrderDetailStatus().ordinal());
			message.setData(r_jo);
			message.setSuccess(true);
			message.setMsg("成功返回");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message getCheckData(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "PLATFORM_NO");
			String cgjhmxbh = converData.getString("cgjhmxbh");//采购计划明细编号
			if(StringUtils.isEmpty(cgjhmxbh)){
				message.setMsg("采购计划明细编号不能为空");
				return message;
			}
			OrderMsg orderMsg = orderMsgService.getByPurchasePlanDetailCode(attributeItem.getField3(), cgjhmxbh);
			if(orderMsg == null){
				message.setMsg("采购计划明细编号有误");
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		return message;
	}

	@Override
	public String list(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.orderMsg_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = listCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第三步验证签名
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "IOCODE_B2B_TO_PE");
			message = checkSign(attributeItem.getField3(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = listMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message listMethod(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "PLATFORM_NO");
			String cfmxh = converData.getString("cfmxh");//采购计划明细编号
			JSONArray array = new JSONArray();
			List<OrderMsg> orderMsgs = orderMsgService.listByPlanDetailCode(attributeItem.getField3(), cfmxh);
			for(OrderMsg orderMsg:orderMsgs){
				JSONObject jo = new JSONObject();
				int zt = orderMsg.getOrderDetailStatus().ordinal();
				if(zt == 5){
					jo.put("zt", 2);
				}else if(zt == 6){
					jo.put("zt", 3);
				}else{
					jo.put("zt", 1);
				}
				jo.put("sj", DateUtil.dateToStr(orderMsg.getStatusDate()));
				array.add(jo);
			}
			
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(array);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message listCheckData(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String cfmxh = converData.getString("cfmxh");//采购计划明细编号
			if(StringUtils.isEmpty(cfmxh)){
				message.setMsg("采购计划明细编号不能为空");
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		return message;
	}
}
