package com.shyl.msc.set.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IExpertDao;
import com.shyl.msc.set.entity.Expert;

@Repository
public class ExpertDao extends BaseDao<Expert, Long> implements IExpertDao {

	public Expert getByEmpId(String expertGroupCode, String empId) {
		return this.getByHql("from Expert where expertGroupCode=? and empId=?", expertGroupCode, empId);
	}

	@Override
	public List<Map<String, Object>> listForCourseCode() {
		String sql = "select t.courseCode,t.courseName,count(t.id) as count from t_set_expert t group by t.courseCode,t.courseName";
		return super.listBySql(sql, null, Map.class);
	}
}
