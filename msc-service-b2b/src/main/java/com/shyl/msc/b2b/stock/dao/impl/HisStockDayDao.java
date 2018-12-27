package com.shyl.msc.b2b.stock.dao.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stock.dao.IHisStockDayDao;
import com.shyl.msc.b2b.stock.entity.HisStockDay;

@Repository
public class HisStockDayDao extends BaseDao<HisStockDay, Long> implements IHisStockDayDao {
	@Override
	public List<Map<String, Object>> getHospitalBeginStock(String beginDate, String endDate) {
		String sql = "select hospitalcode, min(hospitalName) as hospitalName, sum(beginamt) as beginamt "
				+ "from t_stock_his_stock_day where (hospitalcode,stockdate) in "
				+ "(select hospitalcode,min(stockdate) from t_stock_his_stock_day "
				+ "where stockDate>=? and stockDate<=? group by hospitalcode) group by hospitalcode";
		return super.listBySql(sql, null, Map.class,beginDate,endDate);
	} 

	@Override
	public List<Map<String, Object>> getHospitalEndStock(String beginDate, String endDate) {
		String sql = "select hospitalcode, min(hospitalName) as hospitalName, sum(endamt) as endamt "
				+ "from t_stock_his_stock_day where (hospitalcode,stockdate) in "
				+ "(select hospitalcode,max(stockdate) from t_stock_his_stock_day "
				+ "where stockDate>=? and stockDate<=? group by hospitalcode) group by hospitalcode";
		return super.listBySql(sql, null, Map.class,beginDate,endDate);
	}

	@Override
	public HisStockDay getByDateAndProduct(String hospitalCode, String date, String productCode) {
		String hql = "from HisStockDay h where h.hospitalCode=? and h.stockDate=? and h.productCode=?";
		return super.getByHql(hql, hospitalCode, date, productCode);
	}
	
	@Override
	public BigDecimal getStockAmt(String stockDate, String hospitalCode, String productCode) {
		String hql = "select endAmt from HisStockDay where stockDate=? and hospitalCode=? and productCode=?";
		Map<String, Object> map = super.getObject(hql, Map.class, stockDate, hospitalCode, productCode);
		return map==null||map.get("ENDAMT")==null?new BigDecimal(0):new BigDecimal(map.get("ENDAMT").toString());
	}
	
	@Override
	public BigDecimal getStockAmt(String stockDate, String hospitalCode) {
		String hql = "select sum(endAmt) as endAmt from HisStockDay where stockDate=? and hospitalCode=?";
		Map<String, Object> map = super.getObject(hql, Map.class, stockDate, hospitalCode);
		return map==null||map.get("ENDAMT")==null?new BigDecimal(0):new BigDecimal(map.get("ENDAMT").toString());
	}
}
