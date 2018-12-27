package com.shyl.msc.b2b.order.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDao;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;

/**
 * 退货申请单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ReturnsRequestDao extends BaseDao<ReturnsRequest, Long> implements IReturnsRequestDao {

	@Override
	public List<ReturnsRequest> listByDate(String vendorCode, String startDate, String endDate, boolean isGPO) {
		String hql = "";
		if (isGPO) {
			hql = "from ReturnsRequest po where po.gpoCode=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		} else {
			hql = "from ReturnsRequest po where po.vendorCode=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		}
		return listByHql(hql, null, vendorCode, startDate, endDate);
	}

	@Override
	public ReturnsRequest findByCode(String code) {
		String hql = "from ReturnsRequest po where po.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public DataGrid<ReturnsRequest> listByReturnsRequestAndDetail(PageRequest pageable) {
		String sql = "select distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,t.vendorName,t.hospitalName,"
				+ "t.num,t.sum,t.reply,t.status  from t_order_returnsrequest t "
				+ " left join t_order_returnsrequest_detail d on t.id=d.returnsRequestId  left join t_dm_product p on d.productCode=p.code ";
		DataGrid<ReturnsRequest> result = findBySql(sql, pageable, Map.class);
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
