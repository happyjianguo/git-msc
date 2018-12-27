package com.shyl.msc.set.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IPatientDao;
import com.shyl.msc.set.entity.Patient;
import com.shyl.msc.set.service.IPatientService;

@Service
@Transactional(readOnly=true)
public class PatientService extends BaseService<Patient, Long> implements IPatientService {
	
	private IPatientDao patientDao;

	public IPatientDao getPatientDao() {
		return patientDao;
	}

	@Resource
	public void setPatientDao(IPatientDao patientDao) {
		this.patientDao = patientDao;
		super.setBaseDao(patientDao);
	}

	@Override
	public void checkSave(String projectCode, Patient patient) throws Exception {
		if (patient.getIdCode() != null 
				&& patientDao.getByJkCode(patient.getJkCode()) != null) {
			throw new MyException("身份证已存在");
		}
	}
	
	@Override
	public Patient getByJkCode(String projectCode, String jkCode) {
		return patientDao.getByJkCode(jkCode);
	}

	@Override
	public Patient getBySbCode(String projectCode, String sbCode) {
		return patientDao.getBySbCode(sbCode);
	}

	@Override
	public Patient getByYbCode(String projectCode, String ybCode) {
		return patientDao.getByYbCode(ybCode);
	}

	@Override
	public Patient getByIdCode(String projectCode, String idCode) {
		return patientDao.getByIdCode(idCode);
	}

	@Override
	public Patient getByCodeAndType(String projectCode, String code, String type) {
		if(type.equals("1")){
			return patientDao.getByIdCode(code);
		}else if(type.equals("2")){
			return patientDao.getByJkCode(code);
		}else if(type.equals("3")){
			return patientDao.getBySbCode(code);
		}else if(type.equals("4")){
			return patientDao.getByYbCode(code);
		}
		return null;
	}
	
}
