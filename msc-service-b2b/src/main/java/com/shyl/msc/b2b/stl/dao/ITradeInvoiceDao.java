package com.shyl.msc.b2b.stl.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
/**
 * 交易发票
 * 
 * @author a_Q
 *
 */
public interface ITradeInvoiceDao extends IBaseDao<TradeInvoice, Long> {

	public TradeInvoice getByInternalCode(String fph,TradeInvoice.Type type);

	public TradeInvoice getByCode(String companyCode, String code, boolean isGPO);

}
