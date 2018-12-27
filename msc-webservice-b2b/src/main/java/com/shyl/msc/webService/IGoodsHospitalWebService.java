package com.shyl.msc.webService;

/**
 * 药品映射关系
 * 
 * @author a_Q
 *
 */
public interface IGoodsHospitalWebService {
	/**
	 * 医院上传药品映射关系
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	
	/**
	 * 
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType,String data);
}
