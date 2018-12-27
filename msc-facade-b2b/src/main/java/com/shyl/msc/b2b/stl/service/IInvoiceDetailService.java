package com.shyl.msc.b2b.stl.service;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;
/**
 * 发票明细
 * 
 * @author a_Q
 *
 */
public interface IInvoiceDetailService extends IBaseService<InvoiceDetail, Long> {

	/**
	 * 
	 * @param pageable
	 * @param invoiceId
	 * @return
	 */
	public DataGrid<InvoiceDetail> pageByInvoiceId(@ProjectCodeFlag String projectCode, PageRequest pageable, Long invoiceId);

	/**
	 * 
	 * @param invoiceId
	 * @return
	 */
	public List<InvoiceDetail> listByInvoiceId(@ProjectCodeFlag String projectCode, Long invoiceId);
}
