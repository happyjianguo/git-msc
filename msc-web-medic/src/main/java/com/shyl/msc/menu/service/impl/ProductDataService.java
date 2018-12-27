package com.shyl.msc.menu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.IProductDataDao;
import com.shyl.msc.menu.entity.ProductData;
import com.shyl.msc.menu.service.IProductDataService;

@Service
@Transactional
public class ProductDataService extends BaseService<ProductData, Long> implements IProductDataService {

	private IProductDataDao productDataDao;

	public IProductDataDao getProductDataDao() {
		return productDataDao;
	}

	@Resource
	public void setProductDataDao(IProductDataDao productDataDao) {
		super.setBaseDao(productDataDao);
		this.productDataDao = productDataDao;
	}

	public ProductData getByExtId(Integer id, Integer type) {
		return productDataDao.getByExtId(id, type);
	}

}
