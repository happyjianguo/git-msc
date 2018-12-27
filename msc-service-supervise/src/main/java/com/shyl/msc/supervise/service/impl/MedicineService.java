package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IDrugAnalysisDeptDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisDoctorDao;
import com.shyl.msc.supervise.dao.IDrugAnalysisHospitalDao;
import com.shyl.msc.supervise.dao.IMedicineDao;
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.service.IMedicineService;

@Service
@Transactional(readOnly=true)
public class MedicineService extends BaseService<Medicine, Long> implements IMedicineService {

	private IMedicineDao medicineDao;
	@Resource
	private IDrugAnalysisDeptDao drugAnalysisDeptDao;
	@Resource
	private IDrugAnalysisDoctorDao drugAnalysisDoctorDao;
	@Resource
	private IDrugAnalysisHospitalDao drugAnalysisHospitalDao;

	public IMedicineDao getMedicineDao() {
		return medicineDao;
	}
	@Resource
	public void setMedicineDao(IMedicineDao medicineDao) {
		setBaseDao(medicineDao);
		this.medicineDao = medicineDao;
	}

	@Transactional
	public Integer updateAuxiliaryType(String projectCode,Long productId,Integer isAuxiliary) {
		Medicine medic = medicineDao.getById(productId);
		medic.setAuxiliaryType(isAuxiliary);
		medicineDao.update(medic);
		//修改数据
		drugAnalysisDeptDao.updateAuxiliaryType(medic.getCode(), isAuxiliary);
		drugAnalysisDoctorDao.updateAuxiliaryType(medic.getCode(), isAuxiliary);
		drugAnalysisHospitalDao.updateAuxiliaryType(medic.getCode(), isAuxiliary);
		return 1;
		
	}
}
