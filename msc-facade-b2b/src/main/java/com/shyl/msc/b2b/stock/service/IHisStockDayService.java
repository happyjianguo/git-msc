package com.shyl.msc.b2b.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stock.entity.HisStockDay;
import com.shyl.msc.set.entity.Hospital;

public interface IHisStockDayService extends IBaseService<HisStockDay, Long> {
	/**
	 * 取得医院库存周转率
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	 List<Map<String, Object>> getHospitalTurnoverRatio(@ProjectCodeFlag String projectCode, String beginDate, String endDate);

	 
	public HisStockDay getByDateAndProduct(@ProjectCodeFlag String projectCode, String hospitalCode, String date, String productCode);


	public void saveHisStock(@ProjectCodeFlag String projectCode, List<JSONObject> list, String kcrq, Hospital hospital, Long datagramId, String ptdm);

	/**
	 * 取得库存
	 * @param projectCode
	 * @param stockDate
	 * @param hospitalCode
	 * @param productCode
	 * @return
	 */
	public BigDecimal getStockAmt(@ProjectCodeFlag String projectCode, String stockDate, String hospitalCode, String productCode);
	
	/**
	 * 取得库存
	 * @param projectCode
	 * @param stockDate
	 * @param hospitalCode
	 * @return
	 */
	public BigDecimal getStockAmt(@ProjectCodeFlag String projectCode, String stockDate, String hospitalCode);
}
