package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.entity.DrugAnalysisHospital;

@Repository
public class DrugAnalysisHospitalDao extends BaseDao<DrugAnalysisHospital, Long> implements IDrugAnalysisHospitalDao {

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		return this.findBySql("select productCode,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername"
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.hospitalCode", page, Map.class);
	}
	public List<Map<String, Object>> groupByAll(PageRequest page) {
		return this.listBySql2("select productCode,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.hospitalCode", page, Map.class);
	}

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupZoneBy(PageRequest page) {
		return this.findBySql("select productCode,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode", page, Map.class);
	}
	public List<Map<String, Object>> groupZoneByAll(PageRequest page) {
		return this.listBySql2("select productCode,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode", page, Map.class);
	}

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupCountyBy(PageRequest page) {
		return this.findBySql("select productCode,c.countyCode,max(c.countyName) as countyName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.countyCode", page, Map.class);
	}
	public List<Map<String, Object>> groupCountyByAll(PageRequest page) {
		return this.listBySql2("select productCode,c.countyCode,max(c.countyName) as countyName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.countyCode", page, Map.class);
	}
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupCityBy(PageRequest page) {
		return this.findBySql("select productCode,c.cityCode,max(c.cityName) as cityName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.cityCode", page, Map.class);
	}
	public List<Map<String, Object>> groupCityByAll(PageRequest page) {
		return this.listBySql2("select productCode,c.cityCode,max(c.cityName) as cityName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.cityCode", page, Map.class);
	}
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupProvinceBy(PageRequest page) {
		return this.findBySql("select productCode,c.provinceCode,max(c.provinceName) as provinceName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ "from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.provinceCode", page, Map.class);
	}
	public List<Map<String, Object>> groupProvinceByAll(PageRequest page) {
		return this.listBySql2("select productCode,c.provinceCode,max(c.provinceName) as provinceName,sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ "from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.provinceCode", page, Map.class);
	}
	

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugBy(PageRequest page) {
		return this.findBySql("select c.hospitalCode as code,max(c.hospitalName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.hospitalCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugByAll(PageRequest page) {
		return this.listBySql2("select c.hospitalCode as code,max(c.hospitalName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.hospitalCode", page, Map.class);
	}

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugZoneBy(PageRequest page) {
		return this.findBySql("select sum(sum) as sum from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugZoneByAll(PageRequest page) {
		return this.listBySql2("select sum(sum) as sum from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode", page, Map.class);
	}

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugCountyBy(PageRequest page) {
		return this.findBySql("select c.countyCode as code,max(c.countyName) as name,sum(sum) as sum "
				+ "from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.countyCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugCountyByAll(PageRequest page) {
		return this.listBySql2("select c.countyCode as code,max(c.countyName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.countyCode", page, Map.class);
	}
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugCityBy(PageRequest page) {
		return this.findBySql("select c.cityCode as code,max(c.cityName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.cityCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugCityByAll(PageRequest page) {
		return this.listBySql2("select c.cityCode as code,max(c.cityName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.cityCode", page, Map.class);
	}
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugProvinceBy(PageRequest page) {
		return this.findBySql("select c.provinceCode as code,max(c.provinceName) as name,sum(sum) as sum  "
				+ "from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.provinceCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugProvinceByAll(PageRequest page) {
		return this.listBySql2("select c.provinceCode as code,max(c.provinceName) as name,sum(sum) as sum "
				+ "from sup_drug_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by c.provinceCode", page, Map.class);
	}
	
	public Integer updateAuxiliaryType(String productCode, Integer isAuxiliary) {
		return this.executeHql("update DrugAnalysisHospital set auxiliaryType=? where productCode=?", isAuxiliary, productCode);
	}

	public Integer updateAuxiliaryType(String hospitalCode, String productCode, Integer isAuxiliary) {
		return this.executeHql("update DrugAnalysisHospital set auxiliaryType=? where productCode=? and hospitalCode=?", isAuxiliary, productCode, hospitalCode);
	}
	/**
	 * @return
	 */
	public List<Map<String,Object>> queryByCode(String month, String hospitalCode,String code){
		String sql = "select sum(d.sum) as sum,sum(d.num) as num from sup_drug_analysis_hospital d where d.month=? and d.hospitalCode=? and d.productCode = ?";
		return super.listBySql(sql, null, Map.class, month,hospitalCode,code);
	}
}
