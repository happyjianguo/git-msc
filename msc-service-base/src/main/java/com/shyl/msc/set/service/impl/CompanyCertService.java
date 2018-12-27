package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.ICompanyCertDao;
import com.shyl.msc.set.entity.CompanyCert;
import com.shyl.msc.set.service.ICompanyCertService;
/**
 * 企业证照
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class CompanyCertService extends BaseService<CompanyCert, Long> implements ICompanyCertService {	
	private ICompanyCertDao companyCertDao;

	public ICompanyCertDao getCompanyCertDao() {
		return companyCertDao;
	}

	@Resource
	public void setCompanyCertDao(ICompanyCertDao companyCertDao) {
		this.companyCertDao = companyCertDao;
		super.setBaseDao(companyCertDao);
	}

}
