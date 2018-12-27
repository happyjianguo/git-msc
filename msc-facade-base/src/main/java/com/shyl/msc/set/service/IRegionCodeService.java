package com.shyl.msc.set.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.RegionCode;

public interface IRegionCodeService extends IBaseService<RegionCode,Long>{

	public List<RegionCode> getChlidList(@ProjectCodeFlag String projectCode, Long pid);

	public Integer getNewSort(@ProjectCodeFlag String projectCode, Long parentId);

	public List<RegionCode> getLvlone(@ProjectCodeFlag String projectCode);

	public void mktree(@ProjectCodeFlag String projectCode);

	public RegionCode findByCode(@ProjectCodeFlag String projectCode, String code);

}
