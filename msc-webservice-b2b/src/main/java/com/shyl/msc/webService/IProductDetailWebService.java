package com.shyl.msc.webService;

/**
 * 供应商药品供应关系接口
 * 
 * @author a_Q
 *
 */
public interface IProductDetailWebService {

	/**
	 * 2.21供应商药品供应关系下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
}
