package com.shyl.msc.count.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.entity.OrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.count.service.IHospitalCService;
import com.shyl.msc.count.service.IHospitalProductCService;
import com.shyl.msc.count.service.IOrderCService;
import com.shyl.msc.count.service.IOrderDetailCService;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.msc.count.service.IVendorCService;
import com.shyl.msc.count.service.IVendorProductCService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.count.dao.IOrderDetailCDao;
import com.shyl.msc.count.entity.HospitalC;
import com.shyl.msc.count.entity.HospitalProductC;
import com.shyl.msc.count.entity.OrderC;
import com.shyl.msc.count.entity.OrderDetailC;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.entity.VendorC;
import com.shyl.msc.count.entity.VendorProductC;
import com.shyl.msc.enmu.OrderType;

@Service
@Transactional(readOnly=true)
public class OrderDetailCService extends BaseService<OrderDetailC, Long> implements IOrderDetailCService {
	@Resource
	private IProductService productService;
	@Resource
	private IProductCService productCService;
	@Resource
	private IHospitalProductCService hospitalProductCService;
	@Resource
	private IVendorProductCService vendorProductCService;
	@Resource
	private IHospitalCService hospitalCService;
	@Resource
	private IVendorCService vendorCService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Resource
	private IOrderCService orderCService;
	private Map<String, Map<String, OrderDetailC>> data = new HashMap<String, Map<String, OrderDetailC>>();

	private IOrderDetailCDao orderDetailCDao;

	public IOrderDetailCDao getOrderDetailCDao() {
		return orderDetailCDao;
	}

	@Resource
	public void setOrderDetailCDao(IOrderDetailCDao orderDetailCDao) {
		this.orderDetailCDao = orderDetailCDao;
		super.setBaseDao(orderDetailCDao);
	}

