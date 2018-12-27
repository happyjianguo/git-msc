package com.shyl.msc.supervise.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IHospitalZoneDao;
import com.shyl.msc.supervise.entity.HospitalZone;
import com.shyl.msc.supervise.service.IHospitalZoneService;
@Service
@Transactional(readOnly=true)
public class HospitalZoneService extends BaseService<HospitalZone,Long> implements IHospitalZoneService{

	private IHospitalZoneDao hospitalZoneDao;
	
	public IHospitalZoneDao getHospitalZoneDao() {
		return hospitalZoneDao;
	}
	@Resource
	public void setHospitalZoneDao(IHospitalZoneDao hospitalZoneDao) {
		this.hospitalZoneDao = hospitalZoneDao;
		super.setBaseDao(hospitalZoneDao);
	}

	@Override
	public HospitalZone getByCode(String projectCode, String hospitalCode) {
		return hospitalZoneDao.getByCode(hospitalCode);
	}
	@Override
	public List<Map<String, Object>> getHospital(String projectCode, PageRequest page) {
		return hospitalZoneDao.getHospital(page);
	}

}
