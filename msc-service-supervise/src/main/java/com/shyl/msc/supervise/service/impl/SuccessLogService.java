package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.ISuccessLogDao;
import com.shyl.msc.supervise.entity.SuccessLog;
import com.shyl.msc.supervise.service.ISuccessLogService;

@Service
@Transactional
public class SuccessLogService extends BaseService<SuccessLog, Long> implements ISuccessLogService {

	private ISuccessLogDao successLogDao;

	public ISuccessLogDao getSuccessLogDao() {
		return successLogDao;
	}

	@Resource
	public void setSuccessLogDao(ISuccessLogDao successLogDao) {
		setBaseDao(successLogDao);
		this.successLogDao = successLogDao;
	}

}
