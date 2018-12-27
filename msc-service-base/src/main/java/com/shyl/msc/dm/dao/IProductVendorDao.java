package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductVendor;
/**
 * 产品VendorDAO接口
 * 
 * @author a_Q
 *
 */
public interface IProductVendorDao extends IBaseDao<ProductVendor, Long> {

	ProductVendor findByKey(String productCode, String vendorCode);

	DataGrid<ProductVendor> queryByVendor(PageRequest pageable, String vendorCode);

	DataGrid<Map<String, Object>> mapByVendor(PageRequest pageable, String vendorCode);
	
	ProductVendor findByProduct(String productCode);

	public DataGrid<Map<String, Object>> findByStatus(String vendorCode, Integer isDisabled, PageRequest pageable);
	/**
	 * 根据供应商查药品列表
	 * @param vendorCode
	 * @param scgxsj
	 * @return
	 */
	public List<Map<String, Object>> listByVendor(String vendorCode, String scgxsj);
	/**
	 * 根据供应商查药品列表
	 * @param gpoId
	 * @param scgxsj
	 * @return
	 */
	public List<Map<String, Object>> listByGPO(Long gpoId, String scgxsj);

	List<Map<String, Object>> listByVendorAndCode(String vendorCode, String productCode);
}
