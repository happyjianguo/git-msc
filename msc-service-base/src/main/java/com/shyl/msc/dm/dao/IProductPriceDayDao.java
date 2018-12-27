package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductPriceDay;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格每日DAO接口
 * 
 * @author a_Q
 *
 */
public interface IProductPriceDayDao extends IBaseDao<ProductPriceDay, Long> {

	ProductPriceDay findByKey(String productCode, String vendorCode, TradeType tradeType, String hospitalCode, String priceDate);

	List<ProductPriceDay> getbyDate(String date, TradeType tradeType);

	List<ProductPriceDay> getbyDateAndHospital(String date);

	List<Map<String, Object>> listByProduct(String date, String productCode);

	List<Map<String, Object>> listByProductAndHospital(String date, String productCode, String hospitalCode);

}
