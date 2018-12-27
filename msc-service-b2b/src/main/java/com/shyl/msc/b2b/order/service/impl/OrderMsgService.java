
package com.shyl.msc.b2b.order.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.SHA1;
import com.shyl.common.util.ServiceUtil;
import com.shyl.msc.b2b.order.dao.IOrderMsgDao;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.entity.OrderMsg;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.client.service.ISocketService;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;

@Service
@Transactional(readOnly=true)
public class OrderMsgService extends BaseService<OrderMsg, Long> implements IOrderMsgService {

	@Resource
	private ISocketService socketService;
	@Resource
	private IPurchasePlanDetailDao purchasePlanDetailDao;
	
	@Resource
	private IAttributeItemService attributeItemService;
	
	private IOrderMsgDao orderMsgDao;

	public IOrderMsgDao getOrderMsgDao() {
		return orderMsgDao;
	}

	@Resource
	public void setOrderMsgDao(IOrderMsgDao orderMsgDao) {
		this.orderMsgDao = orderMsgDao;
		super.setBaseDao(orderMsgDao);
	}

	@Override
	@Transactional
	public void saveOrderMsg(String projectCode, String pCode, String code, OrderDetailStatus orderDetailStatus) {
		OrderMsg orderMsg = orderMsgDao.findByIndexCode(pCode);
		if(orderMsg == null){
			orderMsg = orderMsgDao.findByPurchasePlanDetailCode(pCode);
		}
		String purchasePlanDetailCode = "";
		String date = "";
		OrderMsg o  = new OrderMsg();
		o.setStatusDate(new Date());
		o.setOrderDetailStatus(orderDetailStatus);
		if(orderMsg == null){
			o.setPurchasePlanDetailCode(pCode);
		}else{
			o.setPurchasePlanDetailCode(orderMsg.getPurchasePlanDetailCode());
			o.setIndexCode(code);
		}
		purchasePlanDetailCode = o.getPurchasePlanDetailCode();
		date = DateUtil.dateToStr(o.getStatusDate());
		
		PurchasePlanDetail planDetail = purchasePlanDetailDao.findByCode(purchasePlanDetailCode);
		if(planDetail != null){
			int planType = planDetail.getPurchasePlan().getPlanType()==null?0:planDetail.getPurchasePlan().getPlanType();
			if(planType == 1){
				System.out.println("--------保存OrderMsg--------");
				orderMsgDao.save(o);
			}
			AttributeItem attributeItem2 = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			AttributeItem attributeItem3 = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IOCODE_B2B_TO_PE");
			if(planType == 1 && attributeItem2.getField3().equals("1") && !orderDetailStatus.equals(OrderDetailStatus.create)){//开启pe模块时
				System.out.println("----------Start推送模块---------");
				JSONObject jo = new JSONObject();
				jo.put("cgjhdmxbh", purchasePlanDetailCode);
				String data = JSON.toJSONString(jo);
				String iocode = attributeItem3.getField3();
				String sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
				String[] datas = new String[] {sign, "1", data};
				String result = "";
				try {
					AttributeItem attributeItem1 = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "WS_PE_PRESCRIPT");
					AttributeItem attributeItem4 = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "WS_TARGETNAMESPACE");
					
					result = ServiceUtil.callWebService(attributeItem1.getField3(), attributeItem4.getField3(), "getByPlanCode", datas);
					
					JSONObject jObject = JSON.parseObject(result);
					String success = jObject.getString("success");
					if(success.equals("true")){
						String arr = jObject.getString("data");
						List<JSONObject> list = JSON.parseArray(arr, JSONObject.class);
						for(JSONObject data_jo:list){
							String cfh = data_jo.getString("cfh");
							String cfmxbh = data_jo.getString("cfmxbh");
							String sfzh = data_jo.getString("sfzh");
							String jkkh = data_jo.getString("jkkh");
							String sbkh = data_jo.getString("sbkh");
							String ybkh = data_jo.getString("ybkh");
							JSONObject r_jo = new JSONObject();
							AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "PATIENT_DISTINGUISH_TYPE");
							
							if(attributeItem.getField3().equals("1")){
								r_jo.put("hzsbhlx", 1);
								r_jo.put("hzsbh", sfzh);
							}else if(attributeItem.getField3().equals("2")){
								r_jo.put("hzsbhlx", 2);
								r_jo.put("hzsbh", jkkh);
							}else if(attributeItem.getField3().equals("3")){
								r_jo.put("hzsbhlx", 3);
								r_jo.put("hzsbh", sbkh);
							}else if(attributeItem.getField3().equals("4")){
								r_jo.put("hzsbhlx", 4);
								r_jo.put("hzsbh", ybkh);
							}
							r_jo.put("cfh", cfh);
							r_jo.put("cfmxh", cfmxbh);
							r_jo.put("zt", orderDetailStatus.ordinal());
							r_jo.put("clsj", date);
							socketService.messageSentAll(r_jo.toJSONString());//推送处方消息
							System.out.println("----------Start推送Data---------"+r_jo.toJSONString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void updateOrderMsg(String projectCode, String pCode, String code) {
		OrderMsg orderMsg = orderMsgDao.findByIndexCode(pCode);
		if(orderMsg != null){
			orderMsg.setIndexCode(code);
			orderMsgDao.update(orderMsg);
		}
	}

	@Override
	public List<OrderMsg> listByPlanDetailCode(String projectCode, String planDetailCode) {
		return orderMsgDao.listByPlanDetailCode(planDetailCode);
	}

	@Override
	public OrderMsg getByPurchasePlanDetailCode(String projectCode, String planDetailCode) {
		return orderMsgDao.getByPurchasePlanDetailCode(planDetailCode);
	}

}
