package com.shyl.msc.dm.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGoodsPriceDao;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;

@Service
@Transactional(readOnly=true)
public class GoodsPriceService extends BaseService<GoodsPrice, Long> implements IGoodsPriceService {

	private IGoodsPriceDao goodsPriceDao;

	public IGoodsPriceDao getGoodsPriceDao() {
		return goodsPriceDao;
	}
	
	@Resource
	public void setGoodsPriceDao(IGoodsPriceDao goodsPriceDao) {
		this.goodsPriceDao = goodsPriceDao;
		super.setBaseDao(goodsPriceDao);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "goodsPrice", allEntries = true)
	public GoodsPrice save(String projectCode, GoodsPrice entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "goodsPrice", allEntries = true)
	public GoodsPrice update(String projectCode, GoodsPrice entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "goodsPrice", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}


	@Override
	public List<GoodsPrice> listByGoods(String projectCode, Long goodsId) {
		return goodsPriceDao.listByGoods(goodsId);
	}

	@Override
	public GoodsPrice getByProductCodeAndVender(String projectCode, String productCode, String vendorCode, String hospitalCode) {
		return goodsPriceDao.getByProductCodeAndVender(productCode, vendorCode, hospitalCode);
	}

	@Override
	@Cacheable(value = "goodsPrice")
	public GoodsPrice findByKey(String projectCode, String productCode, String vendorCode, String hospitalCode,Integer isDisabled,Integer isDisabledByH,Integer isFormContract) {
		return goodsPriceDao.findByKey(productCode, vendorCode, hospitalCode,isDisabled,isDisabledByH,isFormContract) ;
	}

	@Override
	public List<GoodsPrice> listByHospital(String projectCode, String hospitalCode, String scgxsj) {
		return goodsPriceDao.listByHospital(hospitalCode, scgxsj);
	}

	@Override
	public List<Map<String, Object>> getVendorList(String projectCode, String hospitalCode) {
		return goodsPriceDao.getVendorList(hospitalCode);
	}

	@Override
	public List<GoodsPrice> listByIsFormContract(String projectCode) {
		return goodsPriceDao.listByIsFormContract();
	}

	@Override
	public List<GoodsPrice> listByProductCode(String projectCode, String productCode) {
		return goodsPriceDao.listByProductCode(productCode);
	}
}
