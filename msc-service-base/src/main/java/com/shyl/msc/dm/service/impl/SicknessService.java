package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.ISicknessDao;
import com.shyl.msc.dm.entity.Sickness;
import com.shyl.msc.dm.service.ISicknessService;

@Service
@Transactional(readOnly=true)
public class SicknessService extends BaseService<Sickness, Long> implements ISicknessService {

	private ISicknessDao sicknessDao;

	public ISicknessDao getSicknessDao() {
		return sicknessDao;
	}

	@Resource
	public void setSicknessDao(ISicknessDao sicknessDao) {
		this.sicknessDao = sicknessDao;
		super.setBaseDao(sicknessDao);
	}

	@Override
	public Sickness findByCode(String projectCode, String code) {
		return sicknessDao.findByCode(code);
	}
	
	

}
