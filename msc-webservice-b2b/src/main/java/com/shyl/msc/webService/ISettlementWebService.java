package com.shyl.msc.webService;

/**
 * 结算单接口
 * 
 * @author a_Q
 *
 */
public interface ISettlementWebService {

	/**
	 * 结算单上传
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String send(String sign, String dataType,String data);
}
