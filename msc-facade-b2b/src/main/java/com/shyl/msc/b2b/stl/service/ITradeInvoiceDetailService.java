package com.shyl.msc.b2b.stl.service;


import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;

import java.util.List;

/**
 * 交易发票明细
 * 
 * @author a_Q
 *
 */
public interface ITradeInvoiceDetailService extends IBaseService<TradeInvoiceDetail, Long> {

	List<TradeInvoiceDetail> getByCode(String projectCode, String str, TradeInvoice.Type type);

	void updateBatchByCode (String projectCode,List<TradeInvoiceDetail> tradeInvoiceDetails);
}
