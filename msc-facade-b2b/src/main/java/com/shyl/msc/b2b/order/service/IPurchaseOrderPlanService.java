package com.shyl.msc.b2b.order.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.entity.User;

/**
 * 订单计划Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderPlanService extends IBaseService<PurchaseOrderPlan, Long> {

	/**
	 * 存储订单计划
	 * @param purchasePlan
	 * @return
	 */
	public String savePurchaseOrderPlan(@ProjectCodeFlag String projectCode, PurchasePlan purchasePlan);

	/**
	 * 根据供应商ID和时间区间查订单计划列表
	 * @param vendorId
	 * @param startDate
	 * @param endDate
	 * @param isGPO 
	 * @return
	 */
	public List<PurchaseOrderPlan> listByDate(@ProjectCodeFlag String projectCode, String companyCode, String startDate, String endDate, boolean isGPO);

	/**
	 * 根据编号查订单计划
	 * @param ddbh
	 * @return
	 */
	public PurchaseOrderPlan findByCode(@ProjectCodeFlag String projectCode, String code);

	
	/**
	 * 根据医院和医院计划单编号查订单计划
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public PurchaseOrderPlan getByInternalCode(@ProjectCodeFlag String projectCode, String hospitalCode, String internalCode);

	/**
	 * 判断订单计划数目
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public Long getCountByInternalCode(@ProjectCodeFlag String projectCode, String hospitalCode, String internalCode);

	/**
	 * 供应商补货计划
	 * @param purchaseOrderPlan
	 * @param goodspriceids
	 * @param nums
	 * @param user
	 * @return
	 */
	public String mkAutoOrder(@ProjectCodeFlag String projectCode, PurchaseOrderPlan purchaseOrderPlan, Long[] goodspriceids, BigDecimal[] nums, User user);
	
	/**
	 * 获取GPO及时率排名
	 * @param year
	 * @param month
	 * @return
	 */
	public List<Map<String, Object>> queryGpoTimelyRankList(@ProjectCodeFlag String projectCode, String year, String month);
	
	/**
	 * 获取GPO配送时间
	 * @param year
	 * @param month
	 * @param vendorCode
	 * @param delay配送时长(小时)0-6小时 6-12小时 12-24小时 24-48小时 48 -72小时 72小时~（3天以上） 
	 * @return
	 */
	public Long getDeliveryTimely(@ProjectCodeFlag String projectCode, String year, String month, String vendorCode, Integer delayStart, Integer delayEnd);

	/**
	 * 
	 * @param pId
	 * @return
	 */
	public List<PurchaseOrderPlan> listByPatientId(@ProjectCodeFlag String projectCode, Long pId);

	public void fedback(@ProjectCodeFlag String projectCode, JSONObject jObject);

	public JSONArray getToGPO(@ProjectCodeFlag String projectCode, Company gpo, Boolean isGPO, Boolean isCode, JSONObject jObject) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException ;

	public DataGrid<PurchaseOrderPlan> listBypurchaseOrderPlanAndDetail(@ProjectCodeFlag String projectCode, PageRequest pageable);

	/**
	 * 未审核过账列表
	 * @param isPass
	 * @return
	 */
	public List<PurchaseOrderPlan> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	/**
	 * 已审核过账列表
	 * @param isPass
	 * @return
	 */
	public List<PurchaseOrderPlan> listByIsPassAudit(@ProjectCodeFlag String projectCode, int isPassAudit);

	/**
	 * 过滤出没有生效的订单
	 * @param projectCode
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> listByUnEffect(@ProjectCodeFlag String projectCode,PageRequest page);

}
