package com.shyl.msc.set.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.RegulationOrg;

public interface IRegulationOrgDao extends IBaseDao<RegulationOrg,Long>{
	/**
	 * 根据编码查询
	 * @param code
	 * @return
	 */
	public RegulationOrg findByCode(String code);
}
