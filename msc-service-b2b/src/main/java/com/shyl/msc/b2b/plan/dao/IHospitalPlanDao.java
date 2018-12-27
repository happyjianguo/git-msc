package com.shyl.msc.b2b.plan.dao;


import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.plan.entity.HospitalPlan;

public interface IHospitalPlanDao extends IBaseDao<HospitalPlan, Long> {
	
	public DataGrid<Map<String, Object>> nquery(PageRequest pageable, Integer status, String hospitalCode);
	/**
	 * 获取项目计划
	 * @param projectId
	 * @param hospitalCode
	 * @return
	 */
	public HospitalPlan getByDetailId(Long projectDetailId, String hospitalCode);
	
	/**
	 * 根据项目明细ID获取条数
	 * @param projectId
	 * @return
	 */
	public Long getCountByDetailId(Long projectDetailId);
	
	/**
	 * 根据项目ID获取条数
	 * @param projectId
	 * @return
	 */
	public Long getCountByProjectId(Long projectId);

	public List<HospitalPlan> listByProjectDetailId(Long projectDetailId);
	
	public DataGrid<Map<String, Object>> tradeByProduct(PageRequest pageable, String startDate, String endDate);

	public DataGrid<Map<String, Object>> tradeDetailByProduct(PageRequest pageable, String startDate, String endDate);
	
	public DataGrid<Map<String, Object>> reportForProductPlan(String projectCode, String startDate, String endDate,PageRequest pageable);
	public DataGrid<Map<String, Object>> reportDetailForProductPlan(String projectCode, String startDate,String endDate, PageRequest pageable);
}
