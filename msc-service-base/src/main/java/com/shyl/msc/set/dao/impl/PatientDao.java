package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IPatientDao;
import com.shyl.msc.set.entity.Patient;
@Repository
public class PatientDao extends BaseDao<Patient, Long> implements IPatientDao {

	@Override
	public Patient getByJkCode(String jkCode) {
		String hql = "from Patient where jkCode=?";
		return super.getByHql(hql, jkCode);
	}

	@Override
	public Patient getBySbCode(String sbCode) {
		String hql = "from Patient where sbCode=?";
		return super.getByHql(hql, sbCode);
	}

	@Override
	public Patient getByYbCode(String ybCode) {
		String hql = "from Patient where ybCode=?";
		return super.getByHql(hql, ybCode);
	}

	@Override
	public Patient getByIdCode(String idCode) {
		String hql = "from Patient where idCode=?";
		return super.getByHql(hql, idCode);
	}

}
