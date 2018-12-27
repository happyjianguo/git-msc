package com.shyl.msc.dm.service.impl;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.dao.IGoodsPriceDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.dao.IProductPriceDao;
import com.shyl.msc.dm.dao.IProductPriceDayDao;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.entity.ProductPriceRecord.Type;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;
import com.shyl.msc.dm.service.IProductPriceRecordService;
/**
 * 商品Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class GoodsService extends BaseService<Goods, Long> implements IGoodsService {
	private IGoodsDao goodsDao;

	public IGoodsDao getGoodsDao() {
		return goodsDao;
	}
	@Resource
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
		super.setBaseDao(goodsDao);
	}
	
	@Resource
	private IProductDao productDao;
	@Resource
	private IProductPriceDayDao productPriceDayDao;
	@Resource
	private IGoodsPriceDao goodsPriceDao;
	@Resource
	private IProductPriceDao productPriceDao;
	@Resource
	private IProductPriceRecordService productPriceRecordService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;
	
	
	@Override
	@Transactional
	@CacheEvict(value = "goods", allEntries = true)
	public Goods save(String projectCode, Goods entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "goods", allEntries = true)
	public Goods update(String projectCode, Goods entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "goods", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "goods")
	public Goods getById(String projectCode, Long id) {
		return super.getById(projectCode, id);
	}
	
	@Override
	public DataGrid<Goods> pageByGPO(String projectCode, PageRequest pageable,String vendorCode) {
		return goodsDao.pageByGPO(pageable,vendorCode);
	}

	@Override
	public DataGrid<Goods> pageByEnabled(String projectCode, PageRequest pageable) {
		return goodsDao.pageByEnabled(pageable);
	}

	@Override
	@Cacheable(value = "goods")
	public DataGrid<Goods> pageByHospital(String projectCode, PageRequest pageable, String hospitalCode) {
		System.out.println("pageByHospital查询");
		return goodsDao.pageByHospital(pageable, hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> pagePlaceByHospital(String projectCode, PageRequest pageable,String hospitalCode) {
		return goodsDao.pagePlaceByHospital(pageable, hospitalCode);
	}
	
	@Override
	@Cacheable(value = "goods")
	public Goods getByProductAndHospital(String projectCode, String productCode,String hospitalCode,int isDisabled){
		return goodsDao.getByProductAndHospital(productCode, hospitalCode,isDisabled);
	}
	
	@Override
	public List<Map<String, Object>> listByHospital(String projectCode, String hospitalCode, String lastDate) {
		return goodsDao.listByHospital(hospitalCode, lastDate);
	}
	@Override
	@Cacheable(value = "goods")
	public Goods getByProductCodeAndHosiptal(String projectCode, String productCode, String hospitalCode) {
		return goodsDao.getByProductCodeAndHosiptal(productCode, hospitalCode);
	}
	@Override
	public DataGrid<Map<String, Object>> listForAutoOrder(String projectCode, String hospitalCode, String vendorCode,PageRequest pageable) {
		return goodsDao.listForAutoOrder(hospitalCode,vendorCode,pageable);
	}
	@Override
	public DataGrid<Map<String, Object>> hospitalListForAutoOrder(String projectCode, String vendorCode,PageRequest pageable) {
		return goodsDao.hospitalListForAutoOrder(vendorCode,pageable);
	}
	@Override
	@Cacheable(value = "goods")
	public Goods getByCode(String projectCode, String productCode) {
		return goodsDao.getByCode(productCode);
	}
	@Override
	public List<Map<String, Object>> listByHospital(String projectCode, String hospitalCode) {
		return goodsDao.listByHospital(hospitalCode);
	}
	@Override
	public List<Map<String, Object>> listByHospitalWithGPO(String projectCode, String hospitalCode) {
		return goodsDao.listByHospitalWithGPO(hospitalCode);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = {"goods","goodsPrice"}, allEntries = true)
	public void addGoodsAndGoodsprice(String projectCode, Long productId,String hospitalCode) {
		Product product = productDao.getById(productId);
		Goods goods = goodsDao.getByProductAndHospital(product.getCode(), hospitalCode,1);
		if(goods == null){
			goods = new Goods();
			goods.setProduct(product);
			goods.setProductCode(product.getCode());
			goods.setHospitalCode(hospitalCode);						
			goods.setIsDisabled(0);
			
			goods.setModifyDate(new Date());
			goods = this.save(projectCode, goods);
		}else{
			goods.setIsDisabled(0);
			goods.setModifyDate(new Date());
			goods = this.update(projectCode, goods);
		}
		
		//查询这个药品所有供应商的今天价格，goodsPrice全部新增
		//统一价格list
		List<ProductPrice> list = productPriceDao.listByProduct(goods.getProductCode());
		System.out.println("productPriceDayService.list = "+list.size());
		for (ProductPrice pp : list) {
			GoodsPrice goodsPrice = goodsPriceDao.findByKey(goods.getProductCode(),pp.getVendorCode(), goods.getHospitalCode(), null, null,0);
			if(goodsPrice == null){
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(pp.getVendorCode());
				goodsPrice.setBiddingPrice(pp.getBiddingPrice());
				goodsPrice.setFinalPrice(pp.getFinalPrice());
				goodsPrice.setEffectDate(pp.getEffectDate());
				goodsPrice.setBeginDate(pp.getBeginDate());
				goodsPrice.setOutDate(pp.getOutDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(0);
				goodsPrice.setModifyDate(new Date());
				goodsPriceDao.save(goodsPrice);
			}else{
				goodsPrice.setBiddingPrice(pp.getBiddingPrice());
				goodsPrice.setFinalPrice(pp.getFinalPrice());
				goodsPrice.setEffectDate(pp.getEffectDate());
				goodsPrice.setBeginDate(pp.getBeginDate());
				goodsPrice.setOutDate(pp.getOutDate());
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setIsDisabled(0);
				goodsPrice.setPriceType(0);
				goodsPrice.setModifyDate(new Date());
				goodsPriceDao.update(goodsPrice);
			}
			//记录ProductPriceRecord
			recordPrice(projectCode,goodsPrice);
		}
		// 指定医院价格
		List<ProductPrice> listH = productPriceDao.listByProductAndHospital(goods.getProductCode(),goods.getHospitalCode());
		for (ProductPrice pp : listH) {
			GoodsPrice goodsPrice = goodsPriceDao.findByKey(goods.getProductCode(),pp.getVendorCode(), goods.getHospitalCode(), null, null,0);
			if(goodsPrice == null){
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(pp.getVendorCode());
				goodsPrice.setBiddingPrice(pp.getBiddingPrice());
				goodsPrice.setFinalPrice(pp.getFinalPrice());
				goodsPrice.setEffectDate(pp.getEffectDate());
				goodsPrice.setBeginDate(pp.getBeginDate());
				goodsPrice.setOutDate(pp.getOutDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(1);
				goodsPrice.setModifyDate(new Date());
				goodsPriceDao.save(goodsPrice);
			}else{
				goodsPrice.setBiddingPrice(pp.getBiddingPrice());
				goodsPrice.setFinalPrice(pp.getFinalPrice());
				goodsPrice.setEffectDate(pp.getEffectDate());
				goodsPrice.setBeginDate(pp.getBeginDate());
				goodsPrice.setOutDate(pp.getOutDate());
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setIsDisabled(0);
				goodsPrice.setPriceType(1);
				goodsPrice.setModifyDate(new Date());
				goodsPriceDao.update(goodsPrice);
			}
			//记录ProductPriceRecord
			recordPrice(projectCode,goodsPrice);
		}
	}
	
	private void recordPrice(String projectCode,GoodsPrice goodsPrice) {
		
		PageRequest pageable = new PageRequest();
		
		pageable.getQuery().put("t#productCode_S_EQ", goodsPrice.getProductCode());
		pageable.getQuery().put("t#type_S_EQ",Type.notgpo);
		ProductPriceRecord ppr = productPriceRecordService.getByKey(projectCode,pageable);
		//flag=1 新增his表
		int flag = 0;
		//未记录
		if(ppr == null){
			flag = 1;
			ppr = new ProductPriceRecord();
			ppr.setProductCode(goodsPrice.getProductCode());
			ppr.setVendorCode(goodsPrice.getVendorCode());
			ppr.setVendorName(goodsPrice.getVendorName());
			ppr.setFinalPrice(goodsPrice.getFinalPrice());
			ppr.setLastPrice(goodsPrice.getFinalPrice());
			ppr.setPriceCount(1);
			ppr.setType(Type.notgpo);
			productPriceRecordService.save(projectCode,ppr);
		}else if(ppr.getFinalPrice().compareTo(goodsPrice.getFinalPrice())!=0){
			flag = 1;
			ppr.setVendorCode(goodsPrice.getVendorCode());
			ppr.setVendorName(goodsPrice.getVendorName());
			ppr.setLastPrice(ppr.getFinalPrice());
			ppr.setFinalPrice(goodsPrice.getFinalPrice());
			ppr.setPriceCount(ppr.getPriceCount()+1);
			productPriceRecordService.update(projectCode,ppr);
		}
		if(flag == 1){
			ProductPriceRecordHis pprh = new ProductPriceRecordHis();
			pprh.setProductCode(goodsPrice.getProductCode());
			pprh.setVendorCode(goodsPrice.getVendorCode());
			pprh.setVendorName(goodsPrice.getVendorName());
			pprh.setFinalPrice(goodsPrice.getFinalPrice());
			pprh.setType(com.shyl.msc.dm.entity.ProductPriceRecordHis.Type.notgpo);
			productPriceRecordHisService.save(projectCode,pprh);
		}
	}
	
	@Override
	public DataGrid<Map<String, Object>> queryHospitalAndProduct(String projectCode, PageRequest pageable) {
		return goodsDao.queryHospitalAndProduct(pageable);
	}
	@Override
	public List<Map<String, Object>> listByProductCode(String projectCode, String ypbm) {
		return goodsDao.listByProductCodeMap(ypbm);
	}
}
