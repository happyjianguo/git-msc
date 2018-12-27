package com.shyl.msc.set.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Patient;

public interface IPatientDao extends IBaseDao<Patient, Long> {

	public Patient getByJkCode(String jkCode);
	
	public Patient getBySbCode(String sbCode);
	
	public Patient getByYbCode(String ybCode);
	
	public Patient getByIdCode(String idCode);

}
