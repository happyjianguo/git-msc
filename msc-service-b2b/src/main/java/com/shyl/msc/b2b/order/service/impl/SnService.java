package com.shyl.msc.b2b.order.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.Sn;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.enmu.OrderType;
/**
 * 序列号Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class SnService extends BaseService<Sn, Long> implements ISnService {
	
	private ISnDao snDao;

	@Resource
	public void setSnDao(ISnDao snDao) {
		this.snDao = snDao;
		super.setBaseDao(snDao);
	}
	
	@Override
	@Transactional
	public String getCode(String projectCode, OrderType type){
		return snDao.getCode(type);
	}

}
