package com.shyl.msc.b2b.plan.dao.impl;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.plan.dao.IHospitalPlanDetailDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlanDetail;

@Repository
public class HospitalPlanDetailDao extends BaseDao<HospitalPlanDetail, Long> implements IHospitalPlanDetailDao {

	public List<HospitalPlanDetail> getByHospitalPlanId(Long hospitalPlanId) {
		return this.listByHql("from HospitalPlanDetail where hospitalPlanId=? order by month asc",null, hospitalPlanId);
	}

	public int deleteByHospitalPlanId(Long hospitalPlanId) {
		return this.executeHql("delete from HospitalPlanDetail where hospitalPlanId=? ", hospitalPlanId);
	}

}