	@Override
	@Transactional
	public void pass(String projectCode) {
		System.out.println("过产品账务-----------------start");
		
		//设置合同金额、合同品种数
		List<ContractDetail> contractDetails = contractDetailService.listByIsPass(projectCode, 0);
		for(ContractDetail contractDetail:contractDetails){
			String month = DateUtil.getToday10(contractDetail.getContract().getCreateDate()).substring(0,7);
			Contract contract = contractDetail.getContract();
			
			OrderDetailC orderDetailC = getOrderDetailC(projectCode, month, contractDetail.getProduct(), contract.getHospitalCode(), contract.getHospitalName(), contract.getVendorCode(), contract.getVendorName());
			orderDetailC.setContractSum(orderDetailC.getContractSum().add(contractDetail.getContractAmt()));
			//super.update(projectCode, orderDetailC);
			//合同品种数
			if(orderDetailC.getContractFlag() != 1){	
				OrderC orderC = orderCService.getOrderC(projectCode, month, contract.getHospitalCode(), contract.getHospitalName(), contract.getVendorCode(), contract.getVendorName());
				orderC.setContractSpecCount(orderC.getContractSpecCount()+1);
				//orderCService.update(projectCode, orderC);		
			}			
				
			HospitalProductC hospitalProductC = hospitalProductCService.getHospitalProductC(projectCode, month, contractDetail.getProduct(), contract.getHospitalCode(), contract.getHospitalName());
			hospitalProductC.setContractSum(hospitalProductC.getContractSum().add(contractDetail.getContractAmt()));
			//hospitalProductCService.update(projectCode, hospitalProductC);
			if(hospitalProductC.getContractFlag() != 1){			
				HospitalC hospitalC = hospitalCService.getHospitalC(projectCode, month, contract.getHospitalCode(), contract.getHospitalName());
				hospitalC.setContractSpecCount(hospitalC.getContractSpecCount()+1);
				//hospitalCService.update(projectCode, hospitalC);						
			}			
			
			VendorProductC vendorProductC = vendorProductCService.getVendorProductC(projectCode, month, contractDetail.getProduct(), contract.getVendorCode(), contract.getVendorName());
			vendorProductC.setContractSum(vendorProductC.getContractSum().add(contractDetail.getContractAmt()));
			//vendorProductCService.update(projectCode, vendorProductC);
			if(vendorProductC.getContractFlag() != 1){			
				VendorC vendorC = vendorCService.getVendorC(projectCode, month, contract.getVendorCode(), contract.getVendorName());
				vendorC.setContractSpecCount(vendorC.getContractSpecCount()+1);
				//vendorCService.update(projectCode, vendorC);						
			}			
			
			ProductC productC = productCService.getProductC(projectCode, month, contractDetail.getProduct());
			productC.setContractSum(productC.getContractSum().add(contractDetail.getContractAmt()));
			//productCService.update(projectCode, productC);
			
			contractDetail.setIsPass(1);
			contractDetailService.update(projectCode, contractDetail);
		}
		
		//设置断货次数和金额
		List<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = purchaseOrderPlanDetailService.listByIsPass(projectCode, 0);
		for(PurchaseOrderPlanDetail purchaseOrderPlanDetail : purchaseOrderPlanDetails){
			
			purchaseOrderPlanDetail.setOrderType(OrderType.orderPlan);
			
			//计算断货次数和金额
			if(purchaseOrderPlanDetail.getStatus().equals(PurchaseOrderPlanDetail.Status.cancel)){
				Product product = productService.getByCode(projectCode, purchaseOrderPlanDetail.getProductCode());
				PurchaseOrderPlan order = purchaseOrderPlanDetail.getPurchaseOrderPlan();
				this.passOrderDetailC(projectCode, purchaseOrderPlanDetail, order.getHospitalCode(),order.getHospitalName(),
						order.getVendorCode(),order.getVendorName(), product);
				this.passHospitalProductC(projectCode, purchaseOrderPlanDetail, order.getHospitalCode(), order.getHospitalName(), product);
				this.passVendorProductC(projectCode, purchaseOrderPlanDetail, order.getVendorCode(), order.getVendorName(), product);
				this.passProductC(projectCode, purchaseOrderPlanDetail, product);
				
				//借用栏位
				order.setSum(purchaseOrderPlanDetail.getGoodsSum());
				orderCService.passOrderC(projectCode, order, "purchaseOrderPlanDetail");				
				orderCService.passHospitalC(projectCode, order, "purchaseOrderPlanDetail");																
				orderCService.passVendorC(projectCode, order, "purchaseOrderPlanDetail");													
			}			
			
			purchaseOrderPlanDetail.setIsPass(1);
			purchaseOrderPlanDetailService.update(projectCode, purchaseOrderPlanDetail);
		}
		
		//设置交易次数、合同内采购金额、采购数量、采购金额、合同内采购品种数、采购品种数
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailService.listByIsPass(projectCode, 0);
		for(PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails){
			
			purchaseOrderDetail.setOrderType(OrderType.order);
			
			Product product = productService.getByCode(projectCode, purchaseOrderDetail.getProductCode());
			PurchaseOrder order = purchaseOrderDetail.getPurchaseOrder();
			this.passOrderDetailC(projectCode, purchaseOrderDetail, order.getHospitalCode(),order.getHospitalName(),
					order.getVendorCode(),order.getVendorName(), product);
			this.passHospitalProductC(projectCode, purchaseOrderDetail, order.getHospitalCode(), order.getHospitalName(), product);
			this.passVendorProductC(projectCode, purchaseOrderDetail, order.getVendorCode(), order.getVendorName(), product);
			this.passProductC(projectCode, purchaseOrderDetail, product);
			
			purchaseOrderDetail.setIsPass(1);
			purchaseOrderDetailService.update(projectCode, purchaseOrderDetail);
			
			String month = DateUtil.getToday10(purchaseOrderDetail.getPurchaseOrder().getCreateDate()).substring(0,7);
			//借用栏位，计算合同内采购金额
			order.setSum(purchaseOrderDetail.getGoodsSum());
			//借用栏位，判断是否是合同采购
			order.setSegmentStr(purchaseOrderDetail.getContractDetailCode());
			
			OrderDetailC orderDetailC = getOrderDetailC(projectCode, month, product, order.getHospitalCode(), order.getHospitalName(), order.getVendorCode(), order.getVendorName());
			if(orderDetailC.getOrderFlag() != 1){	
				orderCService.passOrderC(projectCode, order, "purchaseOrderDetail");
			}	
			
			HospitalProductC hospitalProductC = hospitalProductCService.getHospitalProductC(projectCode, month, product, order.getHospitalCode(), order.getHospitalName());
			if(hospitalProductC.getContractFlag() != 1){			
				orderCService.passHospitalC(projectCode, order, "purchaseOrderDetail");						
			}			
			
			VendorProductC vendorProductC = vendorProductCService.getVendorProductC(projectCode, month, product, order.getVendorCode(), order.getVendorName());
			if(vendorProductC.getContractFlag() != 1){			
				orderCService.passVendorC(projectCode, order, "purchaseOrderDetail");									
			}				
		}
		
		//设置有效期总天数、配送次数、配送数量、配送金额
		List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.listByIsPass(projectCode, 0);
		for(DeliveryOrderDetail deliveryOrderDetail : deliveryOrderDetails){
			Date exiryDate = null;
			Date batchDate = null;
			if(deliveryOrderDetail.getExpiryDate() != null){
				exiryDate = DateUtil.strToDate(deliveryOrderDetail.getExpiryDate());
			}			
			if(deliveryOrderDetail.getBatchDate() != null){
				batchDate = DateUtil.strToDate(deliveryOrderDetail.getBatchDate());
			}
			int days = 0;
			if(exiryDate != null && batchDate != null){
				days = (int) ((exiryDate.getTime() - batchDate.getTime()) / (1000*3600*24));
			}
			//借用临时栏位，用于计算药品有效天数
			deliveryOrderDetail.setSegmentStr(days+"");
			
			deliveryOrderDetail.setOrderType(OrderType.delivery);
			
			Product product = productService.getByCode(projectCode, deliveryOrderDetail.getProductCode());
			DeliveryOrder order = deliveryOrderDetail.getDeliveryOrder();
			this.passOrderDetailC(projectCode, deliveryOrderDetail, order.getHospitalCode(),order.getHospitalName(),
					order.getVendorCode(),order.getVendorName(), product);
			this.passHospitalProductC(projectCode, deliveryOrderDetail, order.getHospitalCode(), order.getHospitalName(), product);
			this.passVendorProductC(projectCode, deliveryOrderDetail, order.getVendorCode(), order.getVendorName(), product);
			this.passProductC(projectCode, deliveryOrderDetail, product);
			
			//借用栏位
			order.setSegmentLong(new Long(days));
			orderCService.passOrderC(projectCode, order, "deliveryOrderDetail");				
			orderCService.passHospitalC(projectCode, order, "deliveryOrderDetail");																
			orderCService.passVendorC(projectCode, order, "deliveryOrderDetail");	
			
			deliveryOrderDetail.setIsPass(1);
			deliveryOrderDetailService.update(projectCode, deliveryOrderDetail);
		}
		
		//设置入库数量、入库金额
		List<InOutBoundDetail> inOutBoundDetails = inOutBoundDetailService.listByIsPass(projectCode, 0);
		for(InOutBoundDetail inOutBoundDetail : inOutBoundDetails){

			inOutBoundDetail.setOrderType(OrderType.inoutbound);
			
			Product product = productService.getByCode(projectCode, inOutBoundDetail.getProductCode());
			InOutBound order = inOutBoundDetail.getInOutBound();
			this.passOrderDetailC(projectCode, inOutBoundDetail, order.getHospitalCode(),order.getHospitalName(),
					order.getVendorCode(),order.getVendorName(), product);
			this.passHospitalProductC(projectCode, inOutBoundDetail, order.getHospitalCode(), order.getHospitalName(), product);
			this.passVendorProductC(projectCode, inOutBoundDetail, order.getVendorCode(), order.getVendorName(), product);
			this.passProductC(projectCode, inOutBoundDetail, product);
			
			inOutBoundDetail.setIsPass(1);
			inOutBoundDetailService.update(projectCode, inOutBoundDetail);
		}
		
		//设置退货数量、退货金额
		List<ReturnsOrderDetail> returnsOrderDetails = returnsOrderDetailService.listByIsPass(projectCode, 0);
		for(ReturnsOrderDetail returnsOrderDetail : returnsOrderDetails){

			returnsOrderDetail.setOrderType(OrderType.returns);
			
			Product product = productService.getByCode(projectCode, returnsOrderDetail.getProductCode());
			ReturnsOrder order = returnsOrderDetail.getReturnsOrder();
			this.passOrderDetailC(projectCode, returnsOrderDetail, order.getHospitalCode(),order.getHospitalName(),
					order.getVendorCode(),order.getVendorName(), product);
			this.passHospitalProductC(projectCode, returnsOrderDetail, order.getHospitalCode(), order.getHospitalName(), product);
			this.passVendorProductC(projectCode,returnsOrderDetail, order.getVendorCode(), order.getVendorName(), product);
			this.passProductC(projectCode,returnsOrderDetail, product);
			
			returnsOrderDetail.setIsPass(1);
			returnsOrderDetailService.update(projectCode, returnsOrderDetail);
		}

		//批量修改
		this.updateBatch(projectCode);
		hospitalProductCService.updateBatch(projectCode);
		vendorProductCService.updateBatch(projectCode);
		productCService.updateBatch(projectCode);

		//System.out.println("过产品账务-----------------end");
	}

