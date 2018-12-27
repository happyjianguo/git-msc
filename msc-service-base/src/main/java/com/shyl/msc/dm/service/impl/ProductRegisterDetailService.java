package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductRegisterDetailDao;
import com.shyl.msc.dm.entity.ProductRegisterDetail;
import com.shyl.msc.dm.service.IProductRegisterDetailService;

@Service
@Transactional(readOnly = true)
public class ProductRegisterDetailService extends BaseService<ProductRegisterDetail, Long> implements IProductRegisterDetailService {

	private IProductRegisterDetailDao productRegisterDetailDao;

	public IProductRegisterDetailDao getProductRegisterDetailDao() {
		return productRegisterDetailDao;
	}

	@Resource
	public void setProductRegisterDetailDao(IProductRegisterDetailDao productRegisterDetailDao) {
		this.productRegisterDetailDao = productRegisterDetailDao;
		super.setBaseDao(productRegisterDetailDao);
	}
	
}
