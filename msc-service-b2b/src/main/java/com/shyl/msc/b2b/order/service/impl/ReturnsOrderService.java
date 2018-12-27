package com.shyl.msc.b2b.order.service.impl;

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
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDao;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.msc.b2b.order.entity.ReturnsRequest.Status;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;
/**
 * 退货单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ReturnsOrderService extends BaseService<ReturnsOrder, Long> implements IReturnsOrderService {
	private IReturnsOrderDao returnsOrderDao;
	@Resource
	private IReturnsOrderDetailDao returnsOrderDetailDao;
	@Resource
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource 
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IPurchaseOrderPlanDao purchaseOrderPlanDao;
	@Resource
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;
	@Resource
	private IReturnsRequestDao returnsRequestDao;
	@Resource
	private IReturnsRequestDetailDao returnsRequestDetailDao;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource
	private ISnDao sndao;
	@Resource
	private IAttributeItemService attributeItemService;
	
	
	public IReturnsOrderDao getReturnsOrderDao() {
		return returnsOrderDao;
	}

	@Resource
	public void setReturnsOrderDao(IReturnsOrderDao returnsOrderDao) {
		this.returnsOrderDao = returnsOrderDao;
		super.setBaseDao(returnsOrderDao);	
	}
	
	@Override
	@Transactional
	public void saveReturnsOrder(String projectCode, ReturnsOrder returnsOrder){
		returnsOrderDao.saveJDBC(returnsOrder);
		

		StringBuffer detailStr = new StringBuffer();
		for(ReturnsOrderDetail detail:returnsOrder.getReturnsOrderDetails()){
			if (detailStr.length() != 0) {
				detailStr.append(",");
			}
			detailStr.append("'").append(detail.getContractDetailCode()).append("'");
		}

		List<ContractDetail> cds = contractDetailDao.listByCodes(returnsOrder.getHospitalCode(), detailStr.toString());

		Map<String, ContractDetail> cdMap = new HashMap<>();
		for(ContractDetail cd:cds){
			cdMap.put(cd.getCode(), cd);
		}
		
		Map<String, BigDecimal> numMap = new HashMap<>();
		Map<String, BigDecimal> sumMap = new HashMap<>();
		List<ReturnsOrderDetail> returnsOrderDetails = new ArrayList<>();
		List<DeliveryOrderDetail> deliveryOrderDetails = new ArrayList<>();
		List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
		List<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = new ArrayList<>();
		List<ContractDetail> contractDetails = new ArrayList<>();
		for (ReturnsOrderDetail returnsOrderDetail : returnsOrder.getReturnsOrderDetails()) {
			returnsOrderDetail.setReturnsOrder(returnsOrder);
			returnsOrderDetails.add(returnsOrderDetail);
			//returnsOrderDetailDao.save(returnsOrderDetail);
			
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem.getField3().equals("1")){
				orderMsgService.saveOrderMsg(projectCode, returnsOrderDetail.getDeliveryOrderDetailCode(), returnsOrderDetail.getCode(), OrderDetailStatus.returns);
			}
			
			//更新配送单明细
			DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.findByCode(returnsOrderDetail.getDeliveryOrderDetailCode());
			BigDecimal oldDNum = deliveryOrderDetail.getReturnsGoodsNum()==null?new BigDecimal(0):deliveryOrderDetail.getReturnsGoodsNum();
			BigDecimal oldDSum = deliveryOrderDetail.getReturnsGoodsSum()==null?new BigDecimal(0):deliveryOrderDetail.getReturnsGoodsSum();
			deliveryOrderDetail.setReturnsGoodsNum(oldDNum.add(returnsOrderDetail.getGoodsNum()));
			deliveryOrderDetail.setReturnsGoodsSum(oldDSum.add(returnsOrderDetail.getGoodsSum()));
			deliveryOrderDetails.add(deliveryOrderDetail);
			//deliveryOrderDetailDao.update(deliveryOrderDetail);
			
			//更新订单明细
			PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.findByCode(deliveryOrderDetail.getPurchaseOrderDetailCode());
			oldDNum = purchaseOrderDetail.getReturnsGoodsNum()==null?new BigDecimal(0):purchaseOrderDetail.getReturnsGoodsNum();
			oldDSum = purchaseOrderDetail.getReturnsGoodsSum()==null?new BigDecimal(0):purchaseOrderDetail.getReturnsGoodsSum();
			purchaseOrderDetail.setReturnsGoodsNum(oldDNum.add(returnsOrderDetail.getGoodsNum()));
			purchaseOrderDetail.setReturnsGoodsSum(oldDSum.add(returnsOrderDetail.getGoodsSum()));
			purchaseOrderDetails.add(purchaseOrderDetail);
			//purchaseOrderDetailDao.update(purchaseOrderDetail);
			
			//更新订单计划明细
			PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailDao.findByCode(purchaseOrderDetail.getPurchaseOrderPlanDetailCode());
			oldDNum = purchaseOrderPlanDetail.getReturnsGoodsNum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getReturnsGoodsNum();
			oldDSum = purchaseOrderPlanDetail.getReturnsGoodsSum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getReturnsGoodsSum();
			purchaseOrderPlanDetail.setReturnsGoodsNum(oldDNum.add(returnsOrderDetail.getGoodsNum()));
			purchaseOrderPlanDetail.setReturnsGoodsSum(oldDSum.add(returnsOrderDetail.getGoodsSum()));
			purchaseOrderPlanDetails.add(purchaseOrderPlanDetail);
			//purchaseOrderPlanDetailDao.update(purchaseOrderPlanDetail);
			
			String dCode = deliveryOrderDetail.getDeliveryOrder().getCode();//配送单编号
			if(numMap.get(dCode) == null){
				numMap.put(dCode, returnsOrderDetail.getGoodsNum());
			}else{
				numMap.put(dCode, returnsOrderDetail.getGoodsNum().add(numMap.get(dCode)));
			}
			if(sumMap.get(dCode) == null){
				sumMap.put(dCode, returnsOrderDetail.getGoodsSum());
			}else{
				BigDecimal bigDecimal = sumMap.get(dCode).add(returnsOrderDetail.getGoodsSum());
				sumMap.put(dCode, bigDecimal);
			}
			
			//更新合同退货量
			ContractDetail cd = cdMap.get(returnsOrderDetail.getContractDetailCode());
			if(cd != null){
				BigDecimal a = cd.getReturnsNum()==null?new BigDecimal("0"):cd.getReturnsNum();
				cd.setReturnsNum(a.add(returnsOrderDetail.getGoodsNum()));
				//contractDetailService.update(cd);
				BigDecimal b = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
				cd.setPurchasePlanNum(b.subtract(returnsOrderDetail.getGoodsNum()));
				contractDetails.add(cd);
			}
			
		}
		returnsOrderDetailDao.saveBatch(returnsOrderDetails);
		
		deliveryOrderDetailDao.updateBatch(deliveryOrderDetails);
		purchaseOrderDetailDao.updateBatch(purchaseOrderDetails);
		purchaseOrderPlanDetailDao.updateBatch(purchaseOrderPlanDetails);
		contractDetailDao.updateBatch(contractDetails);
		
		List<DeliveryOrder> deliveryOrders = new ArrayList<>();
		List<PurchaseOrder> purchaseOrders = new ArrayList<>();
		List<PurchaseOrderPlan> purchaseOrderPlans = new ArrayList<>();
		for(String key : numMap.keySet()){
			//更新配送单主档
			DeliveryOrder deliveryOrder = deliveryOrderDao.findByCode(key);
			BigDecimal oldNum = deliveryOrder.getReturnsNum()==null?new BigDecimal(0):deliveryOrder.getReturnsNum();
			BigDecimal oldSum = deliveryOrder.getReturnsSum()==null?new BigDecimal(0):deliveryOrder.getReturnsSum();
			deliveryOrder.setReturnsNum(oldNum.add(numMap.get(key)));
			deliveryOrder.setReturnsSum(oldSum.add(sumMap.get(key)));
			deliveryOrders.add(deliveryOrder);
			//deliveryOrderDao.update(deliveryOrder);
			
			//更新订单主档
			PurchaseOrder purchaseOrder = purchaseOrderDao.findByCode(deliveryOrder.getPurchaseOrderCode());
			oldNum = purchaseOrder.getReturnsNum()==null?new BigDecimal(0):purchaseOrder.getReturnsNum();
			oldSum = purchaseOrder.getReturnsSum()==null?new BigDecimal(0):purchaseOrder.getReturnsSum();
			purchaseOrder.setReturnsNum(oldNum.add(numMap.get(key)));
			purchaseOrder.setReturnsSum(oldSum.add(sumMap.get(key)));
			purchaseOrders.add(purchaseOrder);
			//purchaseOrderDao.update(purchaseOrder);
			
			//更新订单计划主档
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanDao.findByCode(purchaseOrder.getPurchaseOrderPlanCode());
			oldNum = purchaseOrderPlan.getReturnsNum()==null?new BigDecimal(0):purchaseOrderPlan.getReturnsNum();
			oldSum = purchaseOrderPlan.getReturnsSum()==null?new BigDecimal(0):purchaseOrderPlan.getReturnsSum();
			purchaseOrderPlan.setReturnsNum(oldNum.add(numMap.get(key)));
			purchaseOrderPlan.setReturnsSum(oldSum.add(sumMap.get(key)));
			purchaseOrderPlans.add(purchaseOrderPlan);
			//purchaseOrderPlanDao.update(purchaseOrderPlan);
		}
		deliveryOrderDao.updateBatch(deliveryOrders);
		purchaseOrderDao.updateBatch(purchaseOrders);
		purchaseOrderPlanDao.updateBatch(purchaseOrderPlans);
	}

	@Override
	@Transactional(readOnly=true)
	public ReturnsOrder findByCode(String projectCode, String code) {
		return returnsOrderDao.findByCode(code);
	}

	@Override
	@Transactional
	public ReturnsOrder getByInternalCode(String projectCode, String vendorCode, String internalCode, boolean isGPO) {
		return returnsOrderDao.getByInternalCode(vendorCode, internalCode, isGPO);
	}

	@Override
	@Transactional
	public String mkreturn(String projectCode, Long orderId, Long[] detailIds, BigDecimal[] nums,String[] reasons,User user) {
		//订单主档 p
		DeliveryOrder p = deliveryOrderDao.getById(orderId);
		
		ReturnsOrder returnsOrder = new ReturnsOrder();
		returnsOrder.setCode(sndao.getCode(OrderType.returns));//退货单号
		returnsOrder.setInternalCode("");
		returnsOrder.setGpoCode(p.getGpoCode());
		returnsOrder.setGpoName(p.getGpoName());
		returnsOrder.setVendorCode(p.getVendorCode());//供应商id
		returnsOrder.setVendorName(p.getVendorName());//供应商名称
		returnsOrder.setHospitalCode(p.getHospitalCode());//医疗机构id
		returnsOrder.setHospitalName(p.getHospitalName());//医疗机构名称
		returnsOrder.setWarehouseCode(p.getWarehouseCode());
		returnsOrder.setWarehouseName(p.getWarehouseName());
		returnsOrder.setReturnsMan(user.getName());
		returnsOrder.setReturnsBeginDate(new Date());
		returnsOrder.setOrderDate(new Date());//退货时间
		returnsOrder.setIsPass(0);
		
		
		BigDecimal returnsNum = new BigDecimal(0); 
		BigDecimal returnsSum = new BigDecimal(0); 
		//订单明细
		for (int i = 0; i < detailIds.length; i++) {
			DeliveryOrderDetail pd = deliveryOrderDetailDao.getById(detailIds[i]);
		
			ReturnsOrderDetail returnsOrderDetail = new ReturnsOrderDetail();
			String detail_code = returnsOrder.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			returnsOrderDetail.setReturnsOrder(returnsOrder);//退货单
			returnsOrderDetail.setOrderDate(returnsOrder.getOrderDate());
			returnsOrderDetail.setCode(detail_code);
			returnsOrderDetail.setInternalCode("");
			returnsOrderDetail.setBatchCode(pd.getBatchCode());//生产批号
			returnsOrderDetail.setBatchDate(pd.getBatchDate());//生产日期
			returnsOrderDetail.setExpiryDate(pd.getExpiryDate());//有效日期
			returnsOrderDetail.setProductCode(pd.getProductCode());
			returnsOrderDetail.setProductName(pd.getProductName());
			returnsOrderDetail.setProducerName(pd.getProducerName());
			returnsOrderDetail.setDosageFormName(pd.getDosageFormName());
			returnsOrderDetail.setModel(pd.getModel());
			returnsOrderDetail.setPackDesc(pd.getPackDesc());
			returnsOrderDetail.setUnit(pd.getUnit());
			returnsOrderDetail.setPrice(pd.getPrice());
			returnsOrderDetail.setGoodsNum(nums[i]);//数量
			returnsOrderDetail.setGoodsSum(returnsOrderDetail.getPrice().multiply(nums[i]));//金额
			returnsOrderDetail.setReason(reasons[i]);//退货原因
			returnsOrderDetail.setDeliveryOrderDetailCode(pd.getCode());
			returnsOrderDetail.setIsPass(0);
			returnsOrderDetail.setContractDetailCode(pd.getContractDetailCode());
			
			
			returnsOrder.getReturnsOrderDetails().add(returnsOrderDetail);
			returnsNum = returnsNum.add(returnsOrderDetail.getGoodsNum());
			returnsSum = returnsSum.add(returnsOrderDetail.getGoodsSum());
		}
		returnsOrder.setNum(returnsNum);
		returnsOrder.setSum(returnsSum);
		returnsOrder.setDatagramId(null);
		this.saveReturnsOrder(projectCode, returnsOrder);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", returnsOrder.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	@Transactional
	public String mkreturn(String projectCode, Long returnrequestId,String explain, String data) {
		//订单主档 p
		ReturnsRequest r = returnsRequestDao.getById(returnrequestId);
		r.setReply(explain);
		r.setStatus(Status.agree);
		returnsRequestDao.update(r);
		List<JSONObject> list = JSON.parseArray(data, JSONObject.class);
		List<ReturnsRequestDetail> rrdlist = new ArrayList<>();
		for(JSONObject jo:list){
			Long id = jo.getLong("id");
			String reply = jo.getString("reply");
			BigDecimal replyNum = jo.getBigDecimal("replyNum");
			ReturnsRequestDetail detail = returnsRequestDetailDao.getById(id);
			detail.setReply(reply);
			detail.setReplyNum(replyNum);
			returnsRequestDetailDao.update(detail);
			rrdlist.add(detail);
		}
		
		ReturnsOrder returnsOrder = new ReturnsOrder();
		returnsOrder.setCode(sndao.getCode(OrderType.returns));//退货单号
		returnsOrder.setInternalCode("");
		returnsOrder.setGpoCode(r.getGpoCode());
		returnsOrder.setGpoName(r.getGpoName());
		returnsOrder.setVendorCode(r.getVendorCode());//供应商id
		returnsOrder.setVendorName(r.getVendorName());//供应商名称
		returnsOrder.setHospitalCode(r.getHospitalCode());//医疗机构id
		returnsOrder.setHospitalName(r.getHospitalName());//医疗机构名称
		returnsOrder.setWarehouseCode(r.getWarehouseCode());
		returnsOrder.setWarehouseName(r.getWarehouseName());
		returnsOrder.setReturnsMan(r.getReturnsMan());
		returnsOrder.setReturnsBeginDate(new Date());
		returnsOrder.setOrderDate(new Date());//退货时间
		returnsOrder.setIsPass(0);
		//returnsOrder.setDeliveryOrderCode(r.getDeliveryOrderCode());
		returnsOrder.setReturnsRequestCode(r.getCode());
		
		
		int i = 0;
		BigDecimal sum = new BigDecimal(0);
		BigDecimal num = new BigDecimal(0);
		for (ReturnsRequestDetail rr : rrdlist) {
			ReturnsOrderDetail returnsOrderDetail = new ReturnsOrderDetail();
			String detail_code = returnsOrder.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			returnsOrderDetail.setReturnsOrder(returnsOrder);//退货单
			returnsOrderDetail.setOrderDate(returnsOrder.getOrderDate());
			returnsOrderDetail.setCode(detail_code);
			returnsOrderDetail.setInternalCode("");
			returnsOrderDetail.setBatchCode(rr.getBatchCode());//生产批号
			returnsOrderDetail.setBatchDate(rr.getBatchDate());//生产日期
			returnsOrderDetail.setExpiryDate(rr.getExpiryDate());//有效日期
			returnsOrderDetail.setProductCode(rr.getProductCode());
			returnsOrderDetail.setProductName(rr.getProductName());
			returnsOrderDetail.setProducerName(rr.getProducerName());
			returnsOrderDetail.setDosageFormName(rr.getDosageFormName());
			returnsOrderDetail.setModel(rr.getModel());
			returnsOrderDetail.setPackDesc(rr.getPackDesc());
			returnsOrderDetail.setUnit(rr.getUnit());
			returnsOrderDetail.setPrice(rr.getPrice());
			returnsOrderDetail.setGoodsNum(rr.getReplyNum());//数量
			returnsOrderDetail.setGoodsSum(rr.getPrice().multiply(rr.getReplyNum()));//金额
			returnsOrderDetail.setReason(rr.getReason());//退货原因
			returnsOrderDetail.setDeliveryOrderDetailCode(rr.getDeliveryOrderDetailCode());
			returnsOrderDetail.setReturnsRequestDetailCode(rr.getCode());
			DeliveryOrderDetail dd = deliveryOrderDetailDao.findByCode(rr.getDeliveryOrderDetailCode());
			returnsOrderDetail.setIsPass(0);
			returnsOrderDetail.setContractDetailCode(dd.getContractDetailCode());
			
			returnsOrder.getReturnsOrderDetails().add(returnsOrderDetail);
			sum = sum.add(returnsOrderDetail.getGoodsSum());
			num = num.add(returnsOrderDetail.getGoodsNum());
			i++;
		}
		returnsOrder.setNum(num);
		returnsOrder.setSum(sum);
		returnsOrder.setDatagramId(null);
		this.saveReturnsOrder(projectCode, returnsOrder);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", returnsOrder.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	public ReturnsOrder findByRequestCode(String projectCode, String requestCode) {
		return returnsOrderDao.findByRequestCode(requestCode);
	}

	@Override
	public JSONArray saveReturnsOrder(String projectCode, ReturnsOrder returnsOrder, List<JSONObject> arr) {
		BigDecimal returnsNum = new BigDecimal(0); 
		BigDecimal returnsSum = new BigDecimal(0); 
		int i = 0;
		JSONArray res_arr = new JSONArray();
		for(JSONObject jo:arr){
			i++;
			int sxh = jo.getIntValue("sxh"); 	//顺序号
			String batchCode = jo.getString("scph");//生产批号
			String batchDate = jo.getString("scrq");//生产日期
			String expiryDate = jo.getString("yxrq");//有效日期
			BigDecimal goodsNum = jo.getBigDecimal("thsl");//退货数量
			BigDecimal returnsPrice = jo.getBigDecimal("thdj");//退货单价
			String reason = jo.getString("thyy");//退货原因
			String psdmxbh = jo.getString("psdmxbh");//订单明细编号
			String yqthdmxbh = jo.getString("yqthdmxbh");//药企退货单明细编号
			String thsqdmxbh = jo.getString("thsqdmxbh");//退货申请单明细编号
			
			DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.findByCode(psdmxbh);
		
			ReturnsOrderDetail returnsOrderDetail = new ReturnsOrderDetail();
			String detail_code = returnsOrder.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			returnsOrderDetail.setReturnsOrder(returnsOrder);//退货单
			returnsOrderDetail.setOrderDate(returnsOrder.getOrderDate());
			returnsOrderDetail.setCode(detail_code);
			returnsOrderDetail.setInternalCode(yqthdmxbh);
			returnsOrderDetail.setProductCode(deliveryOrderDetail.getProductCode());
			returnsOrderDetail.setProductName(deliveryOrderDetail.getProductName());
			returnsOrderDetail.setProducerName(deliveryOrderDetail.getProducerName());
			returnsOrderDetail.setDosageFormName(deliveryOrderDetail.getDosageFormName());
			returnsOrderDetail.setModel(deliveryOrderDetail.getModel());
			returnsOrderDetail.setPackDesc(deliveryOrderDetail.getPackDesc());
			returnsOrderDetail.setUnit(deliveryOrderDetail.getUnit());
			returnsOrderDetail.setPrice(returnsPrice);
			returnsOrderDetail.setBatchCode(batchCode);//生产批号
			returnsOrderDetail.setBatchDate(batchDate);//生产日期
			returnsOrderDetail.setExpiryDate(expiryDate);//有效日期
			returnsOrderDetail.setGoodsNum(goodsNum);//数量
			returnsOrderDetail.setGoodsSum(returnsPrice.multiply(goodsNum));//金额
			returnsOrderDetail.setReason(reason);//退货原因
			returnsOrderDetail.setDeliveryOrderDetailCode(psdmxbh);
			returnsOrderDetail.setIsPass(0);
			returnsOrderDetail.setReturnsRequestDetailCode(thsqdmxbh);
			returnsOrderDetail.setContractDetailCode(deliveryOrderDetail.getContractDetailCode());
			
			returnsOrder.getReturnsOrderDetails().add(returnsOrderDetail);
			returnsNum = returnsNum.add(returnsOrderDetail.getGoodsNum());
			returnsSum = returnsSum.add(returnsOrderDetail.getGoodsSum());
			
			JSONObject res_arr_jo = new JSONObject();
			res_arr_jo.put("sxh", sxh);
			res_arr_jo.put("thdmxbh", detail_code);
			res_arr_jo.put("yqthdmxbh", yqthdmxbh);
			res_arr.add(res_arr_jo);
		}
		returnsOrder.setNum(returnsNum);
		returnsOrder.setSum(returnsSum);
		this.saveReturnsOrder(projectCode, returnsOrder);
	
		return res_arr;
	}
	
	@Override
	public List<ReturnsOrder> listByIsPass(String projectCode, int isPass){
		return returnsOrderDao.listByIsPass(isPass);
	}

	@Override
	public DataGrid<ReturnsOrder> listByReturnsOrderAndDetail(String projectCode, PageRequest pageable) {
		return returnsOrderDao.listByReturnsOrderAndDetail(pageable);
	}
}
