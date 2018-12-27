package com.shyl.msc.b2b.plan.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;

public interface IHospitalPlanDetailService extends IBaseService<HospitalPlanDetail, Long> {

	public List<HospitalPlanDetail> getByHospitalPlanId(@ProjectCodeFlag String projectCode, Long hospitalPlanId);
	
	public int deleteByHospitalPlanId(@ProjectCodeFlag String projectCode, Long hospitalPlanId);
}