	private void passOrderDetailC(String projectCode, OrderDetail orderDetail, String hospitalCode, String hospitalName,
			String vendorCode, String vendorName, Product product){
		String month = DateUtil.dateToStr(orderDetail.getOrderDate()).substring(0, 7);
		OrderDetailC orderDetailC = getOrderDetailC(projectCode, month, product, hospitalCode, hospitalName, vendorCode, vendorName);
		
		if(orderDetail.getOrderType().equals(OrderType.orderPlan)){
			orderDetailC.setShortSupplyTimes(orderDetailC.getShortSupplyTimes() + 1);
			orderDetailC.setShortSupplySum(orderDetailC.getShortSupplySum().add(orderDetail.getGoodsNum()));
		}else if(orderDetail.getOrderType().equals(OrderType.order)){
			if(orderDetail.getContractDetailCode() != null){
				orderDetailC.setContractPurchaseSum(orderDetailC.getContractPurchaseSum().add(orderDetail.getGoodsNum()));
			}
			orderDetailC.setPurchaseTimes(orderDetailC.getPurchaseTimes() + 1);
			orderDetailC.setPurchaseNum(orderDetailC.getPurchaseNum().add(orderDetail.getGoodsNum()));
			orderDetailC.setPurchaseSum(orderDetailC.getPurchaseSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.delivery)){
			orderDetailC.setValidityDayCount(orderDetailC.getValidityDayCount()+Integer.parseInt(orderDetail.getSegmentStr()));
			orderDetailC.setDeliveryTimes(orderDetailC.getDeliveryTimes() + 1);
			orderDetailC.setDeliveryNum(orderDetailC.getDeliveryNum().add(orderDetail.getGoodsNum()));
			orderDetailC.setDeliverySum(orderDetailC.getDeliverySum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.inoutbound)){
			orderDetailC.setInOutBoundNum(orderDetailC.getInOutBoundNum().add(orderDetail.getGoodsNum()));
			orderDetailC.setInOutBoundSum(orderDetailC.getInOutBoundSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.returns)){
			orderDetailC.setReturnsNum(orderDetailC.getReturnsNum().add(orderDetail.getGoodsNum()));
			orderDetailC.setReturnsSum(orderDetailC.getReturnsSum().add(orderDetail.getGoodsSum()));
		}
		//super.update(projectCode, orderDetailC);
	}
	
