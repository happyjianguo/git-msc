package com.shyl.msc.dm.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.ISicknessProductDao;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.SicknessProduct;
import com.shyl.msc.dm.service.ISicknessProductService;

@Service
@Transactional(readOnly = true)
public class SicknessProductService extends BaseService<SicknessProduct, Long> implements ISicknessProductService {
	 
	private ISicknessProductDao sicknessProductDao;

	public ISicknessProductDao getSicknessProductDao() {
		return sicknessProductDao;
	}

	@Resource
	public void setSicknessMedicDao(ISicknessProductDao sicknessProductDao) {
		this.sicknessProductDao = sicknessProductDao;
		super.setBaseDao(sicknessProductDao);
	}

	@Override
	public List<Product> listProductBySicknessCode(String projectCode, String sicknessCode) {
		return sicknessProductDao.listProductBySicknessCode(sicknessCode);
	}

	@Override
	public SicknessProduct findByProductCode(String projectCode, String productCode) {
		return sicknessProductDao.findByProductCode(productCode);
	}

	@Override
	public DataGrid<Product> pageBySicknessCode(String projectCode, PageRequest pageable, String sicknessCode) {
		return sicknessProductDao.pageBySicknessCode(pageable, sicknessCode);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductWithSelected(String projectCode, PageRequest pageable, String sicknessCode) {
		return sicknessProductDao.pageByProductWithSelected(pageable, sicknessCode);
	}

	@Override
	public SicknessProduct findByKey(String projectCode, String sicknessCode, String productCode) {
		return sicknessProductDao.findByKey(sicknessCode, productCode);
	}	

}
