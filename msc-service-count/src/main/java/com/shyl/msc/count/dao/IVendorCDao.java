package com.shyl.msc.count.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.VendorC;

public interface IVendorCDao extends IBaseDao<VendorC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param vendorCode
	 * @return
	 */
	public VendorC getByKey(String month, String vendorCode);
	
	/**
	 * 根据库存标志查找对象列表
	 * @param stockFlag
	 * @param month
	 * @return
	 */
	public List<VendorC> listByStockFlag(int stockFlag, String month);
}
