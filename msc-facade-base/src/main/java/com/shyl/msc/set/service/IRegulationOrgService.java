package com.shyl.msc.set.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.RegulationOrg;

public interface IRegulationOrgService extends IBaseService<RegulationOrg,Long>{

	/**
	 * 根据编码查询
	 * @param code
	 * @return
	 */
	public RegulationOrg findByCode(@ProjectCodeFlag String projectCode, String code);

}
