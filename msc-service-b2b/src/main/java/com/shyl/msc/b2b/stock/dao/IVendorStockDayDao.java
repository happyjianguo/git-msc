package com.shyl.msc.b2b.stock.dao;

import java.math.BigDecimal;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stock.entity.VendorStockDay;

public interface IVendorStockDayDao extends IBaseDao<VendorStockDay, Long> {

	public VendorStockDay getByDateAndProduct(String vendorCode, String stockDate, String productCode);
	/**
	 * 取得库存
	 * @param stockDate
	 * @param vendorCode
	 * @param productCode
	 * @return
	 */
	public BigDecimal getStockAmt(String stockDate, String vendorCode, String productCode);
	
	/**
	 * 取得库存
	 * @param stockDate
	 * @param vendorCode
	 * @return
	 */
	public BigDecimal getStockAmt(String stockDate, String vendorCode);
}
