package com.shyl.msc.b2b.order.dao.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.ICartContractDao;
import com.shyl.msc.b2b.order.entity.CartContract;
/**
 * 购物车DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class CartContractDao extends BaseDao<CartContract, Long> implements ICartContractDao {

	@Override
	public CartContract findByHopitalAndGoods(String hospitalCode, Long contractDId) {
		String hql = "from CartContract t where t.hospitalCode=? and t.contractDetailId=? ";
		return super.getByHql(hql, hospitalCode,contractDId);
	}

	@Override
	public List<CartContract> listByHospital(String hospitalCode) {
		String hql = "from CartContract where hospitalCode=?";
		return super.listByHql(hql, null, hospitalCode);
	}

	@Override
	public List<Map<String, Object>> vendorList(String hospitalCode) {
		String sql = "select d.vendorCode||'_'||d.gpoCode as CODE,d.vendorName||'_'||d.gpoName as NAME from t_order_cartContract a "
				+ " left join t_plan_contract_detail b on a.contractDetailId = b.id "
				+ " left join t_plan_contract d on b.contractId = d.id "
				+ " where a.hospitalCode = ? "
				+ " group by d.vendorCode,d.vendorName,d.gpoCode,d.gpoName";
		return super.listBySql(sql, null, Map.class, hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByHospital(String hospitalCode, PageRequest pageable) {
		String sql = "select a.id as CARTID,d.endValidDate,c.*,a.num,b.price,b.contractNum,b.cartNum,b.purchasePlanNum from t_order_cartContract a "
				+ " left join t_plan_contract_detail b on a.contractDetailId = b.id "
				+ " left join t_dm_product c on b.productId = c.id "
				+ " left join t_plan_contract d on b.contractId = d.id "
				+ " where a.hospitalCode = ? "
				+ " order by c.code ";
		return super.findBySql(sql, pageable, Map.class, hospitalCode);
	}

	@Override
	public Map<String, Object> getTotal(String orgCode) {
		String sql = "select count(a.id) as COUNT,sum(a.num) as NUM,sum(a.num * b.price) as SUM "
				+ "	from t_order_cartContract a "
				+ " left join t_plan_contract_detail b on a.contractDetailId = b.id "
				+ " where a.hospitalCode = ? "
				+ " group by a.hospitalCode ";
		return super.getBySql(sql, Map.class, orgCode);
	}


}
