package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IProjectDao;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.service.IProjectService;

@Service
@Transactional
public class ProjectService extends BaseService<Project, Long> implements IProjectService {

	private IProjectDao projectDao;

	public IProjectDao getProjectDao() {
		return projectDao;
	}

	@Resource
	public void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
		super.setBaseDao(projectDao);
	}
	
	@Override
	public Project getByCode(String projectCode, String code) {
		return this.projectDao.getByCode(code);
	}
}
