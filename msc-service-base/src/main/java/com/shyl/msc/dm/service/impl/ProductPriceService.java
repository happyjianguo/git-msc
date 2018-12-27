package com.shyl.msc.dm.service.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.dao.IGoodsPriceDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.dao.IProductPriceDao;
import com.shyl.msc.dm.dao.IProductPriceDayDao;
import com.shyl.msc.dm.dao.IProductVendorDao;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.entity.ProductVendor;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.enmu.TradeType;
import com.shyl.msc.set.dao.ICompanyDao;
import com.shyl.msc.set.dao.IHospitalDao;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.entity.User;
/**
 * 产品价格Service实现类
 * 
 * @author a_Q
 *
 */
@Service
public class ProductPriceService extends BaseService<ProductPrice, Long> implements IProductPriceService {
	private IProductPriceDao productPriceDao;
	@Resource
	private IGoodsDao goodsDao;
	@Resource
	private IGoodsPriceDao goodsPriceDao;
	@Resource
	private ICompanyDao companyDao;
	@Resource
	private IHospitalDao hospitalDao;
	@Resource
	private IProductDao productDao;
	@Resource
	private IProductVendorDao productVendorDao;
	@Resource
	private IProductPriceDayDao productPriceDayDao;
	
	public IProductPriceDao getProductPriceDao() {
		return productPriceDao;
	}

	@Resource
	public void setProductPriceDao(IProductPriceDao productPriceDao) {
		this.productPriceDao = productPriceDao;
		super.setBaseDao(productPriceDao);
	}
	
	@Override
	@Transactional(readOnly=true)
	public ProductPrice getById(String projectCode, Long id) {
		return super.getById(projectCode, id);
	}
	
	@Override
	@Transactional(readOnly=true)
	public DataGrid<ProductPrice> pageByGPO(String projectCode, PageRequest pageable,String vendorCode) {
		return productPriceDao.pageByGPO(pageable,vendorCode);
	}

	@Override
	@Transactional(readOnly=true)
	public DataGrid<ProductPrice> pageByEnabled(String projectCode, PageRequest pageable) {
		return productPriceDao.pageByEnabled(pageable);
	}

	@Override
	@Transactional(readOnly=true)
	public ProductPrice getByCode(String projectCode, String code) {
		return productPriceDao.getByCode(code);
	}

	@Override
	@Transactional(readOnly=true)
	public ProductPrice getByProductAndHospital(String projectCode, String hospitalCode, String productCode) {
		return productPriceDao.getByProductAndHospital(hospitalCode, productCode);
	}

	@Override
	@Transactional(readOnly=true)
	public DataGrid<ProductPrice> queryByProductAndGpo(String projectCode, String productCode, String vendorCode, PageRequest pageable) {
		return productPriceDao.queryByProductAndGpo(productCode, vendorCode, pageable);
	}

	@Override
	public ProductPrice getByProduct(String projectCode, String productCode, Integer type) {
		if(type == 1){
			return productPriceDao.getByProduct(productCode, TradeType.hospital);
		}
		return productPriceDao.getByProduct(productCode, TradeType.patient);
	}


