package com.shyl.msc.menu.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.BaseDrug;

public interface IBaseDrugDao extends IBaseDao<BaseDrug, Long> {
	public BaseDrug getByExtId(Integer id, Integer type);
}
