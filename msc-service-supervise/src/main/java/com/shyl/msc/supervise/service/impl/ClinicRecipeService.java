package com.shyl.msc.supervise.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IClinicRecipeDao;
import com.shyl.msc.supervise.entity.ClinicRecipe;
import com.shyl.msc.supervise.service.IClinicRecipeService;

@Service
@Transactional(readOnly=true)
public class ClinicRecipeService extends BaseService<ClinicRecipe, Long> implements IClinicRecipeService {

	private IClinicRecipeDao clinicRecipeDao;
	@Resource
	public void setClinicRecipeDao(IClinicRecipeDao clinicRecipeDao) {
		setBaseDao(clinicRecipeDao);
		this.clinicRecipeDao = clinicRecipeDao;
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryRecipeAndReg(String projectCode,PageRequest pageable, Integer sumType) {
		return clinicRecipeDao.queryRecipeAndReg(pageable,sumType);
	}

	@Override
	public List<Map<String, Object>> queryRecipeAndRegAll(String projectCode,PageRequest page, Integer type) {
		return clinicRecipeDao.queryRecipeAndRegAll(page,type);
	}

}
