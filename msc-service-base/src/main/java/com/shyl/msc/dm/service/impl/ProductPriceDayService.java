package com.shyl.msc.dm.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.dao.IGoodsPriceDao;
import com.shyl.msc.dm.dao.IProductPriceDayDao;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.ProductPriceDay;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IProductPriceDayService;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ProductPriceDayService extends BaseService<ProductPriceDay, Long> implements IProductPriceDayService {
	private IProductPriceDayDao productPriceDayDao;
	
	
	public IProductPriceDayDao getProductPriceDayDao() {
		return productPriceDayDao;
	}
	
	@Resource
	IGoodsPriceDao goodsPriceDao;
	@Resource
	IGoodsPriceService goodsPriceService;
	@Resource
	IGoodsDao goodsDao;

	@Resource
	public void setProductPriceDayDao(IProductPriceDayDao productPriceDayDao) {
		this.productPriceDayDao = productPriceDayDao;
		super.setBaseDao(productPriceDayDao);
	}

	@Override
	public ProductPriceDay findByKey(String projectCode, String productCode, String vendorCode, TradeType tradeType, String hospitalCode, String priceDate) {
		return productPriceDayDao.findByKey(productCode,  vendorCode, tradeType, hospitalCode, priceDate);
	}


	@Override
	@Transactional
	public void setPriceTaskJob(String projectCode, ProductPriceDay productPriceDay) {
		//1、根据Product找出所有goods
		List<Goods> glist = goodsDao.listByProductCode(productPriceDay.getProductCode());
		//2、Product+Hospital＋vendor 去goodsprice查询，查不到新增，查到修改
		for (Goods goods : glist) {
			GoodsPrice goodsPrice = goodsPriceDao.findByKey(goods.getProductCode(), productPriceDay.getVendorCode(), goods.getHospitalCode(), null, null,0);
			if(goodsPrice == null){
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(productPriceDay.getVendorCode());
				goodsPrice.setBiddingPrice(productPriceDay.getBiddingPrice());
				goodsPrice.setFinalPrice(productPriceDay.getFinalPrice());
				goodsPrice.setBeginDate(productPriceDay.getBeginDate());
				goodsPrice.setOutDate(productPriceDay.getOutDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPriceService.save(projectCode, goodsPrice);
			}else{
				goodsPrice.setBiddingPrice(productPriceDay.getBiddingPrice());
				goodsPrice.setFinalPrice(productPriceDay.getFinalPrice());
				goodsPrice.setIsDisabled(productPriceDay.getIsDisabled());
				goodsPriceService.update(projectCode, goodsPrice);
			}
		}
	}

	@Override
	public List<ProductPriceDay> getbyDate(String projectCode, String date, TradeType tradeType) {
		return productPriceDayDao.getbyDate( date, tradeType);
	}

	@Override
	public List<ProductPriceDay> getbyDateAndHospital(String projectCode, String date) {
		return productPriceDayDao.getbyDateAndHospital( date);
	}

	@Override
	@Transactional
	public void setPriceForHospital(String projectCode, ProductPriceDay productPriceDay) {
		if(productPriceDay.getIsDisabled() == 1){//指定医院价格禁用，则不覆盖通用价格
			return;
		}
		//查不到新增，查到修改
		GoodsPrice goodsPrice = goodsPriceDao.findByKey(productPriceDay.getProductCode(),productPriceDay.getVendorCode(),productPriceDay.getHospitalCode(),null,null,0);
		if(goodsPrice == null){
			Goods goods = goodsDao.getByProductCodeAndHosiptal(productPriceDay.getProductCode(), productPriceDay.getHospitalCode());
			if(goods != null){//医院未添加这个药品时，不做新增
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(productPriceDay.getVendorCode());
				goodsPrice.setBiddingPrice(productPriceDay.getBiddingPrice());
				goodsPrice.setFinalPrice(productPriceDay.getFinalPrice());
				goodsPrice.setBeginDate(productPriceDay.getBeginDate());
				goodsPrice.setOutDate(productPriceDay.getOutDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPriceService.save(projectCode, goodsPrice);
			}
		}else{
			goodsPrice.setBiddingPrice(productPriceDay.getBiddingPrice());
			goodsPrice.setFinalPrice(productPriceDay.getFinalPrice());
			goodsPrice.setIsDisabled(productPriceDay.getIsDisabled());
			goodsPriceService.update(projectCode, goodsPrice);
		}
	}

	@Override
	@Transactional
	public void setPriceToZero(String projectCode, ProductPriceDay productPriceDay) {
		List<GoodsPrice> list = goodsPriceDao.findByProduct(productPriceDay.getProductCode(),productPriceDay.getVendorCode());
		for (GoodsPrice goodsPrice : list) {
			goodsPrice.setIsDisabled(1);
			goodsPriceService.update(projectCode, goodsPrice);
		}
	}

	@Override
	public List<Map<String, Object>> listByProduct(String projectCode, String date, String productCode) {
		return productPriceDayDao.listByProduct(date, productCode);
	}

	@Override
	public List<Map<String, Object>> listByProductAndHospital(String projectCode, String date, String productCode, String hospitalCode) {
		return productPriceDayDao.listByProductAndHospital(date, productCode,hospitalCode);
	}


}
