package com.shyl.msc.b2b.stl.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
/**
 * 交易发票
 * 
 * @author a_Q
 *
 */
public interface ITradeInvoiceService extends IBaseService<TradeInvoice, Long> {
	
	public void saveTradeInvoice(String projectCode, TradeInvoice tradeInvoice);
	
	public TradeInvoice getByInternalCode(@ProjectCodeFlag String projectCode,String fph, TradeInvoice.Type type);

	public TradeInvoice getByCode(@ProjectCodeFlag String projectCode,String conmpanyCode,String code,boolean isGPO);

	public JSONArray saveTradeInvoice(@ProjectCodeFlag String projectCode, TradeInvoice tradeInvoice, List<JSONObject> arr);
	
}
