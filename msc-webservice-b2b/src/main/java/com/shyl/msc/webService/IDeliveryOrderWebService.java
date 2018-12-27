package com.shyl.msc.webService;

/**
 * 配送单接口
 * 
 * @author a_Q
 *
 */
public interface IDeliveryOrderWebService {
	/**
	 * 
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);

	/**
	 * 
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);

	/**
	 * 医院下载配送单清单
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String list(String sign,String dataType,String data);
}
