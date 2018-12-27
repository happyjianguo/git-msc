package com.shyl.msc.dm.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.DosageForm;
/**
 * 剂型DAO接口
 * 
 * @author a_Q
 *
 */
public interface IDosageFormDao extends IBaseDao<DosageForm, Long> {

	/**
	 * 根据剂型名称查询
	 * @param name
	 * @return
	 */
	public DosageForm getByName(String name, String parentName);
	

	/**
	 * 获取最大编码
	 * @return
	 */
	public String getMaxCode();
	
	/**
	 * 根据编码获取剂型
	 * @param code
	 * @return
	 */
	public DosageForm getByCode(String code);
	
}
