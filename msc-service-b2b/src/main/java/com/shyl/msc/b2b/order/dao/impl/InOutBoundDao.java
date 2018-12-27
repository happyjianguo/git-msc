package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IInOutBoundDao;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;

/**
 * 出入库单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class InOutBoundDao extends BaseDao<InOutBound, Long> implements IInOutBoundDao {

	@Override
	public List<InOutBound> listByIsPass(int isPass) {
		String hql = "from InOutBound i where i.isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public List<InOutBound> listByDate(String companyCode, String startDate, String endDate, boolean isGPO) {
		String hql = "";
		if (isGPO) {
			hql = "from InOutBound i where i.gpoCode=? and to_char(i.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(i.orderDate, 'yyyy-mm-dd hh24:mi:ss')<=? ";
		} else {
			hql = "from InOutBound i where i.vendorCode=? and to_char(i.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(i.orderDate, 'yyyy-mm-dd hh24:mi:ss')<=? ";
		}
		return listByHql(hql, null, companyCode, startDate, endDate);
	}

	@Override
	public Map<String, Object> getNumByDeliveryCode(String code) {
		String sql = "select sum(i.num) as num from t_order_inoutbound i where i.DELIVERYORDERCODE=?";
		return super.getBySql(sql, Map.class, code);
	}

	@Override
	public InOutBound getByInternalCode(String hospitalCode, String internalCode) {
		String hql = "from InOutBound io where io.hospitalCode=? and io.internalCode=?";
		return super.getByHql(hql, hospitalCode, internalCode);
	}

	@Override
	public InOutBound findByCode(String code) {
		String hql = "from InOutBound io where io.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public DataGrid<InOutBound> listByInOutBoundAndDetail(PageRequest pageable) {
		String sql = "select distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,t.vendorName,t.hospitalName,t.operator,t.gpoCode,t.num,t.sum,"
				+ " t.deliveryOrderCode from t_order_inoutbound t LEFT JOIN t_order_inoutbound_detail d ON t.id=d.inoutboundid  left join t_dm_product p on d.productCode=p.code ";
		DataGrid<InOutBound> result = findBySql(sql, pageable, Map.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql = sql + " where 1=1 " + hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}
}
