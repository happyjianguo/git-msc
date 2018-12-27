package com.shyl.msc.set.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IProjectDetailDao;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.service.IProjectDetailService;

@Service
@Transactional
public class ProjectDetailService extends BaseService<ProjectDetail, Long> implements IProjectDetailService {

	private IProjectDetailDao projectDetailDao;

	public IProjectDetailDao getProjectDetailDao() {
		return projectDetailDao;
	}

	@Resource
	public void setProjectDetailDao(IProjectDetailDao projectDetailDao) {
		this.projectDetailDao = projectDetailDao;
		super.setBaseDao(projectDetailDao);
	}
	
	@Override
	public int deleteByProjectId(String projectCode, Long projectId) {
		return this.projectDetailDao.deleteByProjectId(projectId);
	}

	@Override
	public List<ProjectDetail> listByMonth(String projectCode, String month) {
		return projectDetailDao.listByMonth(month);
	}

	@Override
	public ProjectDetail getByDirectoryId(String projectCode, Long projectId, Long directoryId) {
		return projectDetailDao.getByDirectoryId(projectId, directoryId);
	}

	@Override
	public List<ProjectDetail> listByProjectCode(String project_code, String projectCode) {
		return projectDetailDao.listByProjectCode(projectCode);
	}
}
