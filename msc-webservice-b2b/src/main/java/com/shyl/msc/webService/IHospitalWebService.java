package com.shyl.msc.webService;

public interface IHospitalWebService {
	/**
	 * 医院信息下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
	
	
}
