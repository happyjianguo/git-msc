package com.shyl.msc.menu.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.IBaseDrugDao;
import com.shyl.msc.menu.dao.IStupefacientDao;
import com.shyl.msc.menu.dao.ISupplementDao;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.Stupefacient;
import com.shyl.msc.menu.entity.Supplement;
import com.shyl.msc.menu.service.IStupefacientService;

@Service
@Transactional
public class StupefacientService extends BaseService<Stupefacient, Long> implements IStupefacientService {
	
	private IStupefacientDao stupefacientDao;

	public IStupefacientDao getStupefacientDao() {
		return stupefacientDao;
	}
	@Resource
	public void setStupefacientDao(IStupefacientDao stupefacientDao) {
		super.setBaseDao(stupefacientDao);
		this.stupefacientDao = stupefacientDao;
	}

	@Override
	public Stupefacient getByExtId(Integer id, Integer type) {
		return stupefacientDao.getByExtId(id, type);
	}

}
