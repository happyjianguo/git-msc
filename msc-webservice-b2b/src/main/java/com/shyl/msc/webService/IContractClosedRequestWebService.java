package com.shyl.msc.webService;

public interface IContractClosedRequestWebService {
	/**
	 * GPO合同终止申请单下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	/**
	 * GPO合同终止申请单反馈上传
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String fedback(String sign, String dataType, String data);
}
