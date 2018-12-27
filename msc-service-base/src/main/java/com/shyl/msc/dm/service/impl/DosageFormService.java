package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDosageFormDao;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.service.IDosageFormService;
/**
 * 剂型Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class DosageFormService extends BaseService<DosageForm, Long> implements IDosageFormService {
	private IDosageFormDao dosageFormDao;

	public IDosageFormDao getDosageFormDao() {
		return dosageFormDao;
	}

	@Resource
	public void setDosageFormDao(IDosageFormDao dosageFormDao) {
		this.dosageFormDao = dosageFormDao;
		super.setBaseDao(dosageFormDao);
	}
	@Override
	public DosageForm getByName(String projectCode, String name, String parentName) {
		return dosageFormDao.getByName(name, parentName);
	}

	
	/**
	 * 根据编码获取剂型
	 * @param code
	 * @return
	 */
	@Override
	public DosageForm getByCode(String projectCode, String code) {
		return dosageFormDao.getByCode(code);
	}
}
