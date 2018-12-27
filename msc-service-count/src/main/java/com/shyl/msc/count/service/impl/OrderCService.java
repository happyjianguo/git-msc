package com.shyl.msc.count.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.count.dao.IOrderCDao;
import com.shyl.msc.count.entity.HospitalC;
import com.shyl.msc.count.entity.OrderC;
import com.shyl.msc.count.entity.VendorC;
import com.shyl.msc.count.service.IHospitalCService;
import com.shyl.msc.count.service.IOrderCService;
import com.shyl.msc.count.service.IVendorCService;


@Service
@Transactional(readOnly=true)
public class OrderCService extends BaseService<OrderC, Long> implements IOrderCService {
	@Resource
	private IContractService contractService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IInOutBoundService inOutBoundService;
	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IHospitalCService hospitalCService;
	@Resource
	private IVendorCService vendorCService;
	private Map<String, Map<String, OrderC>> data = new HashMap<String, Map<String, OrderC>>();
	
	private IOrderCDao orderCDao;

	public IOrderCDao getOrderCDao() {
		return orderCDao;
	}

	@Resource
	public void setOrderCDao(IOrderCDao orderCDao) {
		this.orderCDao = orderCDao;
		super.setBaseDao(orderCDao);
	}
	
	@Override
	@Transactional
	public void pass(String projectCode) {
		System.out.println("过订单账务-----------------start");
		
		//设置合同总数、合同金额
		List<Contract> contracts = contractService.listByIsPass(projectCode, 0);
        for(Contract contract : contracts){
        	String month = DateUtil.getToday10(contract.getCreateDate()).substring(0,7);
			
			OrderC orderC = getOrderC(projectCode, month, contract.getHospitalCode(), contract.getVendorName(), contract.getVendorCode(), contract.getHospitalName());   
			orderC.setContractCount(orderC.getContractCount()+1);
        	orderC.setContractSum(orderC.getContractSum().add(contract.getAmt()));
        	//super.update(projectCode, orderC);
        	
        	HospitalC hospitalC = hospitalCService.getHospitalC(projectCode, month, contract.getHospitalCode(), contract.getHospitalName());
			hospitalC.setContractCount(hospitalC.getContractCount()+1);
			hospitalC.setContractSum(hospitalC.getContractSum().add(contract.getAmt()));
			//hospitalCService.update(projectCode, hospitalC);
			
			VendorC vendorC = vendorCService.getVendorC(projectCode, month, contract.getVendorCode(), contract.getVendorName());        
			vendorC.setContractCount(vendorC.getContractCount()+1);
			vendorC.setContractSum(vendorC.getContractSum().add(contract.getAmt()));
			//vendorCService.update(projectCode, vendorC);
			
        	contract.setIsPass(1);
        	contractService.update(projectCode, contract);
        }
        
        //设置订单计划总数
        List<PurchaseOrderPlan> purchaseOrderPlans = purchaseOrderPlanService.listByIsPass(projectCode, 0);
        for(PurchaseOrderPlan purchaseOrderPlan : purchaseOrderPlans){      
        	
        	this.passOrderC(projectCode, purchaseOrderPlan, "purchaseOrderPlan");
        	this.passHospitalC(projectCode, purchaseOrderPlan, "purchaseOrderPlan");
        	this.passVendorC(projectCode, purchaseOrderPlan, "purchaseOrderPlan");
        	
        	purchaseOrderPlan.setIsPass(1);
        	purchaseOrderPlanService.update(projectCode, purchaseOrderPlan);
        }
        
        //设置审核次数、审核时间
        List<PurchaseOrderPlan> purchaseOrderPlanAudits = purchaseOrderPlanService.listByIsPassAudit(projectCode, 0);
        for(PurchaseOrderPlan purchaseOrderPlan : purchaseOrderPlanAudits){      
        	
        	//借用修改日期代替审核日期
        	purchaseOrderPlan.setModifyDate(purchaseOrderPlan.getAuditDate());
        	
        	this.passOrderC(projectCode, purchaseOrderPlan, "purchaseOrderPlanAudit");
        	this.passHospitalC(projectCode, purchaseOrderPlan, "purchaseOrderPlanAudit");
        	this.passVendorC(projectCode, purchaseOrderPlan, "purchaseOrderPlanAudit");
        	
        	purchaseOrderPlan.setIsPassAudit(1);
        	purchaseOrderPlanService.update(projectCode, purchaseOrderPlan);
        }
        
        //设置交易次数、采购数量、采购金额
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.listByIsPass(projectCode, 0);
        for(PurchaseOrder purchaseOrder : purchaseOrders){
        	        	
        	this.passOrderC(projectCode, purchaseOrder, "purchaseOrder");
        	this.passHospitalC(projectCode, purchaseOrder, "purchaseOrder");
        	this.passVendorC(projectCode, purchaseOrder, "purchaseOrder");
        	
        	purchaseOrder.setIsPass(1);
        	purchaseOrderService.update(projectCode, purchaseOrder);
        }
        
        //设置及时配送次数
        List<PurchaseOrder> purchaseOrderDeliverys = purchaseOrderService.listByIsPassDelivery(projectCode, 0);
        for(PurchaseOrder purchaseOrder : purchaseOrderDeliverys){      	
        	boolean flag = false;
			if(purchaseOrder.getRequireDate() != null){
				long subtractValue = purchaseOrder.getFirstDeliveryDate().getTime() - purchaseOrder.getRequireDate().getTime();
				flag = subtractValue>=0?true:false;
			}else{
				flag = true;
			}		
	    	if(flag){
	        	this.passOrderC(projectCode, purchaseOrder, "purchaseOrderDelivery");
	        	this.passHospitalC(projectCode, purchaseOrder, "purchaseOrderDelivery");
	        	this.passVendorC(projectCode, purchaseOrder, "purchaseOrderDelivery");
	    	}
	    	
        	purchaseOrder.setIsPassDelivery(1);
        	purchaseOrderService.update(projectCode, purchaseOrder);
        }
        
        //设置配送次数、配送时间、配送数量、配送金额
        List<DeliveryOrder> deliveryOrders = deliveryOrderService.listByIsPass(projectCode, 0);
        for(DeliveryOrder deliveryOrder : deliveryOrders){
        	PurchaseOrder purchaseOrder = purchaseOrderService.findByCode(projectCode, deliveryOrder.getPurchaseOrderCode());
        	long deliveryHour = (deliveryOrder.getCreateDate().getTime() - purchaseOrder.getCreateDate().getTime())/(60*60*1000);
        	//借用临时栏位，设置配送时间
        	deliveryOrder.setSegmentLong(deliveryHour);
        	
        	this.passOrderC(projectCode, deliveryOrder, "deliveryOrder");
        	this.passHospitalC(projectCode, deliveryOrder, "deliveryOrder");
        	this.passVendorC(projectCode, deliveryOrder, "deliveryOrder");
        	
        	deliveryOrder.setIsPass(1);
        	deliveryOrderService.update(projectCode, deliveryOrder);
        }
        
        //设置入库数量、入库金额
        List<InOutBound> inOutBounds = inOutBoundService.listByIsPass(projectCode, 0);
        for(InOutBound inOutBound : inOutBounds){
               	
        	this.passOrderC(projectCode, inOutBound, "inOutBound");
        	this.passHospitalC(projectCode, inOutBound, "inOutBound");
        	this.passVendorC(projectCode, inOutBound, "inOutBound");
        	
        	inOutBound.setIsPass(1);
        	inOutBoundService.update(projectCode, inOutBound);
        }
        
        //设置退货数量、退货金额
        List<ReturnsOrder> returnsOrders = returnsOrderService.listByIsPass(projectCode, 0);
        for(ReturnsOrder returnsOrder : returnsOrders){
         	
        	this.passOrderC(projectCode, returnsOrder, "returnsOrder");
        	this.passHospitalC(projectCode, returnsOrder, "returnsOrder");
        	this.passVendorC(projectCode, returnsOrder, "returnsOrder");
        	
        	returnsOrder.setIsPass(1);
        	returnsOrderService.update(projectCode, returnsOrder);
        }
    
        //批量修改
        this.updateBatch(projectCode);
        hospitalCService.updateBatch(projectCode);
        vendorCService.updateBatch(projectCode);
        
		System.out.println("过订单账务-----------------end");
	}
	
