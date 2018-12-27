package com.shyl.msc.webService;


/**
 * 药品价格接口
 * 
 * @author a_Q
 *
 */
public interface IGoodsPriceWebService {
	
	
	/**
	 * HIS获取价格数据
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String getToHis(String sign, String dataType, String data);
}
