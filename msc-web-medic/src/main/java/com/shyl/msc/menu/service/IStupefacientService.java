package com.shyl.msc.menu.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.Stupefacient;

public interface IStupefacientService extends IBaseService<Stupefacient, Long> {
	
	public Stupefacient getByExtId(Integer id, Integer type);
}
