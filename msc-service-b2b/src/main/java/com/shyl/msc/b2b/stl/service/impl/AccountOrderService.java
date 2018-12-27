package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.stl.dao.IAccountOrderDao;
import com.shyl.msc.b2b.stl.entity.AccountOrder;
import com.shyl.msc.b2b.stl.service.IAccountOrderService;
import com.shyl.msc.enmu.OrderType;
/**
 * 账务
 * 
 * @author a_Q
 *
 */
@Service
public class AccountOrderService extends BaseService<AccountOrder, Long> implements IAccountOrderService {
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource
	private IInOutBoundDao inOutBoundDao;
	@Resource
	private IReturnsOrderDao returnsOrderDao;
	private IAccountOrderDao accountOrderDao;
	
	public IAccountOrderDao getAccountOrderDao() {
		return accountOrderDao;
	}

	@Resource
	public void setAccountOrderDao(IAccountOrderDao accountOrderDao) {
		this.accountOrderDao = accountOrderDao;
		super.setBaseDao(accountOrderDao);
	}
	
	@Override
	@Transactional
	public synchronized void passAccount(String projectCode, Order order){
		String month = DateUtil.dateToStr(order.getOrderDate()).substring(0, 7);
		AccountOrder accountOrder = accountOrderDao.getOne(month,order.getVendorCode(), order.getHospitalCode());
		if(accountOrder == null){
			accountOrder = new AccountOrder();
			accountOrder.setMonth(month);
			accountOrder.setVendorCode(order.getVendorCode());
			accountOrder.setVendorName(order.getVendorName());
			accountOrder.setHospitalCode(order.getHospitalCode());
			accountOrder.setHospitalName(order.getHospitalName());
			accountOrder.setOrderNum(new BigDecimal(0));
			accountOrder.setOrderSum(new BigDecimal(0));
			accountOrder.setDeliveryNum(new BigDecimal(0));
			accountOrder.setDeliverySum(new BigDecimal(0));
			accountOrder.setInOutBoundNum(new BigDecimal(0));
			accountOrder.setInOutBoundSum(new BigDecimal(0));
			accountOrder.setReturnsNum(new BigDecimal(0));
			accountOrder.setReturnsSum(new BigDecimal(0));
			accountOrder = accountOrderDao.save(accountOrder);
		}
		if(order.getOrderType().equals(OrderType.order)){
			accountOrder.setOrderNum(accountOrder.getOrderNum().add(order.getNum()));
			accountOrder.setOrderSum(accountOrder.getOrderSum().add(order.getSum()));
		}else if(order.getOrderType().equals(OrderType.delivery)){
			accountOrder.setDeliveryNum(accountOrder.getDeliveryNum().add(order.getNum()));
			accountOrder.setDeliverySum(accountOrder.getDeliverySum().add(order.getSum()));
		}else if(order.getOrderType().equals(OrderType.inoutbound)){
			accountOrder.setInOutBoundNum(accountOrder.getInOutBoundNum().add(order.getNum()));
			accountOrder.setInOutBoundSum(accountOrder.getInOutBoundSum().add(order.getSum()));
		}else if(order.getOrderType().equals(OrderType.returns)){
			accountOrder.setReturnsNum(accountOrder.getReturnsNum().add(order.getNum()));
			accountOrder.setReturnsSum(accountOrder.getReturnsSum().add(order.getSum()));
		}

		accountOrderDao.update(accountOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> listByHospitalPerMonth(String projectCode, String hospitalCode, String year) {
		return accountOrderDao.listByHospitalPerMonth(hospitalCode, year);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> orderSumByHospitalAndYear(String projectCode, String hospitalCode, String year) {
		return accountOrderDao.orderSumByHospitalAndYear(hospitalCode, year);
	}

	/**
	 *
	 * @param projectCode
	 */
	@Override
	@Transactional
	public void passAccount(String projectCode) {
		System.out.println("过订单账务-----------------start");

		Date t1 = new Date();
        List<PurchaseOrder> purchaseOrders = purchaseOrderDao.listByIsPass(0);
        for(PurchaseOrder purchaseOrder : purchaseOrders){
        	Order order = new Order();
        	order.setCode(purchaseOrder.getCode());
        	order.setOrderDate(purchaseOrder.getOrderDate());
        	order.setVendorCode(purchaseOrder.getVendorCode());
        	order.setVendorName(purchaseOrder.getVendorName());
        	order.setHospitalCode(purchaseOrder.getHospitalCode());
        	order.setHospitalName(purchaseOrder.getHospitalName());
        	order.setWarehouseCode(purchaseOrder.getWarehouseCode());
        	order.setWarehouseName(purchaseOrder.getWarehouseName());
        	order.setNum(purchaseOrder.getNum());
        	order.setSum(purchaseOrder.getSum());
        	order.setOrderType(OrderType.order);
        	this.passAccount(projectCode, order);
        	
        	purchaseOrder.setIsPass(1);
        }
        //批量更新
        purchaseOrderDao.updateBatch(purchaseOrders);

        Date t2 = new Date();
        List<DeliveryOrder> deliveryOrders = deliveryOrderDao.listByIsPass(0);
        for(DeliveryOrder deliveryOrder : deliveryOrders){
        	Order order = new Order();
        	order.setCode(deliveryOrder.getCode());
        	order.setOrderDate(deliveryOrder.getOrderDate());
        	order.setVendorCode(deliveryOrder.getVendorCode());
        	order.setVendorName(deliveryOrder.getVendorName());
        	order.setHospitalCode(deliveryOrder.getHospitalCode());
        	order.setHospitalName(deliveryOrder.getHospitalName());
        	order.setWarehouseCode(deliveryOrder.getWarehouseCode());
        	order.setWarehouseName(deliveryOrder.getWarehouseName());
        	order.setNum(deliveryOrder.getNum());
        	order.setSum(deliveryOrder.getSum());
        	order.setOrderType(OrderType.delivery);
        	this.passAccount(projectCode, order);
        	
        	deliveryOrder.setIsPass(1);
        }
        deliveryOrderDao.updateBatch(deliveryOrders);

        Date t3 = new Date();
        List<InOutBound> inOutBounds = inOutBoundDao.listByIsPass(0);
        for(InOutBound inOutBound : inOutBounds){
        	Order order = new Order();
        	order.setCode(inOutBound.getCode());
        	order.setOrderDate(inOutBound.getOrderDate());
        	order.setVendorCode(inOutBound.getVendorCode());
        	order.setVendorName(inOutBound.getVendorName());
        	order.setHospitalCode(inOutBound.getHospitalCode());
        	order.setHospitalName(inOutBound.getHospitalName());
        	order.setWarehouseCode(inOutBound.getWarehouseCode());
        	order.setWarehouseName(inOutBound.getWarehouseName());
        	order.setNum(inOutBound.getNum());
        	order.setSum(inOutBound.getSum());
        	order.setOrderType(OrderType.inoutbound);
        	this.passAccount(projectCode, order);
        	
        	inOutBound.setIsPass(1);
        }
        inOutBoundDao.updateBatch(inOutBounds);

        Date t4 = new Date();
        List<ReturnsOrder> returnsOrders = returnsOrderDao.listByIsPass(0);
        for(ReturnsOrder returnsOrder : returnsOrders){
        	Order order = new Order();
        	order.setCode(returnsOrder.getCode());
        	order.setOrderDate(returnsOrder.getOrderDate());
        	order.setVendorCode(returnsOrder.getVendorCode());
        	order.setVendorName(returnsOrder.getVendorName());
        	order.setHospitalCode(returnsOrder.getHospitalCode());
        	order.setHospitalName(returnsOrder.getHospitalName());
        	order.setWarehouseCode(returnsOrder.getWarehouseCode());
        	order.setWarehouseName(returnsOrder.getWarehouseName());
        	order.setNum(returnsOrder.getNum());
        	order.setSum(returnsOrder.getSum());
        	order.setOrderType(OrderType.returns);
        	this.passAccount(projectCode, order);
        	
        	returnsOrder.setIsPass(1);
        }
        returnsOrderDao.updateBatch(returnsOrders);
    
		System.out.println("过订单账务-----------------end");
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> orderSumByGpoAndYear(String projectCode, String vendorCode, String year) {
		return accountOrderDao.orderSumByGpoAndYear(vendorCode, year);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> listByGpoPerMonth(String projectCode, String vendorCode, String year) {
		return accountOrderDao.listByGpoPerMonth(vendorCode,year);
	}

}
