package com.shyl.msc.dm.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductPriceRecordDao;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.service.IProductPriceRecordService;

@Service
@Transactional(readOnly=true)
public class ProductPriceRecordService extends BaseService<ProductPriceRecord,Long> implements IProductPriceRecordService{
	private IProductPriceRecordDao productPriceRecordDao;

	public IProductPriceRecordDao getProductPriceRecordDao() {
		return productPriceRecordDao;
	}

	@Resource
	public void setProductPriceRecordDao(IProductPriceRecordDao productPriceRecordDao) {
		this.productPriceRecordDao = productPriceRecordDao;
		super.setBaseDao(productPriceRecordDao);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "productPriceRecord", allEntries = true)
	public ProductPriceRecord save(String projectCode, ProductPriceRecord entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "productPriceRecord", allEntries = true)
	public ProductPriceRecord update(String projectCode, ProductPriceRecord entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "productPriceRecord", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "productPriceRecord")
	public ProductPriceRecord getById(String projectCode, Long id) {
		return super.getById(projectCode, id);
	}

	@Override
	public DataGrid<Map<String, Object>> getIdsForProductPriceMdf(String projectCode, PageRequest pageable) {
		return productPriceRecordDao.getIdsForProductPriceMdf(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> getReportForProductPriceMdf(String projectCode, String ids,
			PageRequest pageable) {
		return productPriceRecordDao.getReportForProductPriceMdf(ids,pageable);
	}
}
