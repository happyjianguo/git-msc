package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HospitalZone;

public interface IHospitalZoneDao extends IBaseDao<HospitalZone,Long>{

	public HospitalZone getByCode(String hospitalCode);
	
	/**
	 * 查询所有医院
	 */
	List<Map<String, Object>> getHospital(PageRequest page);
}
