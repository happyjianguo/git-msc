package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisDoctorDao;
import com.shyl.msc.supervise.entity.DrugAnalysisDoctor;

@Repository
public class DrugAnalysisDoctorDao extends BaseDao<DrugAnalysisDoctor, Long> implements IDrugAnalysisDoctorDao {
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		return this.findBySql("select productCode,c.hospitalCode,max(c.hospitalName) as hospitalName,departCode,subStr(max(departname),instr(max(departname),'-')+1) as DEPARTNAME,doctorCode,max(doctorName) as doctorName,"
				+ "sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc ,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_doctor a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by productCode,c.hospitalCode,departCode,doctorCode", page, Map.class);
	}
	public List<Map<String, Object>> groupByAll(PageRequest page) {
		return this.listBySql2("select productCode,c.hospitalCode,max(c.hospitalName) as hospitalName,departCode,subStr(max(departname),instr(max(departname),'-')+1) as DEPARTNAME,doctorCode,max(doctorName) as doctorName,"
				+ "sum(sum) as sum,sum(num) as num ,sum(ddd) as ddd,"
				+ "sum(ddd*ddds)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as ddds,sum(ddd*dddc)/sum(case when nvl(ddd,0)=0 then 1 else ddd end) as dddc,max(productName) as productname,"
				+ "max(dosageformname) as dosageformname,max(model) as model,max(packdesc) as packdesc,max(producername) as producername "
				+ " from sup_drug_analysis_doctor a,sup_hospital_zone c group by productCode,c.hospitalCode,departCode,doctorCode", page, Map.class);
	}
	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBaseDrugBy(PageRequest page) {
		return this.findBySql("select c.hospitalCode,max(c.hospitalName) as hospitalName,departCode,max(departName) as departName,doctorCode as code,max(doctorName) as name,sum(sum) as sum  "
				+ " from sup_drug_analysis_doctor a,sup_hospital_zone c group by c.hospitalCode,departCode,doctorCode", page, Map.class);
	}
	public List<Map<String, Object>> groupBaseDrugByAll(PageRequest page) {
		return this.listBySql2("select c.hospitalCode,max(c.hospitalName) as hospitalName,departCode,max(departName) as departName,doctorCode as code,max(doctorName) as name,sum(sum) as sum "
				+ " from sup_drug_analysis_doctor a,sup_hospital_zone c group by c.hospitalCode,departCode,doctorCode", page, Map.class);
	}
	
	public Integer updateAuxiliaryType(String productCode, Integer isAuxiliary) {
		return this.executeHql("update DrugAnalysisDoctor set auxiliaryType=? where productCode=?", isAuxiliary, productCode);
	}

	public Integer updateAuxiliaryType(String hospitalCode, String productCode, Integer isAuxiliary) {
		return this.executeHql("update DrugAnalysisDoctor set auxiliaryType=? where productCode=? and hospitalCode=?", isAuxiliary, productCode, hospitalCode);
	}
}
