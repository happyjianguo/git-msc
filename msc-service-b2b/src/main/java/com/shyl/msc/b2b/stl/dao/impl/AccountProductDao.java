package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.IAccountProductDao;
import com.shyl.msc.b2b.stl.entity.AccountProduct;

/**
 * 产品账务
 * 
 * @author a_Q
 *
 */
@Repository
public class AccountProductDao extends BaseDao<AccountProduct, Long> implements IAccountProductDao {

	@Override
	public AccountProduct getByCode(String month, String code) {
		String hql = "from AccountProduct a where a.month=? and a.productCode=?";
		return super.getByHql(hql, month, code);
	}

	@Override
	public List<AccountProduct> getbyMonthAndCode(String key) {
		String sql = "select * from t_stl_account_product t where t.month||t.productCode in ("+ key + ")";
		return this.listBySql(sql,null,AccountProduct.class);
	}

	@Override
	public DataGrid<Map<String, Object>> reportHospitalSB(String name, String dataS, String dataE,
			PageRequest pageable) {
		return null;
//		String sql = "select * "
//				+ "from t_stl_account_product a left join t_dm_goods g on a.productCode = g.code"
//				+ " where a.productCode=?";
//		return super.findBySql(sql, pageable, Map.class, name,dataS,dataE);
	}

	@Override
	public List<Map<String, Object>> reportTrade(String year) {
		String sql = "select month as MONTH,sum(orderSum) as ORDERSUM "
				+ " from t_stl_account_product ap"
				+ " where month like ? "
				+ " group by month order by month";	
		return super.listBySql(sql,null, Map.class, year+"%");
	}

	@Override
	public DataGrid<Map<String, Object>> reportGoodsTrade(String dateS, String dateE, PageRequest pageable) {
		String sql = "select g.id,g.code,g.name,g.dosageFormName,g.model,g.producerName,"
				+ "sum(ap.orderNum) as ORDERNUM,sum(ap.orderSum) as ORDERSUM  "
				+ "from t_stl_account_product ap left join t_dm_product g on ap.productCode = g.Code "
				+ " where ap.month>=? and ap.month<=? and ap.orderSum!=0"
				+ " group by g.id,g.code,g.name,g.dosageFormName,g.model,g.producerName ";	
		return super.findBySql(sql,pageable, Map.class, dateS, dateE);
	}
	
}
