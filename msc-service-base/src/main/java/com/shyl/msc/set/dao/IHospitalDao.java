package com.shyl.msc.set.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Hospital;
/**
 * 医疗机构DAO接口
 * 
 * @author a_Q
 *
 */
public interface IHospitalDao extends IBaseDao<Hospital, Long> {

	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> listAll();
	
	/**
	 * 按区划分
	 * @param pageable
	 * @param province
	 * @param city
	 * @param county
	 * @return
	 */
	public List<Hospital> list(PageRequest pageable,Long province, Long city, Long county);

	/**
	 * 根据编码查询
	 * @param code
	 * @return
	 */
	public Hospital findByCode(String code);
	/**
	 * 根据接口编码查询
	 * @param iocode
	 * @return
	 */
	public Hospital findByIocode(String iocode);

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> count();
	
	/**
	 * 根据医院名称找到医院信息
	 * @param name
	 * @return
	 */
	public Hospital findByName(String name);
}