	private void passHospitalProductC(String projectCode, OrderDetail orderDetail, String hospitalCode, String hospitalName, Product product){
		String month = DateUtil.dateToStr(orderDetail.getOrderDate()).substring(0, 7);
		HospitalProductC hospitalProductC = hospitalProductCService.getHospitalProductC(projectCode, month, product, hospitalCode, hospitalName);
		
		if(orderDetail.getOrderType().equals(OrderType.orderPlan)){
			hospitalProductC.setShortSupplyTimes(hospitalProductC.getShortSupplyTimes() + 1);
			hospitalProductC.setShortSupplySum(hospitalProductC.getShortSupplySum().add(orderDetail.getGoodsNum()));
		}else if(orderDetail.getOrderType().equals(OrderType.order)){
			if(orderDetail.getContractDetailCode() != null){
				hospitalProductC.setContractPurchaseSum(hospitalProductC.getContractPurchaseSum().add(orderDetail.getGoodsNum()));
			}
			hospitalProductC.setPurchaseTimes(hospitalProductC.getPurchaseTimes() + 1);
			hospitalProductC.setPurchaseNum(hospitalProductC.getPurchaseNum().add(orderDetail.getGoodsNum()));
			hospitalProductC.setPurchaseSum(hospitalProductC.getPurchaseSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.delivery)){
			hospitalProductC.setValidityDayCount(hospitalProductC.getValidityDayCount()+Integer.parseInt(orderDetail.getSegmentStr()));
			hospitalProductC.setDeliveryTimes(hospitalProductC.getDeliveryTimes() + 1);
			hospitalProductC.setDeliveryNum(hospitalProductC.getDeliveryNum().add(orderDetail.getGoodsNum()));
			hospitalProductC.setDeliverySum(hospitalProductC.getDeliverySum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.inoutbound)){
			hospitalProductC.setInOutBoundNum(hospitalProductC.getInOutBoundNum().add(orderDetail.getGoodsNum()));
			hospitalProductC.setInOutBoundSum(hospitalProductC.getInOutBoundSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.returns)){
			hospitalProductC.setReturnsNum(hospitalProductC.getReturnsNum().add(orderDetail.getGoodsNum()));
			hospitalProductC.setReturnsSum(hospitalProductC.getReturnsSum().add(orderDetail.getGoodsSum()));
		}
		//hospitalProductCService.update(projectCode, hospitalProductC);
	}
	
