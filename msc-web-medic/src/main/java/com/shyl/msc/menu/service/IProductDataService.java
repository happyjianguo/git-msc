package com.shyl.msc.menu.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.menu.entity.ProductData;

public interface IProductDataService extends IBaseService<ProductData, Long> {

	public ProductData getByExtId(Integer id, Integer type);
	
}
