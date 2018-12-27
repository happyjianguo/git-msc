package com.shyl.msc.supervise.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IDrugAnalysisDeptDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisDoctorDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisHospitalDao;
import com.shyl.msc.supervise.dao.IHisAnalysisDeptDao;
import com.shyl.msc.supervise.dao.IHisAnalysisDoctorDao;
import com.shyl.msc.supervise.dao.IHisAnalysisHospitalDao;
import com.shyl.msc.supervise.dao.IMedicineDao;
import com.shyl.msc.supervise.entity.HisAnalysisHospital;
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.service.IHisAnalysisService;

@Service
@Transactional(readOnly = true)
public class HisAnalysisService extends BaseService<HisAnalysisHospital, Long> implements IHisAnalysisService {
	@Resource
	private IMedicineDao medicineDao;
	@Resource
	private IDrugAnalysisDeptDao drugAnalysisDeptDao;
	@Resource
	private IDrugAnalysisDoctorDao drugAnalysisDoctorDao;
	@Resource
	private IDrugAnalysisHospitalDao drugAnalysisHospitalDao;
	@Resource
	private IHisAnalysisHospitalDao hisAnalysisHospitalDao;
	@Resource
	private IHisAnalysisDoctorDao hisAnalysisDoctorDao;
	@Resource
	private IHisAnalysisDeptDao hisAnalysisDeptDao;

