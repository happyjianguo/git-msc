package com.shyl.msc.b2b.stock.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stock.entity.HisStockDay;

public interface IHisStockDayDao extends IBaseDao<HisStockDay, Long> {
	/**
	 * 取得医院期初库存
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getHospitalBeginStock(String beginDate, String endDate);
	
	/**
	 * 取得医院期末库存
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getHospitalEndStock(String beginDate, String endDate);

	public HisStockDay getByDateAndProduct(String hospitalCode, String date, String productCode);
	
	/**
	 * 取得库存
	 * @param stockDate
	 * @param hospitalCode
	 * @param productCode
	 * @return
	 */
	public BigDecimal getStockAmt(String stockDate, String hospitalCode, String productCode);
	
	/**
	 * 取得库存
	 * @param stockDate
	 * @param hospitalCode
	 * @return
	 */
	public BigDecimal getStockAmt(String stockDate, String hospitalCode);
}
