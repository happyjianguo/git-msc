package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisDoctor;

public interface IClinicAnalysisDoctorDao extends IBaseDao<ClinicAnalysisDoctor, Long>{
	
	public DataGrid<Map<String, Object>> groupBy(PageRequest page);
	
	/*public DataGrid<Map<String, Object>> groupByUrgent(PageRequest page);*/
	//大金额处方分析
	public DataGrid<Map<String, Object>> groupLargeSum(PageRequest page);
	//联合用药分析
	public DataGrid<Map<String, Object>> groupCombDrugBy(PageRequest page);
	//抗菌药物占药品使用比例
	public DataGrid<Map<String, Object>> groupAbsDrugUser(PageRequest page,Integer countType);
	//急诊,门诊药品使用分析页面查看
	public DataGrid<Map<String, Object>> groupClinicMedicine(PageRequest page);
	//急诊,门诊药品使用分析页面查看
	public List<Map<String, Object>> groupClinicMedicineAll(PageRequest page);

}
