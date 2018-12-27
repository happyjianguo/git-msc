package com.shyl.msc.supervise.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IBaseDrugProgressDao;
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.entity.BaseDrugProvide;
import com.shyl.msc.supervise.service.IBaseDrugProgressService;

/**
 * 实施基本药物制度进展Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class BaseDrugProgressService extends BaseService<BaseDrugProgress,Long> implements IBaseDrugProgressService{
	private IBaseDrugProgressDao baseDrugProgressDao ;

	public IBaseDrugProgressDao getBaseDrugProgressDao() {
		return baseDrugProgressDao;
	}
	@Resource
	public void setBaseDrugProgressDao(IBaseDrugProgressDao baseDrugProgressDao) {
		this.baseDrugProgressDao = baseDrugProgressDao;
		super.setBaseDao(baseDrugProgressDao);
	}
	@Override
	public BaseDrugProgress findUnique(String projectCode, String month, String hospitalCode) {
		return baseDrugProgressDao.findUnique(month, hospitalCode);
	}
	@Override
	public BaseDrugProgress findByType(String projectCode, Integer healthStationType) {
		return baseDrugProgressDao.findByType(healthStationType);
	}
	/**
	 * 根据 区 ,医院 和月份逐步钻取
	 */
	@Override
	public DataGrid<Map<String, Object>> queryProgressByPage(String projectCode,PageRequest page, Integer queryType) {
		switch (queryType) {
		case 0:
			return baseDrugProgressDao.groupByCity(page);
		case 1:
			return baseDrugProgressDao.groupByCounty(page);
		case 2:
			return baseDrugProgressDao.groupByHealth(page);
		default:
			return new DataGrid<>();
		}
	}
	
	
}
