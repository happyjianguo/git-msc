package com.shyl.msc.b2b.stl.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;
/**
 * 交易发票明细
 * 
 * @author a_Q
 *
 */
public interface ITradeInvoiceDetailDao extends IBaseDao<TradeInvoiceDetail, Long> {

	/**
	 * 
	 * @param tradeInvoiceId
	 * @return
	 */
	List<TradeInvoiceDetail> listByInvoiceId(Long tradeInvoiceId);


	List<TradeInvoiceDetail> getByCode(String str, TradeInvoice.Type type);
}
