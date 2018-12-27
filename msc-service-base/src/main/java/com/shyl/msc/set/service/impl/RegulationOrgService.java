package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IRegulationOrgDao;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IRegulationOrgService;


@Service
@Transactional(readOnly=true)
public class RegulationOrgService extends BaseService<RegulationOrg,Long> implements IRegulationOrgService{
	private IRegulationOrgDao regulationOrgDao;
	
	@Resource(name="regulationOrgDao")
	public void setRegulationOrgDao(IRegulationOrgDao regulationOrgDao) {
		this.regulationOrgDao = regulationOrgDao;
		super.setBaseDao(regulationOrgDao);
	}

	@Override
	public RegulationOrg findByCode(String projectCode, String code) {		
		return regulationOrgDao.findByCode(code);
	}
	
	
}
