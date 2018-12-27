package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IQuotaDao;
import com.shyl.msc.supervise.entity.Quota;
import com.shyl.msc.supervise.service.IQuotaService;

@Service
@Transactional
public class QuotaService extends BaseService<Quota, Long> implements IQuotaService {
	
	private IQuotaDao quotaDao;

	@Resource
	public void setQuotaDao(IQuotaDao quotaDao) {
		super.setBaseDao(quotaDao);
		this.quotaDao = quotaDao;
	}

	@Override
	public Quota getByCode(String projectCode,String code) {
		return quotaDao.getByCode(code);
	}

}
