package com.shyl.msc.set.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Project;

public interface IProjectDao extends IBaseDao<Project, Long> {

	/**
	 * 通过编码获取项目
	 * @param code
	 * @return
	 */
	public Project getByCode(String code);
}
