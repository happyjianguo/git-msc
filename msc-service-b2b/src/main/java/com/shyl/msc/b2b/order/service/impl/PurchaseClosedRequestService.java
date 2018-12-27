package com.shyl.msc.b2b.order.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.dao.IPurchaseClosedRequestDao;
import com.shyl.msc.b2b.order.dao.IPurchaseClosedRequestDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest.ClosedType;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestService;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Service
@Transactional(readOnly = true)
public class PurchaseClosedRequestService extends BaseService<PurchaseClosedRequest, Long> implements IPurchaseClosedRequestService {

	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IMsgService msgService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IPurchaseClosedRequestDetailDao purchaseClosedRequestDetailDao;
	@Resource
	private ISnDao snDao;
	
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	
	private IPurchaseClosedRequestDao purchaseClosedRequestDao;

	public IPurchaseClosedRequestDao getPurchaseClosedRequestDao() {
		return purchaseClosedRequestDao;
	}

	@Resource
	public void setPurchaseClosedRequestDao(IPurchaseClosedRequestDao purchaseClosedRequestDao) {
		this.purchaseClosedRequestDao = purchaseClosedRequestDao;
		super.setBaseDao(purchaseClosedRequestDao);
	}

	@Override
	public PurchaseClosedRequest findByPurchaseOrderCode(String projectCode, String code) {
		return purchaseClosedRequestDao.findByPurchaseOrderCode(code);
	}

	@Override
	public List<PurchaseClosedRequest> listByDate(String projectCode, String vendorCode, String startDate, String endDate, boolean isGPO) {
		return purchaseClosedRequestDao.listByDate(vendorCode, startDate, endDate, isGPO);
	}

	@Override
	public PurchaseClosedRequest findByCode(String projectCode, String code) {
		return purchaseClosedRequestDao.findByCode(code);
	}

	@Override
	@Transactional
	public void saveRequest(String projectCode, PurchaseClosedRequest purchaseClosedRequest) {
		purchaseClosedRequestDao.update(purchaseClosedRequest);
		if(purchaseClosedRequest.getStatus().equals(PurchaseClosedRequest.Status.agree)){
			PurchaseOrder purchaseOrder = purchaseOrderDao.findByCode(purchaseClosedRequest.getPurchaseOrderCode());
			purchaseOrder.setStatus(PurchaseOrder.Status.forceClosed);
			purchaseOrderDao.update(purchaseOrder);
		}
	}

