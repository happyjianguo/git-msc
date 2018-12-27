package com.shyl.msc.dm.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IDirectoryPriceDao;
import com.shyl.msc.dm.dao.IDirectoryPriceRecordDao;
import com.shyl.msc.dm.entity.DirectoryPrice;
import com.shyl.msc.dm.entity.DirectoryPriceRecord;
import com.shyl.msc.dm.service.IDirectoryPriceService;

@Service
@Transactional(readOnly=true)
public class DirectoryPriceService extends BaseService<DirectoryPrice,Long> implements IDirectoryPriceService{
	@Resource
	private IDirectoryPriceRecordDao directoryPriceRecodeDao;
	
	private IDirectoryPriceDao directoryPriceDao;

	public IDirectoryPriceDao getDirectoryPriceDao() {
		return directoryPriceDao;
	}

	@Resource
	public void setDirectoryPriceDao(IDirectoryPriceDao directoryPriceDao) {
		this.directoryPriceDao = directoryPriceDao;
		super.setBaseDao(directoryPriceDao);
	}

	@Override
	@Transactional
	public void doSave(String projectCode, DirectoryPriceRecord directoryPriceRecord) {
		//新增历史档DirectoryPriceRecode
		directoryPriceRecodeDao.save(directoryPriceRecord);
		//新增记录档DirectoryPrice
		PageRequest pageable = new PageRequest();
		pageable.getQuery().put("t#directory.id_L_EQ", directoryPriceRecord.getDirectory().getId());
		pageable.getQuery().put("t#areaCode_S_EQ", directoryPriceRecord.getAreaCode());
		DirectoryPrice dp = directoryPriceDao.getByKey(pageable);
		if(dp != null){
			dp.setPrice(directoryPriceRecord.getPrice());
			directoryPriceDao.update(dp);
		}else{
			dp = new DirectoryPrice();
			dp.setDirectory(directoryPriceRecord.getDirectory());
			dp.setAreaCode(directoryPriceRecord.getAreaCode());
			dp.setAreaName(directoryPriceRecord.getAreaName());
			dp.setPrice(directoryPriceRecord.getPrice());
			directoryPriceDao.save(dp);
		}
	}
}
