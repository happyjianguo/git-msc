package com.shyl.msc.webService;

/**
 * 付款单接口
 * 
 * @author a_Q
 *
 */
public interface IPaymentWebService {

	/**
	 * 2.12付款单上传
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String send(String sign, String dataType,String data);
}
