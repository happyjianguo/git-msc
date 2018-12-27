package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.DiseaseAnalysisHospital;
import com.shyl.msc.supervise.entity.DiseaseAnalysisItem;

public interface IDiseaseAnalysisService  extends IBaseService<DiseaseAnalysisHospital, Long> {

	public DataGrid<Map<String, Object>> queryDiseaseAnalysisByPage(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	public List<Map<String, Object>> queryDiseaseAnalysisByAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);

	public DataGrid<DiseaseAnalysisItem> groupProductBy(@ProjectCodeFlag String projectCode,PageRequest page);
	
	public DataGrid<Map<String, String>> groupDiseaseBy(@ProjectCodeFlag String projectCode,PageRequest page);
}
