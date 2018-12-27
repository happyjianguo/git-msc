package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.MedicineHospital;

public interface IMedicineHospitalService extends IBaseService<MedicineHospital, Long> {

	public DataGrid<Map<String, Object>> queryByPage(@ProjectCodeFlag String projectCode,PageRequest page);

	public DataGrid<Map<String, Object>> countBaseDrugBy(@ProjectCodeFlag String projectCode,PageRequest page, Integer queryType);

	// 药品目录品规数
	public DataGrid<Map<String, Object>> countDrugCatalogBy(@ProjectCodeFlag String projectCode,PageRequest page, Integer queryType);

	public void updateAuxiliaryType(@ProjectCodeFlag String projectCode,Long id, Integer isAuxiliary);
	
	//药品目录导出
	public List<Map<String,Object>> queryByAll(@ProjectCodeFlag String projectCode,PageRequest page);

}
