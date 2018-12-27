package com.shyl.msc.webService;

/**
 * 发票接口
 * 
 * @author a_Q
 *
 */
public interface IInvoiceWebService {
	/**
	 * 发票上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	/**
	 * 发票下载
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String get(String sign, String dataType, String data);
}
