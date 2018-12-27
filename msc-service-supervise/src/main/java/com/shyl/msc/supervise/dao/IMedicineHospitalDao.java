package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.entity.MedicineHospital;

public interface IMedicineHospitalDao extends IBaseDao<MedicineHospital, Long> {

	public DataGrid<Map<String, Object>> countBaseDrugByHospital(PageRequest page);

	public DataGrid<Map<String, Object>> countBaseDrugByZone(PageRequest page);
	
	public DataGrid<Map<String, Object>> countBaseDrugByCounty(PageRequest page);

	// 药品目录查询
	public DataGrid<Map<String, Object>> countDrugCatalogByCounty(PageRequest page);
	
	public DataGrid<Map<String, Object>> countDrugCatalogByHospital(PageRequest page);

	public DataGrid<Map<String, Object>> countDrugCatalogByZone(PageRequest page);

	public DataGrid<Map<String, Object>> queryByPage(PageRequest page);
	
	public MedicineHospital getByCode(String code);
	
	//医院药品目录导出
	public List<Map<String,Object>> queryByAll(PageRequest page);
}
