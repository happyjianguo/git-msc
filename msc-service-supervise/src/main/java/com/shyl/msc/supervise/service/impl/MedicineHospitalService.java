package com.shyl.msc.supervise.service.impl;

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
import com.shyl.msc.supervise.dao.IMedicineDao;
import com.shyl.msc.supervise.dao.IMedicineHospitalDao;
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.entity.MedicineHospital;
import com.shyl.msc.supervise.service.IMedicineHospitalService;

@SuppressWarnings("unused")
@Service
@Transactional(readOnly=true)
public class MedicineHospitalService extends BaseService<MedicineHospital, Long> implements IMedicineHospitalService {

	private IMedicineHospitalDao medicineHospitalDao;
	@Resource
	private IMedicineDao medicineDao;
	@Resource
	private IDrugAnalysisDeptDao drugAnalysisDeptDao;
	@Resource
	private IDrugAnalysisDoctorDao drugAnalysisDoctorDao;
	@Resource
	private IDrugAnalysisHospitalDao drugAnalysisHospitalDao;

	@Resource
	public void setMedicineHospitalDao(IMedicineHospitalDao medicineHospitalDao) {
		setBaseDao(medicineHospitalDao);
		this.medicineHospitalDao = medicineHospitalDao;
	}

	public DataGrid<Map<String, Object>> countBaseDrugBy(String projectCode,PageRequest page, Integer queryType) {
		switch (queryType) {
		case 0:
			return medicineHospitalDao.countBaseDrugByZone(page);
		case 1:
			return medicineHospitalDao.countBaseDrugByCounty(page);
		case 2:
			return medicineHospitalDao.countBaseDrugByHospital(page);
		default:
			return null;
		}
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode,PageRequest page) {
		return medicineHospitalDao.queryByPage(page);
	}
	@Transactional
	public void updateAuxiliaryType(String projectCode,Long id, Integer isAuxiliary) {
		MedicineHospital medicH = medicineHospitalDao.getById(id);
		medicH.setAuxiliaryType(isAuxiliary);
		medicineHospitalDao.update(medicH);
		if (medicH.getProductId() != null) {
			Medicine medicine = medicineDao.getById(medicH.getProductId());
			if(medicine!=null){
				// 如果药品已经是辅助用药了，直接返回
				if (medicine.getAuxiliaryType() != null && medicine.getAuxiliaryType() == 1) {
					return;
				}
			}	
		}
		drugAnalysisDeptDao.updateAuxiliaryType(medicH.getHospitalCode(), medicH.getProductCode(), isAuxiliary);
		drugAnalysisDoctorDao.updateAuxiliaryType(medicH.getHospitalCode(), medicH.getProductCode(), isAuxiliary);
		drugAnalysisHospitalDao.updateAuxiliaryType(medicH.getHospitalCode(), medicH.getProductCode(), isAuxiliary);

	}

	@Override

	public DataGrid<Map<String, Object>> countDrugCatalogBy(String projectCode,PageRequest page, Integer queryType) {
		switch (queryType) {
		case 0:
			return medicineHospitalDao.countDrugCatalogByZone(page);
		case 1:
			return medicineHospitalDao.countDrugCatalogByCounty(page);
		case 2:
			return medicineHospitalDao.countDrugCatalogByHospital(page);
		default:
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryByAll(String projectCode, PageRequest page) {
		return medicineHospitalDao.queryByAll(page);
	}

}
