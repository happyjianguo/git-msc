package com.shyl.msc.b2b.stl.service.impl;

import javax.annotation.Resource;

import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.stl.dao.ITradeInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceDetailService;

import java.util.List;

/**
 * 交易发票明细
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class TradeInvoiceDetailService extends BaseService<TradeInvoiceDetail, Long> implements ITradeInvoiceDetailService {
	private ITradeInvoiceDetailDao tradeInvoiceDetailDao;

	public ITradeInvoiceDetailDao getTradeInvoiceDetailDao() {
		return tradeInvoiceDetailDao;
	}

	@Resource
	public void setTradeInvoiceDetailDao(ITradeInvoiceDetailDao tradeInvoiceDetailDao) {
		this.tradeInvoiceDetailDao = tradeInvoiceDetailDao;
		super.setBaseDao(tradeInvoiceDetailDao);
	}

	@Override
	public List<TradeInvoiceDetail> getByCode(String projectCode, String str, TradeInvoice.Type type) {
		return tradeInvoiceDetailDao.getByCode(str,type);
	}

	@Override
	@Transactional
	public void updateBatchByCode(String projectCode, List<TradeInvoiceDetail> tradeInvoiceDetails) {
		tradeInvoiceDetailDao.updateBatch(tradeInvoiceDetails);
	}
}
