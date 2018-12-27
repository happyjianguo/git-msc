package com.shyl.msc.supervise.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IClinicRecipeItemDao;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;
import com.shyl.msc.supervise.service.IClinicRecipeItemService;

@SuppressWarnings("unused")
@Service
@Transactional(readOnly=true)
public class ClinicRecipeItemService extends BaseService<ClinicRecipeItem, Long> implements IClinicRecipeItemService {

	private IClinicRecipeItemDao clinicRecipeItemDao;
	@Resource
	public void setClinicRecipeItemDao(IClinicRecipeItemDao clinicRecipeItemDao) {
		setBaseDao(clinicRecipeItemDao);
		this.clinicRecipeItemDao = clinicRecipeItemDao;
	}
	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode,PageRequest page) {
		return clinicRecipeItemDao.queryByPage(page);
	}
	
}
