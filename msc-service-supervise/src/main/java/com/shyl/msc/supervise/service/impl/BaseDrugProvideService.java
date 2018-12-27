package com.shyl.msc.supervise.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IBaseDrugProvideDao;
import com.shyl.msc.supervise.entity.BaseDrugProvide;
import com.shyl.msc.supervise.service.IBaseDrugProvideService;

/**
 * 医院基本药物配备使用比例Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class BaseDrugProvideService extends BaseService<BaseDrugProvide, Long> implements IBaseDrugProvideService{
	private IBaseDrugProvideDao baseDrugProvideDao;
	
	public IBaseDrugProvideDao getBaseDrugProvideDao() {
		return baseDrugProvideDao;
	}
	@Resource
	public void setBaseDrugProvideDao(IBaseDrugProvideDao baseDrugProvideDao) {
		this.baseDrugProvideDao = baseDrugProvideDao;
		super.setBaseDao(baseDrugProvideDao);
	}

	@Override
	public BaseDrugProvide findUnique(String projectCode, String month, String hospitalCode) {
		return baseDrugProvideDao.findUnique(month,hospitalCode);
	}
	/**
	 * 根据 区 ,医院 和月份逐步钻取
	 */
	@Override
	public DataGrid<Map<String, Object>> queryProvideByPage(String projectCode, PageRequest page, Integer queryType) {
		switch (queryType) {
		case 0:
			return baseDrugProvideDao.groupByCounty(page);
		case 1:
			return baseDrugProvideDao.groupByHospital(page);
		case 2:
			return baseDrugProvideDao.groupByMonth(page);
		default:
			return new DataGrid<>();
		}
	}

}
