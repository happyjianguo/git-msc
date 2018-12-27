package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.ICompanyCertDao;
import com.shyl.msc.set.entity.CompanyCert;
/**
 * 企业证照
 * 
 * @author a_Q
 *
 */
@Repository
public class CompanyCertDao extends BaseDao<CompanyCert, Long> implements ICompanyCertDao {
	@Override
	public CompanyCert getUnique(String typeCode, String code){
		return super.getByHql("from CompanyCert where typeCode=? and code=?", typeCode, code);
	}
	
}
