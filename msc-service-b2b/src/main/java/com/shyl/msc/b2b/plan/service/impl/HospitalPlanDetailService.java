package com.shyl.msc.b2b.plan.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.plan.dao.IHospitalPlanDetailDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;
import com.shyl.msc.b2b.plan.service.IHospitalPlanDetailService;

@Service
@Transactional
public class HospitalPlanDetailService extends BaseService<HospitalPlanDetail, Long> implements IHospitalPlanDetailService {

	private IHospitalPlanDetailDao hospitalPlanDetailDao;

	@Resource
	public void setHospitalPlanDetailDao(IHospitalPlanDetailDao hospitalPlanDetailDao) {
		this.hospitalPlanDetailDao = hospitalPlanDetailDao;
		super.setBaseDao(hospitalPlanDetailDao);
	}
	@Override
	public List<HospitalPlanDetail> getByHospitalPlanId(String projectCode, Long hospitalPlanId) {
		return hospitalPlanDetailDao.getByHospitalPlanId(hospitalPlanId);
	}
	
	@Override
	public int deleteByHospitalPlanId(String projectCode, Long hospitalPlanId) {
		return hospitalPlanDetailDao.deleteByHospitalPlanId(hospitalPlanId);
	}

}
