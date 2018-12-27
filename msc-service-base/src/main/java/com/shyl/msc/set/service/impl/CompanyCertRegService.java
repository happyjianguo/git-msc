package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.ICompanyCertDao;
import com.shyl.msc.set.dao.ICompanyCertRegDao;
import com.shyl.msc.set.entity.CompanyCert;
import com.shyl.msc.set.entity.CompanyCertReg;
import com.shyl.msc.set.entity.CompanyCertReg.AuditStatus;
import com.shyl.msc.set.service.ICompanyCertRegService;
/**
 * 企业证照注册申请
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class CompanyCertRegService extends BaseService<CompanyCertReg, Long> implements ICompanyCertRegService {	
	@Resource
	private ICompanyCertDao companyCertDao;
	private ICompanyCertRegDao companyCertRegDao;

	public ICompanyCertRegDao getCompanyCertRegDao() {
		return companyCertRegDao;
	}

	@Resource
	public void setCompanyCertRegDao(ICompanyCertRegDao companyCertRegDao) {
		this.companyCertRegDao = companyCertRegDao;
		super.setBaseDao(companyCertRegDao);
	}

	@Override
	@Transactional
	public void copy(String projectCode, CompanyCertReg companyCertReg) {
		companyCertReg = super.updateWithInclude(projectCode, companyCertReg, "auditTime", "auditStatus", "auditNote");
		if(companyCertReg.getAuditStatus().equals(AuditStatus.pass)){
			CompanyCert companyCert = companyCertDao.getUnique(companyCertReg.getTypeCode(), companyCertReg.getCode());
			if(companyCert == null){
				companyCert = new CompanyCert();
				companyCert.setCompany(companyCertReg.getCompany());
				companyCert.setTypeCode(companyCertReg.getTypeCode());
				companyCert.setTypeName(companyCertReg.getTypeName());
				companyCert.setCode(companyCertReg.getCode());
				companyCert.setName(companyCertReg.getName());
				companyCert.setIssueDate(companyCertReg.getIssueDate());
				companyCert.setValidDate(companyCertReg.getValidDate());
				companyCert.setDept(companyCertReg.getDept());
				companyCert.setImagePath(companyCertReg.getImagePath());
				companyCert.setNote(companyCertReg.getNote());
				companyCert.setScope(companyCertReg.getScope());
				companyCert.setStatus(companyCertReg.getStatus());	
				companyCertDao.save(companyCert);
			}else{
				companyCert.setCompany(companyCertReg.getCompany());
				companyCert.setName(companyCertReg.getName());
				companyCert.setIssueDate(companyCertReg.getIssueDate());
				companyCert.setValidDate(companyCertReg.getValidDate());
				companyCert.setDept(companyCertReg.getDept());
				companyCert.setImagePath(companyCertReg.getImagePath());
				companyCert.setNote(companyCertReg.getNote());
				companyCert.setScope(companyCertReg.getScope());
				companyCert.setStatus(companyCertReg.getStatus());	
				companyCertDao.update(companyCert);
			}
		}
	}
}
