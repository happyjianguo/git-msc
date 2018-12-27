package com.shyl.msc.count.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IVendorCDao;
import com.shyl.msc.count.entity.VendorC;


@Repository
public class VendorCDao extends BaseDao<VendorC, Long> implements IVendorCDao {

	@Override
	public VendorC getByKey(String month, String vendorCode){
		return super.getByHql("from VendorC where month=?  "
				+ " and vendorCode=?", month, vendorCode);
	}
	
	@Override
	public List<VendorC> listByStockFlag(int stockFlag, String month) {
		String hql = "from VendorC where stockFlag=? and month<?";
		return super.listByHql(hql, null, stockFlag, month);
	}
}
