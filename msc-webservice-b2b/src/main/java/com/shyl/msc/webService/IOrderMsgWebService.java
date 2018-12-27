package com.shyl.msc.webService;

/**
 * 订单状态接口
 * 
 * @author a_Q
 *
 */
public interface IOrderMsgWebService {
	/**
	 * 
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	public String list(String sign, String dataType, String data);
}
