package com.shyl.msc.menu.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.menu.entity.Supplement;

public interface ISupplementService extends IBaseService<Supplement, Long> {

	public Supplement getByExtId(Integer extId);
}