	public void setHisAnalysisHospitalDao(IHisAnalysisHospitalDao hisAnalysisHospitalDao) {
		super.setBaseDao(hisAnalysisHospitalDao);
		this.hisAnalysisHospitalDao = hisAnalysisHospitalDao;
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryHisAnalysisByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupBy(page);
		case 1:
			return hisAnalysisHospitalDao.groupByProvince(page);
		case 2:
			return hisAnalysisHospitalDao.groupByCity(page);
		case 3:
			return hisAnalysisHospitalDao.groupByCountry(page);
		case 4:
			return hisAnalysisHospitalDao.groupByHospital(page);
		case 5:
			return hisAnalysisDeptDao.groupByDept(page);
		case 6:
			return hisAnalysisDoctorDao.groupByDoctor(page);
		default:
			return new DataGrid<>();
		}
	}

	@Override
	public DataGrid<Map<String, Object>> queryHisDrugByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupHisDrugBy(page);
		case 1:
			return hisAnalysisHospitalDao.groupHisDrugByProvince(page);
		case 2:
			return hisAnalysisHospitalDao.groupHisDrugByCity(page);
		case 3:
			return hisAnalysisHospitalDao.groupHisDrugByCountry(page);
		case 4:
			return hisAnalysisHospitalDao.groupHisDrugByHospital(page);
		case 5:
			return hisAnalysisDeptDao.groupHisDrugByDept(page);
		case 6:
			return hisAnalysisDoctorDao.groupHisDrugByDoctor(page);
		default:
			return new DataGrid<>();
		}
	}

	@Override
	public DataGrid<Map<String, Object>> queryDrugRankingByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupRankingBy(page);
		case 1:
			return hisAnalysisHospitalDao.groupRankingByProvince(page);
		case 2:
			return hisAnalysisHospitalDao.groupRankingByCity(page);
		case 3:
			return hisAnalysisHospitalDao.groupRankingByCountry(page);
		case 4:
			return hisAnalysisHospitalDao.groupRankingByHospital(page);
		case 5:
			return hisAnalysisDeptDao.groupRankingByDept(page);
		case 6:
			return hisAnalysisDoctorDao.groupRankingByDoctor(page);
		default:
			return new DataGrid<>();
		}
	}

	// 导出

	@Override
	public List<Map<String, Object>> queryHisAnalysisByPageAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupByAll(page);
		case 1:
			return hisAnalysisHospitalDao.groupByProvinceAll(page);
		case 2:
			return hisAnalysisHospitalDao.groupByCityAll(page);
		case 3:
			return hisAnalysisHospitalDao.groupByCountryAll(page);
		case 4:
			return hisAnalysisHospitalDao.groupByHospitalAll(page);
		case 5:
			return hisAnalysisDeptDao.groupByDeptAll(page);
		case 6:
			return hisAnalysisDoctorDao.groupByDoctorAll(page);
		default:
			return new ArrayList<>();
		}
	}

	@Override
	public List<Map<String, Object>> queryHisDrugByPageAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupHisDrugByAll(page);
		case 1:
			return hisAnalysisHospitalDao.groupHisDrugByProvinceAll(page);
		case 2:
			return hisAnalysisHospitalDao.groupHisDrugByCityAll(page);
		case 3:
			return hisAnalysisHospitalDao.groupHisDrugByCountryAll(page);
		case 4:
			return hisAnalysisHospitalDao.groupHisDrugByHospitalAll(page);
		case 5:
			return hisAnalysisDeptDao.groupHisDrugByDeptAll(page);
		case 6:
			return hisAnalysisDoctorDao.groupHisDrugByDoctorAll(page);
		default:
			return new ArrayList<>();
		}
	}

	@Override
	public List<Map<String, Object>> queryDrugRankingByPageAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupRankingByAll(page);
		case 1:
			return hisAnalysisHospitalDao.groupRankingByProvinceAll(page);
		case 2:
			return hisAnalysisHospitalDao.groupRankingByCityAll(page);
		case 3:
			return hisAnalysisHospitalDao.groupRankingByCountryAll(page);
		case 4:
			return hisAnalysisHospitalDao.groupRankingByHospitalAll(page);
		case 5:
			return hisAnalysisDeptDao.groupRankingByDeptAll(page);
		case 6:
			return hisAnalysisDoctorDao.groupRankingByDoctorAll(page);
		default:
			return new ArrayList<>();
		}
	}

	// 住院药品新算法
	@Override
	public DataGrid<Map<String, Object>> queryHisMedicineByPage(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupHisMedicineBy(page);
		case 1:
			return hisAnalysisHospitalDao.groupHisMedicineByProvince(page);
		case 2:
			return hisAnalysisHospitalDao.groupHisMedicineByCity(page);
		case 3:
			return hisAnalysisHospitalDao.groupHisMedicineByCountry(page);
		case 4:
			return hisAnalysisHospitalDao.groupHisMedicineByHospital(page);
		case 5:
			return hisAnalysisDeptDao.groupHisMedicineByDept(page);
		case 6:
			return hisAnalysisDoctorDao.groupHisMedicineByDoctor(page);
		default:
			return new DataGrid<>();
		}
	}

	@Override
	public List<Map<String, Object>> queryHisMedicineByPageAll(String projectCode,PageRequest page, Integer type) {
		switch (type) {
		case 0:
			return hisAnalysisHospitalDao.groupHisMedicineByAll(page);
		case 1:
			return hisAnalysisHospitalDao.groupHisMedicineByProvinceAll(page);
		case 2:
			return hisAnalysisHospitalDao.groupHisMedicineByCityAll(page);
		case 3:
			return hisAnalysisHospitalDao.groupHisMedicineByCountryAll(page);
		case 4:
			return hisAnalysisHospitalDao.groupHisMedicineByHospitalAll(page);
		case 5:
			return hisAnalysisDeptDao.groupHisMedicineByDeptAll(page);
		case 6:
			return hisAnalysisDoctorDao.groupHisMedicineByDoctorAll(page);
		default:
			return new ArrayList<>();
		}
	}

	@Override
	public List<Map<String, Object>> groupMonthByHospital(String projectCode,PageRequest page) {
		return hisAnalysisHospitalDao.groupMonthByHospital(page);
	}

	@Override
	public List<Map<String, Object>> groupMonthByCountyCode(String projectCode,PageRequest page) {
		return hisAnalysisHospitalDao.groupMonthByCountyCode(page);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByColumn(String projectCode,PageRequest page, String groupColumn) {
		return hisAnalysisHospitalDao.groupByColumn(page,groupColumn);
	}

	@Override
	public BigDecimal getAbsNum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getAbsNum(page);
	}

	@Override
	public BigDecimal getDrugNum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getDrugNum(page);
	}

	@Override
	public BigDecimal getSum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getSum(page);
	}

	@Override
	public BigDecimal getDaySum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getDaySum(page);
	}

	@Override
	public BigDecimal getDddSum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getDddSum(page);
	}

	@Override
	public BigDecimal getDrugSum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getDrugSum(page);
	}

	@Override
	public BigDecimal getOutNum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getOutNum(page);
	}

	@Override
	public List<Map<String, Object>> getCombInDurgNum(String projectCode, PageRequest page) {
		return hisAnalysisHospitalDao.getCombInDurgNum(page);
	}

}