	@Override
	@Transactional
	public JSONArray getToGPO(String projectCode, Company company, boolean isGPO, Boolean isCode, JSONObject jObject) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		String cxkssj = jObject.getString("cxkssj");
		String cxjssj = jObject.getString("cxjssj");
		String ddjasqdbh = jObject.getString("ddjasqdbh");
		List<PurchaseClosedRequest> purchaseClosedRequests = new ArrayList<>();
		if(isCode){
			PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestDao.findByCode(ddjasqdbh);
			purchaseClosedRequests.add(purchaseClosedRequest);
		}else{
			purchaseClosedRequests = purchaseClosedRequestDao.listByDate(company.getCode(), cxkssj, cxjssj, isGPO);
		}
		JSONArray r_jos = new JSONArray();
		for(PurchaseClosedRequest request:purchaseClosedRequests){
			JSONObject r_jo = new JSONObject();
			r_jo.put("ddjasqdbh", request.getCode());//订单结案申请单编号
			r_jo.put("ddbh", request.getPurchaseOrderCode());//订单编号
			r_jo.put("yybm", request.getHospitalCode());//医院编码
			if(request.getGpoCode() != null){
				r_jo.put("gpobm", request.getGpoCode());//GPO编码
			}else{
				r_jo.put("gpobm", "");//GPO编码
			}
			if(request.getVendorCode() != null){
				r_jo.put("gysbm", request.getVendorCode());//供应商编码
			}else{
				r_jo.put("gysbm", "");//供应商编码
			}
			r_jo.put("jasqr", request.getClosedMan());//结案申请人
			r_jo.put("jasqsj", DateUtil.dateToStr(request.getClosedRequestDate()));//结案申请时间
			r_jo.put("yy", request.getReason());	//原因
			if(request.getIsRead() == null || request.getIsRead()==0){
				request.setIsRead(1);
				this.updateWithInclude(projectCode, request, "isRead");
			}
			r_jo.put("jalx", request.getClosedType().ordinal()+1);
			if(request.getClosedType().equals(PurchaseClosedRequest.ClosedType.orderDetailClosed)){
				JSONArray mx_arr = new JSONArray();
				List<PurchaseClosedRequestDetail> purchaseClosedRequestDetails = purchaseClosedRequestDetailDao.listByRequestId(request.getId());
				int k = 0;
				for(PurchaseClosedRequestDetail detail:purchaseClosedRequestDetails){
					k++;
					JSONObject mx_js = new JSONObject();
					mx_js.put("sxh", k);
					mx_js.put("ddmxbh", detail.getPurchaseOrderDetailCode());
					mx_js.put("ypbm", detail.getProductCode());
					mx_arr.add(mx_js);
				}
				r_jo.put("mx", mx_arr);
			}
			r_jos.add(r_jo);
		}
		return r_jos;
	}

	@Override
	@Transactional
	public void closeCommit(String projectCode, Long id, String status, String reply) {
		PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestDao.getById(id);
		purchaseClosedRequest.setReply(reply);
		if(status.equals("2")){
			purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.disagree);
			//发送系统消息
			this.sendMsg(projectCode, purchaseClosedRequest);
		}else if(status.equals("1")){
			purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.agree);
			if(purchaseClosedRequest.getClosedType().equals(ClosedType.orderClosed)){//订单结案时
				PurchaseOrder purchaseOrder = purchaseOrderDao.findByCode(purchaseClosedRequest.getPurchaseOrderCode());
				purchaseOrder.setStatus(PurchaseOrder.Status.forceClosed);
				purchaseOrderDao.update(purchaseOrder);
				
				//扣减合同采购计划量,更新结案量
				List<PurchaseOrderDetail> podlist = purchaseOrderDetailDao.listByOrderId(purchaseOrder.getId());
				for (PurchaseOrderDetail pod : podlist) {
					ContractDetail cd = contractDetailService.findByCode(projectCode, pod.getContractDetailCode());
					if(cd != null){
						BigDecimal a = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
						BigDecimal goodsNum = pod.getGoodsNum() == null?new BigDecimal("0"):pod.getGoodsNum();
						BigDecimal deliveryGoodsNum = pod.getDeliveryGoodsNum() == null?new BigDecimal("0"):pod.getDeliveryGoodsNum();
						cd.setPurchasePlanNum(a.subtract(goodsNum).add(deliveryGoodsNum));
						BigDecimal b = cd.getClosedNum()==null?new BigDecimal("0"):cd.getClosedNum();
						cd.setClosedNum(b.add(deliveryGoodsNum));
						contractDetailService.update(projectCode, cd);
					}
				}
			}else{//明细结案时
				List<PurchaseClosedRequestDetail> details = purchaseClosedRequestDetailDao.listByRequestId(purchaseClosedRequest.getId());
				for(PurchaseClosedRequestDetail detail:details){
					String orderDetailCode = detail.getPurchaseOrderDetailCode();
					PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.findByCode(orderDetailCode);
					ContractDetail cd = contractDetailService.findByCode(projectCode, purchaseOrderDetail.getContractDetailCode());
					if(cd != null){
						BigDecimal a = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
						BigDecimal goodsNum = purchaseOrderDetail.getGoodsNum() == null?new BigDecimal("0"):purchaseOrderDetail.getGoodsNum();
						BigDecimal deliveryGoodsNum = purchaseOrderDetail.getDeliveryGoodsNum() == null?new BigDecimal("0"):purchaseOrderDetail.getDeliveryGoodsNum();
						cd.setPurchasePlanNum(a.subtract(goodsNum).add(deliveryGoodsNum));
						BigDecimal b = cd.getClosedNum()==null?new BigDecimal("0"):cd.getClosedNum();
						cd.setClosedNum(b.add(deliveryGoodsNum));
						contractDetailService.update(projectCode, cd);
					}
					
					purchaseOrderDetail.setStatus(PurchaseOrderDetail.Status.stop);
					purchaseOrderDetailDao.update(purchaseOrderDetail);
				}
			}
		}
		purchaseClosedRequestDao.update(purchaseClosedRequest);
	}
	
	private void sendMsg(String projectCode, PurchaseClosedRequest purchaseClosedRequest) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		msg.setTitle("结案审核未通过。-- "+purchaseClosedRequest.getCode());
		msg.setAttach("/hospital/purchaseRequest.htmlx?code="+purchaseClosedRequest.getCode());
		
		Hospital hospital = hospitalService.findByCode(projectCode, purchaseClosedRequest.getHospitalCode());
		Company vendor = companyService.findByCode(projectCode,purchaseClosedRequest.getVendorCode(), "isVendor=1");
		//查询医院的orgId
		Organization oh = organizationService.getByOrgId(projectCode, hospital.getId(), 1);
		//查询供应商的orgId
		Organization og = organizationService.getByOrgId(projectCode, vendor.getId(), 2);
		if(og != null){
			msg.setOrganizationId(og.getId());
			msg.setOrgName(og.getOrgName());
		}
		Long ohId = Long.parseLong("-1");
		if(oh != null){
			ohId = oh.getId();
		}
		try {
			msgService.sendBySYSToOrg(projectCode, msg, ohId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PurchaseClosedRequest findByPurchaseOrderCode(String projectCode, String code, ClosedType closedType) {
		return purchaseClosedRequestDao.findByPurchaseOrderCode(code, closedType);
	}

	@Override
	@Transactional
	public Message savePurchaseRequest(String projectCode, Long id, String reason, String data, User user) {
		Message message = new Message();
		try {
			PurchaseOrder purchaseOrder = purchaseOrderDao.getById(id);
			PurchaseClosedRequest purchaseClosedRequest_order = purchaseClosedRequestDao.findByPurchaseOrderCode(purchaseOrder.getCode(), PurchaseClosedRequest.ClosedType.orderClosed);
			if(purchaseClosedRequest_order != null){
				message.setSuccess(false);
				message.setMsg("该订单已经提交申请结案");
				return message;
			}
			PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestDao.findByPurchaseOrderCode(purchaseOrder.getCode(), PurchaseClosedRequest.ClosedType.orderDetailClosed);
			List<JSONObject> list = JSON.parseArray(data, JSONObject.class);
			if(purchaseClosedRequest != null){
				List<PurchaseClosedRequestDetail> details = purchaseClosedRequestDetailDao.listByRequestId(purchaseClosedRequest.getId());
				Map<String, String> map = new HashMap<>();
				for(PurchaseClosedRequestDetail detail:details){
					map.put(detail.getPurchaseOrderDetailCode(), detail.getPurchaseOrderDetailCode());
				}
				for(JSONObject jo:list){
					String productCode = jo.getString("productCode");
					String purchaseOrderDetailCode = jo.getString("code");
					if(map.get(purchaseOrderDetailCode) != null){
						message.setSuccess(false);
						message.setMsg("该订单明细（"+productCode+"）已经提交申请结案");
						return message;
					}
				}
			}
			BigDecimal num = new BigDecimal(0);
			BigDecimal sum = new BigDecimal(0);
			for(JSONObject jo:list){
				String purchaseOrderDetailCode = jo.getString("code");
				PurchaseOrderDetail orderDetail = purchaseOrderDetailDao.findByCode(purchaseOrderDetailCode);
				num = num.add(orderDetail.getGoodsNum());
				sum = sum.add(orderDetail.getGoodsSum());
			}
			
			purchaseClosedRequest = new PurchaseClosedRequest();
			purchaseClosedRequest.setPurchaseOrderCode(purchaseOrder.getCode());
			purchaseClosedRequest.setClosedMan(user.getName());
			purchaseClosedRequest.setClosedRequestDate(new Date());
			purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.unaudit);
			purchaseClosedRequest.setReason(reason);
			
			purchaseClosedRequest.setCode(snDao.getCode(OrderType.orderClosedRequest));
			purchaseClosedRequest.setInternalCode(purchaseOrder.getInternalCode());
			purchaseClosedRequest.setOrderDate(purchaseOrder.getOrderDate());
			purchaseClosedRequest.setVendorCode(purchaseOrder.getVendorCode());
			purchaseClosedRequest.setVendorName(purchaseOrder.getVendorName());
			purchaseClosedRequest.setHospitalCode(purchaseOrder.getHospitalCode());
			purchaseClosedRequest.setHospitalName(purchaseOrder.getHospitalName());
			purchaseClosedRequest.setWarehouseCode(purchaseOrder.getWarehouseCode());
			purchaseClosedRequest.setWarehouseName(purchaseOrder.getWarehouseName());
			purchaseClosedRequest.setNum(num);
			purchaseClosedRequest.setSum(sum);
			purchaseClosedRequest.setGpoCode(purchaseOrder.getGpoCode());
			purchaseClosedRequest.setGpoName(purchaseOrder.getGpoName());
			purchaseClosedRequest.setIsPass(0);
			purchaseClosedRequest.setIsAuto(0);
			purchaseClosedRequest.setOrderType(OrderType.orderClosedRequest);
			purchaseClosedRequest.setClosedType(PurchaseClosedRequest.ClosedType.orderDetailClosed);
			
			purchaseClosedRequestDao.save(purchaseClosedRequest);
			
			for(JSONObject jo:list){
				String productCode = jo.getString("productCode");
				String purchaseOrderDetailCode = jo.getString("code");
				PurchaseOrderDetail orderDetail = purchaseOrderDetailDao.findByCode(purchaseOrderDetailCode);
				PurchaseClosedRequestDetail detail = new PurchaseClosedRequestDetail();
				detail.setProjectCode(user.getProjectCode());
				detail.setProductCode(productCode);
				detail.setPurchaseOrderDetailCode(purchaseOrderDetailCode);
				detail.setPurchaseClosedRequest(purchaseClosedRequest);
				detail.setProductName(orderDetail.getProductName());
				detail.setProducerName(orderDetail.getProducerName());
				detail.setDosageFormName(orderDetail.getDosageFormName());
				detail.setModel(orderDetail.getModel());
				detail.setPackDesc(orderDetail.getPackDesc());
				detail.setUnit(orderDetail.getUnit());
				detail.setPrice(orderDetail.getPrice());
				detail.setGoodsNum(orderDetail.getGoodsNum());
				detail.setGoodsSum(orderDetail.getGoodsSum());
				
				purchaseClosedRequestDetailDao.save(detail);
			}
			message.setMsg("申请结案成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("申请结案失败！");
		}
		return message;
	}
	
}
