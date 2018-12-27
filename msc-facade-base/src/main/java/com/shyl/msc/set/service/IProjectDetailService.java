package com.shyl.msc.set.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.ProjectDetail;

public interface IProjectDetailService extends IBaseService<ProjectDetail, Long> {

	/**
	 * 根据项目ID删除明细
	 * @param projectCode
	 * @return
	 */
	public int deleteByProjectId(@ProjectCodeFlag String projectCode, Long ProjectId);

	public List<ProjectDetail> listByMonth(@ProjectCodeFlag String projectCode, String month);
	
	public ProjectDetail getByDirectoryId(@ProjectCodeFlag String projectCode, Long projectId, Long directoryId);

	public List<ProjectDetail> listByProjectCode(@ProjectCodeFlag String projectCode, String projectcode);
}