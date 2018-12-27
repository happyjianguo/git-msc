package com.shyl.msc.dm.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDrugDao;
import com.shyl.msc.dm.entity.Drug;
import com.shyl.msc.dm.service.IDrugService;
/**
 * 药品Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class DrugService extends BaseService<Drug, Long> implements IDrugService {
	private IDrugDao drugDao;

	public IDrugDao getDrugDao() {
		return drugDao;
	}

	@Resource
	public void setDrugDao(IDrugDao drugDao) {
		this.drugDao = drugDao;
		super.setBaseDao(drugDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "drug", allEntries = true)
	public Drug getByCode(String projectCode, String code) {
		return drugDao.getByCode(code);
	}
	@Override
	@Transactional
	@CacheEvict(value = "drug", allEntries = true)
	public Drug save(String projectCode, Drug entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "drug", allEntries = true)
	public Drug update(String projectCode, Drug entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "drug", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}

	@Override
	@Transactional
	public String getMaxCode(String projectCode, String genericName) {
		return drugDao.getMaxCode(genericName);
	}
	
	@Override
	public Map<String, String> queryDrugInfoByName(String projectCode, String name, String productCode) {
		return drugDao.queryDrugInfoByName(name,productCode);
	}
	@Override
	@Cacheable(value = "drug")
	public Drug getByName(String projectCode, String name, String dosageFormName) {
		return drugDao.getByName(name, dosageFormName);
	}

	@Override
	@Cacheable(value = "drug")
	public Drug getByNameOnly(String projectCode, String name) {
		return drugDao.getByNameOnly(name);
	}
}
