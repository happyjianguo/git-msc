package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDao;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
/**
 * 退货单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ReturnsOrderDao extends BaseDao<ReturnsOrder, Long> implements IReturnsOrderDao{

	@Override
	public List<ReturnsOrder> listByIsPass(int isPass) {
		String hql = "from ReturnsOrder where isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public ReturnsOrder findByCode(String code) {
		String hql = "from ReturnsOrder where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public ReturnsOrder getByInternalCode(String companyCode, String internalCode, boolean isGPO) {
		String hql = "";
		if(isGPO){
			hql = "from ReturnsOrder ro where ro.gpoCode=? and ro.internalCode=?";
		}else{
			hql = "from ReturnsOrder ro where ro.vendorCode=? and ro.internalCode=?";
		}
		return super.getByHql(hql, companyCode, internalCode);
	}

	@Override
	public ReturnsOrder findByRequestCode(String requestCode) {
		String hql = "from ReturnsOrder ro where ro.returnsRequestCode=?";
		return super.getByHql(hql, requestCode);
	}
	
	@Override
	public DataGrid<ReturnsOrder> listByReturnsOrderAndDetail(PageRequest pageable) {
		String sql="SELECT t.* from t_order_returnsorder t LEFT JOIN "
					+" t_order_returnsorder_detail d ON t.id=d.returnsorderid  left join t_dm_product p on d.productCode=p.code";
		DataGrid<ReturnsOrder> result = findBySql(sql, pageable, ReturnsOrder.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql+=hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}

}
