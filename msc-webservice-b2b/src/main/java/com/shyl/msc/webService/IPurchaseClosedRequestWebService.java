package com.shyl.msc.webService;

public interface IPurchaseClosedRequestWebService {
	/**
	 * 医院结案申请单上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	
	/**
	 * GPO结案申请单下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	/**
	 * GPO结案申请单反馈上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String fedback(String sign, String dataType, String data);
}
