package com.shyl.msc.b2b.order.service;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;

/**
 * 订单计划Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchasePlanDetailService extends IBaseService<PurchasePlanDetail, Long> {

	public PurchasePlanDetail findByCode(@ProjectCodeFlag String projectCode, String code);

	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable);

	public DataGrid<PurchasePlanDetail> queryByPurchasePlanAndCode(String projectCode, PageRequest pageable);
}
