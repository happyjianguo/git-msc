package com.shyl.msc.dm.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.DrugType;
/**
 * 药品分类Service接口
 * 
 * @author a_Q
 *
 */
public interface IDrugTypeService extends IBaseService<DrugType,Long>{
	/**
	 * 根据父节点取得列表
	 * @param parentId
	 * @return
	 */
	public List<DrugType> listByParentId(@ProjectCodeFlag String projectCode, Long parentId) ;

	/**
	 * 
	 * @param code
	 * @return
	 */
	public DrugType findByCode(@ProjectCodeFlag String projectCode, String code);
	
	public void mktree(@ProjectCodeFlag String projectCode) ;
}
