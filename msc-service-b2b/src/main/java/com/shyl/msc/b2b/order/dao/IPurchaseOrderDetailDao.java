package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
/**
 * 补货订单明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderDetailDao extends IBaseDao<PurchaseOrderDetail, Long> {
	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public DataGrid<PurchaseOrderDetail> pageByOrderId(PageRequest pageable,Long orderId);

	public PurchaseOrderDetail findByCode(String code);

	public List<PurchaseOrderDetail> listByOrderId(Long orderId);
	
	public DataGrid<Map<String, String>> report2(String name, String dataS, String dataE, PageRequest pageable);

	public DataGrid<Map<String, String>> report1(String year, PageRequest pageable);
	public List<Map<String, Object>> chart1(String year);
	
	public DataGrid<Map<String, Object>> report4(String name, PageRequest pageable);

	public DataGrid<Map<String, Object>> report5(String name, String month, PageRequest pageable);

	public List<Map<String, Object>> reportAll(String name, String month, PageRequest pageable);
	/**
	 * 
	 * @param pageable
	 * @param name
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportProducerTrade(PageRequest pageable, String name, String year);

	public DataGrid<Map<String, Object>> report5mx(String hospitalcode, String month, PageRequest pageable);

	/**
	 * 
	 * @param pageable
	 * @param name
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportVendorTrade(PageRequest pageable, String name, String year);

	public DataGrid<Map<String, Object>> report7(String name, String model, String producerName, String vendorName,
			PageRequest pageable);

	public DataGrid<Map<String, Object>> reportGoodsTradeMX(Long id, String dateS, String dateE, PageRequest pageable);

	public List<Map<String, Object>> reportProducerTrade(Long producerId,
			String year);

	public List<Map<String, Object>> chart4(String name, String dateS, String dateE);

	public List<PurchaseOrderDetail> listByIsPass(int isPass);

	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> reportForProductOrder(String projectCode, String startDate, String endDate,
			PageRequest pageable);

	public DataGrid<Map<String, Object>> reportDetailForProductOrder(String projectCode, String startDate,
			String endDate, PageRequest pageable);

	public DataGrid<PurchaseOrderDetail> queryBycode(PageRequest pageable);
}
