package com.shyl.msc.set.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.entity.User;

/**
 * 中标企业Service接口
 * 
 * @author a_Q
 *
 */
public interface ICompanyService extends IBaseService<Company, Long> {

	/**
	 * GPO列表
	 * @return
	 */
	public List<Map<String, Object>> listGPO(@ProjectCodeFlag String projectCode);
	
	/**
	 * 配送企业列表
	 * @return
	 */
	public List<Map<String, Object>> listVendor(@ProjectCodeFlag String projectCode);

	public Company findByCode1(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 
	 * @param code
	 * @param para("isProducer=1" || "isVendor=1" || "isGPO=1")
	 * @return
	 */
	public Company findByCode(@ProjectCodeFlag String projectCode, String code,String para);

	/**
	 * 
	 * @param pageable
	 * @param name
	 * @param para("isProducer=1" || "isVendor=1" || "isGPO=1")
	 * @return
	 */
	public DataGrid<Map<String, Object>> pageByType(@ProjectCodeFlag String projectCode, PageRequest pageable, String name, String para);

	/**
	 * 
	 * @param type("isProducer=1" || "isVendor=1" || "isGPO=1")
	 * @return
	 */
	public Map<String, Object> count(@ProjectCodeFlag String projectCode, String type);

	/**
	 * 
	 * @param pageable
	 * @param companyType
	 * @return
	 */
	public DataGrid<Company> pageByType(@ProjectCodeFlag String projectCode, PageRequest pageable, String companyType);
	
	/**
	 * 查询公司
	 * @param name
	 * @return
	 */
	public Company findByName(@ProjectCodeFlag String projectCode, String name);
	/**
	 * 获取最大编号
	 * @return
	 */
	public String getMaxCode(@ProjectCodeFlag String projectCode);

	/**
	 * 
	 * @param company
	 */
	public void saveCompany(@ProjectCodeFlag String projectCode, Company company);

	public void updateCompany(@ProjectCodeFlag String projectCode, Company company);

	public void deleteCompany(@ProjectCodeFlag String projectCode, Company company);
	/**
	 * 导入excel新增
	 * @param upExcel
	 * @param user
	 * @return
	 * @throws Exception
	 */
	 String doExcelH(@ProjectCodeFlag String projectCode, String[][] upExcel, User user) throws Exception;

}
