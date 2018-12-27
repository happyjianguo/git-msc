package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IClinicDiagnosisDao;
import com.shyl.msc.supervise.entity.ClinicDiagnosis;
import com.shyl.msc.supervise.service.IClinicDiagnosisService;

@SuppressWarnings("unused")
@Service
@Transactional(readOnly=true)
public class ClinicDiagnosisService extends BaseService<ClinicDiagnosis, Long> implements IClinicDiagnosisService {
	
	private IClinicDiagnosisDao diagnosisDao;

	@Resource
	public void setDiagnosisDao(IClinicDiagnosisDao diagnosisDao) {
		setBaseDao(diagnosisDao);
		this.diagnosisDao = diagnosisDao;
	}
	
	

}
