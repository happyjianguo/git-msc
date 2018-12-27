package com.shyl.msc.menu.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.ProductData;

public interface IProductDataDao extends IBaseDao<ProductData, Long> {

	public ProductData getByExtId(Integer id, Integer type);
}
