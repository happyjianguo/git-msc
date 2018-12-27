package com.shyl.msc.supervise.service.impl;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IDrugAnalysisDeptDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisDoctorDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.entity.DrugAnalysisHospital;
import com.shyl.msc.supervise.service.IDrugAnalysisService;

@Service
@Transactional(readOnly=true)
public class DrugAnalysisService extends BaseService<DrugAnalysisHospital, Long> implements IDrugAnalysisService {

	private IDrugAnalysisHospitalDao drugAnalysisHospitalDao;
	@Resource
	private IDrugAnalysisDoctorDao drugAnalysisDoctorDao;
	@Resource
	private IDrugAnalysisDeptDao drugAnalysisDeptDao;
	@Resource
	public void setDrugAnalysisHospitalDao(IDrugAnalysisHospitalDao drugAnalysisHospitalDao) {
		super.setBaseDao(drugAnalysisHospitalDao);
		this.drugAnalysisHospitalDao = drugAnalysisHospitalDao;
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryDrugAnalysisByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
			case 0:
				return drugAnalysisHospitalDao.groupZoneBy(page);
			case 1:
				return drugAnalysisHospitalDao.groupProvinceBy(page);
			case 2:
				return drugAnalysisHospitalDao.groupCityBy(page);
			case 3:
				return drugAnalysisHospitalDao.groupCountyBy(page);
			case 4:
				return drugAnalysisHospitalDao.groupBy(page);
			case 5:
				return drugAnalysisDeptDao.groupBy(page);
			case 6:
				return drugAnalysisDoctorDao.groupBy(page);
			default:
				return new DataGrid<>();
		}
	}
	
	@Override
	public List<Map<String, Object>> queryDrugAnalysisByAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return drugAnalysisHospitalDao.groupZoneByAll(page);
		case 1:
			return drugAnalysisHospitalDao.groupProvinceByAll(page);
		case 2:
			return drugAnalysisHospitalDao.groupCityByAll(page);
		case 3:
			return drugAnalysisHospitalDao.groupCountyByAll(page);
		case 4:
			return drugAnalysisHospitalDao.groupByAll(page);
		case 5:
			return drugAnalysisDeptDao.groupByAll(page);
		case 6:
			return drugAnalysisDoctorDao.groupByAll(page);
		default:
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<Map<String, Object>> queryBaseDrugByAll(String projectCode,PageRequest page, Integer type) {
		List<Map<String, Object>> data = null;
		List<Map<String, Object>> data1 = null;
		String baseDrugType = (String)page.getQuery().get("baseDrugType_S_EQ");
		page.getQuery().remove("baseDrugType_S_EQ");
		switch (type) {
			case 0:
				
				//查询所有药品汇总数据
				data1 = drugAnalysisHospitalDao.groupBaseDrugZoneByAll(page);
				break;
			case 1:
				
				data1 = drugAnalysisHospitalDao.groupBaseDrugProvinceByAll(page);
				break;
			case 2:
				
				data1 = drugAnalysisHospitalDao.groupBaseDrugCityByAll(page);
				break;
			case 3:
				
				data1 = drugAnalysisHospitalDao.groupBaseDrugCountyByAll(page);
				break;
			case 4:
				
				data1 = drugAnalysisHospitalDao.groupBaseDrugByAll(page);
				break;
			case 5:
				
				data1 = drugAnalysisDeptDao.groupBaseDrugByAll(page);
				break;
			case 6:
				
				data1 = drugAnalysisDoctorDao.groupBaseDrugByAll(page);
				break;
			default:
				return new ArrayList<>();
		}
		if (data1.size() > 0) {
			List<String> codes = new ArrayList<>();
			for (Map<String, Object> bean : data1) {
				codes.add((String)bean.get("CODE"));
			}
			page.getQuery().put("baseDrugType_S_EQ", baseDrugType);
			page.getQuery().put("baseDrugType_NULL_NOT",1);
			if(type==5){
				String newCode = "";
				for (String str : codes) {
					newCode+="'"+str+"',";
				}
				page.getQuery().put("departCode_S_IN", newCode);
			}
			switch (type) {
			case 0:
				data = drugAnalysisHospitalDao.groupBaseDrugZoneByAll(page);
				break;
			case 1:
				data = drugAnalysisHospitalDao.groupBaseDrugProvinceByAll(page);
				break;
			case 2:
				data = drugAnalysisHospitalDao.groupBaseDrugCityByAll(page);
				break;
			case 3:
				data = drugAnalysisHospitalDao.groupBaseDrugCountyByAll(page);
				break;
			case 4:
				data = drugAnalysisHospitalDao.groupBaseDrugByAll(page);
				break;
			case 5:
				data = drugAnalysisDeptDao.groupBaseDrugByAll(page);
				break;
			case 6:
				data = drugAnalysisDoctorDao.groupBaseDrugByAll(page);
				break;
			default:
				return new ArrayList<>();
		}Map<String, BigDecimal> map = new HashMap<>();
		for (Map<String, Object> bean : data) {
			String key = (String)bean.get("CODE");
			if (key == null) {
				key = "";
			}
			map.put(key, bean.get("SUM") == null ? new BigDecimal(0): (BigDecimal)bean.get("SUM"));
		}
		for (Map<String, Object> bean : data1) {
			String key = (String)bean.get("CODE");
			if (key == null) {
				key = "";
			}
			//查询基药汇总数据
			BigDecimal baseDrugSum = map.get(key);
			if (baseDrugSum == null) {
				baseDrugSum = new BigDecimal(0);
			}
			bean.put("BASEDRUGSUM", baseDrugSum);
			if(bean.get("SUM")!=null){
				bean.put("SUM", bean.get("SUM") == null ? new BigDecimal(0): (BigDecimal)bean.get("SUM"));
				bean.put("JYZB", baseDrugSum.multiply(new BigDecimal(100)).divide((BigDecimal)bean.get("SUM"), 2, RoundingMode.HALF_UP));
			}else{
				data1 =  new ArrayList<>();
			}
		}
		}
		return data1;
	}
	

	
	@Override
	public DataGrid<Map<String, Object>> queryBaseDrugByPage(String projectCode,PageRequest page, Integer type) {
		DataGrid<Map<String, Object>> data = null;
		DataGrid<Map<String, Object>> data1 = null;
		
		String baseDrugType = (String)page.getQuery().get("baseDrugType_L_EQ");
		page.getQuery().remove("baseDrugType_L_EQ");
		
		switch (type) {
			case 0:
				data1 = drugAnalysisHospitalDao.groupBaseDrugZoneBy(page);				
				break;
			case 1:
				data1 = drugAnalysisHospitalDao.groupBaseDrugProvinceBy(page);
				break;
			case 2:
				data1 = drugAnalysisHospitalDao.groupBaseDrugCityBy(page);
				break;
			case 3:
				data1 = drugAnalysisHospitalDao.groupBaseDrugCountyBy(page);
				break;
			case 4:
				data1 = drugAnalysisHospitalDao.groupBaseDrugBy(page);
				break;
			case 5:
				data1 = drugAnalysisDeptDao.groupBaseDrugBy(page);
				break;
			case 6:
				data1 = drugAnalysisDoctorDao.groupBaseDrugBy(page);
				break;
			default:
				return new DataGrid<>();
		}

		if (data1.getRows().size() > 0) {
			List<String> codes = new ArrayList<>();
			for (Map<String, Object> bean : data1.getRows()) {
				codes.add((String)bean.get("CODE"));
			}
			page.getQuery().put("baseDrugType_L_EQ", baseDrugType);
			page.getQuery().put("baseDrugType_NULL_NOT",1);
			if(type==5){
				String newCode = "";
				for (String str : codes) {
					newCode+="'"+str+"',";
				}
				page.getQuery().put("departCode_S_IN", newCode);
			}	
			switch (type) {
				case 0:
					data = drugAnalysisHospitalDao.groupBaseDrugZoneBy(page);
					break;
				case 1:
					data = drugAnalysisHospitalDao.groupBaseDrugProvinceBy(page);
					break;
				case 2:
					data = drugAnalysisHospitalDao.groupBaseDrugCityBy(page);
					break;
				case 3:
					data = drugAnalysisHospitalDao.groupBaseDrugCountyBy(page);
					break;
				case 4:
					data = drugAnalysisHospitalDao.groupBaseDrugBy(page);
					break;
				case 5:
					data = drugAnalysisDeptDao.groupBaseDrugBy(page);
					break;
				case 6:
					data = drugAnalysisDoctorDao.groupBaseDrugBy(page);
					break;
				default:
					return new DataGrid<>();
			}
			Map<String, BigDecimal> map = new HashMap<>();
			for (Map<String, Object> bean : data.getRows()) {
				String key = (String)bean.get("CODE");
				if (key == null) {
					key = "";
				}
				map.put(key, bean.get("SUM") == null ? new BigDecimal(0): (BigDecimal)bean.get("SUM"));
			}
			for (Map<String, Object> bean : data1.getRows()) {
				String key = (String)bean.get("CODE");
				if (key == null) {
					key = "";
				}
				//查询基药汇总数据
				BigDecimal baseDrugSum = map.get(key);
				if (baseDrugSum == null) {
					baseDrugSum = new BigDecimal(0);
				}
				bean.put("BASEDRUGSUM", baseDrugSum);
				if(bean.get("SUM")!=null){
					bean.put("SUM", bean.get("SUM") == null ? new BigDecimal(0): (BigDecimal)bean.get("SUM"));
					bean.put("JYZB", baseDrugSum.multiply(new BigDecimal(100)).divide((BigDecimal)bean.get("SUM"), 2, RoundingMode.HALF_UP));
				}else{
					data1 = new DataGrid<Map<String, Object>>();
				}
			}

			return data1;
		} else {
			return new DataGrid<>();
		}
	}

	@Override
	public List<Map<String,Object>> queryByCode(String projectCode,String month, String hospitalCode, String code) {
		return drugAnalysisHospitalDao.queryByCode(month,hospitalCode,code);
	}
}
