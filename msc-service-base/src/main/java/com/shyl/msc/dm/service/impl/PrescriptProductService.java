package com.shyl.msc.dm.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IPrescriptProductDao;
import com.shyl.msc.dm.entity.PrescriptProduct;
import com.shyl.msc.dm.service.IPrescriptProductService;

@Service
@Transactional(readOnly = true)
public class PrescriptProductService extends BaseService<PrescriptProduct, Long> implements IPrescriptProductService {
	 
	private IPrescriptProductDao prescriptProductDao;

	public IPrescriptProductDao getSicknessProductDao() {
		return prescriptProductDao;
	}

	@Resource
	public void setSicknessMedicDao(IPrescriptProductDao prescriptProductDao) {
		this.prescriptProductDao = prescriptProductDao;
		super.setBaseDao(prescriptProductDao);
	}

	@Override
	public List<PrescriptProduct> listProductByModifyDate(String projectCode, String modifyDate) {
		return prescriptProductDao.listProductByModifyDate(modifyDate);
	}

	@Override
	public PrescriptProduct findByProductId(String projectCode, Long productId) {
		return prescriptProductDao.findByProductId(productId);
	}
	
	@Override
	public DataGrid<Map<String, Object>> pageByProductWithSelected(String projectCode, PageRequest pageable) {
		return prescriptProductDao.pageByProductWithSelected(pageable);
	}


}
