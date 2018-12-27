package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductVendor;
/**
 * 产品VendorService接口
 * 
 * @author a_Q
 *
 */
public interface IProductVendorService extends IBaseService<ProductVendor, Long> {

	
	/**
	 * 
	 * @param productCode
	 * @param vendorCode
	 * @return
	 */
	ProductVendor findByKey(@ProjectCodeFlag String projectCode, String productCode,String vendorCode);

	/**
	 * 
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	DataGrid<ProductVendor> queryByVendor(@ProjectCodeFlag String projectCode, PageRequest pageable, String vendorCode);
	/**
	 * 
	 * @param pageable
	 * @param vendorCode
	 * @return
	 */
	DataGrid<Map<String, Object>> mapByVendor(@ProjectCodeFlag String projectCode, PageRequest pageable, String vendorCode);
	/**
	 * 
	 * @param productCode
	 * @return
	 */
	ProductVendor findByProduct(@ProjectCodeFlag String projectCode, String productCode);
	/**
	 * 供应商的药品目录
	 * @param vendorCode
	 * @param isDisabled
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> findByStatus(@ProjectCodeFlag String projectCode, String vendorCode, Integer isDisabled, PageRequest pageable);
	/**
	 * 根据供应商查药品列表
	 * @param vendorCode
	 * @param scgxsj
	 * @return
	 */
	public List<Map<String, Object>> listByVendor(@ProjectCodeFlag String projectCode, String vendorCode, String scgxsj);

	/**
	 * 根据供应商查药品列表
	 * @param gpoId
	 * @param scgxsj
	 * @return
	 */
	public List<Map<String, Object>> listByGPO(@ProjectCodeFlag String projectCode, Long gpoId, String scgxsj);

	List<Map<String, Object>> listByVendorAndCode(@ProjectCodeFlag String projectCode, String vendorCode, String productCode);
}
