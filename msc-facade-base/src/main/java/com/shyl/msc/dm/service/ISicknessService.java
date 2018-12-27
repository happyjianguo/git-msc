package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Sickness;

public interface ISicknessService extends IBaseService<Sickness, Long> {

	public Sickness findByCode(@ProjectCodeFlag String projectCode, String code);

}
