package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.IInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;

/**
 * 发票明细
 * 
 * @author a_Q
 *
 */
@Repository
public class InvoiceDetailDao extends BaseDao<InvoiceDetail, Long> implements IInvoiceDetailDao {

	@Override
	public DataGrid<InvoiceDetail> pageByInvoiceId(PageRequest pageable,
			Long invoiceId) {
		String hql = "from InvoiceDetail d where d.invoice.id=?";
		return super.query(hql, pageable, invoiceId);
	}
	
	@Override
	public List<InvoiceDetail> listByInvoiceId(Long invoiceId) {
		String hql = "from InvoiceDetail d where d.invoice.id=?";
		return super.listByHql(hql, null, invoiceId);
	}
}
