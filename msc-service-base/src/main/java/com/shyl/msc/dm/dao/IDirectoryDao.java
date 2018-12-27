package com.shyl.msc.dm.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Directory;


public interface IDirectoryDao extends IBaseDao<Directory, Long> {
	/**
	 * 查询字典
	 * @param genericName
	 * @param dosageFormName
	 * @param model
	 * @param qualityLevel
	 * @param producerNames
	 * @param minUnit
	 * @param note
	 * @return
	 */
	public Directory findDirectory(String genericName, String dosageFormName, String model, 
			String qualityLevel, String producerNames, String minUnit, String note);
}
