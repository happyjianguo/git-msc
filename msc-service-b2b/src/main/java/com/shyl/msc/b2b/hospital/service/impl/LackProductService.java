package com.shyl.msc.b2b.hospital.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.hospital.dao.ILackProductDao;
import com.shyl.msc.b2b.hospital.entity.LackProduct;
import com.shyl.msc.b2b.hospital.service.ILackProductService;

/**
 * 短缺药品上报Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class LackProductService extends BaseService<LackProduct, Long> implements ILackProductService {
	private ILackProductDao LackProductDao;

	public ILackProductDao getLackProductDao() {
		return LackProductDao;
	}

	@Resource
	public void setLackProductDao(ILackProductDao LackProductDao) {
		this.LackProductDao = LackProductDao;
		super.setBaseDao(LackProductDao);
	}
	
	@Override
	public LackProduct findUnique(@ProjectCodeFlag String projectCode, String month, String hospitalCode, Long productId) {
		return LackProductDao.findUnique(month, hospitalCode, productId);
	}
	
	@Override
	public List<Map<String, Object>> queryBy(@ProjectCodeFlag String projectCode, PageRequest page) {
		return LackProductDao.queryBy(page);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByCount(String projectCode, PageRequest page) {
		return LackProductDao.queryByCount(page);
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryByMx(String projectCode, PageRequest page) {
		return LackProductDao.queryByMx(page);
	}

}
