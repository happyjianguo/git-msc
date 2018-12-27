package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IOrderMsgDao;
import com.shyl.msc.b2b.order.entity.OrderMsg;

@Repository
public class OrderMsgDao extends BaseDao<OrderMsg, Long> implements IOrderMsgDao {

	@Override
	public OrderMsg findByIndexCode(String code) {
		String hql = "from OrderMsg o where o.indexCode=?";
		return super.getByHql(hql, code);
	}

	@Override
	public OrderMsg findByPurchasePlanDetailCode(String code) {
		String hql = "from OrderMsg o where o.purchasePlanDetailCode=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<OrderMsg> listByPlanDetailCode(String planDetailCode) {
		String hql = "from OrderMsg o where o.purchasePlanDetailCode=?";
		return super.listByHql(hql, null, planDetailCode);
	}

	@Override
	public OrderMsg getByPurchasePlanDetailCode(String planDetailCode) {
		String hql = "from OrderMsg o where o.purchasePlanDetailCode=?";
		Sort sort = new Sort(new Order(Direction.DESC,"o.orderDetailStatus"));
		List<OrderMsg> orderMsgs = super.limitList(hql, 1, sort, planDetailCode);
		if(orderMsgs.size()>0){
			return orderMsgs.get(0);
		}else{
			return null;
		}
	}
}
