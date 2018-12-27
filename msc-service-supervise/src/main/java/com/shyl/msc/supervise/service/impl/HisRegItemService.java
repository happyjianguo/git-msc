package com.shyl.msc.supervise.service.impl;

import javax.annotation.Resource;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.supervise.dao.IHisRegItemDao;
import com.shyl.msc.supervise.entity.HisRegItem;
import com.shyl.msc.supervise.service.IHisRegItemService;

import java.util.Map;

@SuppressWarnings("unused")
@Service
@Transactional(readOnly=true)
public class HisRegItemService extends BaseService<HisRegItem, Long> implements IHisRegItemService {

	private IHisRegItemDao hisRegItemDao;
	@Resource
	public void setHisRegItemDao(IHisRegItemDao hisRegItemDao) {
		setBaseDao(hisRegItemDao);
		this.hisRegItemDao = hisRegItemDao;
	}
	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest page) {
		return hisRegItemDao.queryByPage(page);
	}
	
}
