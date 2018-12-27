package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.DrugAnalysisDept;

public interface IDrugAnalysisDeptDao extends IBaseDao<DrugAnalysisDept, Long> {

	public DataGrid<Map<String, Object>> groupBy(PageRequest page);
	
	public List<Map<String, Object>> groupByAll(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupBaseDrugBy(PageRequest page);
	
	public List<Map<String, Object>> groupBaseDrugByAll(PageRequest page);
	
	public Integer updateAuxiliaryType(String productCode, Integer isAuxiliary);
	
	public Integer updateAuxiliaryType(String hospitalCode, String productCode, Integer isAuxiliary);
}
