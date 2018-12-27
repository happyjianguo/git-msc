package com.shyl.msc.webService;

/**
 * 订单计划接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderPlanWebService {
	/**
	 * 医院上传采购订单计划
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	
	/**
	 * GPO下载订单计划
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	/**
	 * GPO反馈订单计划
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String fedback(String sign, String dataType, String data);
	/**
	 *  采购计划细项取消 
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String cancel(String sign, String dataType, String data);
	
	/**
	 * 采购计划医院取消项下载 
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String getCancel(String sign, String dataType, String data);
}
