package com.shyl.msc.dm.dao;

import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.Drug;
/**
 * 药品Dao接口
 * 
 * @author a_Q
 *
 */
public interface IDrugDao extends IBaseDao<Drug, Long> {
	/**
	 * 药品编码
	 * @param code
	 * @return
	 */
	public Drug getByCode(String code);

	/**
	 * 根据剂型名称查询
	 * @param name
	 * @return
	 */
	public Drug getByName(String name, String dosa);
	

	/**
	 * 获取最大编码
	 * @return
	 */
	public String getMaxCode(String genericName);
	
	/**
	 * 根据剂型名称查询,仅一条数
	 * @param name
	 * @return
	 */
	public Drug getByNameOnly(String name);
	
	/**
	 * 医院未对照到处查询drug详情信息
	 * @param name
	 * @return
	 */
	public Map<String, String> queryDrugInfoByName(String name, String productCode);
}
