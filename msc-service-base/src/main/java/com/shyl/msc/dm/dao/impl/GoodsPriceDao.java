package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IGoodsPriceDao;
import com.shyl.msc.dm.entity.GoodsPrice;

@Repository
public class GoodsPriceDao extends BaseDao<GoodsPrice, Long> implements IGoodsPriceDao {

	@Override
	public List<GoodsPrice> listByGoods(Long goodsId) {
		String hql = "from GoodsPrice gp where gp.goodsId=? and gp.isDisabled=0 and gp.isDisabledByH=0 order by gp.productCode ";
		return super.listByHql(hql, null, goodsId);
	}

	@Override
	public GoodsPrice getByProductCodeAndVender(String productCode, String vendorCode, String hospitalCode) {
		String hql = "from GoodsPrice gp where gp.productCode=? and gp.vendorCode=? and gp.hospitalCode=?  and gp.isDisabled=0 and gp.isDisabledByH=0";
		return super.getByHql(hql, productCode, vendorCode, hospitalCode);
	}

	@Override
	public List<GoodsPrice> findByProduct(String productCode, String vendorCode) {
		String hql = "from GoodsPrice gp where gp.productCode=? and gp.vendorCode=?  and gp.isDisabledByH=0";
		return super.listByHql(hql, null, productCode, vendorCode);
	}

	@Override
	public GoodsPrice findByKey(String productCode, String vendorCode,  String hospitalCode,Integer isDisabled,Integer isDisabledByH,Integer isFormContract) {
		String hql = "from GoodsPrice gp where gp.productCode=? and gp.vendorCode=?  and gp.hospitalCode=? and gp.isFormContract=?  ";
		if(isDisabled != null){
			hql += "and gp.isDisabled="+isDisabled;
		}
		if(isDisabledByH != null){
			hql += "and gp.isDisabledByH="+isDisabledByH;
		}
		return super.getByHql(hql, productCode, vendorCode,hospitalCode,isFormContract);
	}
	
	@Override
	public List<GoodsPrice> listByHospital(String hospitalCode, String scgxsj) {
		String hql = "from GoodsPrice gp where gp.hospitalCode=? and to_char(gp.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?";
		return super.listByHql(hql, null, hospitalCode, scgxsj);
	}

	@Override
	public List<Map<String, Object>> getVendorList(String hospitalCode) {
		String sql = "select distinct t.vendorCode as id, c.fullName as NAME from t_dm_goods_price t "
				+ " left join t_set_company c on t.vendorCode = c.CODE "
				+ "where t.hospitalCode=? and t.isDisabled=0 and isDisabledByH=0 ";
		return super.listBySql(sql, null, Map.class, hospitalCode);
	}

	@Override
	public List<GoodsPrice> listByIsFormContract() {
		String hql = "from GoodsPrice gp where gp.isFormContract =1";
		return super.listByHql(hql, null);
	}

	@Override
	public List<GoodsPrice> listByProductCode(String productCode) {
		String hql = "from GoodsPrice gp where gp.productCode=? ";
		return super.listByHql(hql, null, productCode);
	}
}
