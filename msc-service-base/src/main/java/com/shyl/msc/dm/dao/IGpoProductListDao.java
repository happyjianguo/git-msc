package com.shyl.msc.dm.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.GpoProductList;

public interface IGpoProductListDao extends IBaseDao<GpoProductList, Long> {

	public GpoProductList findByCode(Long productId, String venderCode);
	
	public GpoProductList getPrice(Long productId);

}
