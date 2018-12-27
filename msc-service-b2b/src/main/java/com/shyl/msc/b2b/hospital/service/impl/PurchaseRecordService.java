package com.shyl.msc.b2b.hospital.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.hospital.dao.IPurchaseRecordDao;
import com.shyl.msc.b2b.hospital.entity.PurchaseRecord;
import com.shyl.msc.b2b.hospital.service.IPurchaseRecordService;

/**
 * 采购记录上报Service实现类
 * 
 *
 */
@Service
@Transactional(readOnly=true)
public class PurchaseRecordService extends BaseService<PurchaseRecord, Long> implements IPurchaseRecordService {
	private IPurchaseRecordDao PurchaseRecordDao;

	public IPurchaseRecordDao getPurchaseRecordDao() {
		return PurchaseRecordDao;
	}

	@Resource
	public void setPurchaseRecordDao(IPurchaseRecordDao PurchaseRecordDao) {
		this.PurchaseRecordDao = PurchaseRecordDao;
		super.setBaseDao(PurchaseRecordDao);
	}
	
	@Override
	public List<Map<String, Object>> query(@ProjectCodeFlag String projectCode, String startMonth, String toMonth, String hospitalCode) {
		return PurchaseRecordDao.query(startMonth, toMonth, hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest page) {
		return PurchaseRecordDao.queryByPage(page);
	}
}
