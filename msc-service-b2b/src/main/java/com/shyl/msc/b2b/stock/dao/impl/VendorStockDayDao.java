package com.shyl.msc.b2b.stock.dao.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stock.dao.IVendorStockDayDao;
import com.shyl.msc.b2b.stock.entity.VendorStockDay;

@Repository
public class VendorStockDayDao extends BaseDao<VendorStockDay, Long> implements IVendorStockDayDao {

	@Override
	public VendorStockDay getByDateAndProduct(String vendorCode, String stockDate, String productCode) {
		String hql = "from VendorStockDay vsd where vsd.vendorCode=? and vsd.stockDate=? and vsd.productCode=?";
		return super.getByHql(hql, vendorCode, stockDate, productCode);
	}
	
	@Override
	public BigDecimal getStockAmt(String stockDate, String vendorCode, String productCode) {
		String hql = "select endAmt from VendorStockDay where stockDate=? and vendorCode=? and productCode=?";
		Map<String, Object> map = super.getObject(hql, Map.class, stockDate, vendorCode, productCode);
		return map==null||map.get("ENDAMT")==null?new BigDecimal(0):new BigDecimal(map.get("ENDAMT").toString());
	}
	
	@Override
	public BigDecimal getStockAmt(String stockDate, String vendorCode) {
		String hql = "select sum(endAmt) as endAmt from VendorStockDay where stockDate=? and vendorCode=?";
		Map<String, Object> map = super.getObject(hql, Map.class, stockDate, vendorCode);
		return map==null||map.get("ENDAMT")==null?new BigDecimal(0):new BigDecimal(map.get("ENDAMT").toString());
	}
}
