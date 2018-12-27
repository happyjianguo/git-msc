package com.shyl.msc.set.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.CompanyCert;
/**
 * 企业证照
 * 
 * @author a_Q
 *
 */
public interface ICompanyCertDao extends IBaseDao<CompanyCert, Long> {
	/**
	 * 根据证照类型和代号取得唯一一笔
	 * @param typeCode
	 * @param code
	 * @return
	 */
	public CompanyCert getUnique(String typeCode, String code);

}
