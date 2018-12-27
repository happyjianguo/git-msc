package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.HospitalZone;

public interface IHospitalZoneService extends IBaseService<HospitalZone,Long>{
	/**
	 * 查询区
	 * @param hospitalCode
	 * @return
	 */
	public HospitalZone getByCode(@ProjectCodeFlag String projectCode,String hospitalCode);
	/**
	 * 查询所有医院
	 * @param hospitalCode
	 * @return
	 */
	public List<Map<String, Object>> getHospital(@ProjectCodeFlag String projectCode,PageRequest page);
	
}
