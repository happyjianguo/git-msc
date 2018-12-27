package com.shyl.msc.webService;

/**
 * 退货申请
 * @author a_Q
 *
 */
public interface IReturnsRequestWebService {
	/**
	 * 医院退货申请单上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	
	/**
	 * GPO退货申请单下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	/**
	 * GPO退货申请单反馈上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String fedback(String sign, String dataType, String data);
}
