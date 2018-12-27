package com.shyl.msc.webService;

/**
 * 入库单接口
 * 
 * @author a_Q
 *
 */
public interface IInOutBoundWebService {
	/**
	 * 医院入库单上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	
	/**
	 * GPO入库单下载
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
}
