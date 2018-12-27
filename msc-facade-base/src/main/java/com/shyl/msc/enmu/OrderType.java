package com.shyl.msc.enmu;

/**
 * 单据类型
 */
public enum OrderType {
	/** 采购计划 */
	plan,
	
	/** 订单计划 */
	orderPlan,
	
	/** 订单 */
	order,

	/** 配送单 */
	delivery,

	/** 退货单 */
	returns,
	
	/** 入库单 */
	inoutbound,
	
	/** 发票*/
	invoice,
	
	/** 结算单*/
	settlement,
	
	/** 请付单*/
	payment,
	
	/** 退货申请单*/
	returnsRequest,
	
	/** 订单结案申请单*/
	orderClosedRequest,
	
	/** 处方*/
	prescript,
	
	/** 合同*/
	contract,
	
	/** 合同结案申请单*/
	contractClosedRequest,
	
	/** 交易发票*/
	tradeInvoice
}
