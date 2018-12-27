package com.shyl.msc.menu.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.menu.entity.MedicalDevices;

public interface IMedicalDevicesService extends IBaseService<MedicalDevices, Long> {

	public MedicalDevices getByExtId(Integer id, Integer type);
	
}
