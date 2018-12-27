package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HisAnalysisDept;

public interface IHisAnalysisDeptDao extends IBaseDao<HisAnalysisDept, Long> {

	// 查询所有科室汇总情况
	public DataGrid<Map<String, Object>> groupByDept(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByDept(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByDept(PageRequest page);

	// 药品分析新算法 new
	public DataGrid<Map<String, Object>> groupHisMedicineByDept(PageRequest page);

	// 导出
	public List<Map<String, Object>> groupByDeptAll(PageRequest page);

	public List<Map<String, Object>> groupHisDrugByDeptAll(PageRequest page);

	public List<Map<String, Object>> groupRankingByDeptAll(PageRequest page);

	// 药品分析新算法 new------导出
	public List<Map<String, Object>> groupHisMedicineByDeptAll(PageRequest page);

}