	private void passVendorProductC(String projectCode, OrderDetail orderDetail, String vendorCode, String vendorName, Product product){
		String month = DateUtil.dateToStr(orderDetail.getOrderDate()).substring(0, 7);
		VendorProductC vendorProductC = vendorProductCService.getVendorProductC(projectCode, month, product, vendorCode, vendorName);
		
		if(orderDetail.getOrderType().equals(OrderType.orderPlan)){
			vendorProductC.setShortSupplyTimes(vendorProductC.getShortSupplyTimes() + 1);
			vendorProductC.setShortSupplySum(vendorProductC.getShortSupplySum().add(orderDetail.getGoodsNum()));
		}else if(orderDetail.getOrderType().equals(OrderType.order)){
			if(orderDetail.getContractDetailCode() != null){
				vendorProductC.setContractPurchaseSum(vendorProductC.getContractPurchaseSum().add(orderDetail.getGoodsNum()));
			}
			vendorProductC.setPurchaseTimes(vendorProductC.getPurchaseTimes() + 1);
			vendorProductC.setPurchaseNum(vendorProductC.getPurchaseNum().add(orderDetail.getGoodsNum()));
			vendorProductC.setPurchaseSum(vendorProductC.getPurchaseSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.delivery)){
			vendorProductC.setValidityDayCount(vendorProductC.getValidityDayCount()+Integer.parseInt(orderDetail.getSegmentStr()));
			vendorProductC.setDeliveryTimes(vendorProductC.getDeliveryTimes() + 1);
			vendorProductC.setDeliveryNum(vendorProductC.getDeliveryNum().add(orderDetail.getGoodsNum()));
			vendorProductC.setDeliverySum(vendorProductC.getDeliverySum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.inoutbound)){
			vendorProductC.setInOutBoundNum(vendorProductC.getInOutBoundNum().add(orderDetail.getGoodsNum()));
			vendorProductC.setInOutBoundSum(vendorProductC.getInOutBoundSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.returns)){
			vendorProductC.setReturnsNum(vendorProductC.getReturnsNum().add(orderDetail.getGoodsNum()));
			vendorProductC.setReturnsSum(vendorProductC.getReturnsSum().add(orderDetail.getGoodsSum()));
		}
		//vendorProductCService.update(projectCode, vendorProductC);
	}
	
