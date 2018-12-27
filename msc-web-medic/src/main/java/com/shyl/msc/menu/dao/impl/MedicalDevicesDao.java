package com.shyl.msc.menu.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.IMedicalDevicesDao;
import com.shyl.msc.menu.entity.MedicalDevices;

@Repository
public class MedicalDevicesDao extends BaseDao<MedicalDevices, Long> implements IMedicalDevicesDao {

	public MedicalDevices getByExtId(Integer id, Integer type) {
		return this.getByHql("from MedicalDevices where extId=? and type=?", id, type);
	}

}