	@Override
	@Transactional
	public String doExcelH(String projectCode, String[][] excelarr, User user) throws Exception {
//		Hospital hospital = hospitalService.getById(user.getOrganization().getOrgId().longValue());
//		if(hospital == null){
//			return "该帐号不是医院帐号，无权操作";
//		}
		System.out.println("excelarr="+excelarr);
		for (int i = 0; i < excelarr.length; i++) {
			String[] row = excelarr[i];
			//row ,0药品编码	1供应商code 2价格 3有效期start 4有效期end 5指定医院code 
			BigDecimal price = new BigDecimal("0");
			try {
				if(row[2].trim().equals(""))
					continue;
				price = new BigDecimal(row[2]);
			} catch (Exception e) {
				continue;
			}

			if(price.compareTo(new BigDecimal("0"))<=0){
				continue;
			}
			
			Product product = productDao.getByCode(row[0]);
			if(product == null){
				throw new MyException("第"+i+"笔数据异常，药品编码"+row[0]+"不存在");
				//return "第"+i+"笔数据异常，药品编码"+row[0]+"不存在";
			}
			
			Company vendor = companyDao.findByCode(row[1]);
			if(vendor == null){
				throw new MyException("第"+i+"笔数据异常，供应商编码"+row[1]+"不存在");
			}
			Hospital hospital = null;
			if(!row[5].trim().equals("")){
				hospital = hospitalDao.findByCode(row[5]);
				if(hospital == null){
					throw new MyException("第"+i+"笔数据异常，医院编码"+row[1]+"不存在");
				}
			}
				
			String beginDate = row[3];
			if(!DateUtil.checkDateFMT(beginDate, 1) && !DateUtil.checkDateFMT(beginDate, 2)){
				throw new MyException("第"+i+"笔数据异常，有效期起"+row[3]+"格式错误");
			}
			String outDate = row[4];
			if(!DateUtil.checkDateFMT(outDate, 1) && !DateUtil.checkDateFMT(outDate, 2)){
				throw new MyException("第"+i+"笔数据异常，有效期止"+row[4]+"格式错误");
			}
			beginDate = beginDate.substring(0,10);
			outDate = outDate.substring(0,10);
			
			ProductVendor pgpo = productVendorDao.findByKey(product.getCode(), vendor.getCode());
			
			if(pgpo == null ){
				pgpo = new ProductVendor();
				pgpo.setProductCode(product.getCode());
				pgpo.setVendorCode(vendor.getCode());
				pgpo.setIsDisabled(0);
				pgpo.setModifyDate(new Date());
				productVendorDao.save(pgpo);
			}else if(pgpo.getIsDisabled() == 1){
				pgpo.setIsDisabled(0);
				productVendorDao.update(pgpo);
			}
			
			ProductPrice pp = new ProductPrice();
			if(vendor != null){
				pp.setVendorCode(vendor.getCode());
				pp.setVendorName(vendor.getFullName());
			}
			
			if(hospital != null){
				pp.setHospitalCode(hospital.getCode());
				pp.setHospitalName(hospital.getFullName());
			}
			if(product != null){
				pp.setProductCode(product.getCode());
				pp.setProductName(product.getName());
			}	
			pp.setFinalPrice(price);
			pp.setBiddingPrice(price);
			pp.setTradeType(TradeType.hospital);
			//pp.setIsSplited(0);
			pp.setEffectDate(beginDate);
			pp.setBeginDate(beginDate);
			pp.setOutDate(outDate);
			
			productPriceDao.save(pp);
		}
		 return "成功导入"+excelarr.length+"笔数据";
	}

	@Override
	public ProductPrice getPatientPrice(String projectCode, String productCode, String vendorCode) {
		return productPriceDao.getPatientPrice(productCode,vendorCode);
	}

	@Override
	public List<ProductPrice> effectList(String projectCode, String today) {
		return productPriceDao.effectList(today);
	}

	@Override
	@Transactional
	@CacheEvict(value = {"goods","goodsPrice"}, allEntries = true)
	public void effectPrice(String projectCode, ProductPrice productPrice) {
		//查询唯一一笔生效的数据,将他过期
		ProductPrice oldpp = productPriceDao.findByKey(productPrice.getProductCode(),productPrice.getVendorCode(),productPrice.getHospitalCode(),productPrice.getTradeType());
		if(oldpp != null){
			oldpp.setIsEffected(2);
			productPriceDao.update(oldpp);
		}
		//如果是统一价格 覆盖所有价格的，还需把生效的 所有价格 设置为过期
		if(productPrice.getTradeType().equals(TradeType.hospital) && productPrice.getHospitalCode() == null){
			if(productPrice.getEffectType() == 1){
				List<ProductPrice> effectlist = productPriceDao.effectList(productPrice.getProductCode(),productPrice.getVendorCode());
				for (ProductPrice pp : effectlist) {
					pp.setIsEffected(2);
					productPriceDao.update(pp);
				}
			}
		}
		//生效价格
		productPrice.setIsEffected(1);
		productPriceDao.update(productPrice);
		
		//同步goodsprice
		if(productPrice.getTradeType().equals(TradeType.hospital)){
			//如果是指定医院价格
			if(productPrice.getHospitalCode() != null){
				updateGoodsprice1(productPrice);
			}else{//统一价格
				updateGoodsprice2(productPrice);
			}
			
		}
	}

