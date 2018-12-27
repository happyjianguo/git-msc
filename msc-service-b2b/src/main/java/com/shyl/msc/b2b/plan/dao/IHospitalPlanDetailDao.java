package com.shyl.msc.b2b.plan.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;

public interface IHospitalPlanDetailDao extends IBaseDao<HospitalPlanDetail, Long> {
	
	public List<HospitalPlanDetail> getByHospitalPlanId(Long hospitalPlanId);
	
	public int deleteByHospitalPlanId(Long hospitalPlanId);
}
