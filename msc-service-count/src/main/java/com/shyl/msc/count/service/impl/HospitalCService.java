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
import com.shyl.msc.count.dao.IHospitalCDao;
import com.shyl.msc.count.entity.HospitalC;
import com.shyl.msc.count.service.IHospitalCService;


@Service
@Transactional(readOnly=true)
public class HospitalCService extends BaseService<HospitalC, Long> implements IHospitalCService {
	private Map<String, Map<String, HospitalC>> data = new HashMap<String, Map<String, HospitalC>>();

	private IHospitalCDao hospitalCDao;

	public IHospitalCDao getHospitalCDao() {
		return hospitalCDao;
	}

	@Resource
	public void setHospitalCDao(IHospitalCDao hospitalCDao) {
		this.hospitalCDao = hospitalCDao;
		super.setBaseDao(hospitalCDao);
	}
	
	@Override
	public HospitalC getHospitalC(String projectCode, String month, String hospitalCode, String hospitalName){
		Map<String, HospitalC> hospitalCs = data.get(projectCode);
		if(hospitalCs == null){
			hospitalCs = new HashMap<String, HospitalC>();
			data.put(projectCode, hospitalCs);
		}
		HospitalC hospitalC = hospitalCs.get(month+hospitalCode);
		if(hospitalC == null){
			hospitalC = hospitalCDao.getByKey(month, hospitalCode);
			if(hospitalC == null){
				hospitalC = new HospitalC();	    
				hospitalC.setMonth(month);	     
				hospitalC.setHospitalCode(hospitalCode);
				hospitalC.setHospitalName(hospitalName);
				hospitalC.setContractCount(0);
				hospitalC.setContractSpecCount(0);
				hospitalC.setContractPurchaseSpecCount(0);
				hospitalC.setContractSum(new BigDecimal(0));		
	        	hospitalC.setContractPurchaseSum(new BigDecimal(0));
	        	hospitalC.setInsuranceDrugSum(new BigDecimal(0));
	        	hospitalC.setBaseDrugSum(new BigDecimal(0));
	        	hospitalC.setPrescriptDrugSum(new BigDecimal(0));
	        	hospitalC.setPurchaseTimes(0);
	        	hospitalC.setPurchaseSpecCount(0);
	        	hospitalC.setReviewHour(new BigDecimal(0));
	        	hospitalC.setReviewTimes(0);
	        	hospitalC.setDeliveryHour(new BigDecimal(0));
	        	hospitalC.setDeliveryTimes(0);
	        	hospitalC.setDeliveryTimelyTimes(0);
	        	hospitalC.setOrderPlanCount(0);
	        	hospitalC.setShortSupplyTimes(0);
	        	hospitalC.setShortSupplySum(new BigDecimal(0));
	        	hospitalC.setValidityDayCount(0);
	        	hospitalC.setPurchaseNum(new BigDecimal(0));
	        	hospitalC.setPurchaseSum(new BigDecimal(0));
				hospitalC.setDeliveryNum(new BigDecimal(0));
				hospitalC.setDeliverySum(new BigDecimal(0));
				hospitalC.setInOutBoundNum(new BigDecimal(0));
				hospitalC.setInOutBoundSum(new BigDecimal(0));
				hospitalC.setReturnsNum(new BigDecimal(0));
				hospitalC.setReturnsSum(new BigDecimal(0));
				hospitalCDao.save(hospitalC);
			}	
			hospitalCs.put(month+hospitalCode, hospitalC);
		}
		return hospitalC;
	}
	
	@Override
	public void updateBatch(String projectCode){
		List<HospitalC> list = new ArrayList<HospitalC>();
		Map<String, HospitalC> map = data.get(projectCode);
		if(map != null){
			for(String key:map.keySet()){
				list.add(map.get(key));
			}
		}
		hospitalCDao.updateBatch(list);
		data.put(projectCode, null);
	}
}
