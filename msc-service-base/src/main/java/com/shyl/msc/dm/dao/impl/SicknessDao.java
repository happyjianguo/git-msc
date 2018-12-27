package com.shyl.msc.dm.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.ISicknessDao;
import com.shyl.msc.dm.entity.Sickness;

/**
 * 
 * 
 * @author a_Q
 *
 */
@Repository
public class SicknessDao extends BaseDao<Sickness, Long> implements ISicknessDao{

	@Override
	public Sickness findByCode(String code) {
		String hql = "from Sickness s where s.code=?";
		return super.getByHql(hql, code);
	}

}
