package com.shyl.msc.menu.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.Supplement;

public interface ISupplementDao extends IBaseDao<Supplement, Long> {
	
	public Supplement getByExtId(Integer extId);
}
