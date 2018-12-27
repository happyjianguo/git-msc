package com.shyl.msc.supervise.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.Quota;

@Service
@Transactional(readOnly=true)
public interface IQuotaDao extends IBaseDao<Quota, Long> {
	
	public Quota getByCode(String code); 
}
