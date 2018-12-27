package com.shyl.msc.menu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.menu.dao.ISupplementDao;
import com.shyl.msc.menu.entity.Supplement;
import com.shyl.msc.menu.service.ISupplementService;

@Service
@Transactional
public class SupplementService extends BaseService<Supplement, Long> implements ISupplementService {

	private ISupplementDao supplementDao;

	public ISupplementDao getSupplementDao() {
		return supplementDao;
	}

	@Resource
	public void setSupplementDao(ISupplementDao supplementDao) {
		super.setBaseDao(supplementDao);
		this.supplementDao = supplementDao;
	}
	public Supplement getByExtId(Integer extId) {
		return supplementDao.getByExtId(extId);
	}
}
