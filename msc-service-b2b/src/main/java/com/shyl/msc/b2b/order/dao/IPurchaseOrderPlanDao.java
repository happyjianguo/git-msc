package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;

/**
 * 订单计划DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderPlanDao extends IBaseDao<PurchaseOrderPlan, Long> {

	List<PurchaseOrderPlan> listByDate(String companyCode, String startDate, String endDate, boolean isGPO);

	PurchaseOrderPlan findByCode(String code);

	/**
	 * 根据医院和医院计划单编号查订单计划
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	PurchaseOrderPlan getByInternalCode(String hospitalCode, String internalCode);
	
	/**
	 * 判断订单计划数目
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public Long getCountByInternalCode(String hospitalCode, String internalCode);
	

	/**
	 * 获取GPO及时率排名
	 * @param year
	 * @param monthe
	 * @return
	 */
	public List<Map<String, Object>> queryGpoTimelyRankList(String year, String monthe);
	
	/**
	 * 获取供应商配送时间
	 * @param year
	 * @param month
	 * @param vendorCode
	 * @param delay配送时长(小时)0-6小时 6-12小时 12-24小时 24-48小时 48 -72小时 72小时~（3天以上） 
	 * @return
	 */
	public Long getDeliveryTimely(String year, String month, String vendorCode, Integer delayStart, Integer delayEnd);

	/**
	 * 
	 * @param pId
	 * @return
	 */
	List<PurchaseOrderPlan> listByPatientId(Long pId);

	DataGrid<PurchaseOrderPlan> listBypurchaseOrderPlanAndDetail(PageRequest pageable);
	
	public List<PurchaseOrderPlan> listByIsPass(Integer isPass);
	
	public List<PurchaseOrderPlan> listByIsPassAudit(Integer isPassAudit);

	public List<Map<String, Object>> listByUnEffect(PageRequest page);
}
