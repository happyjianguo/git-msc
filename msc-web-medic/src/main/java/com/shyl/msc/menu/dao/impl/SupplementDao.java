package com.shyl.msc.menu.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.ISupplementDao;
import com.shyl.msc.menu.entity.Supplement;

@Repository
public class SupplementDao extends BaseDao<Supplement, Long> implements ISupplementDao {

	public Supplement getByExtId(Integer extId) {
		return this.getByHql("from Supplement where extId=?", extId);
	}
}
