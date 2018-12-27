package com.shyl.msc.supervise.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IClinicAnalysisDeptDao;
import com.shyl.msc.supervise.dao.IClinicAnalysisDoctorDao;
import com.shyl.msc.supervise.dao.IClinicAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisHospital;
import com.shyl.msc.supervise.service.IClinicAnalysisService;

@Service
@Transactional(readOnly=true)
public class ClinicAnalysisService extends BaseService<ClinicAnalysisHospital, Long> implements IClinicAnalysisService {
	
	@Resource
	private IClinicAnalysisDeptDao clinicAnalysisDeptDao;
	@Resource
	private IClinicAnalysisDoctorDao clinicAnalysisDoctorDao;
	@Resource
	private IClinicAnalysisHospitalDao clinicAnalysisHospitalDao;
	
	public void setClinicAnalysisHospitalDao(IClinicAnalysisHospitalDao clinicAnalysisHospitalDao) {
		super.setBaseDao(clinicAnalysisHospitalDao);
		this.clinicAnalysisHospitalDao = clinicAnalysisHospitalDao;
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryClinicAnalysisByPage(String projectCode,PageRequest page, Integer type){
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectBy(page);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceBy(page);
		case 2:
			return clinicAnalysisHospitalDao.groupCityBy(page);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyBy(page);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalBy(page);
		case 5:
			return clinicAnalysisDeptDao.groupBy(page);
		case 6:
			return clinicAnalysisDoctorDao.groupBy(page);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}

	@Override
//	@Cacheable(value = "clinicAnalysis")
	public DataGrid<Map<String, Object>> queryHisDrugInByPage(String projectCode,PageRequest page, Integer type,Integer countType) {
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectAbsDrugUser(page,countType);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceAbsDrugUser(page,countType);
		case 2:
			return clinicAnalysisHospitalDao.groupCityAbsDrugUser(page,countType);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyAbsDrugUser(page,countType);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalAbsDrugUser(page,countType);
		case 5:
			return clinicAnalysisHospitalDao.groupMonthBy(page,countType);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryLargeSumByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectLargeSum(page);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceLargeSum(page);
		case 2:
			return clinicAnalysisHospitalDao.groupCityLargeSum(page);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyLargeSum(page);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalLargeSum(page);
		case 5:
			return clinicAnalysisDeptDao.groupLargeSum(page);
		case 6:
			return clinicAnalysisDoctorDao.groupLargeSum(page);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}
	
	@Override
	public List<Map<String, Object>> selectRecipeNum(String projectCode,String monthBegin,String monthEnd){
		return clinicAnalysisHospitalDao.selectRecipeNum(monthBegin,monthEnd);
	}
	
	@Override
	public List<Map<String, Object>> selectRecipeDefault(String projectCode){
		return clinicAnalysisHospitalDao.selectRecipeDefault();
	}		
	
	@Override
	public DataGrid<Map<String, Object>> queryAbsDrugUserByPage(String projectCode,PageRequest page, Integer type,Integer countType) {
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectAbsDrugUser(page,countType);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceAbsDrugUser(page,countType);
		case 2:
			return clinicAnalysisHospitalDao.groupCityAbsDrugUser(page,countType);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyAbsDrugUser(page,countType);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalAbsDrugUser(page,countType);
		case 5:
			return clinicAnalysisDeptDao.groupAbsDrugUser(page,countType);
		case 6:
			return clinicAnalysisDoctorDao.groupAbsDrugUser(page,countType);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}
	//联合用药分析
	@Override
	public DataGrid<Map<String, Object>> queryCombDrugAnalysisByPage(String projectCode,PageRequest page, Integer type){
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectCombDrugBy(page);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceCombDrugBy(page);
		case 2:
			return clinicAnalysisHospitalDao.groupCityCombDrugBy(page);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyCombDrugBy(page);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalCombDrugBy(page);
		case 5:
			return clinicAnalysisDeptDao.groupCombDrugBy(page);
		case 6:
			return clinicAnalysisDoctorDao.groupCombDrugBy(page);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}
	
	//门/急诊药品使用分析
	@Override
	public DataGrid<Map<String, Object>> queryClinicMedicineByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return clinicAnalysisHospitalDao.selectClinicMedicine(page);
		case 1:
			return clinicAnalysisHospitalDao.groupProvinceClinicMedicine(page);
		case 2:
			return clinicAnalysisHospitalDao.groupCityClinicMedicine(page);
		case 3:
			return clinicAnalysisHospitalDao.groupCountyClinicMedicine(page);
		case 4:
			return clinicAnalysisHospitalDao.groupHospitalClinicMedicine(page);
		case 5:
			return clinicAnalysisDeptDao.groupClinicMedicine(page);
		case 6:
			return clinicAnalysisDoctorDao.groupClinicMedicine(page);
		default:
			//查询总数据
			return new DataGrid<>();
		}
	}
	
	//门/急诊药品使用分析导出
		@Override
		public List<Map<String, Object>> queryClinicMedicineByPageAll(String projectCode,PageRequest page, Integer type) {
			switch (type) {
			case 0:
				return clinicAnalysisHospitalDao.selectClinicMedicineAll(page);
			case 1:
				return clinicAnalysisHospitalDao.groupProvinceClinicMedicineAll(page);
			case 2:
				return clinicAnalysisHospitalDao.groupCityClinicMedicineAll(page);
			case 3:
				return clinicAnalysisHospitalDao.groupCountyClinicMedicineAll(page);
			case 4:
				return clinicAnalysisHospitalDao.groupHospitalClinicMedicineAll(page);
			case 5:
				return clinicAnalysisDeptDao.groupClinicMedicineAll(page);
			case 6:
				return clinicAnalysisDoctorDao.groupClinicMedicineAll(page);
			default:
				//查询总数据
				return new ArrayList<>();
			}
		}

		@Override
		public List<Map<String, Object>> groupMonthByHospital(String projectCode,PageRequest page) {
			return clinicAnalysisHospitalDao.groupMonthByHospital(page);
		}

		@Override
		public List<Map<String, Object>> groupMonthByCountyCode(String projectCode,PageRequest page) {
			return clinicAnalysisHospitalDao.groupMonthByCountyCode(page);
		}

		@Override
		public DataGrid<Map<String, Object>> groupByColumn(String projectCode, PageRequest page, String groupColumn) {
			return clinicAnalysisHospitalDao.groupByColumn(page, groupColumn);
		}

		@Override
		public BigDecimal getVisitorNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getVisitorNum(page);
		}

		@Override
		public BigDecimal getRegRecipeNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getRegRecipeNum(page);
		}

		@Override
		public BigDecimal getDrugNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getDrugNum(page);
		}

		@Override
		public BigDecimal getDrugSum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getDrugSum(page);
		}

		@Override
		public BigDecimal getSum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getSum(page);
		}

		@Override
		public List<Map<String, Object>> getCombDurgNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getCombDurgNum(page);
		}

		@Override
		public BigDecimal getRecipeNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getRecipeNum(page);
		}

		@Override
		public BigDecimal getLargeRecipeNum(String projectCode, PageRequest page) {
			return clinicAnalysisHospitalDao.getLargeRecipeNum(page);
		}
}
