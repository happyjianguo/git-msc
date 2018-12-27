package com.shyl.msc.supervise.dao.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IDiseaseAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.DiseaseAnalysisHospital;

@Repository
public class DiseaseAnalysisHospitalDao extends BaseDao<DiseaseAnalysisHospital, Long> implements IDiseaseAnalysisHospitalDao {

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		return this.findBySql("select a.hospitalCode,max(a.hospitalName) as hospitalName ,diseaseCode,max(diseaseName) as diseaseName,sum(sum) as sum,"
				+ "sum(treatmentTotal) as treatmentTotal,isOperation,round(sum(treatmentRate),3) as treatmentRate "
				+ " from sup_disease_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode  group by a.hospitalCode,diseaseCode,isOperation", page, Map.class);
	}
	public List<Map<String, Object>> groupByAll(PageRequest page) {
		return this.listBySql2("select a.hospitalCode,max(a.hospitalName) as hospitalName ,diseaseCode,max(diseaseName) as diseaseName,sum(sum) as sum,"
				+ "sum(treatmentTotal) as treatmentTotal,isOperation,round(sum(treatmentRate),3) as treatmentRate "
				+ " from sup_disease_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode  group by a.hospitalCode,diseaseCode,isOperation", page, Map.class);
	}

	/**
	 * group by 所有数据
	 * @return
	 */
	public DataGrid<Map<String, Object>> groupZoneBy(PageRequest page) {
		return this.findBySql("select diseaseCode,max(diseaseName) as diseaseName,sum(sum) as sum,"
				+ "sum(treatmentTotal) as treatmentTotal,isOperation,round(sum(treatmentTotal)/sum(treatmentTotal/treatmentRate),3) as treatmentRate "
				+ " from sup_disease_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by diseaseCode,isOperation", page, Map.class);
	}
	public List<Map<String, Object>> groupZoneByAll(PageRequest page) {
		return this.listBySql2("select diseaseCode,max(diseaseName) as diseaseName,sum(sum) as sum,"
				+ "sum(treatmentTotal) as treatmentTotal,isOperation,round(sum(treatmentRate),3) as treatmentRate "
				+ " from sup_disease_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode group by diseaseCode,isOperation", page, Map.class);
	}

	public DataGrid<Map<String, String>> groupDiseaseBy(PageRequest page) {
		String sql =  "select diseaseCode,diseaseName from sup_disease_analysis_hospital a,sup_hospital_zone c where a.hospitalCode=c.hospitalCode ";
		return this.findBySql(sql, page, Map.class);
	}
}
