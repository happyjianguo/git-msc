package com.shyl.msc.dm.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductVendorDao;
import com.shyl.msc.dm.entity.ProductVendor;
import com.shyl.msc.dm.service.IProductVendorService;
/**
 * 产品VendorService实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ProductVendorService extends BaseService<ProductVendor, Long> implements IProductVendorService {
	private IProductVendorDao productVendorDao;

	public IProductVendorDao getProductVendorDao() {
		return productVendorDao;
	}

	@Resource
	public void setProductVendorDao(IProductVendorDao productVendorDao) {
		this.productVendorDao = productVendorDao;
		super.setBaseDao(productVendorDao);
	}

	@Override
	public ProductVendor findByKey(String projectCode, String productCode,String vendorCode) {
		return productVendorDao.findByKey(productCode, vendorCode);
	}

	@Override
	public DataGrid<ProductVendor> queryByVendor(String projectCode, PageRequest pageable, String vendorCode) {
		return productVendorDao.queryByVendor(pageable,vendorCode);
	}
	
	@Override
	public DataGrid<Map<String, Object>> mapByVendor(String projectCode, PageRequest pageable, String vendorCode) {
		return productVendorDao.mapByVendor(pageable,vendorCode);
	}

	@Override
	public ProductVendor findByProduct(String projectCode, String productCode) {
		return productVendorDao.findByProduct(productCode);
	}

	@Override
	public DataGrid<Map<String, Object>> findByStatus(String projectCode, String vendorCode, Integer isDisabled, PageRequest pageable) {
		return productVendorDao.findByStatus(vendorCode,isDisabled,pageable);
	}

	@Override
	public List<Map<String, Object>> listByVendor(String projectCode, String vendorCode, String scgxsj) {
		return productVendorDao.listByVendor(vendorCode, scgxsj);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> listByGPO(String projectCode, Long gpoId, String scgxsj) {
		return productVendorDao.listByGPO(gpoId, scgxsj);
	}

	@Override
	public List<Map<String, Object>> listByVendorAndCode(String projectCode, String vendorCode, String productCode) {
		return productVendorDao.listByVendorAndCode(vendorCode, productCode);
	}

}
