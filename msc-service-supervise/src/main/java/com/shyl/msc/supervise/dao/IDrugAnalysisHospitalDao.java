package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.entity.DrugAnalysisHospital;

public interface IDrugAnalysisHospitalDao extends IBaseDao<DrugAnalysisHospital, Long> {

	public DataGrid<Map<String, Object>> groupBy(PageRequest page);
	
	public List<Map<String, Object>> groupByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupZoneBy(PageRequest page);
	
	public List<Map<String, Object>> groupZoneByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCountyBy(PageRequest page);
	
	public List<Map<String, Object>> groupCountyByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCityBy(PageRequest page);
	
	public List<Map<String, Object>> groupCityByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupProvinceBy(PageRequest page);
	
	public List<Map<String, Object>> groupProvinceByAll(PageRequest page);
	

	public DataGrid<Map<String, Object>> groupBaseDrugBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupBaseDrugZoneBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugZoneByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupBaseDrugCountyBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugCountyByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupBaseDrugCityBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugCityByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupBaseDrugProvinceBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugProvinceByAll(PageRequest page);
	
	public Integer updateAuxiliaryType(String productCode, Integer isAuxiliary);
	
	public Integer updateAuxiliaryType(String hospitalCode, String productCode, Integer isAuxiliary);
	
	public List<Map<String,Object>> queryByCode(String month, String hospitalCode,String code);
}
