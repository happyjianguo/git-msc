package com.shyl.msc.dm.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;

/**
 * 产品Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ProductService extends BaseService<Product, Long> implements IProductService {

	@Autowired(required = false)
	private RedisTemplate redisTemplate;

	private IProductDao productDao;

	public IProductDao getProductDao() {
		return productDao;
	}

	@Resource
	public void setProductDao(IProductDao productDao) {
		this.productDao = productDao;
		super.setBaseDao(productDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "product", allEntries = true)
	public Product save(String projectCode, Product entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "product", allEntries = true)
	public Product update(String projectCode, Product entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "product", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "product")
	public Product getById(String projectCode, Long id) {
		return productDao.getById(id);
	}
	
	@Override
	@Cacheable(value = "product")
	public Product getByCode(String projectCode, String code) {
		return productDao.getByCode(code);
	}

	@Override
	@Cacheable(value = "product")
	public DataGrid<Map<String, Object>> npquery(String projectCode, String code,String name, PageRequest pageable) {
		return productDao.npquery(code,name,pageable);
	}
	
	@Override
	@Cacheable(value = "product")
	public DataGrid<Product> query(String projectCode, PageRequest pageable) {
		return productDao.query(pageable);
	}

	@Override
	@Cacheable(value = "product")
	public Map<String, Object> count(String projectCode) {
		return productDao.count();
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getCentralizedPercent(String projectCode, int maxsize,String year,String month) {
		return productDao.getCentralizedPercent(maxsize,year,month);
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String,Object> getGpoPercent(String projectCode) {	
		 Map<String,Object> map = new HashMap<String,Object>();
		 map.put("percent",productDao.getGpoPercent());
		return map;
	}

	@Override
	@Transactional(readOnly=true)
	public DataGrid<Map<String, Object>> npquery(String projectCode, List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable) {
		return productDao.npquery(productWords, dosageWords, producerWords, productCode, pageable);
	}

	@Override
	public DataGrid<Product> pageByGPO(String projectCode, PageRequest pageable, List<Long> gpoIds) {
		return productDao.pageByGPO(pageable, gpoIds);
	}

	@Override
	public List<Product> listByGPOS(String projectCode, PageRequest pageRequest, List<Long> gpoIds) {
		return productDao.listByGPOS(pageRequest, gpoIds);
	}
	
	@Override
	public DataGrid<Product> pageInAttribute(String projectCode, PageRequest pageable){
		return productDao.pageInAttribute(pageable);
	}

	@Override
	public DataGrid<Product> pageInProductVendor(String projectCode, PageRequest pageable, String hospitalCode, Integer isInProductVendor){
		return productDao.pageInProductVendor(pageable, hospitalCode, isInProductVendor);
	}

	@Override
	public List<Map<String, Object>> listByGPO(String projectCode, Long gpoId, String scgxsj) {
		return productDao.listByGPO(gpoId, scgxsj);
	}

	@Override
	public List<Map<String, Object>> listByDate(String projectCode, String scgxsj) {
		return productDao.listByDate(scgxsj);
	}

	@Override
	public String getMaxCode(String projectCode) {
		return productDao.getMaxCode();
	}

	@Override
	public String getMaxModelCode(String projectCode, String newcode, String model) {
		return productDao.getMaxModelCode(newcode, model);
	}

	@Override
	public String getMaxPackCode(String projectCode, String newcode, String model, String packDesc) {
		return productDao.getMaxModelCode(newcode, model);
	}

	@Override
	public List<Map<String, Object>> listByGPOAndCode(String projectCode, Long gpoId, String code) {
		return productDao.listByGPOAndCode(gpoId, code);
	}

	@Override
	public List<Map<String, Object>> producerComb(String projectCode, String productName) {
		return productDao.producerComb(projectCode, productName);
	}

	@Override
	public List<Map<String, Object>> productPiciMap() {
		return productDao.productPiciMap();
	}

	@Override
	public DataGrid<Product> listByVendorAndDate(String projectCode, String vendorCode, String startDate, String endDate,
			PageParam pageable) {
		return productDao.listByVendorAndDate(vendorCode, startDate, endDate, pageable);
	}


	public DataGrid<Map<String, Object>> queryByGoodsHospital(String projectCode, String scgxsj, PageRequest page) {
		return productDao.queryByGoodsHospital(scgxsj, page);
	}

	@Override
	public void getByAllByAsync(String projectCode,Integer i) {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPageSize(10000);
		pageRequest.setPage(i);
		List<Product> productList = productDao.getAll(pageRequest);
		/*Map<String,Product> productMap = new HashMap<>();
		for(Product p : productList){
			productMap.put(p.getCode(),p);
		}*/
		redisTemplate.opsForList().leftPush("productList"+i,productList);
	}
}
