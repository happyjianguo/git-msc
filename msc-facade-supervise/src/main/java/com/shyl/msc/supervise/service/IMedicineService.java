package com.shyl.msc.supervise.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.Medicine;

public interface IMedicineService extends IBaseService<Medicine, Long> {
	
	public Integer updateAuxiliaryType(@ProjectCodeFlag String projectCode,Long productId,Integer isAuxiliary);
	
	
}
