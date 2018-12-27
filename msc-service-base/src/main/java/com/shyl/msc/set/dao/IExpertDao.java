package com.shyl.msc.set.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Expert;

public interface IExpertDao extends IBaseDao<Expert, Long> {

	public Expert getByEmpId(String expertGroupCode, String empId);

	public List<Map<String, Object>> listForCourseCode();
}
