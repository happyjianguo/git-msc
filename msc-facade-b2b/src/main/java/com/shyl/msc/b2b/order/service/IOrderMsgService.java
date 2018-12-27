package com.shyl.msc.b2b.order.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.OrderMsg;
import com.shyl.msc.enmu.OrderDetailStatus;

public interface IOrderMsgService extends IBaseService<OrderMsg, Long> {

	public void saveOrderMsg(@ProjectCodeFlag String projectCode, String pCode, String code, OrderDetailStatus orderDetailStatus);

	public void updateOrderMsg(@ProjectCodeFlag String projectCode, String pCode, String code);

	public List<OrderMsg> listByPlanDetailCode(@ProjectCodeFlag String projectCode, String planDetailCode);

	public OrderMsg getByPurchasePlanDetailCode(@ProjectCodeFlag String projectCode, String planDetailCode);

}
