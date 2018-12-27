package com.shyl.msc.dm.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.DrugType;

public interface IDrugTypeDao extends IBaseDao<DrugType, Long> {
	/**
	 * 根据父节点取得列表
	 * @param parentId
	 * @return
	 */
	public List<DrugType> listByParentId(Long parentId) ;

	/**
	 * 
	 * @param code
	 * @return
	 */
	public DrugType findByCode(String code);
	
	/**
	 * 判断目录下有没有同名称的分类
	 * @param code
	 * @param treepath
	 * @return
	 */
	public DrugType findByTree(String name, String treepath);
	
	/**
	 * 获取最大code
	 * @param parentCode
	 * @return
	 */
	public String getMaxCode(String typ);
}
