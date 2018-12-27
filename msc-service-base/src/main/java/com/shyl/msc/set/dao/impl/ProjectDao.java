package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IProjectDao;
import com.shyl.msc.set.entity.Project;

@Repository
public class ProjectDao extends BaseDao<Project, Long> implements IProjectDao {

	@Override
	public Project getByCode(String code) {
		return this.getByHql("from Project where code=?", code);
	}
	
}
