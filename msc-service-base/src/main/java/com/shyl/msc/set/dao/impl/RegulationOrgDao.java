package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IRegulationOrgDao;
import com.shyl.msc.set.entity.RegulationOrg;

@Repository
public class RegulationOrgDao extends BaseDao<RegulationOrg,Long> implements IRegulationOrgDao{

	@Override
	public RegulationOrg findByCode(String code) {
		String hql = "from RegulationOrg where code=?";
		return super.getByHql(hql, code);
	}
	
}