	private void updateGoodsprice2(ProductPrice productPrice) {
		//1、根据Product找出所有goods
		List<Goods> glist = goodsDao.listByProductCode(productPrice.getProductCode());
		//2、Product+Hospital＋vendor 去goodsprice查询，查不到新增，查到修改
		for (Goods goods : glist) {
			GoodsPrice goodsPrice = goodsPriceDao.findByKey(goods.getProductCode(), productPrice.getVendorCode(), goods.getHospitalCode(), null, null,0);
			if(goodsPrice == null){
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(productPrice.getVendorCode());
				goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
				goodsPrice.setFinalPrice(productPrice.getFinalPrice());
				goodsPrice.setBeginDate(productPrice.getBeginDate());
				goodsPrice.setOutDate(productPrice.getOutDate());
				goodsPrice.setEffectDate(productPrice.getEffectDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(0);
				goodsPriceDao.save(goodsPrice);
			}else if(productPrice.getEffectType() == 1){//覆盖所有
				goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
				goodsPrice.setFinalPrice(productPrice.getFinalPrice());
				goodsPrice.setBeginDate(productPrice.getBeginDate());
				goodsPrice.setOutDate(productPrice.getOutDate());
				goodsPrice.setEffectDate(productPrice.getEffectDate());
				goodsPrice.setIsDisabled(productPrice.getIsDisabled());
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(0);
				goodsPriceDao.update(goodsPrice);
			}else if(productPrice.getEffectType() == 0){//不覆盖指定医院的价格
				if(goodsPrice.getPriceType() == 0){//统一价格
					goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
					goodsPrice.setFinalPrice(productPrice.getFinalPrice());
					goodsPrice.setBeginDate(productPrice.getBeginDate());
					goodsPrice.setOutDate(productPrice.getOutDate());
					goodsPrice.setEffectDate(productPrice.getEffectDate());
					goodsPrice.setIsDisabled(productPrice.getIsDisabled());
					goodsPrice.setIsDisabledByH(0);
					goodsPrice.setPriceType(0);
					goodsPriceDao.update(goodsPrice);
				}
			}
		}
	}
	
	private void updateGoodsprice1(ProductPrice productPrice) {
		//1、找出对应医院的goods
		Goods goods = goodsDao.getByProductAndHospital(productPrice.getProductCode(), productPrice.getHospitalCode(),0);
		if(goods != null){
			//2、找出goodsPrice
			GoodsPrice goodsPrice = goodsPriceDao.findByKey(goods.getProductCode(), productPrice.getVendorCode(), goods.getHospitalCode(), null, null,0);
			if(goodsPrice == null){
				goodsPrice = new GoodsPrice();
				goodsPrice.setGoodsId(goods.getId());
				goodsPrice.setProductCode(goods.getProductCode());
				goodsPrice.setHospitalCode(goods.getHospitalCode());
				goodsPrice.setVendorCode(productPrice.getVendorCode());
				goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
				goodsPrice.setFinalPrice(productPrice.getFinalPrice());
				goodsPrice.setBeginDate(productPrice.getBeginDate());
				goodsPrice.setOutDate(productPrice.getOutDate());
				goodsPrice.setEffectDate(productPrice.getEffectDate());
				goodsPrice.setIsDisabled(0);
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(1);
				goodsPriceDao.save(goodsPrice);
			}else{
				goodsPrice.setBiddingPrice(productPrice.getBiddingPrice());
				goodsPrice.setFinalPrice(productPrice.getFinalPrice());
				goodsPrice.setBeginDate(productPrice.getBeginDate());
				goodsPrice.setOutDate(productPrice.getOutDate());
				goodsPrice.setEffectDate(productPrice.getEffectDate());
				goodsPrice.setIsDisabled(productPrice.getIsDisabled());
				goodsPrice.setIsDisabledByH(0);
				goodsPrice.setPriceType(1);
				goodsPriceDao.update(goodsPrice);
			}
		}
	}

	@Override
	@Transactional
	public void deletePrice(String projectCode, Long id) {
		ProductPrice productPrice = productPriceDao.getById(id);
		if(productPrice != null){
			if(productPrice.getIsEffected() == 0){//未生效的直接删除
				productPriceDao.delete(productPrice);
			}else if(productPrice.getIsEffected() == 1){
				productPrice.setIsDisabled(1);
				productPrice.setIsEffected(2);
				productPriceDao.update(productPrice);
				if(productPrice.getTradeType().equals(TradeType.hospital)){
					//指定医院价格作废
					if(productPrice.getHospitalCode() != null){
						delGoodsPrice1(productPrice);
					}else{//通用价格作废
						delGoodsPrice2(productPrice);
					}
				}
			}//过期的价格不处理
		}
	}

	private void delGoodsPrice2(ProductPrice productPrice) {
		//找到所有productPrice生效的goodsPrice,将他们禁用；如果有指定价格，修改为指定价格
		List<GoodsPrice> glist = goodsPriceDao.findByProduct(productPrice.getProductCode(), productPrice.getVendorCode());
		for (GoodsPrice goodsPrice : glist) {
			//查看ProductCode＋VendorCode＋HospitalCode是否有生效的指定价格
			ProductPrice pp = productPriceDao.findByKey(productPrice.getProductCode(), productPrice.getVendorCode(), productPrice.getHospitalCode(), TradeType.hospital);
			if(pp != null){
				goodsPrice.setBiddingPrice(pp.getBiddingPrice());
				goodsPrice.setFinalPrice(pp.getFinalPrice());
				goodsPrice.setBeginDate(pp.getBeginDate());
				goodsPrice.setOutDate(pp.getOutDate());
				goodsPrice.setEffectDate(pp.getEffectDate());
				goodsPrice.setPriceType(1);
				goodsPriceDao.update(goodsPrice);
			}else{
				goodsPrice.setIsDisabled(1);
				goodsPriceDao.update(goodsPrice);
			}
		}
	}

	private void delGoodsPrice1(ProductPrice productPrice) {
		//找到唯一的一笔生效的goodsPrice
		GoodsPrice gp = goodsPriceDao.findByKey(productPrice.getProductCode(), productPrice.getVendorCode(), productPrice.getHospitalCode(), 0, 0,0);
		if(gp != null && gp.getPriceType() == 1){//如果是指定价格，则将其修改为通用价格
			//查看ProductCode＋VendorCode是否有生效的通用价格
			ProductPrice pp = productPriceDao.findByKey(productPrice.getProductCode(), productPrice.getVendorCode(), null, TradeType.hospital);
			if(pp != null){
				gp.setBiddingPrice(pp.getBiddingPrice());
				gp.setFinalPrice(pp.getFinalPrice());
				gp.setBeginDate(pp.getBeginDate());
				gp.setOutDate(pp.getOutDate());
				gp.setEffectDate(pp.getEffectDate());
				gp.setPriceType(0);
				goodsPriceDao.update(gp);
			}else{
				gp.setIsDisabled(1);
				goodsPriceDao.update(gp);
			}
		}
		
	}

	@Override
	public List<ProductPrice> listByProduct(String projectCode, String productCode) {
		return productPriceDao.listByProduct(productCode);
	}

	@Override
	public List<ProductPrice> listByProductAndHospital(String projectCode, String productCode, String orgCode) {
		return productPriceDao.listByProductAndHospital(productCode, orgCode);
	}

	@Override
	public ProductPrice findByKey(String projectCode, String productCode, String vendorCode, String hospitalCode, TradeType type) {
		return productPriceDao.findByKey(productCode, vendorCode, hospitalCode, type);
	}

	@Override
	public void saveProductPrice(List<JSONObject> list, String ptdm) {
		for(JSONObject jo:list){
			String ypbm = jo.getString("ypbm");		//药品编码
			String yybm = jo.getString("yybm");		//医院编码
			BigDecimal zbj = jo.getBigDecimal("zbj");	//中标价
			BigDecimal cjj = jo.getBigDecimal("cjj");	//成交价
			String sxrq = jo.getString("sxrq");		//有效期起
			String yxqq = jo.getString("yxqq");		//有效期起
			String yxqz = jo.getString("yxqz");		//有效期止
			int sfjy = jo.getIntValue("sfjy");		//是否禁用
			String gysbm = jo.getString("gysbm"); 	//供应商编码
			
			Product product = productDao.getByCode(ypbm);
			Hospital hospital = null;
			if(!StringUtils.isEmpty(yybm)){
				hospital = hospitalDao.findByCode(yybm);
			}			
			Company vendor = companyDao.findByCode(gysbm, "isVendor=1");
			ProductPrice productPrice = new ProductPrice();
			productPrice.setProductCode(product.getCode());
			productPrice.setProductName(product.getName());
			productPrice.setVendorCode(vendor.getCode());
			productPrice.setVendorName(vendor.getFullName());
			if( hospital != null){
				productPrice.setHospitalCode(hospital.getCode());
				productPrice.setHospitalName(hospital.getFullName());
			}else{
				int tyjgsxfs_i = jo.getIntValue("tyjgsxfs");//统一价格生效方式 
				productPrice.setEffectType(tyjgsxfs_i);
			}
			productPrice.setEffectDate(sxrq);
			productPrice.setBeginDate(yxqq);
			productPrice.setOutDate(yxqz);
			productPrice.setBiddingPrice(zbj);
			productPrice.setFinalPrice(cjj);
			productPrice.setIsDisabled(sfjy);				
			productPrice.setModifyDate(new Date());
			productPrice.setTradeType(TradeType.hospital);
			productPrice.setProjectCode(ptdm);//平台代码
			
			productPriceDao.save(productPrice);
		}
	}
	
	
}
