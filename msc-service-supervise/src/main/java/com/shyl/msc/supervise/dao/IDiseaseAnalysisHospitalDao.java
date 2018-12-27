package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.DiseaseAnalysisHospital;

public interface IDiseaseAnalysisHospitalDao extends IBaseDao<DiseaseAnalysisHospital, Long> {
	
	public DataGrid<Map<String, Object>> groupBy(PageRequest page);
	
	public List<Map<String, Object>> groupByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupZoneBy(PageRequest page);
	
	public List<Map<String, Object>> groupZoneByAll(PageRequest page);
	
	public DataGrid<Map<String, String>> groupDiseaseBy(PageRequest page);
}
