package com.shyl.msc.set.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Expert;

public interface IExpertService extends IBaseService<Expert, Long> {

	public Expert getByEmpId(@ProjectCodeFlag String projectCode, String expertGroupCode, String empId);

	public List<Map<String, Object>> listForCourseCode(@ProjectCodeFlag String projectCode);
}
