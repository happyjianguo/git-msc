package com.shyl.msc.count.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IProductCDao;
import com.shyl.msc.count.entity.ProductC;

@Repository
public class ProductCDao extends BaseDao<ProductC, Long> implements IProductCDao {

	@Override
	public ProductC getByKey(String month, Long productId){
		return super.getByHql("from ProductC where month=? and product.id=? "
				, month, productId);
	}
	


	@Override
	public Map<String, Object> countByMonth(String month) {
		String sql = "select count(id) as COUNT from t_count_product_c where month = ?";
		return super.getBySql(sql, Map.class, month);
	}
	
	@Override
	public List<Map<String, Object>> reportCTrade(String year) {
		String sql = "select month as MONTH,sum(purchaseSum) as ORDERSUM "
				+ " from t_count_product_c ap"
				+ " where month like ? "
				+ " group by month order by month";	
		return super.listBySql(sql,null, Map.class, year+"%");
	}
	
	@Override
	public DataGrid<Map<String, Object>> reportCGoodsTrade(String dateS, String dateE, PageRequest pageable) {
		String sql = "select g.id,g.code,g.name,g.dosageFormName,g.model,g.producerName,"
				+ "sum(ap.purchaseNum) as ORDERNUM,sum(ap.purchaseSum) as ORDERSUM  "
				+ "from t_count_product_c ap left join t_dm_product g on ap.productId = g.id "
				+ " where ap.month>=? and ap.month<=? and ap.purchaseSum!=0"
				+ " group by g.id,g.code,g.name,g.dosageFormName,g.model,g.producerName ";	
		return super.findBySql(sql,pageable, Map.class, dateS, dateE);
	}
}
