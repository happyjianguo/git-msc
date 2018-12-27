package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IHospitalZoneDao;
import com.shyl.msc.supervise.entity.HospitalZone;
@Repository
public class HospitalZoneDao extends BaseDao<HospitalZone,Long> implements IHospitalZoneDao{

	@Override
	public HospitalZone getByCode(String hospitalCode) {
		String hql = "from HospitalZone where hospitalCode=?";
		return super.getByHql(hql, hospitalCode);
	}

	@Override
	public List<Map<String, Object>> getHospital(PageRequest page) {
		String sql = "select z.hospitalCode from sup_hospital_zone z";
		return super.listBySql2(sql, page, Map.class);
	}

}
