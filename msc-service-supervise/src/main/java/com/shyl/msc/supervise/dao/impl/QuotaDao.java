package com.shyl.msc.supervise.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IQuotaDao;
import com.shyl.msc.supervise.entity.Quota;

@Repository
public class QuotaDao extends BaseDao<Quota, Long> implements IQuotaDao {

	@Override
	public Quota getByCode(String code) {
		String hql = "from Quota a where a.code = ?";
		return this.getByHql(hql, code);
	}
	
}
