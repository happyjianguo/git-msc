package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
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
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
/**
 * 配送单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class DeliveryOrderService extends BaseService<DeliveryOrder, Long> implements IDeliveryOrderService {
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;	
	@Resource
	private IPurchasePlanDetailDao purchasePlanDetailDao;
	@Resource
	private IPurchaseOrderPlanDao purchaseOrderPlanDao;
	@Resource
	private ICompanyService  companyService;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private ISnDao sndao;
	
	@Resource
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;
	public IDeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	@Resource
	public void setDeliveryOrderDao(IDeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
		super.setBaseDao(deliveryOrderDao);
	}

	@Override
	@Transactional(readOnly=true)
	public DeliveryOrder findByCode(String projectCode, String code) {
		return deliveryOrderDao.findByCode(code);
	}
	
	@Override
	@Transactional
	public void saveDeliveryOrder(String projectCode, DeliveryOrder deliveryOrder){
		deliveryOrderDao.save(deliveryOrder);
		PurchaseOrder purchaseOrder = null;
		//当采购单不强制关联时，不对采购单进行更新
		AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo(projectCode, "WEBSERVICECHECK", "DELIVERY");
		if(!wsCheck.getField3().equals("0")){	
		    //更新订单主档
		    purchaseOrder = purchaseOrderDao.findByCode(deliveryOrder.getPurchaseOrderCode());
		    BigDecimal oldNum = purchaseOrder.getDeliveryNum()==null?new BigDecimal(0):purchaseOrder.getDeliveryNum();
		    BigDecimal oldSum = purchaseOrder.getDeliverySum()==null?new BigDecimal(0):purchaseOrder.getDeliverySum();
			purchaseOrder.setDeliveryNum(oldNum.add(deliveryOrder.getNum()));
			purchaseOrder.setDeliverySum(oldSum.add(deliveryOrder.getSum()));
			if(purchaseOrder.getFirstDeliveryDate() == null){
				purchaseOrder.setFirstDeliveryDate(deliveryOrder.getOrderDate());
			}
			purchaseOrder.setLastDeliveryDate(deliveryOrder.getOrderDate());
			Map<String, Object> map = deliveryOrderDao.getNumByPurchaseCode(purchaseOrder.getCode());//取该订单下所有配送总量
			BigDecimal deliveryNum = deliveryOrder.getNum();
			if(map.get("NUM") != null){
				deliveryNum = deliveryNum.add(new BigDecimal(map.get("NUM").toString()));
			}
			if(purchaseOrder.getNum().equals(deliveryNum)){//采购数量和配送总量相等时
				purchaseOrder.setStatus(Status.sent);//配送完成
			}else {
				purchaseOrder.setStatus(Status.sending);
			}
			purchaseOrderDao.update(purchaseOrder);
				
			//更新订单计划主档
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanDao.findByCode(purchaseOrder.getPurchaseOrderPlanCode());
			oldNum = purchaseOrderPlan.getDeliveryNum()==null?new BigDecimal(0):purchaseOrderPlan.getDeliveryNum();
			oldSum = purchaseOrderPlan.getDeliverySum()==null?new BigDecimal(0):purchaseOrderPlan.getDeliverySum();
			purchaseOrderPlan.setDeliveryNum(oldNum.add(deliveryOrder.getNum()));
			purchaseOrderPlan.setDeliverySum(oldSum.add(deliveryOrder.getSum()));
			if(purchaseOrderPlan.getFirstDeliveryDate() == null){
				purchaseOrderPlan.setFirstDeliveryDate(deliveryOrder.getOrderDate());
			}
			purchaseOrderPlan.setLastDeliveryDate(deliveryOrder.getOrderDate());
			purchaseOrderPlanDao.update(purchaseOrderPlan);
		}
		
		for (DeliveryOrderDetail deliveryOrderDetail : deliveryOrder.getDeliveryOrderDetails()) {
			deliveryOrderDetail.setDeliveryOrder(deliveryOrder);
			deliveryOrderDetailDao.save(deliveryOrderDetail);
			
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem.getField3().equals("1")){
				orderMsgService.saveOrderMsg(projectCode, deliveryOrderDetail.getPurchaseOrderDetailCode(), deliveryOrderDetail.getCode(), OrderDetailStatus.sent);
			}
			if(!wsCheck.getField3().equals("0")){
				//更新订单明细
				PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.findByCode(deliveryOrderDetail.getPurchaseOrderDetailCode());
				BigDecimal oldPNum = purchaseOrderDetail.getDeliveryGoodsNum()==null?new BigDecimal(0):purchaseOrderDetail.getDeliveryGoodsNum();
				BigDecimal oldPSum = purchaseOrderDetail.getDeliveryGoodsSum()==null?new BigDecimal(0):purchaseOrderDetail.getDeliveryGoodsSum();
				purchaseOrderDetail.setDeliveryGoodsNum(oldPNum.add(deliveryOrderDetail.getGoodsNum()));
				purchaseOrderDetail.setDeliveryGoodsSum(oldPSum.add(deliveryOrderDetail.getGoodsSum()));
				purchaseOrderDetailDao.update(purchaseOrderDetail);
				
				//更新订单计划明细
				PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailDao.findByCode(purchaseOrderDetail.getPurchaseOrderPlanDetailCode());
				oldPNum = purchaseOrderPlanDetail.getDeliveryGoodsNum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getDeliveryGoodsNum();
				oldPSum = purchaseOrderPlanDetail.getDeliveryGoodsSum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getDeliveryGoodsSum();
				purchaseOrderPlanDetail.setDeliveryGoodsNum(oldPNum.add(deliveryOrderDetail.getGoodsNum()));
				purchaseOrderPlanDetail.setDeliveryGoodsSum(oldPSum.add(deliveryOrderDetail.getGoodsSum()));
				purchaseOrderPlanDetailDao.update(purchaseOrderPlanDetail);
				
				//更新配送的明细
				deliveryOrderDetail.setPurchaseOrderPlanDetailCode(purchaseOrderPlanDetail.getCode());
				deliveryOrderDetail.setPurchasePlanDetailCode(purchaseOrderPlanDetail.getPurchasePlanDetailCode());
				deliveryOrderDetailDao.update(deliveryOrderDetail);
				
				//更新合同配送量
				ContractDetail cd = contractDetailService.findByCode(projectCode, deliveryOrderDetail.getContractDetailCode());
				if(cd != null){
					BigDecimal a = cd.getDeliveryNum()==null?new BigDecimal("0"):cd.getDeliveryNum();
					cd.setDeliveryNum(a.add(deliveryOrderDetail.getGoodsNum()));
					//订单自动结案时，更新合同结案量
					if(purchaseOrder.getStatus().equals(Status.sent)){
						BigDecimal b = cd.getClosedNum()==null?new BigDecimal("0"):cd.getClosedNum();
						cd.setClosedNum(b.add(deliveryOrderDetail.getGoodsNum()));
					}
					contractDetailService.update(projectCode, cd);
					
				}
			}
		}

	}

	@Override
	@Transactional
	public DeliveryOrder getByInternalCode(String projectCode, String vendorCode, String internalCode, boolean isGPO) {
		return deliveryOrderDao.getByInternalCode(vendorCode, internalCode, isGPO);
	}
	
	@Override
	@Transactional
	public DeliveryOrder getByBarcode(String projectCode, String barcode) {
		return deliveryOrderDao.getByBarcode(barcode);
	}

	@Override
	@Transactional
	public String mkdelivery(String projectCode, Long orderId, String senderCode,String barcode,Long[] detailIds, BigDecimal[] nums, String[] batchCodes, String[] batchDates, String[] expiryDates, String[] qualityRecords, String[] inspectionReportUrls) {
		//订单主档 p
		PurchaseOrder p = purchaseOrderDao.getById(orderId);
		
		Company c = companyService.findByCode(projectCode,senderCode, "isSender=1");
		//主档新增 d
		DeliveryOrder deliveryOrder = new DeliveryOrder();
		deliveryOrder.setCode(sndao.getCode(OrderType.delivery));
		deliveryOrder.setHospitalCode(p.getHospitalCode());
		deliveryOrder.setHospitalName(p.getHospitalName());
		deliveryOrder.setGpoCode(p.getGpoCode());
		deliveryOrder.setGpoName(p.getGpoName());
		deliveryOrder.setVendorCode(p.getVendorCode());
		deliveryOrder.setVendorName(p.getVendorName());
		deliveryOrder.setWarehouseCode(p.getWarehouseCode());
		deliveryOrder.setWarehouseName(p.getWarehouseName());
		deliveryOrder.setSenderCode(senderCode);
		if(c != null){
			deliveryOrder.setSenderName(c.getFullName());
		}
		deliveryOrder.setReceiveDate(new Date());
		deliveryOrder.setOrderDate(new Date());
		deliveryOrder.setBarcode(barcode);
		deliveryOrder.setPurchaseOrderCode(p.getCode());
		deliveryOrder.setPurchaseOrderPlanCode(p.getPurchaseOrderPlanCode());
		deliveryOrder.setPurchasePlanCode(p.getPurchasePlanCode());
		deliveryOrder.setStatus(DeliveryOrder.Status.unreceive);
		deliveryOrder.setIsPass(0);
		deliveryOrder.setDatagramId(null);
		
		//明细新增
		BigDecimal deliveryNum = new BigDecimal(0); 
		BigDecimal deliverySum = new BigDecimal(0); 
		//订单明细
		for (int i = 0; i < detailIds.length; i++) {
			PurchaseOrderDetail pd = purchaseOrderDetailDao.getById(detailIds[i]);

			DeliveryOrderDetail deliveryOrderDetail = new DeliveryOrderDetail();
			String detail_code = deliveryOrder.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			deliveryOrderDetail.setIsPass(0);
			deliveryOrderDetail.setOrderDate(deliveryOrder.getOrderDate());
			deliveryOrderDetail.setCode(detail_code);//配送单明细编号
			deliveryOrderDetail.setInternalCode("");
			deliveryOrderDetail.setDeliveryOrder(deliveryOrder);
			deliveryOrderDetail.setBatchCode(batchCodes[i]);
			deliveryOrderDetail.setBatchDate(batchDates[i]);
			deliveryOrderDetail.setQualityRecord(qualityRecords[i]);
			deliveryOrderDetail.setInspectionReportUrl(inspectionReportUrls[i]);
			deliveryOrderDetail.setBarcode("");
			deliveryOrderDetail.setExpiryDate(expiryDates[i]);		
			deliveryOrderDetail.setPurchaseOrderDetailCode(pd.getCode());
			deliveryOrderDetail.setProductCode(pd.getProductCode());
			deliveryOrderDetail.setProductName(pd.getProductName());
			deliveryOrderDetail.setProducerName(pd.getProducerName());
			deliveryOrderDetail.setDosageFormName(pd.getDosageFormName());
			deliveryOrderDetail.setModel(pd.getModel());
			deliveryOrderDetail.setPackDesc(pd.getPackDesc());
			deliveryOrderDetail.setUnit(pd.getUnit());
			deliveryOrderDetail.setPrice(pd.getPrice());
			deliveryOrderDetail.setGoodsNum(nums[i]);//配送数量
			deliveryOrderDetail.setGoodsSum(deliveryOrderDetail.getPrice().multiply(nums[i]));
			deliveryOrderDetail.setContractDetailCode(pd.getContractDetailCode());
			
			deliveryOrder.getDeliveryOrderDetails().add(deliveryOrderDetail);
			
			deliveryNum = deliveryNum.add(deliveryOrderDetail.getGoodsNum());
			deliverySum = deliverySum.add(deliveryOrderDetail.getGoodsSum());
		}		
		deliveryOrder.setNum(deliveryNum);
		deliveryOrder.setSum(deliverySum);
		
		this.saveDeliveryOrder(projectCode, deliveryOrder);

		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", deliveryOrder.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	public DeliveryOrder listByHospitalAndBarcode(String projectCode, String hospitalCode, String barcode) {
		return deliveryOrderDao.listByHospitalAndBarcode(hospitalCode, barcode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryUninvoice(String projectCode, PageRequest pageable, String code) {
		return deliveryOrderDao.queryUninvoice(pageable,code);
	}

	@Override
	public DataGrid<DeliveryOrder> listByDeliveryOrderAndDetail(PageRequest pageable) {
		// TODO Auto-generated method stub
		return deliveryOrderDao.listByDeliveryOrderAndDetail(pageable);
	}
	
	@Override
	public List<DeliveryOrder> listByIsPass(String projectCode, int isPass){
		return deliveryOrderDao.listByIsPass(isPass);
	}

	@Override
	public List<DeliveryOrder> listByDate(String projectCode, String hospitalCode, String startDate, String endDate) {
		return deliveryOrderDao.listByDate(hospitalCode,startDate,endDate);
	}

	@Override
	@Transactional
	public void returnDeliveryBack(String projectCode, DeliveryOrder deliveryOrder,List<DeliveryOrderDetail> deliveryOrderDetails) {
		//配送单明细

		List<PurchaseOrderDetail> pdList = new ArrayList<>();
		List<PurchaseOrderPlanDetail> popList = new ArrayList<>();
		for(DeliveryOrderDetail dd : deliveryOrderDetails){
			//合同明细编号
			String contractDetailCode = dd.getContractDetailCode();
			PageRequest pageRequest = new PageRequest();
			pageRequest.getQuery().put("t#code_S_EQ",contractDetailCode);
			ContractDetail cd = contractDetailService.getByKey(projectCode,pageRequest);
			//采购订单明细编号
			String pdCode = dd.getPurchaseOrderDetailCode();
			PurchaseOrderDetail pd = purchaseOrderDetailDao.findByCode(pdCode);
			//采购订单主表
			PurchaseOrder p = pd.getPurchaseOrder();
			p.setDeliveryNum(p.getDeliveryNum().subtract(dd.getGoodsNum()));
			p.setDeliverySum(p.getDeliverySum().subtract(dd.getGoodsSum()));
			purchaseOrderDao.update(p);
			//更新配送数量
			pd.setDeliveryGoodsNum(pd.getDeliveryGoodsNum().subtract(dd.getGoodsNum()));
			pd.setDeliveryGoodsSum(pd.getDeliveryGoodsSum().subtract(dd.getGoodsSum()));

			pdList.add(pd);

			//采购订单计划明细编号
			String ppd = dd.getPurchaseOrderPlanDetailCode();
			PurchaseOrderPlanDetail pop = purchaseOrderPlanDetailDao.findByCode(ppd);
			PurchaseOrderPlan pp = pop.getPurchaseOrderPlan();
			pp.setDeliveryNum(pp.getDeliveryNum().subtract(dd.getGoodsNum()));
			pp.setDeliverySum(pp.getDeliverySum().subtract(dd.getGoodsSum()));
			purchaseOrderPlanDao.update(pp);

			pop.setDeliveryGoodsNum(pop.getDeliveryGoodsNum().subtract(dd.getGoodsNum()));
			pop.setDeliveryGoodsSum(pop.getDeliveryGoodsSum().subtract(dd.getGoodsSum()));

			popList.add(pop);

			//更新合同
			cd.setDeliveryNum(cd.getDeliveryNum().subtract(dd.getGoodsNum()));
			cd.setDeliveryAmt(cd.getDeliveryAmt().subtract(dd.getGoodsSum()));
			contractDetailService.update(projectCode,cd);
		}
		purchaseOrderDetailDao.updateBatch(pdList);
		purchaseOrderPlanDetailDao.updateBatch(popList);

		//删除配送单
		deliveryOrderDetailDao.deleteBatch(deliveryOrderDetails);
		deliveryOrderDao.delete(deliveryOrder);
	}
}
