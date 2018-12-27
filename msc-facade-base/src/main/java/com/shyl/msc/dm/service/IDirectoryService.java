package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Directory;

public interface IDirectoryService extends IBaseService<Directory, Long> {
	/**
	 * 查询遴选目录
	 * @param genericName
	 * @param dosageFormName
	 * @param model
	 * @param qualityLevel
	 * @return
	 */
	public Directory findDirectory(@ProjectCodeFlag String projectCode, String genericName, String dosageFormName, 
			String model, String qualityLevel, String producerNames, String minUnit, String note);
}
