package com.shyl.msc.supervise.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IMonthlyPurchaseDao;
import com.shyl.msc.supervise.entity.MonthlyPurchase;
import com.shyl.msc.supervise.service.IMonthlyPurchaseService;
/**
 * 医院药品月采购Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class MonthlyPurchaseService extends BaseService<MonthlyPurchase,Long> implements IMonthlyPurchaseService{
	private IMonthlyPurchaseDao monthlyPurchaseDao;

	public IMonthlyPurchaseDao getMonthlyPurchaseDao() {
		return monthlyPurchaseDao;
	}
	@Resource
	public void setMonthlyPurchaseDao(IMonthlyPurchaseDao monthlyPurchaseDao) {
		this.monthlyPurchaseDao = monthlyPurchaseDao;
		super.setBaseDao(monthlyPurchaseDao);
	}
	@Override
	public List<Map<String, Object>> query(String projectCode,PageRequest page) {
		return monthlyPurchaseDao.query(page);
	}
	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest page) {
		return monthlyPurchaseDao.queryByPage(page);
	}
	
}
