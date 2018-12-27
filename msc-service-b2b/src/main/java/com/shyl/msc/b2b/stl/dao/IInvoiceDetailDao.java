package com.shyl.msc.b2b.stl.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;
/**
 * 发票明细
 * 
 * @author a_Q
 *
 */
public interface IInvoiceDetailDao extends IBaseDao<InvoiceDetail, Long> {

	/**
	 * 
	 * @param pageable
	 * @param invoiceId
	 * @return
	 */
	public DataGrid<InvoiceDetail> pageByInvoiceId(PageRequest pageable, Long invoiceId);
	
	/**
	 * 
	 * @param invoiceId
	 * @return
	 */
	public List<InvoiceDetail> listByInvoiceId(Long invoiceId);
	
}
