package com.shyl.msc.webService;

/**
 * 订单接口
 * 
 * @author a_Q
 *
 */
public interface IPurchaseOrderWebService {
	/**
	 * GPO上传采购订单
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
}
