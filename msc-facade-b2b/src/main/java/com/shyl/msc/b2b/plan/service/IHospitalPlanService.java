package com.shyl.msc.b2b.plan.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;
import com.shyl.sys.entity.User;

public interface IHospitalPlanService extends IBaseService<HospitalPlan, Long> {

	public DataGrid<Map<String, Object>> nquery(@ProjectCodeFlag String projectCode, PageRequest pageable, Integer status, String hospitalCode);
	/**
	 * 获取项目计划
	 * @param projectId
	 * @param hospitalCode
	 * @return
	 */
	public HospitalPlan getByDetailId(@ProjectCodeFlag String projectCode, Long projectDetailId, String hospitalCode);
	
	/**
	 * 根据项目ID获取条数
	 * @param projectId
	 * @return
	 */
	public Long getCountByDetailId(@ProjectCodeFlag String projectCode, Long projectDetailId);
	
	
	/**
	 * 根据项目ID获取条数
	 * @param projectId
	 * @return
	 */
	public Long getCountByProjectId(@ProjectCodeFlag String projectCode, Long ProjectId);

	public List<HospitalPlan> listByProjectDetailId(@ProjectCodeFlag String projectCode, Long projectDetailId);

	public DataGrid<Map<String, Object>> tradeByProduct(@ProjectCodeFlag String projectCode, PageRequest pageable, String startDate, String endDate);

	public DataGrid<Map<String, Object>> tradeDetailByProduct(@ProjectCodeFlag String projectCode, PageRequest pageable, String startDate, String endDate);

	public JSONArray getToGPO(@ProjectCodeFlag String projectCode, long xmmxdh_l);
	
	public void setup(@ProjectCodeFlag String projectCode,String jsonStr, User user);
	
	public DataGrid<Map<String, Object>> reportForProductPlan(@ProjectCodeFlag String projectCode, String startDate, String endDate,PageRequest pageable);
	public DataGrid<Map<String, Object>> reportDetailForProductPlan(@ProjectCodeFlag String projectCode, String startDate, String endDate,PageRequest pageable);
}
