package com.shyl.msc.b2b.order.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
/**
 * 补货订单Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderService extends IBaseService<PurchaseOrder, Long> {
	
	/**
	 * 下单确认
	 * @param id
	 */
	public void commit(@ProjectCodeFlag String projectCode, Long id)throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException;
	
	/**
	 * 查订单列表
	 * @param vendorCode 
	 * @param startDate  开始日期
	 * @param endDate  结束日期
	 * @return
	 */
	public List<PurchaseOrder> listByDate(@ProjectCodeFlag String projectCode, String vendorCode, String startDate, String endDate);
	
	/**
	 * 根据订单编号查询
	 * @param ddbh
	 * @return
	 */
	public PurchaseOrder findByCode(@ProjectCodeFlag String projectCode, String ddbh);
	
	/**
	 * 
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportByYear(@ProjectCodeFlag String projectCode, PageRequest pageable, String year);
	
	/**
	 * 根据年份查交易总额
	 * @param year
	 * @return
	 */
	public Map<String, Object> totalTradeByYear(@ProjectCodeFlag String projectCode, String year);
	
	/**
	 * 根据月份查交易总额
	 * @param year
	 * @return
	 */
	public Map<String, Object> totalTradeByMonth(@ProjectCodeFlag String projectCode, String string);
	
	/**
	 * 根据年份统计订单情况
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> reportByYear(@ProjectCodeFlag String projectCode, String year);
	
	/**
	 * 配送企业年度交易汇总统计图
	 * @param vendorId
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> reportVendorTrade(@ProjectCodeFlag String projectCode, String vendorCode, String year);
	
	/**
	 * 保存采购订单
	 * @param purchaseOrders
	 */
	public void savePurchaseOrders(@ProjectCodeFlag String projectCode, List<PurchaseOrder> purchaseOrders);

	/**
	 * 未结案的订单
	 * @param orgId
	 * @param pageable
	 * @return
	 */
	public DataGrid<PurchaseOrder> queryUnClosed(@ProjectCodeFlag String projectCode, String hospitalCode, PageRequest pageable);

	/**
	 * 
	 * @param vendorId
	 * @param internalCode
	 * @return
	 */
	public PurchaseOrder getByInternalCode(@ProjectCodeFlag String projectCode, String vendorCode, String internalCode);
	
	/**
	 * 新增订单
	 * @param purchaseOrder
	 * @return
	 */
	public void addOrder(@ProjectCodeFlag String projectCode, PurchaseOrder purchaseOrder);

	/**
	 * 医院GPO采购集中度
	 * @param year
	 * @param month
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoCentralizedByHospital(@ProjectCodeFlag String projectCode, String year, String month, int maxsize);

	/**
	 * 医院订单GPO采购金额对比
	 * @param year
	 * @param month
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoSumByHospital(@ProjectCodeFlag String projectCode, String year, String month, int maxsize);
	/**
	 * 县区订单药品采购集中度
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoCentralizedByRegion(@ProjectCodeFlag String projectCode, String year, String month, String treepath, int maxsize);
	/**
	 * 县区订单GPO采购金额对比
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoSumByRegion(@ProjectCodeFlag String projectCode, String year, String month, String treepath, int maxsize);

	/**
	 * 审单
	 * @param id
	 */
	public String checkPlan(@ProjectCodeFlag String projectCode, Long id);


	public DataGrid<PurchaseOrder> queryByStatus(@ProjectCodeFlag String projectCode, PageRequest pageable);

	/**
	 * 
	 * @param planCode
	 * @return
	 */
	public PurchaseOrder getByPlanCode(@ProjectCodeFlag String projectCode, String planCode);

	public JSONArray addOrder(@ProjectCodeFlag String projectCode, PurchaseOrder purchaseOrder, List<JSONObject> list);

	public DataGrid<PurchaseOrder> listBypurchaseOrderAndDetail(PageRequest pageable);
	
	/**
	 * 医院导出未配送的订单
	 * @param projectCode
	 * @param code
	 * @param string
	 * @return
	 */
	public List<Map<String,Object>> listByNODelivery(PageRequest page);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<PurchaseOrder> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	
	/**
	 * 已配送订单过账列表
	 * @param isPassDelivery
	 * @return
	 */
	public List<PurchaseOrder> listByIsPassDelivery(@ProjectCodeFlag String projectCode, int isPassDelivery);

	public DataGrid<PurchaseOrder> listByPurchaseOrderAndDetailStatus(String projectCode, PageRequest pageable);

	/**
	 * 医院某年的采购金额
	 * @param projectCode
	 * @param code
	 * @param string
	 * @return
	 */
	public Map<String, Object> orderSumByHospitalAndYear(@ProjectCodeFlag String projectCode, String hospitalCode, String year);
	/**
	 * 供应商某年的交易金额
	 * @param projectCode
	 * @param code
	 * @param string
	 * @return
	 */
	public Map<String, Object> orderSumByGpoAndYear(String projectCode, String VendorCode, String year);
	
}
