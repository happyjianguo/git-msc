package com.shyl.msc.menu.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.IProductDataDao;
import com.shyl.msc.menu.entity.ProductData;

@Repository
public class ProductDataDao extends BaseDao<ProductData, Long> implements IProductDataDao {

	public ProductData getByExtId(Integer id, Integer type) {
		return this.getByHql("from ProductData where extId=? and type=?", id, type);
	}

}
