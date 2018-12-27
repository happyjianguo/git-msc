package com.shyl.msc.dm.service;

import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.Drug;
/**
 * 药品Service接口
 * 
 * @author a_Q
 *
 */
public interface IDrugService extends IBaseService<Drug, Long> {

	/**
	 * 药品编码
	 * @param code
	 * @return
	 */
	public Drug getByCode(@ProjectCodeFlag String projectCode, String code);
	/**
	 * 获取最大编码
	 * @return
	 */
	public String getMaxCode(@ProjectCodeFlag String projectCode, String genericName);
	
	/**
	 * 医院未对照到处查询drug详情信息
	 * @param name
	 * @param productCode
	 * @return
	 */
	public Map<String, String> queryDrugInfoByName(@ProjectCodeFlag String projectCode, String name, String productCode);
	/**
	 * 获取药品信息
	 * @param name
	 * @param dosageFormName
	 * @return
	 */
	public Drug getByName(@ProjectCodeFlag String projectCode, String name, String dosageFormName);
	
	/**
	 * 通过drug名称获取drug信息
	 * @param name
	 * @return
	 */
	public Drug getByNameOnly(@ProjectCodeFlag String projectCode, String name);
}
