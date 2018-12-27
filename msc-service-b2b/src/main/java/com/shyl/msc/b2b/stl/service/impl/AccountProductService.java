package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.collections.map.AbstractMapDecorator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDetailDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.entity.OrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.stl.dao.IAccountProductDao;
import com.shyl.msc.b2b.stl.entity.AccountProduct;
import com.shyl.msc.b2b.stl.service.IAccountProductService;
import com.shyl.msc.enmu.OrderType;
/**
 * 产品账务
 * 
 * @author a_Q
 *
 */
@Service
public class AccountProductService extends BaseService<AccountProduct, Long> implements IAccountProductService {

	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	private IInOutBoundDetailDao inOutBoundDetailDao;
	@Resource
	private IReturnsOrderDetailDao returnsOrderDetailDao;
	
	private IAccountProductDao accountProductDao;

	public IAccountProductDao getAccountProductDao() {
		return accountProductDao;
	}

	@Resource
	public void setAccountProductDao(IAccountProductDao accountProductDao) {
		this.accountProductDao = accountProductDao;
		super.setBaseDao(accountProductDao);
	}

	@Override
	@Transactional
	public void passAccount(String projectCode) {
		System.out.println("过产品账务-----------------start");

		Date t1 = new Date();
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailDao.listByIsPass(0);
		for(PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails){
			purchaseOrderDetail.setOrderType(OrderType.order);
			this.passAccount(purchaseOrderDetail);
			
			purchaseOrderDetail.setIsPass(1);
		}
		purchaseOrderDetailDao.updateBatch(purchaseOrderDetails);

		Date t2 = new Date();
		List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailDao.listByIsPass(0);
		for(DeliveryOrderDetail deliveryOrderDetail : deliveryOrderDetails){
			deliveryOrderDetail.setOrderType(OrderType.delivery);
			this.passAccount(deliveryOrderDetail);
			
			deliveryOrderDetail.setIsPass(1);
		}
		deliveryOrderDetailDao.updateBatch(deliveryOrderDetails);

		Date t3 = new Date();
		List<InOutBoundDetail> inOutBoundDetails = inOutBoundDetailDao.listByIsPass(0);
		for(InOutBoundDetail inOutBoundDetail : inOutBoundDetails){
			inOutBoundDetail.setOrderType(OrderType.inoutbound);
			this.passAccount(inOutBoundDetail);
			
			inOutBoundDetail.setIsPass(1);
		}
		inOutBoundDetailDao.updateBatch(inOutBoundDetails);

		Date t4 = new Date();
		List<ReturnsOrderDetail> returnsOrderDetails = returnsOrderDetailDao.listByIsPass(0);
		for(ReturnsOrderDetail returnsOrderDetail : returnsOrderDetails){
			returnsOrderDetail.setOrderType(OrderType.returns);
			this.passAccount(returnsOrderDetail);
			
			returnsOrderDetail.setIsPass(1);
		}
		returnsOrderDetailDao.updateBatch(returnsOrderDetails);

		//System.out.println("过产品账务-----------------end");
	}

	@Transactional
	public void passAccount(OrderDetail orderDetail){
		String month = DateUtil.dateToStr(orderDetail.getOrderDate()).substring(0, 7);
		System.out.println("month="+month+"          "+orderDetail.getOrderType());
		String code = orderDetail.getProductCode();
		AccountProduct accountProduct = accountProductDao.getByCode(month, code);
		System.out.println(orderDetail.getCode()+"="+code);
		if(accountProduct == null){
			accountProduct = new AccountProduct();
			accountProduct.setMonth(month);
			accountProduct.setProductCode(code);
			accountProduct.setOrderNum(new BigDecimal(0));
			accountProduct.setOrderSum(new BigDecimal(0));
			accountProduct.setDeliveryNum(new BigDecimal(0));
			accountProduct.setDeliverySum(new BigDecimal(0));
			accountProduct.setInOutBoundNum(new BigDecimal(0));
			accountProduct.setInOutBoundSum(new BigDecimal(0));
			accountProduct.setReturnsNum(new BigDecimal(0));
			accountProduct.setReturnsSum(new BigDecimal(0));
			accountProductDao.save(accountProduct);
		}
		if(orderDetail.getOrderType().equals(OrderType.order)){
			accountProduct.setOrderNum(accountProduct.getOrderNum().add(orderDetail.getGoodsNum()));
			accountProduct.setOrderSum(accountProduct.getOrderSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.delivery)){
			accountProduct.setDeliveryNum(accountProduct.getDeliveryNum().add(orderDetail.getGoodsNum()));
			accountProduct.setDeliverySum(accountProduct.getDeliverySum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.inoutbound)){
			accountProduct.setInOutBoundNum(accountProduct.getInOutBoundNum().add(orderDetail.getGoodsNum()));
			accountProduct.setInOutBoundSum(accountProduct.getInOutBoundSum().add(orderDetail.getGoodsSum()));
		}else if(orderDetail.getOrderType().equals(OrderType.returns)){
			accountProduct.setReturnsNum(accountProduct.getReturnsNum().add(orderDetail.getGoodsNum()));
			accountProduct.setReturnsSum(accountProduct.getReturnsSum().add(orderDetail.getGoodsSum()));
		}
		accountProductDao.update(accountProduct);
	}

