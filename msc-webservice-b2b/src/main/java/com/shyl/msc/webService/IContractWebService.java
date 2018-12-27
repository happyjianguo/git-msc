package com.shyl.msc.webService;

public interface IContractWebService {
	/**
	 * 2.20医院三方合同下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	/**
	 * 2.21医院三方合同签章回传
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String fedback(String sign, String dataType, String data);
	
	/**
	 * 2.10三方合同剩余量下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String rest(String sign, String dataType, String data);
}
