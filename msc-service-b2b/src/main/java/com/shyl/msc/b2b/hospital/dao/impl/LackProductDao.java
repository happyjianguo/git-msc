package com.shyl.msc.b2b.hospital.dao.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.hospital.dao.ILackProductDao;
import com.shyl.msc.b2b.hospital.entity.LackProduct;
/**
 * 短缺药品上报DAO实现类
 * 
 *
 */
@Repository
public class LackProductDao extends BaseDao<LackProduct, Long> implements ILackProductDao {

	@Override
	public LackProduct findUnique(String month, String hospitalCode, Long productId) {
		String hql = "from LackProduct where month=? and hospitalCode=? and product.id=?";
		return super.getByHql(hql, month, hospitalCode, productId);
	}
	
	@Override
	public List<Map<String, Object>> queryBy(PageRequest page) {
		String sql = "select a.month,b.code,count(*) as num,max(b.name) as name,max(b.dosageFormName) as dosageFormName,"
				+ " max(b.model) as model,max(b.packDesc) as packDesc,max(b.producerName) as producerName,max(b.ISGPOPURCHASE) as ISGPOPURCHASE from t_hospital_lackproduct a,t_dm_product b where a.productid=b.id group by b.code,a.month";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByCount(PageRequest page) {
		String sql = "select b.code,count(*) as num,max(b.name) as name,max(b.dosageFormName) as dosageFormName,"
				+ " max(b.model) as model,max(b.packDesc) as packDesc,max(b.producerName) as producerName,max(b.ISGPOPURCHASE) as ISGPOPURCHASE from t_hospital_lackproduct a,t_dm_product b where a.productid=b.id group by b.code";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByMx(PageRequest page) {
		String sql = "select a.hospitalCode,max(a.hospitalName)as hospitalName,max(a.reason) as reason,a.month from t_hospital_lackproduct a,t_dm_product b where a.productid=b.id group by a.hospitalCode,a.month";
		return this.findBySql(sql, page, Map.class);
	}
}
