package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.dao.IProductPriceRecordHisDao;

@Repository
public class ProductPriceRecordHisDao extends BaseDao<ProductPriceRecordHis,Long> implements IProductPriceRecordHisDao{

	@Override
	public Map<String, Object> getReportForProductPriceMdf(String productCode) {
		String sql = "select max(id) from t_dm_product_price_record_his where id not in (select max(id) from t_dm_product_price_record_his where   productcode = ?) and productcode = ?";
		sql = "select finalPrice from t_dm_product_price_record_his where id in ("+sql+")";
		return super.getBySql(sql, Map.class, productCode,productCode);
	}
	
	@Override
	public List<ProductPriceRecordHis> listByVendorAndDate(String vendorCode, String productCode, String startDate, String endDate) {
		String hql = " from ProductPriceRecordHis "
				+ "where vendorCode=? and productCode=? and to_char(createDate,'yyyy-MM-dd')>=? and to_char(createDate,'yyyy-MM-dd')<=?"
				+ "order by productCode,createDate";
		return super.list(hql, null, vendorCode, productCode, startDate, endDate);
	}
}
