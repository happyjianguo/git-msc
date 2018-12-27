package com.shyl.msc.menu.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.IBaseDrugDao;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.MedicalDevices;

@Repository
public class BaseDrugDao extends BaseDao<BaseDrug, Long> implements IBaseDrugDao {
	public BaseDrug getByExtId(Integer id, Integer type) {
		return this.getByHql("from BaseDrug where extId=? and type=?", id, type);
	}

}