	@Override
	public void passOrderC(String projectCode, Order order, String type){
		String month = DateUtil.dateToStr(order.getOrderDate()).substring(0, 7);
		OrderC orderC = getOrderC(projectCode, month, order.getHospitalCode(), order.getHospitalName(), order.getVendorCode(), order.getVendorName());
		
		if(type.equals("purchaseOrderPlan")){
			orderC.setOrderPlanCount(orderC.getOrderPlanCount() + 1);
		}else if(type.equals("purchaseOrderPlanAudit")){
			//借用修改时间栏位，实际为审核时间
			long reviewHour = (order.getModifyDate().getTime() - order.getCreateDate().getTime())/(60*60*1000);
			orderC.setReviewHour(orderC.getReviewHour().add(new BigDecimal(reviewHour)));
			orderC.setReviewTimes(orderC.getReviewTimes() + 1);			
		}else if(type.equals("purchaseOrder")){
			orderC.setPurchaseTimes(orderC.getPurchaseTimes() + 1);
			orderC.setPurchaseNum(orderC.getPurchaseNum().add(order.getNum()));
			orderC.setPurchaseSum(orderC.getPurchaseSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDelivery")){
    		orderC.setDeliveryTimelyTimes(orderC.getDeliveryTimelyTimes() + 1);
		}else if(type.equals("deliveryOrder")){
			orderC.setDeliveryHour(orderC.getDeliveryHour().add(new BigDecimal(order.getSegmentLong())));
			orderC.setDeliveryTimes(orderC.getDeliveryTimes() + 1);
			orderC.setDeliveryNum(orderC.getDeliveryNum().add(order.getNum()));
			orderC.setDeliverySum(orderC.getDeliverySum().add(order.getSum()));
		}else if(type.equals("deliveryOrderDetail")){
			orderC.setValidityDayCount(orderC.getValidityDayCount() + order.getSegmentLong().intValue());
		}else if(type.equals("inOutBound")){
			orderC.setInOutBoundNum(orderC.getInOutBoundNum().add(order.getNum()));
			orderC.setInOutBoundSum(orderC.getInOutBoundSum().add(order.getSum()));
		}else if(type.equals("returnsOrder")){
			orderC.setReturnsNum(orderC.getReturnsNum().add(order.getNum()));
			orderC.setReturnsSum(orderC.getReturnsSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderPlanDetail")){
			orderC.setShortSupplyTimes(orderC.getShortSupplyTimes() + 1);
			orderC.setShortSupplySum(orderC.getShortSupplySum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDetail")){
			if(order.getSegmentStr() != null && !order.getSegmentStr().equals("")){
				orderC.setContractPurchaseSpecCount(orderC.getContractPurchaseSpecCount()+1);
				orderC.setContractPurchaseSum(orderC.getContractPurchaseSum().add(order.getSum()));		
			}
			orderC.setPurchaseSpecCount(orderC.getPurchaseSpecCount()+1);		
		}
		
		//super.update(projectCode, orderC);
	}
	
	@Override
	public void passHospitalC(String projectCode, Order order, String type){
		String month = DateUtil.dateToStr(order.getOrderDate()).substring(0, 7);
		HospitalC hospitalC = hospitalCService.getHospitalC(projectCode, month, order.getHospitalCode(), order.getHospitalName());
		
		if(type.equals("purchaseOrderPlan")){
			hospitalC.setOrderPlanCount(hospitalC.getOrderPlanCount() + 1);
		}else if(type.equals("purchaseOrderPlanAudit")){
			//借用修改时间栏位，实际为审核时间
			long reviewHour = (order.getModifyDate().getTime() - order.getCreateDate().getTime())/(60*60*1000);
			hospitalC.setReviewHour(hospitalC.getReviewHour().add(new BigDecimal(reviewHour)));
			hospitalC.setReviewTimes(hospitalC.getReviewTimes() + 1);			
		}else if(type.equals("purchaseOrder")){
			hospitalC.setPurchaseTimes(hospitalC.getPurchaseTimes() + 1);
			hospitalC.setPurchaseNum(hospitalC.getPurchaseNum().add(order.getNum()));
			hospitalC.setPurchaseSum(hospitalC.getPurchaseSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDelivery")){
    		hospitalC.setDeliveryTimelyTimes(hospitalC.getDeliveryTimelyTimes() + 1);
		}else if(type.equals("deliveryOrder")){
			hospitalC.setDeliveryHour(hospitalC.getDeliveryHour().add(new BigDecimal(order.getSegmentLong())));
			hospitalC.setDeliveryTimes(hospitalC.getDeliveryTimes() + 1);
			hospitalC.setDeliveryNum(hospitalC.getDeliveryNum().add(order.getNum()));
			hospitalC.setDeliverySum(hospitalC.getDeliverySum().add(order.getSum()));
		}else if(type.equals("deliveryOrderDetail")){
			hospitalC.setValidityDayCount(hospitalC.getValidityDayCount() + order.getSegmentLong().intValue());
		}else if(type.equals("inOutBound")){
			hospitalC.setInOutBoundNum(hospitalC.getInOutBoundNum().add(order.getNum()));
			hospitalC.setInOutBoundSum(hospitalC.getInOutBoundSum().add(order.getSum()));
		}else if(type.equals("returnsOrder")){
			hospitalC.setReturnsNum(hospitalC.getReturnsNum().add(order.getNum()));
			hospitalC.setReturnsSum(hospitalC.getReturnsSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderPlanDetail")){
			hospitalC.setShortSupplyTimes(hospitalC.getShortSupplyTimes() + 1);
			hospitalC.setShortSupplySum(hospitalC.getShortSupplySum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDetail")){
			if(order.getSegmentStr() != null){
				hospitalC.setContractPurchaseSpecCount(hospitalC.getContractPurchaseSpecCount()+1);
				hospitalC.setContractPurchaseSum(hospitalC.getContractPurchaseSum().add(order.getSum()));		
			}
			hospitalC.setPurchaseSpecCount(hospitalC.getPurchaseSpecCount()+1);		
		}
		
		//hospitalCService.update(projectCode, hospitalC);
	}
	
	@Override
	public void passVendorC(String projectCode, Order order, String type){
		String month = DateUtil.dateToStr(order.getOrderDate()).substring(0, 7);
		VendorC vendorC = vendorCService.getVendorC(projectCode, month, order.getVendorCode(), order.getVendorName());
		
		if(type.equals("purchaseOrderPlan")){
			vendorC.setOrderPlanCount(vendorC.getOrderPlanCount() + 1);
		}else if(type.equals("purchaseOrderPlanAudit")){
			//借用修改时间栏位，实际为审核时间
			long reviewHour = (order.getModifyDate().getTime() - order.getCreateDate().getTime())/(60*60*1000);
			vendorC.setReviewHour(vendorC.getReviewHour().add(new BigDecimal(reviewHour)));
			vendorC.setReviewTimes(vendorC.getReviewTimes() + 1);	
		}else if(type.equals("purchaseOrder")){
			vendorC.setPurchaseTimes(vendorC.getPurchaseTimes() + 1);
			vendorC.setPurchaseNum(vendorC.getPurchaseNum().add(order.getNum()));
			vendorC.setPurchaseSum(vendorC.getPurchaseSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDelivery")){
    		vendorC.setDeliveryTimelyTimes(vendorC.getDeliveryTimelyTimes() + 1);
		}else if(type.equals("deliveryOrder")){
			vendorC.setDeliveryHour(vendorC.getDeliveryHour().add(new BigDecimal(order.getSegmentLong())));
			vendorC.setDeliveryTimes(vendorC.getDeliveryTimes() + 1);
			vendorC.setDeliveryNum(vendorC.getDeliveryNum().add(order.getNum()));
			vendorC.setDeliverySum(vendorC.getDeliverySum().add(order.getSum()));
		}else if(type.equals("deliveryOrderDetail")){
			vendorC.setValidityDayCount(vendorC.getValidityDayCount() + order.getSegmentLong().intValue());
		}else if(type.equals("inOutBound")){
			vendorC.setInOutBoundNum(vendorC.getInOutBoundNum().add(order.getNum()));
			vendorC.setInOutBoundSum(vendorC.getInOutBoundSum().add(order.getSum()));
		}else if(type.equals("returnsOrder")){
			vendorC.setReturnsNum(vendorC.getReturnsNum().add(order.getNum()));
			vendorC.setReturnsSum(vendorC.getReturnsSum().add(order.getSum()));
		}else if(type.equals("purchaseOrderPlanDetail")){
			vendorC.setShortSupplyTimes(vendorC.getShortSupplyTimes() + 1);
			vendorC.setShortSupplySum(vendorC.getShortSupplySum().add(order.getSum()));
		}else if(type.equals("purchaseOrderDetail")){
			if(order.getSegmentStr() != null){
				vendorC.setContractPurchaseSpecCount(vendorC.getContractPurchaseSpecCount()+1);
				vendorC.setContractPurchaseSum(vendorC.getContractPurchaseSum().add(order.getSum()));		
			}
			vendorC.setPurchaseSpecCount(vendorC.getPurchaseSpecCount()+1);		
		}
		
		//vendorCService.update(projectCode, vendorC);
	}
	
	@Override
	public OrderC getOrderC(String projectCode, String month, String hospitalCode, String hospitalName, String vendorCode, String vendorName){
		Map<String, OrderC> orderCs = data.get(projectCode);
		if(orderCs == null){
			orderCs = new HashMap<String, OrderC>();
			data.put(projectCode, orderCs);
		}
		OrderC orderC = orderCs.get(month+hospitalCode+vendorCode);
		if(orderC == null){
			orderC = orderCDao.getByKey(month, hospitalCode, vendorCode);
			if(orderC == null){
	        	orderC = new OrderC();	    
	        	orderC.setMonth(month);
	        	orderC.setVendorCode(vendorCode);
	        	orderC.setVendorName(vendorName);
	        	orderC.setHospitalCode(hospitalCode);
	        	orderC.setHospitalName(hospitalName);
	        	orderC.setContractCount(0);
	        	orderC.setContractSpecCount(0);
	        	orderC.setContractPurchaseSpecCount(0);
	        	orderC.setContractSum(new BigDecimal(0));
	        	orderC.setContractPurchaseSum(new BigDecimal(0));
	        	orderC.setInsuranceDrugSum(new BigDecimal(0));
	        	orderC.setBaseDrugSum(new BigDecimal(0));
	        	orderC.setPrescriptDrugSum(new BigDecimal(0));
	        	orderC.setPurchaseTimes(0);
	        	orderC.setPurchaseSpecCount(0);
	        	orderC.setReviewHour(new BigDecimal(0));
	        	orderC.setReviewTimes(0);
	        	orderC.setDeliveryHour(new BigDecimal(0));
	        	orderC.setDeliveryTimes(0);
	        	orderC.setDeliveryTimelyTimes(0);
	        	orderC.setOrderPlanCount(0);
	        	orderC.setShortSupplyTimes(0);
	        	orderC.setShortSupplySum(new BigDecimal(0));
	        	orderC.setValidityDayCount(0);
	        	orderC.setPurchaseNum(new BigDecimal(0));
				orderC.setPurchaseSum(new BigDecimal(0));
				orderC.setDeliveryNum(new BigDecimal(0));
				orderC.setDeliverySum(new BigDecimal(0));
				orderC.setInOutBoundNum(new BigDecimal(0));
				orderC.setInOutBoundSum(new BigDecimal(0));
				orderC.setReturnsNum(new BigDecimal(0));
				orderC.setReturnsSum(new BigDecimal(0));
	        	orderCDao.save(orderC);
			}
			orderCs.put(month+hospitalCode+vendorCode, orderC);
		}
		
		return orderC;
	}
	
	@Override
	public void updateBatch(String projectCode){
		List<OrderC> list = new ArrayList<OrderC>();
		Map<String, OrderC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		orderCDao.updateBatch(list);
		data.put(projectCode, null);
	}

	@Override
	public List<Map<String, Object>> countByVendor(PageRequest pageable) {
		return orderCDao.countByVendor(pageable);
	}
}
