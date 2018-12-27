package com.shyl.msc.b2b.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.stock.dao.IVendorStockDayDao;
import com.shyl.msc.b2b.stock.entity.VendorStockDay;
import com.shyl.msc.b2b.stock.service.IVendorStockDayService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;

@Service
@Transactional(readOnly = true)
public class VendorStockDayService extends BaseService<VendorStockDay, Long> implements IVendorStockDayService {

	private IVendorStockDayDao vendorStockDayDao;

	public IVendorStockDayDao getVendorStockDayDao() {
		return vendorStockDayDao;
	}
	@Resource
	private IProductService productService;
	@Resource
	public void setVendorStockDayDao(IVendorStockDayDao vendorStockDayDao) {
		this.vendorStockDayDao = vendorStockDayDao;
		super.setBaseDao(vendorStockDayDao);
	}

	@Override
	public void saveVendorStock(String projectCode, List<JSONObject> list, String kcrq, Company vendor, Long datagramId) {
		for(JSONObject jo:list){
			String ypbm = jo.getString("ypbm"); 	//药品编码
			BigDecimal qckcsl = jo.getBigDecimal("qckcsl"); //期初库存数量
			BigDecimal qckcje = jo.getBigDecimal("qckcje"); //期初库存金额
			BigDecimal qmkcsl = jo.getBigDecimal("qmkcsl"); //期末库存数量
			BigDecimal qmkcje = jo.getBigDecimal("qmkcje"); //期末库存金额
			Product product = productService.getByCode(projectCode, ypbm);
			
			VendorStockDay vendorStockDay = vendorStockDayDao.getByDateAndProduct(vendor.getCode(), kcrq, product.getCode());
			if(vendorStockDay == null){
				vendorStockDay = new VendorStockDay();
				vendorStockDay.setStockDate(kcrq);						//日期
				vendorStockDay.setVendorCode(vendor.getCode());		//供应商code
				vendorStockDay.setVendorName(vendor.getFullName());//供应商名称
				vendorStockDay.setProductCode(product.getCode());		//产品code
			}
			vendorStockDay.setProductName(product.getName());		//产品名称
			vendorStockDay.setBeginNum(qckcsl);					//起初库存数量
			vendorStockDay.setBeginAmt(qckcje);					//起初库存金额
			vendorStockDay.setEndNum(qmkcsl);						//期末库存数量
			vendorStockDay.setEndAmt(qmkcje);						//期末库存金额
			vendorStockDay.setDatagramId(datagramId);
			vendorStockDayDao.saveOrUpdate(vendorStockDay);
		}
	}

	@Override
	public BigDecimal getStockAmt(String projectCode, String stockDate, String vendorCode, String productCode) {
		return vendorStockDayDao.getStockAmt(stockDate, vendorCode, productCode);
	}

	@Override
	public BigDecimal getStockAmt(String projectCode, String stockDate, String vendorCode) {
		return vendorStockDayDao.getStockAmt(stockDate, vendorCode);
	}
}
