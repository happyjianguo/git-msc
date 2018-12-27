package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;

import javax.xml.crypto.Data;

/**
 * 补货订单明细Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderDetailService extends IBaseService<PurchaseOrderDetail, Long> {


	public DataGrid<PurchaseOrderDetail> pageByOrderId(@ProjectCodeFlag String projectCode, PageRequest pageable,Long orderId);

	public PurchaseOrderDetail findByCode(@ProjectCodeFlag String projectCode, String code);
	
	public List<PurchaseOrderDetail> listByOrderId(@ProjectCodeFlag String projectCode, Long orderId);

	

	/**
	 * 1、社保药品汇总统计（含图）
	 */
	public DataGrid<Map<String, String>> report1(@ProjectCodeFlag String projectCode, String year, PageRequest pageable);
	public List<Map<String, Object>> chart1(@ProjectCodeFlag String projectCode, String year);
	
	/**
	 * 2、医疗机构社保药统计
	 */
	public DataGrid<Map<String, String>> report2(@ProjectCodeFlag String projectCode, String name, String dataS, String dataE, PageRequest pageable);

	
	/**
	 * 4、医疗机构采购汇总（含图）
	 */
	public DataGrid<Map<String, Object>> report4(@ProjectCodeFlag String projectCode, String name, PageRequest pageable);
	public List<Map<String, Object>> chart4(@ProjectCodeFlag String projectCode, String name, String dateS, String dateE);

	/**
	 * 5、医疗机构采购情况
	 */
	public DataGrid<Map<String, Object>> report5(@ProjectCodeFlag String projectCode, String name, String string, PageRequest pageable);
	/**
	 * 5、医疗机构采购情况导出
	 */
	public List<Map<String, Object>> reportAll(@ProjectCodeFlag String projectCode, String name, String string, PageRequest pageable);
	/**
	 * 5、医疗机构采购情况 明细
	 */
	public DataGrid<Map<String, Object>> report5mx(@ProjectCodeFlag String projectCode, String hospitalcode, String string, PageRequest pageable);

	/**
	 * 生产厂家交易汇总统计
	 * @param pageable
	 * @param name
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportProducerTrade(@ProjectCodeFlag String projectCode, PageRequest pageable, String name, String year);

	/**
	 * 配送企业交易汇总统计
	 * @param pageable
	 * @param name
	 * @param year
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportVendorTrade(@ProjectCodeFlag String projectCode, PageRequest pageable, String name, String year);
	
	/**
	 * 7、品种交易汇总
	 */
	public DataGrid<Map<String, Object>> report7(@ProjectCodeFlag String projectCode, String name, String model, String producerName, String vendorName,
			PageRequest pageable);
	/**
	 * 7、品种交易汇总 明细
	 */
	public DataGrid<Map<String, Object>> reportGoodsTradeMX(@ProjectCodeFlag String projectCode, Long id, String dateS, String dateE, PageRequest pageable);

	/**
	 * 生产企业年度交易额统计图
	 * @param producerId
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> reportProducerTrade(@ProjectCodeFlag String projectCode, Long producerId, String year);

	/**
	 * 
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> pageByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> reportForProductOrder(@ProjectCodeFlag String projectCode, String startDate, String endDate,PageRequest pageable);

	public DataGrid<Map<String, Object>> reportDetailForProductOrder(@ProjectCodeFlag String projectCode, String startDate,String endDate, PageRequest pageable);

	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<PurchaseOrderDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);

	public DataGrid<PurchaseOrderDetail> queryBycode(@ProjectCodeFlag String projectCode, PageRequest pageable);
}
