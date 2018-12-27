package com.shyl.msc.b2b.order.dao;


import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;

import java.util.List;

/**
 * 订单计划DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchasePlanDao extends IBaseDao<PurchasePlan, Long> {

	PurchasePlan findByCode(String purchasePlanCode);

	DataGrid<PurchasePlan> listByPurchasePlanAndDetail(PageRequest pageable);

	List<PurchasePlan> queryByPlanCode();

	List<PurchasePlanDetail> getDetailById(Long purchasePlanId);
}
