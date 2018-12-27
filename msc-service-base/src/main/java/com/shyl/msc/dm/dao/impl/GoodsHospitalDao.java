package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import com.shyl.msc.dm.entity.Product;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IGoodsHospitalDao;
import com.shyl.msc.dm.entity.GoodsHospital;
/**
 * 
 * 
 * @author a_Q
 *
 */
@Repository
public class GoodsHospitalDao extends BaseDao<GoodsHospital, Long> implements IGoodsHospitalDao {

	@Override
	public GoodsHospital getByGoodsId(Long id) {
		String hql = "from GoodsHospital gh where gh.goodsId=?";
		return super.getByHql(hql, id);
	}

	@Override
	public GoodsHospital getByInternalCode(String hospitalCode, String internalCode) {
		return super.getByHql("from GoodsHospital gh where gh.hospitalCode=? and gh.internalCode=?", hospitalCode, internalCode);
	}

	@Override
	public GoodsHospital getByProductCode(String hospitalCode, String productCode) {
		return super.getByHql("from GoodsHospital gh where gh.hospitalCode=? and gh.productCode=?", hospitalCode, productCode);
	}

	@Override
	public List<Map<String, Object>> listByDate(String scgxsj) {
		String hql = "select gh.*,p.isDisabled from t_dm_goods_hospital gh"
				+ " left join t_dm_product p on gh.productId=p.id"
				+ " where to_char(gh.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?";
		return super.listBySql(hql, null, Map.class, scgxsj);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {
		String sql = "select t.*,p.isGPOPurchase from t_dm_goods_hospital t left join t_dm_product p on t.productId=p.id";
		return super.findBySql(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> queryByAll(PageRequest page) {
		String sql = "select t.*,p.isGPOPurchase from t_dm_goods_hospital t left join t_dm_product p on t.productId=p.id";
		return super.listBySql2(sql, page, Map.class);
	}

}
