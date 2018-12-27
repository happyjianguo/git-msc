package com.shyl.msc.dm.dao.impl;

import org.springframework.stereotype.Repository;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IGpoProductListDao;
import com.shyl.msc.dm.entity.GpoProductList;

@Repository
public class GpoProductListDao extends BaseDao<GpoProductList, Long> implements IGpoProductListDao {

	public GpoProductList findByCode(Long productId, String venderCode) {
		return this.getByHql("from GpoProductList where product.id=? and vendorCode=?", productId, venderCode);
	}

	@Override
	public GpoProductList getPrice(Long productId) {
		String hql = "from GpoProductList where product.id=? and rownum=1";
		return this.getByHql(hql, productId);
	}

}
