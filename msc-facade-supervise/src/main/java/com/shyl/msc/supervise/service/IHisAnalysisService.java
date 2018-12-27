package com.shyl.msc.supervise.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.HisAnalysisHospital;

public interface IHisAnalysisService extends IBaseService<HisAnalysisHospital, Long> {

	public DataGrid<Map<String, Object>> queryHisAnalysisByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	// 查询药品排名
	public DataGrid<Map<String, Object>> queryDrugRankingByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	// 住院药品使用、住院药品使用强度查询
	public DataGrid<Map<String, Object>> queryHisDrugByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	// 药品分析新算法 new
	public DataGrid<Map<String, Object>> queryHisMedicineByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	// 导出
	public List<Map<String, Object>> queryHisAnalysisByPageAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	public List<Map<String, Object>> queryDrugRankingByPageAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	public List<Map<String, Object>> queryHisDrugByPageAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	// 药品分析新算法 new------导出
	public List<Map<String, Object>> queryHisMedicineByPageAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String, Object>> groupMonthByHospital(@ProjectCodeFlag String projectCode,PageRequest page);
	
	public List<Map<String, Object>> groupMonthByCountyCode(@ProjectCodeFlag String projectCode,PageRequest page);
	//按照医院进行统计
	public DataGrid<Map<String, Object>> groupByColumn(@ProjectCodeFlag String projectCode,PageRequest page, String groupColumn);
	
	public BigDecimal getAbsNum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getDrugNum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getSum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getDaySum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getDddSum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getDrugSum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getOutNum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public List<Map<String, Object>> getCombInDurgNum(@ProjectCodeFlag String projectCode,PageRequest page);
	
}
