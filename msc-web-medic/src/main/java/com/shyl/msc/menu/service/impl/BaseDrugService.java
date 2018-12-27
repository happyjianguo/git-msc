package com.shyl.msc.menu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.IBaseDrugDao;
import com.shyl.msc.menu.dao.IMedicalDevicesDao;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.MedicalDevices;
import com.shyl.msc.menu.service.IBaseDrugService;

@Service
@Transactional
public class BaseDrugService extends BaseService<BaseDrug, Long> implements IBaseDrugService {

	private IBaseDrugDao baseDrugDao;

	public IBaseDrugDao getBaseDrugDao() {
		return baseDrugDao;
	}
	@Resource
	public void setBaseDrugDao(IBaseDrugDao baseDrugDao) {
		super.setBaseDao(baseDrugDao);
		this.baseDrugDao = baseDrugDao;
	}

	
	public BaseDrug getByExtId(Integer id, Integer type) {
		return baseDrugDao.getByExtId(id, type);
	}

}
