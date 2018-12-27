package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IHisDiagnosisDao;
import com.shyl.msc.supervise.entity.HisDiagnosis;
import com.shyl.msc.supervise.service.IHisDiagnosisService;

@SuppressWarnings("unused")
@Service
@Transactional(readOnly=true)
public class HisDiagnosisService extends BaseService<HisDiagnosis, Long> implements IHisDiagnosisService {
	
	private IHisDiagnosisDao hisDiagnosisDao;

	@Resource
	public void setHisDiagnosisDao(IHisDiagnosisDao hisDiagnosisDao) {
		setBaseDao(hisDiagnosisDao);
		this.hisDiagnosisDao = hisDiagnosisDao;
	}

}
