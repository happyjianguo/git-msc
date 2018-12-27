package com.shyl.msc.set.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Project;

public interface IProjectService extends IBaseService<Project, Long> {
	/**
	 * 通过编码获取药品
	 * @param code
	 * @return
	 */
	public Project getByCode(@ProjectCodeFlag String projectCode, String code);
}
