package com.shyl.msc.set.service;

import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.CompanyCertReg;
/**
 * 企业资质注册申请
 * 
 * @author a_Q
 *
 */
public interface ICompanyCertRegService extends IBaseService<CompanyCertReg, Long> {
	/**
	 * 修改并复制资料
	 * @param projectCode
	 * @param companyCertReg
	 */
	public void copy(String projectCode, CompanyCertReg companyCertReg) ;
}
