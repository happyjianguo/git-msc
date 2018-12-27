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

import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.count.service.IVendorProductCService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.b2b.stock.service.IVendorStockDayService;
import com.shyl.msc.count.dao.IVendorCDao;
import com.shyl.msc.count.dao.IVendorProductCDao;
import com.shyl.msc.count.entity.VendorC;
import com.shyl.msc.count.entity.VendorProductC;


@Service
@Transactional(readOnly=true)
public class VendorProductCService extends BaseService<VendorProductC, Long> implements IVendorProductCService {
	private Map<String, Map<String, VendorProductC>> data = new HashMap<String, Map<String, VendorProductC>>();

	@Resource
	private IVendorStockDayService vendorStockDayService;
	@Resource
	private IVendorCDao vendorCDao;
	
	private IVendorProductCDao vendorProductCDao;

	public IVendorProductCDao getVendorProductCDao() {
		return vendorProductCDao;
	}

	@Resource
	public void setVendorProductCDao(IVendorProductCDao vendorProductCDao) {
		this.vendorProductCDao = vendorProductCDao;
		super.setBaseDao(vendorProductCDao);
	}

	@Override
	public VendorProductC getByKey(String projectCode, String month, Long productId, String vendorCode) {
		return vendorProductCDao.getByKey(month, productId, vendorCode);
	}
	
	@Override
	public VendorProductC getVendorProductC(String projectCode, String month, Product product, String vendorCode, String vendorName){
		Map<String, VendorProductC> vendorProductCs = data.get(projectCode);
		if(vendorProductCs == null){
			vendorProductCs = new HashMap<String, VendorProductC>();
			data.put(projectCode, vendorProductCs);
		}
		VendorProductC vendorProductC = vendorProductCs.get(month+product.getId()+vendorCode);
		if(vendorProductC == null){
			vendorProductC = vendorProductCDao.getByKey(month, product.getId(), vendorCode);
			if(vendorProductC == null){
				vendorProductC = new VendorProductC();
				vendorProductC.setMonth(month);
				vendorProductC.setVendorCode(vendorCode);
				vendorProductC.setVendorName(vendorName);
				vendorProductC.setProduct(product);
				vendorProductC.setContractSum(new BigDecimal(0));
				vendorProductC.setContractPurchaseSum(new BigDecimal(0));
				vendorProductC.setPurchaseTimes(0);
				vendorProductC.setDeliveryTimes(0);
				vendorProductC.setShortSupplyTimes(0);
				vendorProductC.setShortSupplySum(new BigDecimal(0));
				vendorProductC.setValidityDayCount(0);
				vendorProductC.setPurchaseNum(new BigDecimal(0));
				vendorProductC.setPurchaseSum(new BigDecimal(0));
				vendorProductC.setDeliveryNum(new BigDecimal(0));
				vendorProductC.setDeliverySum(new BigDecimal(0));
				vendorProductC.setInOutBoundNum(new BigDecimal(0));
				vendorProductC.setInOutBoundSum(new BigDecimal(0));
				vendorProductC.setReturnsNum(new BigDecimal(0));
				vendorProductC.setReturnsSum(new BigDecimal(0));
				vendorProductC.setContractFlag(0);
				vendorProductC.setOrderFlag(0);
				vendorProductCDao.save(vendorProductC);
			}
			vendorProductCs.put(month+product.getId()+vendorCode, vendorProductC);
		}
		return vendorProductC;
	}

	@Override
	public void updateBatch(String projectCode){
		List<VendorProductC> list = new ArrayList<VendorProductC>();
		Map<String, VendorProductC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		vendorProductCDao.updateBatch(list);
		data.put(projectCode, null);
	}
	
	@Override
	@Transactional
	public void pass(String projectCode) {
		System.out.println("过供应商库存账务-----------------start");
		String nowMonth = DateUtil.dateToStr(new Date()).substring(0, 7);

		List<VendorProductC> vendorProductCs = vendorProductCDao.listByStockFlag(0, nowMonth);
		
		for(VendorProductC vendorProductC:vendorProductCs){
			String month = vendorProductC.getMonth();
			
			BigDecimal beginAmt = vendorStockDayService.getStockAmt(projectCode, DateUtil.getFirstDayOfMonth(month), vendorProductC.getVendorCode(),vendorProductC.getProduct().getCode());
			BigDecimal endAmt = vendorStockDayService.getStockAmt(projectCode, DateUtil.getLastDayOfMonth(month), vendorProductC.getVendorCode(),vendorProductC.getProduct().getCode());
			
			vendorProductC.setBeginStockSum(beginAmt);
			vendorProductC.setEndStockSum(endAmt);
			vendorProductC.setStockFlag(1);
		}
		
		List<VendorC> vendorCs = vendorCDao.listByStockFlag(0, nowMonth);
		
		for(VendorC vendorC:vendorCs){
			String month = vendorC.getMonth();
			
			BigDecimal beginAmt = vendorStockDayService.getStockAmt(projectCode, DateUtil.getFirstDayOfMonth(month), vendorC.getVendorCode());
			BigDecimal endAmt = vendorStockDayService.getStockAmt(projectCode, DateUtil.getLastDayOfMonth(month), vendorC.getVendorCode());
			
			vendorC.setBeginStockSum(beginAmt);
			vendorC.setEndStockSum(endAmt);
			vendorC.setStockFlag(1);
		}
		
		vendorProductCDao.updateBatch(vendorProductCs);
		vendorCDao.updateBatch(vendorCs);
		
		System.out.println("过供应商库存账务-----------------end");
	}
}
