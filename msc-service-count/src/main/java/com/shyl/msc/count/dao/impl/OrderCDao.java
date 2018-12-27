package com.shyl.msc.count.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IOrderCDao;
import com.shyl.msc.count.entity.OrderC;

@Repository
public class OrderCDao extends BaseDao<OrderC, Long> implements IOrderCDao {

	@Override
	public OrderC getByKey(String month, String hospitalCode, String vendorCode){
		return super.getByHql("from OrderC where month=?  "
				+ "and hospitalCode=? and vendorCode=?", month, hospitalCode, vendorCode);
	}

	@Override
	public List<Map<String, Object>> countByVendor(PageRequest pageable) {
		String sql = "select t.month,t.vendorCode,count(distinct t.hospitalCode) as COUNT"
				+ " from t_count_order_c t group by t.month,t.vendorCode";
		return super.listBySql2(sql, pageable, Map.class);
	}
}
