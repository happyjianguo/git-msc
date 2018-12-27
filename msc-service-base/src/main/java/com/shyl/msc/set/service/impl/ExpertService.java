package com.shyl.msc.set.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IExpertDao;
import com.shyl.msc.set.entity.Expert;
import com.shyl.msc.set.service.IExpertService;

@Service
@Transactional(readOnly=true)
public class ExpertService extends BaseService<Expert, Long> implements IExpertService {
	
	private IExpertDao expertDao;
	
	@Resource
	public void setExpertDao(IExpertDao expertDao) {
		this.expertDao = expertDao;
		super.setBaseDao(expertDao);
	}

	public Expert getByEmpId(String projectCode, String expertGroupCode, String empId) {
		return this.expertDao.getByEmpId(expertGroupCode, empId);
	}

	@Override
	public List<Map<String, Object>> listForCourseCode(String projectCode) {
		
		return expertDao.listForCourseCode();
	}
}