	private void passProductC(String projectCode, OrderDetail orderDetail, Product product){
		String month = DateUtil.dateToStr(orderDetail.getOrderDate()).substring(0, 7);
		ProductC productC = productCService.getProductC(projectCode, month, product);
		
		if(orderDetail.getOrderType().equals(OrderType.orderPlan)){
			productC.setShortSupplyTimes(productC.getShortSupplyTimes() + 1);
			productC.setShortSupplySum(productC.getShortSupplySum().add(orderDetail.getGoodsNum()));
		}else if(orderDetail.getOrderType().equals(OrderType.order)){
			if(orderDetail.getContractDetailCode() != null){
				productC.setContractPurchaseSum(productC.getContractPurchaseSum().add(orderDetail.getGoodsNum()));
			}
			productC.setPurchaseTimes(productC.getPurchaseTimes() + 1);
			productC.setPurchaseNum(productC.getPurchaseNum().add(orderDetail.getGoodsNum()));
			productC.setPurchaseSum(productC.getPurchaseSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.delivery)){
			productC.setValidityDayCount(productC.getValidityDayCount()+Integer.parseInt(orderDetail.getSegmentStr()));
			productC.setDeliveryTimes(productC.getDeliveryTimes() + 1);
			productC.setDeliveryNum(productC.getDeliveryNum().add(orderDetail.getGoodsNum()));
			productC.setDeliverySum(productC.getDeliverySum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.inoutbound)){
			productC.setInOutBoundNum(productC.getInOutBoundNum().add(orderDetail.getGoodsNum()));
			productC.setInOutBoundSum(productC.getInOutBoundSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.returns)){
			productC.setReturnsNum(productC.getReturnsNum().add(orderDetail.getGoodsNum()));
			productC.setReturnsSum(productC.getReturnsSum().add(orderDetail.getGoodsSum()));
		}
		//productCService.update(projectCode, productC);
	}
		
	@Override
	public OrderDetailC getOrderDetailC(String projectCode, String month, Product product, String hospitalCode, String hospitalName, String vendorCode, String vendorName){
		Map<String, OrderDetailC> orderDetailCs = data.get(projectCode);
		if(orderDetailCs == null){
			orderDetailCs = new HashMap<String, OrderDetailC>();
			data.put(projectCode, orderDetailCs);
		}
		OrderDetailC orderDetailC = orderDetailCs.get(month+product.getId()+hospitalCode+vendorCode);
		if(orderDetailC == null){
			orderDetailC = orderDetailCDao.getByKey(month, product.getId(), hospitalCode, vendorCode);
			if(orderDetailC == null){
				orderDetailC = new OrderDetailC();
				orderDetailC.setMonth(month);
				orderDetailC.setHospitalCode(hospitalCode);
				orderDetailC.setHospitalName(hospitalName);
				orderDetailC.setVendorCode(vendorCode);
				orderDetailC.setVendorName(vendorName);
				orderDetailC.setProduct(product);
				orderDetailC.setContractSum(new BigDecimal(0));
				orderDetailC.setContractPurchaseSum(new BigDecimal(0));
				orderDetailC.setPurchaseTimes(0);
				orderDetailC.setDeliveryTimes(0);
				orderDetailC.setShortSupplyTimes(0);
				orderDetailC.setShortSupplySum(new BigDecimal(0));
				orderDetailC.setValidityDayCount(0);
				orderDetailC.setPurchaseNum(new BigDecimal(0));
				orderDetailC.setPurchaseSum(new BigDecimal(0));
				orderDetailC.setDeliveryNum(new BigDecimal(0));
				orderDetailC.setDeliverySum(new BigDecimal(0));
				orderDetailC.setInOutBoundNum(new BigDecimal(0));
				orderDetailC.setInOutBoundSum(new BigDecimal(0));
				orderDetailC.setReturnsNum(new BigDecimal(0));
				orderDetailC.setReturnsSum(new BigDecimal(0));
				orderDetailC.setContractFlag(0);
				orderDetailC.setOrderFlag(0);
				
				orderDetailCDao.save(orderDetailC);
			}
			orderDetailCs.put(month+product.getId()+hospitalCode+vendorCode, orderDetailC);
		}
		return orderDetailC;
	}
	
	@Override
	public void updateBatch(String projectCode){
		List<OrderDetailC> list = new ArrayList<OrderDetailC>();
		Map<String, OrderDetailC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		orderDetailCDao.updateBatch(list);
		data.put(projectCode, null);
	}

	@Override
	public DataGrid<Map<String, Object>> reportCGoodsTradeMX(String projectCode,Long id, String dateS, String dateE,
			PageRequest pageable) {
		return orderDetailCDao.reportCGoodsTradeMX(id, dateS, dateE, pageable);
	}
}
