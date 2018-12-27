package com.shyl.msc.set.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Patient;

public interface IPatientService extends IBaseService<Patient, Long> {

	public Patient getByJkCode(@ProjectCodeFlag String projectCode, String jkCode);
	
	public Patient getBySbCode(@ProjectCodeFlag String projectCode, String sbCode);
	
	public Patient getByYbCode(@ProjectCodeFlag String projectCode, String sbCode);
	
	public Patient getByIdCode(@ProjectCodeFlag String projectCode, String sbCode);
	
	public void checkSave(@ProjectCodeFlag String projectCode, Patient patient) throws Exception;

	public Patient getByCodeAndType(@ProjectCodeFlag String projectCode, String hzsbh, String hzsbhlx);

}
