package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDirectoryDao;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.dm.service.IDirectoryService;

@Service
@Transactional
public class DirectoryService extends BaseService<Directory, Long> implements IDirectoryService {

	private IDirectoryDao directoryDao;

	@Resource
	public void setDirectoryDao(IDirectoryDao directoryDao) {
		this.directoryDao = directoryDao;
		super.setBaseDao(directoryDao);
	}
	@Override
	public Directory findDirectory(String projectCode, String genericName, String dosageFormName, 
			String model, String qualityLevel, String producerNames, String minUnit, String note) {
		return directoryDao.findDirectory(genericName, dosageFormName, model, qualityLevel, producerNames,minUnit,note);
	}
}
