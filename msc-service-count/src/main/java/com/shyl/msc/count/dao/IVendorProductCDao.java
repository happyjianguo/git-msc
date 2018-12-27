package com.shyl.msc.count.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.VendorProductC;

public interface IVendorProductCDao extends IBaseDao<VendorProductC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param productId
	 * @param vendorCode
	 * @return
	 */
	public VendorProductC getByKey(String month, Long productId, String hospitalCode);
	
	/**
	 * 根据库存标志查找对象列表
	 * @param stockFlag
	 * @param month
	 * @return
	 */
	public List<VendorProductC> listByStockFlag(int stockFlag, String month);
}
