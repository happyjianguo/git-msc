package com.shyl.msc.menu.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.MedicalDevices;

public interface IMedicalDevicesDao extends IBaseDao<MedicalDevices, Long> {

	public MedicalDevices getByExtId(Integer id, Integer type);
}
