package com.shyl.msc.supervise.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisHospital;

public interface IClinicAnalysisHospitalDao extends IBaseDao<ClinicAnalysisHospital, Long>{
	
	
	//统计
	public DataGrid<Map<String, Object>> selectBy(PageRequest page);
	//按省分组
	public DataGrid<Map<String, Object>> groupProvinceBy(PageRequest page);
	//按市
	public DataGrid<Map<String, Object>> groupCityBy(PageRequest page);
	//按区
	public DataGrid<Map<String, Object>> groupCountyBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupHospitalBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupMonthBy(PageRequest page,Integer countType);
	
	public List<Map<String, Object>> selectRecipeNum(String monthBegin,String monthEnd);
	
	public List<Map<String, Object>> selectRecipeDefault();
	
	
	//大金额处方分析
	public DataGrid<Map<String, Object>> selectLargeSum(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupProvinceLargeSum(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCityLargeSum(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCountyLargeSum(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupHospitalLargeSum(PageRequest page);
	
	//抗菌药物占药品使用比例
	public DataGrid<Map<String, Object>> selectAbsDrugUser(PageRequest page,Integer countType);
	
	public DataGrid<Map<String, Object>> groupProvinceAbsDrugUser(PageRequest page,Integer countType);
	
	public DataGrid<Map<String, Object>> groupCityAbsDrugUser(PageRequest page,Integer countType);
	
	public DataGrid<Map<String, Object>> groupCountyAbsDrugUser(PageRequest page,Integer countType);
	
	public DataGrid<Map<String, Object>> groupHospitalAbsDrugUser(PageRequest page,Integer countType);
	
	//联合用药分析
	public DataGrid<Map<String, Object>> selectCombDrugBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupProvinceCombDrugBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCityCombDrugBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCountyCombDrugBy(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupHospitalCombDrugBy(PageRequest page);
	
	//急诊,门诊药品使用分析页面查看
	public DataGrid<Map<String, Object>> selectClinicMedicine(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupProvinceClinicMedicine(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCityClinicMedicine(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupCountyClinicMedicine(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupHospitalClinicMedicine(PageRequest page);
	
	//急诊,门诊药品使用分析页面导出
	public List<Map<String, Object>> selectClinicMedicineAll(PageRequest page);
	
	public List<Map<String, Object>> groupProvinceClinicMedicineAll(PageRequest page);
	
	public List<Map<String, Object>> groupCityClinicMedicineAll(PageRequest page);
	
	public List<Map<String, Object>> groupCountyClinicMedicineAll(PageRequest page);
	
	public List<Map<String, Object>> groupHospitalClinicMedicineAll(PageRequest page);

	//关键指标按月查询分析统计
	public List<Map<String, Object>> groupMonthByHospital(PageRequest page);
	
	public List<Map<String, Object>> groupMonthByCountyCode(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByColumn(PageRequest page, String groupColumn);
	
	public BigDecimal getVisitorNum(PageRequest page);

	public BigDecimal getRegRecipeNum(PageRequest page);

	public BigDecimal getDrugNum(PageRequest page);

	public BigDecimal getDrugSum(PageRequest page);

	public BigDecimal getSum(PageRequest page);
	
	public List<Map<String, Object>> getCombDurgNum(PageRequest page);
	
	public BigDecimal getRecipeNum(PageRequest page);
	
	public BigDecimal getLargeRecipeNum(PageRequest page);
}
