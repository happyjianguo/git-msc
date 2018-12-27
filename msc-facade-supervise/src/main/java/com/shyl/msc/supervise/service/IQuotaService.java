package com.shyl.msc.supervise.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.Quota;

public interface IQuotaService extends IBaseService<Quota, Long> {
	
	public Quota getByCode(@ProjectCodeFlag String projectCode,String code); 
}
