package com.shyl.msc.set.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Hospital;
/**
 * 医疗机构Service接口
 * 
 * @author a_Q
 *
 */
public interface IHospitalService extends IBaseService<Hospital, Long> {
	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> listAll(@ProjectCodeFlag String projectCode);

	
	/**
	 * 按区划分
	 * @param pageable
	 * @param province
	 * @param city
	 * @param county
	 * @return
	 */
	public List<Hospital> list(@ProjectCodeFlag String projectCode, PageRequest pageable,Long province, Long city, Long county);
	/**
	 * 根据编码查询
	 * @param code
	 * @return
	 */
	public Hospital findByCode(@ProjectCodeFlag String projectCode, String code);
	/**
	 * 检查新增资料
	 */
	public Hospital checkAndSave(@ProjectCodeFlag String projectCode, Hospital hospital) throws Exception;
	/**
	 * 检查修改资料
	 */
	public Hospital checkAndUpdate(@ProjectCodeFlag String projectCode, Hospital hospital) throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public Map<String, Object> count(@ProjectCodeFlag String projectCode);

}
