package com.shyl.msc.enmu;

/**
 * 处方状态
 */
public enum PrescriptStatus {
	/** 医生待审核 */
	waitAudit,
	/** 医生审核通过 */
	doctorAudit,
	/** 医生审核不通过 */
	doctorReject,
	/** 缴费完成 */
	payComplete,
	/** 药师审核通过 */
	apothecaryAudit,
	/** 药师审核未通过 **/
	apothecaryReject,
	/** 作废*/
	invalid,
	/** 已生成订单(处方信息使用) **/
	createOrder
}
