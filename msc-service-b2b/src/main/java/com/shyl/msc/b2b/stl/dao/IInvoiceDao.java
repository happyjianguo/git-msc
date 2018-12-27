package com.shyl.msc.b2b.stl.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.Invoice;
/**
 * 发票
 * 
 * @author a_Q
 *
 */
public interface IInvoiceDao extends IBaseDao<Invoice, Long> {

	/**
	 * 
	 * @param vendorId
	 * @param fph
	 * @param isGPO 
	 * @return
	 */
	public Invoice getByInternalCode(String companyCode, String fph, boolean isGPO);

	public DataGrid<Map<String, Object>> hospitalListForSettle(String vendorCode, PageRequest pageable);

	public List<Map<String, Object>> listForSettle(String hospitalCode, String vendorCode);

	public Invoice findByCode(String code);

	public List<Invoice> listByDeliveryOrReturnsCode(String stdbh);
	
	public List<Invoice> listByDate(String hospitalCode, String startDate, String endDate) ;
	
	public Invoice getByInternalCode(String hospitalCode, String code);
}