	@Override
	@Transactional
	public void passAccountBetter(String projectCode) {
		System.out.println("过产品账务-----------------start");
		List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailDao.listByIsPass(0);
		Map<String,PurchaseOrderDetail> purchaseOrderDetailMap = new HashMap<>();
		for(PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails){
			purchaseOrderDetail.setOrderType(OrderType.order);
			purchaseOrderDetail.setIsPass(1);
			String month = DateUtil.dateToStr(purchaseOrderDetail.getOrderDate()).substring(0, 7);
			String code = purchaseOrderDetail.getProductCode();
			PurchaseOrderDetail p = purchaseOrderDetailMap.get(month+code);
			if(null != p){
				if(p.getGoodsNum() != null){
					p.setGoodsNum(p.getGoodsNum().add(purchaseOrderDetail.getGoodsNum()));
				}
				if(p.getGoodsSum() != null){
					p.setGoodsSum(p.getGoodsSum().add(purchaseOrderDetail.getGoodsSum()));
				}
			}else{
				p = new PurchaseOrderDetail();
				p.setGoodsNum(purchaseOrderDetail.getGoodsNum());
				p.setGoodsSum(purchaseOrderDetail.getGoodsSum());
			}
			purchaseOrderDetailMap.put(month+code,p);
		}
		purchaseOrderDetailDao.updateBatch(purchaseOrderDetails);
		purchaseOrderDetailDao.flush();
		purchaseOrderDetailDao.clear();
		Set<Map.Entry<String,PurchaseOrderDetail>> entrySet = purchaseOrderDetailMap.entrySet();
		Iterator<Map.Entry<String,PurchaseOrderDetail>> iter = entrySet.iterator();
		StringBuilder builder = new StringBuilder();
		List<AccountProduct> accountProductList = new ArrayList<>();
		builder.append("'0'");
		int i=0;
		while(iter.hasNext()){
			i++;
			Map.Entry<String,PurchaseOrderDetail> entry = iter.next();
			String key = entry.getKey();
			builder.append(",'"+key+"'");
			if(i % 300 == 0){
				accountProductList.addAll(accountProductDao.getbyMonthAndCode(builder.toString()));
				builder = new StringBuilder();
			}
		}
		accountProductList.addAll(accountProductDao.getbyMonthAndCode(builder.toString()));
		Map<String,AccountProduct> accountProductMap = new HashMap<>();
		for(AccountProduct accountProduct : accountProductList){
			accountProductMap.put(accountProduct.getMonth()+accountProduct.getProductCode(),accountProduct);
		}
		List<AccountProduct> saveAccountProduct = new ArrayList<>();
		List<AccountProduct> updateAccountProduct = new ArrayList<>();
		while(iter.hasNext()){
			Map.Entry<String,PurchaseOrderDetail> entry = iter.next();
			String key = entry.getKey();
			PurchaseOrderDetail p = entry.getValue();
			AccountProduct accountProduct = accountProductMap.get(key);
			if(null != accountProduct){
				accountProduct.setOrderNum(accountProduct.getOrderNum().add(p.getGoodsNum()));
				accountProduct.setOrderSum(accountProduct.getOrderSum().add(p.getGoodsSum()));
				updateAccountProduct.add(accountProduct);
			}else{
				accountProduct = new AccountProduct();
				accountProduct.setOrderNum(p.getGoodsNum());
				accountProduct.setOrderSum(p.getGoodsSum());
				saveAccountProduct.add(accountProduct);
			}
		}
		accountProductDao.saveBatch(saveAccountProduct);
		accountProductDao.flush();
		accountProductDao.clear();
		accountProductDao.updateBatch(updateAccountProduct);

	}

	@Override
	@Transactional(readOnly=true)
	public DataGrid<Map<String, Object>> reportHospitalSB(String projectCode, String name, String dataS, String dataE,
			PageRequest pageable) {
		return accountProductDao.reportHospitalSB(name, dataS, dataE,pageable);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> reportTrade(String projectCode, String year) {
		return accountProductDao.reportTrade(year);
	}

	@Override
	@Transactional(readOnly=true)
	public DataGrid<Map<String, Object>> reportGoodsTrade(String projectCode, String dateS, String dateE, PageRequest pageable) {
		return accountProductDao.reportGoodsTrade(dateS, dateE, pageable);
	}

	/**
	 * 获取时间差（秒）
	 * @param d1：开始时间
	 * @param d2：结束时间
	 * @return d1与d2之间的时间差
	 */
	public Long getTimeDifference(Date d1, Date d2){
		int exe_time_year = (d2.getYear() - d1.getYear()) * (365*24*60*60);
		int exe_time_month = (d2.getMonth() - d1.getMonth())* (30*24*60*60);
		int exe_time_day = (d2.getDay() - d1.getDay())* (24*60*60);
		int exe_time_hour = (d2.getHours() - d1.getHours())* (60*60);
		int exe_time_minute = (d2.getMinutes() - d1.getMinutes())* (60);
		int exe_time_second = d2.getSeconds() - d1.getSeconds();

		return Long.valueOf((exe_time_day + exe_time_hour + exe_time_minute + exe_time_month + exe_time_second + exe_time_year));
	}
}
