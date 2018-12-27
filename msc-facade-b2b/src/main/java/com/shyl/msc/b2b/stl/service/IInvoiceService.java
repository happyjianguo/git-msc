package com.shyl.msc.b2b.stl.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.Invoice;
import com.shyl.sys.entity.User;
/**
 * 发票
 * 
 * @author a_Q
 *
 */
public interface IInvoiceService extends IBaseService<Invoice, Long> {
	
	/**
	 * 保存发票
	 * @param invoice
	 */
	public void saveInvoice(@ProjectCodeFlag String projectCode, Invoice invoice);

	/**
	 * 
	 * @param vendorCode
	 * @param fph
	 * @param isGPO 
	 * @return
	 */
	public Invoice getByInternalCode(@ProjectCodeFlag String projectCode, String vendorCode, String fph, boolean isGPO);

	/**
	 * 上传蓝字发票
	 * @param orderCode 
	 * @param deliveryId
	 * @param invoiceCode
	 * @param invoiceDate
	 * @param tax 
	 * @param user
	 * @return
	 */
	public String mkinvoiceBlue(@ProjectCodeFlag String projectCode, String orderCode, Long[] detailIds,BigDecimal[] sums, String invoiceCode, String invoiceDate, BigDecimal tax, User user);

	/**
	 * 上传红字发票
	 * @param returnId
	 * @param invoiceCode
	 * @param invoiceDate
	 * @param taxRate
	 * @param user
	 * @return
	 */
	public String mkinvoiceRed(@ProjectCodeFlag String projectCode, Long returnId, String invoiceCode, String invoiceDate, BigDecimal taxRate, User user);

	/**
	 * 发票 所属 医院列表
	 * @param vendorCode
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> hospitalListForSettle(@ProjectCodeFlag String projectCode, String vendorCode, PageRequest pageable);

	/**
	 * gpo，医院 的发票列表
	 * @param hospitalCode
	 * @param vendorCode
	 * @return
	 */
	public List<Map<String, Object>> listForSettle(@ProjectCodeFlag String projectCode, String hospitalCode, String vendorCode);

	public Invoice findByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 根据送退单编号查列表
	 * @param stdbh
	 * @return
	 */
	public List<Invoice> listByDeliveryOrReturnsCode(@ProjectCodeFlag String projectCode, String stdbh);

	public JSONArray saveInvoice(@ProjectCodeFlag String projectCode, Invoice invoice, List<JSONObject> arr);
	
	/**
	 * 根据时间查询发票
	 * 
	 * @param hospitalCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Invoice> listByDate(@ProjectCodeFlag String projectCode, String hospitalCode, String startDate, String endDate) ;
	
	/**
	 * 医院查询发票信息
	 * @param hospitalCode
	 * @param code
	 * @param code
	 * @return
	 */
	public Invoice getByInternalCode(@ProjectCodeFlag String projectCode,String hospitalCode, String code);
}
