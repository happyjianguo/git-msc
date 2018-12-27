package com.shyl.msc.b2b.stl.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.stl.dao.IInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;
import com.shyl.msc.b2b.stl.service.IInvoiceDetailService;
/**
 * 发票明细
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class InvoiceDetailService extends BaseService<InvoiceDetail, Long> implements IInvoiceDetailService {
	private IInvoiceDetailDao iInvoiceDetailDao;

	public IInvoiceDetailDao getiInvoiceDetailDao() {
		return iInvoiceDetailDao;
	}

	@Resource
	public void setiInvoiceDetailDao(IInvoiceDetailDao iInvoiceDetailDao) {
		this.iInvoiceDetailDao = iInvoiceDetailDao;
		super.setBaseDao(iInvoiceDetailDao);
	}

	@Override
	public DataGrid<InvoiceDetail> pageByInvoiceId(String projectCode, PageRequest pageable,
			Long invoiceId) {
		return iInvoiceDetailDao.pageByInvoiceId(pageable, invoiceId);
	}
	
	@Override
	public List<InvoiceDetail> listByInvoiceId(String projectCode, Long invoiceId){
		return iInvoiceDetailDao.listByInvoiceId(invoiceId);
	}
	
}
