package com.shyl.msc.count.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IVendorProductCDao;
import com.shyl.msc.count.entity.VendorProductC;

@Repository
public class VendorProductCDao extends BaseDao<VendorProductC, Long> implements IVendorProductCDao {

	@Override
	public VendorProductC getByKey(String month, Long productId, String vendorCode){
		return super.getByHql("from VendorProductC where month=? and product.id=? "
				+ "and vendorCode=? ", month, productId, vendorCode);
	}
	
	@Override
	public List<VendorProductC> listByStockFlag(int stockFlag, String month) {
		String hql = "from VendorProductC where stockFlag=? and month<?";
		return super.listByHql(hql, null, stockFlag, month);
	}
}
