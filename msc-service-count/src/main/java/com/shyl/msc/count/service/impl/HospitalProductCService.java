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
import com.shyl.msc.count.service.IHospitalProductCService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.b2b.stock.service.IHisStockDayService;
import com.shyl.msc.count.dao.IHospitalCDao;
import com.shyl.msc.count.dao.IHospitalProductCDao;
import com.shyl.msc.count.entity.HospitalC;
import com.shyl.msc.count.entity.HospitalProductC;


@Service
@Transactional(readOnly=true)
public class HospitalProductCService extends BaseService<HospitalProductC, Long> implements IHospitalProductCService {
	private Map<String, Map<String, HospitalProductC>> data = new HashMap<String, Map<String, HospitalProductC>>();
	@Resource
	private IHisStockDayService hisStockDayService;
	@Resource
	private IHospitalCDao hospitalCDao;
	
	private IHospitalProductCDao hospitalProductCDao;

	public IHospitalProductCDao getHospitalProductCDao() {
		return hospitalProductCDao;
	}

	@Resource
	public void setHospitalProductCDao(IHospitalProductCDao hospitalProductCDao) {
		this.hospitalProductCDao = hospitalProductCDao;
		super.setBaseDao(hospitalProductCDao);
	}
	
	@Override
	public HospitalProductC getByKey(String projectCode, String month, Long productId, String hospitalCode) {
		return hospitalProductCDao.getByKey(month, productId, hospitalCode);
	}
	
	@Override
	public HospitalProductC getHospitalProductC(String projectCode, String month, Product product, String hospitalCode, String hospitalName){
		Map<String, HospitalProductC> hospitalProductCs = data.get(projectCode);
		if(hospitalProductCs == null){
			hospitalProductCs = new HashMap<String, HospitalProductC>();
			data.put(projectCode, hospitalProductCs);
		}
		HospitalProductC hospitalProductC = hospitalProductCs.get(month+product.getId()+hospitalCode);
		if(hospitalProductC == null){
			hospitalProductC = hospitalProductCDao.getByKey(month, product.getId(), hospitalCode);
			if(hospitalProductC == null){
				hospitalProductC = new HospitalProductC();
				hospitalProductC.setMonth(month);
				hospitalProductC.setHospitalCode(hospitalCode);
				hospitalProductC.setHospitalName(hospitalName);
				hospitalProductC.setProduct(product);
				hospitalProductC.setContractSum(new BigDecimal(0));
				hospitalProductC.setContractPurchaseSum(new BigDecimal(0));
				hospitalProductC.setPurchaseTimes(0);
				hospitalProductC.setDeliveryTimes(0);
				hospitalProductC.setShortSupplyTimes(0);
				hospitalProductC.setShortSupplySum(new BigDecimal(0));
				hospitalProductC.setValidityDayCount(0);
				hospitalProductC.setPurchaseNum(new BigDecimal(0));
				hospitalProductC.setPurchaseSum(new BigDecimal(0));
				hospitalProductC.setDeliveryNum(new BigDecimal(0));
				hospitalProductC.setDeliverySum(new BigDecimal(0));
				hospitalProductC.setInOutBoundNum(new BigDecimal(0));
				hospitalProductC.setInOutBoundSum(new BigDecimal(0));
				hospitalProductC.setReturnsNum(new BigDecimal(0));
				hospitalProductC.setReturnsSum(new BigDecimal(0));
				hospitalProductC.setContractFlag(0);
				hospitalProductC.setOrderFlag(0);
				
				hospitalProductCDao.save(hospitalProductC);
			}
			hospitalProductCs.put(month+product.getId()+hospitalCode, hospitalProductC);
		}
		return hospitalProductC;
	}

	@Override
	public void updateBatch(String projectCode){
		List<HospitalProductC> list = new ArrayList<HospitalProductC>();
		Map<String, HospitalProductC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		hospitalProductCDao.updateBatch(list);
		data.put(projectCode, null);
	}
	
	@Override
	@Transactional
	public void pass(String projectCode) {
		System.out.println("过医院库存账务-----------------start");
		String nowMonth = DateUtil.dateToStr(new Date()).substring(0, 7);
		
		List<HospitalProductC> hospitalProductCs = hospitalProductCDao.listByStockFlag(0, nowMonth);
		
		for(HospitalProductC hospitalProductC:hospitalProductCs){
			String month = hospitalProductC.getMonth();
			System.out.println("当前月份是-------------"+month);
			BigDecimal beginAmt = hisStockDayService.getStockAmt(projectCode, DateUtil.getFirstDayOfMonth(month), hospitalProductC.getHospitalCode(),hospitalProductC.getProduct().getCode());
			BigDecimal endAmt = hisStockDayService.getStockAmt(projectCode, DateUtil.getLastDayOfMonth(month), hospitalProductC.getHospitalCode(),hospitalProductC.getProduct().getCode());
			
			hospitalProductC.setBeginStockSum(beginAmt);
			hospitalProductC.setEndStockSum(endAmt);
			hospitalProductC.setStockFlag(1);
		}
		
		List<HospitalC> hospitalCs = hospitalCDao.listByStockFlag(0, nowMonth);
		
		for(HospitalC hospitalC:hospitalCs){
			String month = hospitalC.getMonth();
			
			BigDecimal beginAmt = hisStockDayService.getStockAmt(projectCode, DateUtil.getFirstDayOfMonth(month), hospitalC.getHospitalCode());
			BigDecimal endAmt = hisStockDayService.getStockAmt(projectCode, DateUtil.getLastDayOfMonth(month), hospitalC.getHospitalCode());
			
			hospitalC.setBeginStockSum(beginAmt);
			hospitalC.setEndStockSum(endAmt);
			hospitalC.setStockFlag(1);
		}
		
		hospitalProductCDao.updateBatch(hospitalProductCs);
		hospitalCDao.updateBatch(hospitalCs);
		
		System.out.println("过医院库存账务-----------------end");
	}

}
