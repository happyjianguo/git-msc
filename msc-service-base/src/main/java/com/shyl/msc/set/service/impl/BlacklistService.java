package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IBlacklistDao;
import com.shyl.msc.set.entity.Blacklist;
import com.shyl.msc.set.service.IBlacklistService;
/**
 * 企业黑名单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class BlacklistService extends BaseService<Blacklist, Long> implements IBlacklistService {
	private IBlacklistDao blacklistDao;

	public IBlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	@Resource
	public void setBlacklistDao(IBlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
		super.setBaseDao(blacklistDao);
	}

}
