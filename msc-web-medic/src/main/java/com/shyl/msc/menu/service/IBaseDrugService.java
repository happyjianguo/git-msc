package com.shyl.msc.menu.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.menu.entity.BaseDrug;

public interface IBaseDrugService extends IBaseService<BaseDrug, Long> {
	public BaseDrug getByExtId(Integer id, Integer type);
}
