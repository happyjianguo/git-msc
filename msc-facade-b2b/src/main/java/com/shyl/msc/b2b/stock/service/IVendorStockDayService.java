package com.shyl.msc.b2b.stock.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stock.entity.VendorStockDay;
import com.shyl.msc.set.entity.Company;

public interface IVendorStockDayService extends IBaseService<VendorStockDay, Long> {

	public void saveVendorStock(@ProjectCodeFlag String projectCode, List<JSONObject> list, String kcrq, Company vendor, Long datagramId);
	/**
	 * 取得库存
	 * @param projectCode
	 * @param stockDate
	 * @param vendorCode
	 * @param productCode
	 * @return
	 */
	public BigDecimal getStockAmt(@ProjectCodeFlag String projectCode, String stockDate, String vendorCode, String productCode);
	
	/**
	 * 取得库存
	 * @param projectCode
	 * @param stockDate
	 * @param vendorCode
	 * @return
	 */
	public BigDecimal getStockAmt(@ProjectCodeFlag String projectCode, String stockDate, String vendorCode);
}
