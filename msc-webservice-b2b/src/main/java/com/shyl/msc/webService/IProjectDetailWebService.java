package com.shyl.msc.webService;

/**
 * 项目计划接口
 * 
 * @author a_Q
 *
 */
public interface IProjectDetailWebService {

	/**
	 * 2.17品种计划下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
}
