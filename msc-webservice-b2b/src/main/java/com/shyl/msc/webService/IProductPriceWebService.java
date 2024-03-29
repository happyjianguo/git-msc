package com.shyl.msc.webService;

/**
 * 药品价格接口
 * 
 * @author a_Q
 *
 */
public interface IProductPriceWebService {
	/**
	 * GPO上传药品价格
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	/**
	 * 
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String get(String sign, String dataType, String data);
}
