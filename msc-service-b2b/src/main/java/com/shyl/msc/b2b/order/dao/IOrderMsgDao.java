package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.OrderMsg;

public interface IOrderMsgDao extends IBaseDao<OrderMsg, Long> {

	public OrderMsg findByIndexCode(String code);

	public OrderMsg findByPurchasePlanDetailCode(String code);

	public List<OrderMsg> listByPlanDetailCode(String planDetailCode);

	public OrderMsg getByPurchasePlanDetailCode(String planDetailCode);

}
