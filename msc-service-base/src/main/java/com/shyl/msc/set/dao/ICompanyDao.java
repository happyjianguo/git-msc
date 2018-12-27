package com.shyl.msc.set.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Company;
/**
 * 中标企业DAO接口
 * 
 * @author a_Q
 *
 */
public interface ICompanyDao extends IBaseDao<Company, Long> {

	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> listGPO();
	
	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> listVendor();

	public Company findByCode(String code);

	public Company findByCode(String code, String string);
	
	/**
	 * 根据公司名称查找公司
	 * @param name
	 * @return
	 */
	public Company findByName(String name);

	/**
	 * 
	 * @param pageable
	 * @param name
	 * @param para
	 * @return
	 */
	public DataGrid<Map<String, Object>> pageByType(PageRequest pageable,
			String name, String para);

	/**
	 * 
	 * @param type
	 * @return
	 */
	public Map<String, Object> count(String type);

	/**
	 * 
	 * @param pageable
	 * @param companyType
	 * @return
	 */
	public DataGrid<Company> pageByType(PageRequest pageable, String companyType);
	
	/**
	 * 获取最大单据号
	 * @return
	 */
	public String getMaxCode();

}
