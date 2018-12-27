package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IProductPriceDayDao;
import com.shyl.msc.dm.entity.ProductPriceDay;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
@Transactional(readOnly = true)
public class ProductPriceDayDao extends BaseDao<ProductPriceDay, Long> implements IProductPriceDayDao {

	@Override
	public ProductPriceDay findByKey(String productCode, String vendorCode, TradeType tradeType, String hospitalCode, String priceDate) {
		
		String hql = "from ProductPriceDay where productCode=? and vendorCode=?  and tradeType=? and priceDate=? ";
		if(hospitalCode != null){
			hql += " and hospitalCode='"+hospitalCode+"'";
		}else{
			hql += " and hospitalCode is null ";
		}
		return super.getByHql(hql, productCode,vendorCode, tradeType, priceDate);
	}

	@Override
	public List<ProductPriceDay> getbyDate(String date, TradeType tradeType) {
		String hql = "from ProductPriceDay where priceDate =? and tradeType=? and hospitalCode is null ";
		return super.listByHql(hql, null,date, tradeType);
	}

	@Override
	public List<ProductPriceDay> getbyDateAndHospital(String date) {
		String hql = "from ProductPriceDay where priceDate =? and tradeType=? and hospitalCode is not null ";
		return super.listByHql(hql, null,date, TradeType.hospital);
	}

	@Override
	public List<Map<String, Object>> listByProduct(String date, String productCode) {
		String sql = "select * from t_dm_product_price_day  where priceDate =? and tradeType=1 and productCode=? and hospitalCode is null and isDisabled=0  ";
		return super.listBySql(sql, null, Map.class, date,productCode);
	}

	@Override
	public List<Map<String, Object>> listByProductAndHospital(String date, String productCode, String hospitalCode) {
		String sql = "select * from t_dm_product_price_day  where priceDate =? and tradeType=1 and productCode=? and hospitalCode =? and isDisabled=0 ";
		return super.listBySql(sql, null, Map.class, date,productCode,hospitalCode);
	}

}
