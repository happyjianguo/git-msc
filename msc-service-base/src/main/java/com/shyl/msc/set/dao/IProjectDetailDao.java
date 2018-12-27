package com.shyl.msc.set.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.ProjectDetail;

public interface IProjectDetailDao extends IBaseDao<ProjectDetail, Long> {

	/**
	 * 根据项目ID删除明细
	 * @param projectId
	 * @return
	 */
	public int deleteByProjectId(Long projectId);

	public List<ProjectDetail> listByMonth(String month);
	
	public ProjectDetail getByDirectoryId(Long projectId, Long directoryId);

	public List<ProjectDetail> listByProjectCode(String projectCode);
}
