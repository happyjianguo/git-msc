package com.shyl.msc.set.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.RegionCode;

public interface IRegionCodeDao extends IBaseDao<RegionCode,Long>{

	public List<RegionCode> getChlidList(Long pid);

	public List<RegionCode> getLvlone();

	public RegionCode findByCode(String code);
	
}
