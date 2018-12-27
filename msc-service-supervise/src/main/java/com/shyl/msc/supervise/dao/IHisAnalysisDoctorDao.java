package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HisAnalysisDoctor;

public interface IHisAnalysisDoctorDao extends IBaseDao<HisAnalysisDoctor, Long> {

	// 根据医生分组查询
	public DataGrid<Map<String, Object>> groupByDoctor(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByDoctor(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByDoctor(PageRequest page);

	// 药品分析新算法 new
	public DataGrid<Map<String, Object>> groupHisMedicineByDoctor(PageRequest page);

	// 导出
	public List<Map<String, Object>> groupByDoctorAll(PageRequest page);

	public List<Map<String, Object>> groupHisDrugByDoctorAll(PageRequest page);

	public List<Map<String, Object>> groupRankingByDoctorAll(PageRequest page);

	// 药品分析新算法 new------导出
	public List<Map<String, Object>> groupHisMedicineByDoctorAll(PageRequest page);
}
