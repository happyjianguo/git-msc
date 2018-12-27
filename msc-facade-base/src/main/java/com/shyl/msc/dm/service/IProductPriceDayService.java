package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductPriceDay;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格每日Service接口
 * 
 * @author a_Q
 *
 */
public interface IProductPriceDayService extends IBaseService<ProductPriceDay, Long> {

	/**
	 * 根据key取出唯一一笔数据
	 * @param productCode
	 * @param vendorCode 
	 * @param tradeType
	 * @param hospitalCode
	 * @param priceDate
	 * @return
	 */
	ProductPriceDay findByKey(@ProjectCodeFlag String projectCode, String productCode, String vendorCode, TradeType tradeType, String hospitalCode, String priceDate);

	/**
	 * 设置价格批次作业
	 * @param productPriceDay 
	 */
	void setPriceTaskJob(@ProjectCodeFlag String projectCode, ProductPriceDay productPriceDay);


	/**
	 * 按照日期获取
	 * @param date
	 * @param tradeType
	 * @return
	 */
	List<ProductPriceDay> getbyDate(@ProjectCodeFlag String projectCode, String date, TradeType tradeType);

	/**
	 * 按照日期 获取指定医院 
	 * @param date
	 * @return
	 */
	List<ProductPriceDay> getbyDateAndHospital(@ProjectCodeFlag String projectCode, String date);

	/**
	 * 按照医院设定价格
	 * @param productPriceDay
	 */
	void setPriceForHospital(@ProjectCodeFlag String projectCode, ProductPriceDay productPriceDay);

	/**
	 * 设置加
	 * @param productPriceDay
	 */
	void setPriceToZero(@ProjectCodeFlag String projectCode, ProductPriceDay productPriceDay);

	/**
	 * 按照 日期 产品获取
	 * @param today 
	 * @param productCode
	 * @return
	 */
	List<Map<String, Object>> listByProduct(@ProjectCodeFlag String projectCode, String date, String productCode);
	
	/**
	 * 按照 日期 产品 指定医院 获取
	 * @param today
	 * @param productCode
	 * @param orgId
	 * @return
	 */
	List<Map<String, Object>> listByProductAndHospital(@ProjectCodeFlag String projectCode, String today, String productCode, String hospitalCode);

}
