package com.shyl.msc.menu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.IMedicalDevicesDao;
import com.shyl.msc.menu.dao.IProductDataDao;
import com.shyl.msc.menu.entity.MedicalDevices;
import com.shyl.msc.menu.entity.ProductData;
import com.shyl.msc.menu.service.IMedicalDevicesService;
import com.shyl.msc.menu.service.IProductDataService;

@Service
@Transactional
public class MedicalDevicesService extends BaseService<MedicalDevices, Long> implements IMedicalDevicesService {

	private IMedicalDevicesDao medicalDevicesDao;

	public IMedicalDevicesDao getMedicalDevicesDao() {
		return medicalDevicesDao;
	}
	@Resource
	public void setMedicalDevicesDao(IMedicalDevicesDao medicalDevicesDao) {
		super.setBaseDao(medicalDevicesDao);
		this.medicalDevicesDao = medicalDevicesDao;
	}

	
	public MedicalDevices getByExtId(Integer id, Integer type) {
		return medicalDevicesDao.getByExtId(id, type);
	}

}
