package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.entity.DrugAnalysisHospital;

public interface IDrugAnalysisService extends IBaseService<DrugAnalysisHospital, Long> {

	public DataGrid<Map<String, Object>> queryDrugAnalysisByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String, Object>> queryDrugAnalysisByAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	public DataGrid<Map<String, Object>> queryBaseDrugByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String, Object>> queryBaseDrugByAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
	
	public List<Map<String,Object>> queryByCode(@ProjectCodeFlag String projectCode,String month, String hospitalCode,String code);
}
