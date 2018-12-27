package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
/**
 * 补货订单DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderDao extends IBaseDao<PurchaseOrder, Long> {

	/**
	 * 查订单列表
	 * @param vendorCode 
	 * @param startDate  开始日期
	 * @param endDate  结束日期
	 * @return
	 */
	public List<PurchaseOrder> listByDate(String vendorCode, String startDate, String endDate);

	public PurchaseOrder findByCode(String ddbh);

	/**
	 * 
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportByYear(PageRequest pageable, String year);

	/**
	 * 根据年度查交易总额
	 * @param year
	 * @return
	 */
	public Map<String, Object> totalTradeByYear(String year);

	public Map<String, Object> totalTradeByMonth(String month);

	/**
	 * 
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> reportByYear(String year);

	public List<Map<String, Object>> reportVendorTrade(String vendorCode, String year);

	public List<PurchaseOrder> listByIsPass(Integer isPass);
	
	public List<PurchaseOrder> listByIsPassDelivery(Integer isPassDelivery);

	public DataGrid<PurchaseOrder> queryUnClosed(String hospitalCode, PageRequest pageable);

	/**
	 * 
	 * @param vendorCode
	 * @param internalCode
	 * @return
	 */
	public PurchaseOrder getByInternalCode(String vendorCode, String internalCode);

	/**
	 * 医院GPO采购集中度
	 * @param year
	 * @param month
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoCentralizedByHospital(String year, String month, int maxsize);

	/**
	 * 医院订单GPO采购金额对比
	 * @param year
	 * @param month
	 * @param parentCode
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoSumByHospital(String year, String month, int maxsize);

	/**
	 * 县区订单药品采购集中度
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoCentralizedByRegion(String year, String month, String treepath, int maxsize);

	/**
	 * 县区订单GPO采购金额对比
	 * @param year
	 * @param month
	 * @param treepath
	 * @param maxsize
	 * @return
	 */
	public List<Map<String, Object>> getGpoSumByRegion(String year, String month, String treepath, int maxsize);
	
	/**
	 * 统计医院采购金额
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> totalHospitalSum(String beginDate, String endDate);

	public DataGrid<PurchaseOrder> queryByStatus(PageRequest pageable);

	/**
	 * 
	 * @param planCode
	 * @return
	 */
	public PurchaseOrder getByPlanCode(String planCode);

	public DataGrid<PurchaseOrder> listBypurchaseOrderAndDetail(PageRequest pageable);
	
	/**
	 * 医院导出未配送的订单
	 * @param projectCode
	 * @param code
	 * @param string
	 * @return
	 */
	public List<Map<String,Object>> listByNODelivery(PageRequest page);

	public DataGrid<PurchaseOrder> listByPurchaseOrderAndDetailStatus(PageRequest pageable);

	public Map<String, Object> orderSumByHospitalAndYear(String hospitalCode, String year);

	public Map<String, Object> orderSumByGpoAndYear(String vendorCode, String year);
}
