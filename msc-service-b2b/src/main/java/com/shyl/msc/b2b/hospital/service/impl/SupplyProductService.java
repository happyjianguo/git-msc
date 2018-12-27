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
import com.shyl.msc.b2b.hospital.dao.ISupplyProductDao;
import com.shyl.msc.b2b.hospital.entity.SupplyProduct;
import com.shyl.msc.b2b.hospital.service.ISupplyProductService;

/**
 * 供应药品上报Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class SupplyProductService extends BaseService<SupplyProduct, Long> implements ISupplyProductService {
	private ISupplyProductDao supplyProductDao;

	public ISupplyProductDao getSupplyProductDao() {
		return supplyProductDao;
	}

	@Resource
	public void setSupplyProductDao(ISupplyProductDao supplyProductDao) {
		this.supplyProductDao = supplyProductDao;
		super.setBaseDao(supplyProductDao);
	}
	
	@Override
	public List<Map<String, Object>> query(@ProjectCodeFlag String projectCode, String startMonth, String toMonth, String hospitalCode) {
		return supplyProductDao.query(startMonth, toMonth, hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest page) {
		return supplyProductDao.queryByPage(page);
	}
}
