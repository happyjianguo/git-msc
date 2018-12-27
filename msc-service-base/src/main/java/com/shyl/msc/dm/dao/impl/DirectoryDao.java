package com.shyl.msc.dm.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IDirectoryDao;
import com.shyl.msc.dm.entity.Directory;

@Repository
public class DirectoryDao extends BaseDao<Directory, Long> implements IDirectoryDao {

	
	public Directory findDirectory(String genericName, String dosageFormName, String model, 
			String qualityLevel, String producerNames, String minUnit, String note) {
		
		
		return this.getByHql(" from Directory where genericName=? and dosageFormName=? and model=? and qualityLevel=? and producerNames=? and minUnit=? and note=?",
				genericName, dosageFormName, model, qualityLevel, producerNames, minUnit, note);
	}
}
