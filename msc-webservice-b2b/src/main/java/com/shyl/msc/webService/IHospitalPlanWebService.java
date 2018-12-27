package com.shyl.msc.webService;

public interface IHospitalPlanWebService {
	/**
	 * 2.18医院报量计划下载
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	public String get(String sign, String dataType, String data);
}
