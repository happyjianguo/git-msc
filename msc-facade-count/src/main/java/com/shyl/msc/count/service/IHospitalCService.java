package com.shyl.msc.count.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.HospitalC;

public interface IHospitalCService extends IBaseService<HospitalC, Long> {
	/**
	 * 获得对象
	 * @param month
	 * @param hospitalCode
	 * @param hospitalName
	 * @return
	 */
	public HospitalC getHospitalC(@ProjectCodeFlag String projectCode, String month, String hospitalCode, String hospitalName);
	/**
	 * 批量修改
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);
}
