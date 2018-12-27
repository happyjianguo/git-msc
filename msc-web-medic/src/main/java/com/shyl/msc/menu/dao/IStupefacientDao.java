package com.shyl.msc.menu.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.Stupefacient;

public interface IStupefacientDao extends IBaseDao<Stupefacient, Long>{
	
	public Stupefacient getByExtId(Integer id, Integer type);
}
