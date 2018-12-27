package com.shyl.msc.supervise.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IClinicRecipeDao;
import com.shyl.msc.supervise.dao.IDiseaseAnalysisHospitalDao;
import com.shyl.msc.supervise.dao.IDiseaseAnalysisItemDao;
import com.shyl.msc.supervise.dao.IHisRegItemDao;
import com.shyl.msc.supervise.entity.DiseaseAnalysisHospital;
import com.shyl.msc.supervise.entity.DiseaseAnalysisItem;
import com.shyl.msc.supervise.service.IDiseaseAnalysisService;

@Service
@Transactional(readOnly=true)
public class DiseaseAnalysisService extends BaseService<DiseaseAnalysisHospital, Long> 
	implements IDiseaseAnalysisService {

	  private static Logger logger = LoggerFactory.getLogger(DiseaseAnalysisService.class);
	  
	private IDiseaseAnalysisHospitalDao diseaseAnalysisHospitalDao;
	@Resource
	private IDiseaseAnalysisItemDao diseaseAnalysisItemDao;
	@Resource
	private IClinicRecipeDao clinicRecipeDao;
	@Resource
	private IHisRegItemDao hisRegItemDao;
	@Resource
	public void setDiseaseAnalysisHospitalDao(IDiseaseAnalysisHospitalDao diseaseAnalysisHospitalDao) {
		super.setBaseDao(diseaseAnalysisHospitalDao);
		this.diseaseAnalysisHospitalDao = diseaseAnalysisHospitalDao;
	}
	
	
	@Override
	public DataGrid<Map<String, Object>> queryDiseaseAnalysisByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			logger.info("进入1");
			DataGrid<Map<String, Object>> data = diseaseAnalysisHospitalDao.groupZoneBy(page);
			logger.info("进入2");
			page.setMySort(null);
			int i=0;
			for (Map<String, Object> map : data.getRows()) {
				page.getQuery().put("a#diseaseCode_S_EQ", map.get("DISEASECODE"));
				page.getQuery().put("a#isOperation_S_EQ", map.get("ISOPERATION"));
				map.put("DRUGNUM", diseaseAnalysisItemDao.getDrugNum(page));
				logger.info("进入3:"+i);
				i++;
			}
			logger.info("结束查询");
			return data;
		case 1:
			data = diseaseAnalysisHospitalDao.groupBy(page);
			page.setMySort(null);
			for (Map<String, Object> map : data.getRows()) {
				page.getQuery().put("a#diseaseCode_S_EQ", map.get("DISEASECODE"));
				page.getQuery().put("a#isOperation_S_EQ", map.get("ISOPERATION"));
				map.put("DRUGNUM", diseaseAnalysisItemDao.getDrugNum(page));
			}
			return data;
		default:
			return new DataGrid<>();
		}
	}

	@Override
	public List<Map<String, Object>> queryDiseaseAnalysisByAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			List<Map<String, Object>> data = diseaseAnalysisHospitalDao.groupZoneByAll(page);
			page.setMySort(null);
			for (Map<String, Object> map : data) {
				page.getQuery().put("a#diseaseCode_S_EQ", map.get("DISEASECODE"));
				page.getQuery().put("a#isOperation_S_EQ", map.get("ISOPERATION"));
				map.put("DRUGNUM", diseaseAnalysisItemDao.getDrugNum(page));
			}
			return data;
		case 1:
			data = diseaseAnalysisHospitalDao.groupByAll(page);
			page.setMySort(null);
			for (Map<String, Object> map : data) {
				page.getQuery().put("a#diseaseCode_S_EQ", map.get("DISEASECODE"));
				page.getQuery().put("a#isOperation_S_EQ", map.get("ISOPERATION"));
				map.put("DRUGNUM", diseaseAnalysisItemDao.getDrugNum(page));
			}
			return data;
		default:
			return new ArrayList<>();
		}
	}

	
	public DataGrid<DiseaseAnalysisItem> groupProductBy(String projectCode,PageRequest page) {
		return diseaseAnalysisItemDao.groupProductBy(page);
	}
	
	public DataGrid<Map<String, String>> groupDiseaseBy(String projectCode,PageRequest page) {
		return diseaseAnalysisHospitalDao.groupDiseaseBy(page);
	}
}
