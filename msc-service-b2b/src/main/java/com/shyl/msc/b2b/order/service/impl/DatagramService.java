package com.shyl.msc.b2b.order.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IDatagramDao;
import com.shyl.msc.b2b.order.entity.Datagram;
import com.shyl.msc.b2b.order.service.IDatagramService;

@Service
public class DatagramService extends BaseService<Datagram, Long> implements IDatagramService {
	
	private IDatagramDao datagramDao;

	public IDatagramDao getDatagramDao() {
		return datagramDao;
	}
	
	@Resource
	public void setDatagramDao(IDatagramDao datagramDao) {
		this.datagramDao = datagramDao;
		super.setBaseDao(datagramDao);
	}
	
}
