package com.shyl.msc.count.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.count.dao.IVendorCDao;
import com.shyl.msc.count.entity.VendorC;
import com.shyl.msc.count.service.IVendorCService;


@Service
@Transactional(readOnly=true)
public class VendorCService extends BaseService<VendorC, Long> implements IVendorCService {
	private Map<String, Map<String, VendorC>> data = new HashMap<String, Map<String, VendorC>>();

	private IVendorCDao vendorCDao;

	public IVendorCDao getVendorCDao() {
		return vendorCDao;
	}

	@Resource
	public void setVendorCDao(IVendorCDao vendorCDao) {
		this.vendorCDao = vendorCDao;
		super.setBaseDao(vendorCDao);
	}
	
	@Override
	public VendorC getVendorC(String projectCode, String month, String vendorCode, String vendorName){
		Map<String, VendorC> vendorCs = data.get(projectCode);
		if(vendorCs == null){
			vendorCs = new HashMap<String, VendorC>();
			data.put(projectCode, vendorCs);
		}
		VendorC vendorC = vendorCs.get(month+vendorCode);
		if(vendorC == null){
			vendorC = vendorCDao.getByKey(month, vendorCode);
			if(vendorC == null){
				vendorC = new VendorC();	    
				vendorC.setMonth(month);	     
				vendorC.setVendorCode(vendorCode);
				vendorC.setVendorName(vendorName);
				vendorC.setContractCount(0);
				vendorC.setContractSpecCount(0);
				vendorC.setContractPurchaseSpecCount(0);
				vendorC.setContractSum(new BigDecimal(0));		
	        	vendorC.setContractPurchaseSum(new BigDecimal(0));
	        	vendorC.setInsuranceDrugSum(new BigDecimal(0));
	        	vendorC.setBaseDrugSum(new BigDecimal(0));
	        	vendorC.setPrescriptDrugSum(new BigDecimal(0));
	        	vendorC.setPurchaseTimes(0);
	        	vendorC.setPurchaseSpecCount(0);
	        	vendorC.setReviewHour(new BigDecimal(0));
	        	vendorC.setReviewTimes(0);
	        	vendorC.setDeliveryHour(new BigDecimal(0));
	        	vendorC.setDeliveryTimes(0);
	        	vendorC.setDeliveryTimelyTimes(0);
	        	vendorC.setOrderPlanCount(0);
	        	vendorC.setShortSupplyTimes(0);
	        	vendorC.setShortSupplySum(new BigDecimal(0));
	        	vendorC.setValidityDayCount(0);
	        	vendorC.setPurchaseNum(new BigDecimal(0));
	        	vendorC.setPurchaseSum(new BigDecimal(0));
				vendorC.setDeliveryNum(new BigDecimal(0));
				vendorC.setDeliverySum(new BigDecimal(0));
				vendorC.setInOutBoundNum(new BigDecimal(0));
				vendorC.setInOutBoundSum(new BigDecimal(0));
				vendorC.setReturnsNum(new BigDecimal(0));
				vendorC.setReturnsSum(new BigDecimal(0));
				vendorCDao.save(vendorC);
			}	
			vendorCs.put(month+vendorCode, vendorC);
		}
		return vendorC;
	}
	
	@Override
	public void updateBatch(String projectCode){
		List<VendorC> list = new ArrayList<VendorC>();
		Map<String, VendorC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		vendorCDao.updateBatch(list);
		data.put(projectCode, null);
	}
}
