package com.shyl.msc.set.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Expert;
import com.shyl.msc.set.entity.ProjectExpert;

public interface IProjectExpertService extends IBaseService<ProjectExpert,Long>{

	/**
	 * 随机抽取专家
	 * @param projectCode
	 * @param projectId 
	 * @param expertMap
	 * @return
	 * @throws Exception 
	 */
	List<Expert> rdmCourse(@ProjectCodeFlag String projectCode, Long projectId, Map<String, Object> expertMap) throws Exception;

	/**
	 * 生成专家组
	 * @param projectCode
	 * @param projectId
	 * @param expertMap
	 * @throws Exception 
	 */
	void saveRdmCourse(@ProjectCodeFlag String projectCode, Long projectId, Map<String, Object> expertMap) throws Exception;

}
