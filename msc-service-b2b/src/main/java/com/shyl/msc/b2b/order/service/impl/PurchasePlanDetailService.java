package com.shyl.msc.b2b.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.IPurchasePlanDetailService;


/**
 * 订单计划Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class PurchasePlanDetailService extends BaseService<PurchasePlanDetail, Long> implements IPurchasePlanDetailService {
	
	private IPurchasePlanDetailDao purchasePlanDetailDao;
	
	public IPurchasePlanDetailDao getPurchasePlanDetailDao() {
		return purchasePlanDetailDao;
	}

	@Resource
	public void setPurchasePlanDetailDao(IPurchasePlanDetailDao purchasePlanDetailDao) {
		this.purchasePlanDetailDao = purchasePlanDetailDao;
		super.setBaseDao(purchasePlanDetailDao);
	}

	@Override
	public PurchasePlanDetail findByCode(String projectCode, String code) {
		return purchasePlanDetailDao.findByCode(code);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return purchasePlanDetailDao.pageByProductReport(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return purchasePlanDetailDao.pageByProductDetailReport(pageable);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return purchasePlanDetailDao.listByProductReport(pageable);
	}

	@Override
	public DataGrid<PurchasePlanDetail> queryByPurchasePlanAndCode(String projectCode, PageRequest pageable) {
		return purchasePlanDetailDao.queryByPurchasePlanAndCode(pageable);
	}


}
