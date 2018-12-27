package com.shyl.msc.b2b.judge.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.judge.dao.IServiceJudgeDao;
import com.shyl.msc.b2b.judge.entity.ServiceJudge;
import com.shyl.msc.b2b.judge.service.IServiceJudgeService;

/**
 * 服务投诉Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ServiceJudgeService extends BaseService<ServiceJudge, Long> implements IServiceJudgeService {
	private IServiceJudgeDao serviceJudgeDao;

	public IServiceJudgeDao getServiceJudgeDao() {
		return serviceJudgeDao;
	}

	@Resource
	public void setServiceJudgeDao(IServiceJudgeDao serviceJudgeDao) {
		this.serviceJudgeDao = serviceJudgeDao;
		super.setBaseDao(serviceJudgeDao);
	}
	

}
