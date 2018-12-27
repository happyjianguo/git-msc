package com.shyl.msc.dm.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.DosageForm;
/**
 * 剂型Service接口
 * 
 * @author a_Q
 *
 */
public interface IDosageFormService extends IBaseService<DosageForm, Long> {
	/**
	 * 通过剂型获取
	 * @param name
 	 * @param parentName
	 * @return
	 */
	public DosageForm getByName(@ProjectCodeFlag String projectCode, String name, String parentName);

	
	/**
	 * 根据编码获取剂型
	 * @param code
	 * @return
	 */
	public DosageForm getByCode(@ProjectCodeFlag String projectCode, String code);
}
