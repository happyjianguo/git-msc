package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.set.entity.Company;

/**
 * 订单计划Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderPlanDetailService extends IBaseService<PurchaseOrderPlanDetail, Long> {

	/**
	 * 根据订单计划ID查明细列表
	 * @param id
	 * @return
	 */
	public List<PurchaseOrderPlanDetail> listByOrderPlanId(@ProjectCodeFlag String projectCode, Long id);

	/**
	 * 根据订单计划明细编号查订单明细
	 * @param ddmxbh
	 * @return
	 */
	public PurchaseOrderPlanDetail findByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 根据订单计划ID查订单计划明细page
	 * @param pageable
	 * @param orderId
	 * @return
	 */
	public DataGrid<PurchaseOrderPlanDetail> pageByOrderId(@ProjectCodeFlag String projectCode, PageRequest pageable, Long orderId);

	/**
	 * 根据日期查询供应商断供次数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String,Object>> listByDate(@ProjectCodeFlag String projectCode, String beginDate,String endDate);

	public void cancelDetail(@ProjectCodeFlag String projectCode, List<JSONObject> list);

	public PurchaseOrderPlanDetail findByPlanCode(@ProjectCodeFlag String projectCode, String planCode);
	
	/**
	 * 医院取消计划明细
	 * @param id
	 */
	public void checkDetailByH(@ProjectCodeFlag String projectCode, Long id);

	/**
	 * 根据日期查询某供应商断供明细
	 * @param vendorCode
	 * @param beginDate
	 * @param endDate
	 * @param pageable 
	 * @return
	 */
	public DataGrid<Map<String, Object>> listByHospitalDate(@ProjectCodeFlag String projectCode, String vendorCode, String beginDate,String endDate, PageRequest pageable);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<PurchaseOrderPlanDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	
	/**
	 * 订单计划医院取消项查询
	 * @param projectCode
	 * @param company
	 * @param isGPO
	 * @return
	 */
	public JSONArray getCancel(@ProjectCodeFlag String projectCode, Company company, Boolean isGPO);

	public DataGrid<PurchaseOrderPlanDetail> queryByCode(@ProjectCodeFlag String projectCode, PageRequest pageable);

}
