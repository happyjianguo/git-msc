package com.shyl.msc.b2b.order.dao.impl;


import java.util.List;
import java.util.Map;

import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
/**
 * 订单计划DAO实现
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchasePlanDao extends BaseDao<PurchasePlan, Long> implements IPurchasePlanDao {

	@Override
	public PurchasePlan findByCode(String purchasePlanCode) {
		String hql = "from PurchasePlan t where t.code = ?";
		return super.getByHql(hql, purchasePlanCode);
	}

	@Override
	public DataGrid<PurchasePlan> listByPurchasePlanAndDetail(PageRequest pageable) {
		String sql = "SELECT distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,to_char(t.requireDate,'yyyy-MM-dd HH24:mi:ss') as requireDate,t.hospitalName,t.warehouseName,"
				+" t.num,t.sum,t.urgencyLevel from t_order_purchaseplan t LEFT JOIN "
				+" t_order_purchaseplan_detail d ON t.id=d.purchasePlanId  left join t_dm_product p on d.productCode=p.code ";
		DataGrid<PurchasePlan> result = findBySql(sql, pageable, Map.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql = sql + " where 1=1 " + hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}

	@Override
	public List<PurchasePlan> queryByPlanCode() {
		String hql = "from PurchasePlan t where not exists (select 1 from PurchaseOrderPlan p where t.code = p.purchasePlanCode)";
		return super.listByHql(hql,null);
	}

	@Override
	public List<PurchasePlanDetail> getDetailById(Long purchasePlanId) {
		String hql = "from PurchasePlanDetail t where t.purchasePlan.id = ?";
		return listByHql(hql,null,purchasePlanId);
	}
}
