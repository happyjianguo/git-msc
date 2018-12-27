package com.shyl.msc.set.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IProjectDetailDao;
import com.shyl.msc.set.entity.ProjectDetail;

@Repository
public class ProjectDetailDao extends BaseDao<ProjectDetail, Long> implements IProjectDetailDao {

	@Override
	public int deleteByProjectId(Long projectId) {
		return this.executeSql("delete from t_set_project_detail where projectId=?", projectId);
	}

	@Override
	public List<ProjectDetail> listByMonth(String month) {
		String hql = "from ProjectDetail where to_char(project.startDate,'yyyy-MM')<=? and to_char(project.endDate,'yyyy-MM')>=?";
		return super.list(hql, null, month, month);
	}

	@Override
	public ProjectDetail getByDirectoryId(Long projectId, Long directoryId) {
		return super.getByHql("from ProjectDetail where project.id=? and directory.id=?",projectId , directoryId);
	}

	@Override
	public List<ProjectDetail> listByProjectCode(String projectCode) {
		String hql = "from ProjectDetail pd left join fetch pd.project p where p.code=?";
		return super.list(hql, null, projectCode);
	}
}
