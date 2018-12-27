package com.shyl.msc.menu.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.IBaseDrugDao;
import com.shyl.msc.menu.dao.IStupefacientDao;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.Stupefacient;
@Repository
public class StupefacientDao extends BaseDao<Stupefacient, Long> implements IStupefacientDao {

	@Override
	public Stupefacient getByExtId(Integer id, Integer type) {
		return this.getByHql("from Stupefacient where extId=? and type=?", id, type);
	}

}
