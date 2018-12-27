package com.shyl.msc.menu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.ISpecialDrugDao;
import com.shyl.msc.menu.entity.SpecialDrug;
import com.shyl.msc.menu.service.ISpecialDrugService;

@Service
@Transactional
public class SpecialDrugService extends BaseService<SpecialDrug, Long> implements ISpecialDrugService {

	private ISpecialDrugDao specialDrugDao;

	public ISpecialDrugDao getSpecialDrugDao() {
		return specialDrugDao;
	}

	@Resource
	public void setSpecialDrugDao(ISpecialDrugDao specialDrugDao) {
		this.specialDrugDao = specialDrugDao;
	}
}
