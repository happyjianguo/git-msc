package com.shyl.msc.dm.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.entity.ProductPriceRecord.Type;
import com.shyl.msc.dm.dao.IProductPriceRecordDao;

@Repository
public class ProductPriceRecordDao extends BaseDao<ProductPriceRecord,Long> implements IProductPriceRecordDao{

	@Override
	public DataGrid<Map<String, Object>> getIdsForProductPriceMdf(PageRequest pageable) {
		String sql = "select t.PRODUCTCODE,max(t.id) as id from t_dm_product_price_record t  where t.type = "+Type.gpo.ordinal()+" group by t.PRODUCTCODE order by t.PRODUCTCODE ";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> getReportForProductPriceMdf(String ids, PageRequest pageable) {
		String sql = "select p.code,p.name,p.dosageFormName,p.model,p.packDesc,p.producerName,p.standardCode,p.nationalCode,ppr.finalPrice "
				+ "from t_dm_product_price_record ppr left join t_dm_product p on ppr.productCode = p.code  "
				+ "where ppr.id in "+ids+"  ";
		return super.findBySql(sql, pageable, Map.class);
	}

}
