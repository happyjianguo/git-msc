package com.shyl.msc.supervise.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.ClinicAnalysisHospital;

public interface IClinicAnalysisService extends IBaseService<ClinicAnalysisHospital, Long>{
	
	public DataGrid<Map<String, Object>> queryClinicAnalysisByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public DataGrid<Map<String, Object>> queryHisDrugInByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type,Integer countType);
	
	public DataGrid<Map<String, Object>> queryLargeSumByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public DataGrid<Map<String, Object>> queryAbsDrugUserByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type,Integer countType);
	
	public DataGrid<Map<String, Object>> queryCombDrugAnalysisByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String, Object>> selectRecipeNum(@ProjectCodeFlag String projectCode,String monthBegin,String monthEnd);

	public List<Map<String, Object>> selectRecipeDefault(@ProjectCodeFlag String projectCode);
	
	public DataGrid<Map<String, Object>> queryClinicMedicineByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String, Object>> queryClinicMedicineByPageAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	//关键指标按月查询分析统计
	public List<Map<String, Object>> groupMonthByHospital(@ProjectCodeFlag String projectCode,PageRequest page);
	
	public List<Map<String, Object>> groupMonthByCountyCode(@ProjectCodeFlag String projectCode,PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByColumn(@ProjectCodeFlag String projectCode, PageRequest page, String groupColumn);

	public BigDecimal getVisitorNum(@ProjectCodeFlag String projectCode, PageRequest page);

	public BigDecimal getRegRecipeNum(@ProjectCodeFlag String projectCode, PageRequest page);

	public BigDecimal getDrugNum(@ProjectCodeFlag String projectCode, PageRequest page);

	public BigDecimal getDrugSum(@ProjectCodeFlag String projectCode, PageRequest page);

	public BigDecimal getSum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public List<Map<String, Object>> getCombDurgNum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getRecipeNum(@ProjectCodeFlag String projectCode, PageRequest page);
	
	public BigDecimal getLargeRecipeNum(@ProjectCodeFlag String projectCode, PageRequest page);
}
