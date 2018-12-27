package com.shyl.msc.supervise.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HisAnalysisHospital;

public interface IHisAnalysisHospitalDao extends IBaseDao<HisAnalysisHospital, Long> {
	// 出院人次药品费用、住院床日药品费用、住院抗菌药物使用率、住院抗菌药物强度查询
	public DataGrid<Map<String, Object>> groupBy(PageRequest page);

	public DataGrid<Map<String, Object>> groupByProvince(PageRequest page);

	public DataGrid<Map<String, Object>> groupByCity(PageRequest page);

	public DataGrid<Map<String, Object>> groupByCountry(PageRequest page);

	public DataGrid<Map<String, Object>> groupByHospital(PageRequest page);

	// 住院药品使用分析和主要药品使用强度分析模块查询
	public DataGrid<Map<String, Object>> groupHisDrugBy(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByProvince(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByCity(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByCountry(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisDrugByHospital(PageRequest page);

	// 药品排名查询
	public DataGrid<Map<String, Object>> groupRankingBy(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByProvince(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByCity(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByCountry(PageRequest page);

	public DataGrid<Map<String, Object>> groupRankingByHospital(PageRequest page);
	
	//药品分析新算法   new
	public DataGrid<Map<String, Object>> groupHisMedicineBy(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisMedicineByProvince(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisMedicineByCity(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisMedicineByCountry(PageRequest page);

	public DataGrid<Map<String, Object>> groupHisMedicineByHospital(PageRequest page);
	
	
	/**导出**/
	// 出院人次药品费用、住院床日药品费用、住院抗菌药物使用率、住院抗菌药物强度查询
		public List<Map<String, Object>> groupByAll(PageRequest page);
        
		public List<Map<String, Object>> groupByProvinceAll(PageRequest page);
        
		public List<Map<String, Object>> groupByCityAll(PageRequest page);
       
		public List<Map<String, Object>> groupByCountryAll(PageRequest page);
       
		public List<Map<String, Object>> groupByHospitalAll(PageRequest page);

		// 住院药品使用分析和主要药品使用强度分析模块查询
		public List<Map<String, Object>> groupHisDrugByAll(PageRequest page);
    
		public List<Map<String, Object>> groupHisDrugByProvinceAll(PageRequest page);
        
		public List<Map<String, Object>> groupHisDrugByCityAll(PageRequest page);
    
		public List<Map<String, Object>> groupHisDrugByCountryAll(PageRequest page);

		public List<Map<String, Object>> groupHisDrugByHospitalAll(PageRequest page);

		// 药品排名查询
		public List<Map<String, Object>> groupRankingByAll(PageRequest page);
       
		public List<Map<String, Object>> groupRankingByProvinceAll(PageRequest page);
        
		public List<Map<String, Object>> groupRankingByCityAll(PageRequest page);
        
		public List<Map<String, Object>> groupRankingByCountryAll(PageRequest page);
     
		public List<Map<String, Object>> groupRankingByHospitalAll(PageRequest page);
		
		//药品分析新算法   new------导出
		public List<Map<String, Object>> groupHisMedicineByAll(PageRequest page);

		public List<Map<String, Object>> groupHisMedicineByProvinceAll(PageRequest page);

		public List<Map<String, Object>> groupHisMedicineByCityAll(PageRequest page);

		public List<Map<String, Object>> groupHisMedicineByCountryAll(PageRequest page);

		public List<Map<String, Object>> groupHisMedicineByHospitalAll(PageRequest page);
		
		public List<Map<String, Object>> groupMonthByHospital(PageRequest page);
		
		public List<Map<String, Object>> groupMonthByCountyCode(PageRequest page);
		//根据医院来统计
		public DataGrid<Map<String, Object>> groupByColumn(PageRequest page, String groupColumn);
		
		public BigDecimal getAbsNum(PageRequest page);
		
		public BigDecimal getDrugNum(PageRequest page);
		
		public BigDecimal getSum(PageRequest page);
		
		public BigDecimal getDaySum(PageRequest page);
		
		public BigDecimal getDddSum(PageRequest page);
		
		public BigDecimal getDrugSum(PageRequest page);
		
		public BigDecimal getOutNum(PageRequest page);	
		
		public List<Map<String, Object>> getCombInDurgNum(PageRequest page);
}
