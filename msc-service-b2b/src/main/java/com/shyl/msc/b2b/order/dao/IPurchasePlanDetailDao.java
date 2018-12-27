package com.shyl.msc.b2b.order.dao;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;

/**
 * 订单计划DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchasePlanDetailDao extends IBaseDao<PurchasePlanDetail, Long> {

	PurchasePlanDetail findByCode(String code);

	List<PurchasePlanDetail> listByPId(Long id);

	DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	List<Map<String, Object>> listByProductReport(PageRequest pageable);

	DataGrid<PurchasePlanDetail> queryByPurchasePlanAndCode(PageRequest pageable);
}
