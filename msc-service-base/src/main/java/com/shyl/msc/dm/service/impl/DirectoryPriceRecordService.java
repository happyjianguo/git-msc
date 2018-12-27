package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDirectoryPriceRecordDao;
import com.shyl.msc.dm.entity.DirectoryPriceRecord;
import com.shyl.msc.dm.service.IDirectoryPriceRecordService;

@Service
@Transactional(readOnly=true)
public class DirectoryPriceRecordService extends BaseService<DirectoryPriceRecord,Long> implements IDirectoryPriceRecordService{
	private IDirectoryPriceRecordDao directoryPriceRecordDao;

	public IDirectoryPriceRecordDao getDirectoryPriceRecordDao() {
		return directoryPriceRecordDao;
	}

	@Resource
	public void setDirectoryPriceRecordDao(IDirectoryPriceRecordDao directoryPriceRecordDao) {
		this.directoryPriceRecordDao = directoryPriceRecordDao;
		super.setBaseDao(directoryPriceRecordDao);
	}
}
