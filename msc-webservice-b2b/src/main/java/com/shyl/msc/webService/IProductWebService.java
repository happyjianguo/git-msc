package com.shyl.msc.webService;

/**
 * 药品信息接口
 * 
 * @author a_Q
 *
 */
public interface IProductWebService {
	/**
	 * 
	 * @param sign 加密摘要
	 * @param dataType “1”为json格式，“2”为xml格式
	 * @param data 数据
	 * @return
	 */
	public String send(String sign, String dataType, String data);
	/**
	 * 根据条件药品信息下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	/**
	 * 医院获取药品编码接口
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String getToHis(String sign, String dataType, String data);

	/**
	 * GPO获取药品编码接口
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String getToCom(String sign, String dataType, String data);
	
	/**
	 * 药品信息下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String getAll(String sign, String dataType, String data);
}
