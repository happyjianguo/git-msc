package com.shyl.msc.dm.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Sickness;

public interface ISicknessDao extends IBaseDao<Sickness, Long> {

	public Sickness findByCode(String code);

}
