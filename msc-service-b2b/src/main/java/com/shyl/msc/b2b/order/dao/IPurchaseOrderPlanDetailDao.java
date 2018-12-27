package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;

/**
 * 订单计划明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderPlanDetailDao extends IBaseDao<PurchaseOrderPlanDetail, Long> {

	/**
	 * 根据订单计划ID查明细列表
	 * @param id
	 * @return
	 */
	public List<PurchaseOrderPlanDetail> listByOrderPlanId(Long id);

	/**
	 * 根据订单计划明细编号查订单明细
	 * @param ddmxbh
	 * @return
	 */
	public PurchaseOrderPlanDetail findByCode(String code);

	/**
	 * 根据订单计划ID查订单计划明细page
	 * @param pageable
	 * @param orderId
	 * @return
	 */
	public DataGrid<PurchaseOrderPlanDetail> pageByOrderId(PageRequest pageable, Long orderId);

	/**
	 * 根据日期查询供应商断供次数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String,Object>> listByDate(String beginDate,String endDate);

	public List<PurchaseOrderPlanDetail> listByOrderPlanId(Long id,
			PurchaseOrderPlanDetail.Status status);

	public PurchaseOrderPlanDetail findByPlanCode(String planCode);

	public DataGrid<Map<String, Object>> listByHospitalDate(String projectCode, String vendorCode, String beginDate,
			String endDate, PageRequest pageable);
	
	public List<PurchaseOrderPlanDetail> listByIsPass(int isPass);
	
	/**
	 * 订单计划医院取消项下载
	 * @param companyCode
	 * @param isGPO
	 * @return
	 */
	public List<PurchaseOrderPlanDetail> listGetCancel(String companyCode, boolean isGPO);

	public DataGrid<PurchaseOrderPlanDetail> queryByCode(PageRequest pageable);
}
