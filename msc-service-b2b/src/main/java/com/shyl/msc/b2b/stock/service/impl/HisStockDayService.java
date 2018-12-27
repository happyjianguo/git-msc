package com.shyl.msc.b2b.stock.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.stock.dao.IHisStockDayDao;
import com.shyl.msc.b2b.stock.entity.HisStockDay;
import com.shyl.msc.b2b.stock.service.IHisStockDayService;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Hospital;

@Service
@Transactional
public class HisStockDayService extends BaseService<HisStockDay, Long> implements IHisStockDayService{
	private IHisStockDayDao hisStockDayDao;
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IProductService productService;
	@Resource
	private IGoodsService goodsService;
	
	public IHisStockDayDao getHisStockDayDao() {
		return hisStockDayDao;
	}

	@Resource
	public void setHisStockDayDao(IHisStockDayDao hisStockDayDao) {
		this.hisStockDayDao = hisStockDayDao;
		super.setBaseDao(hisStockDayDao);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getHospitalTurnoverRatio(String projectCode, String beginDate, String endDate) {
		List<Map<String, Object>> purchases = purchaseOrderDao.totalHospitalSum(beginDate, endDate);
		List<Map<String, Object>> beginStocks = hisStockDayDao.getHospitalBeginStock(beginDate, endDate);
		List<Map<String, Object>> endStocks = hisStockDayDao.getHospitalEndStock(beginDate, endDate);
		
		Map<String,Object> beginStockMap = new HashMap<String,Object>();
		for(Map<String, Object> map:beginStocks){
			beginStockMap.put(map.get("HOSPITALCODE").toString(), map.get("BEGINAMT"));
		}
		Map<String,Object> endStockMap = new HashMap<String,Object>();
		for(Map<String, Object> map:endStocks){
			endStockMap.put(map.get("HOSPITALCODE").toString(), map.get("ENDAMT"));
		}
		for(Map<String, Object> map:purchases){
			if(map != null && map.get("HOSPITALCODE") != null){
				map.put("BEGINAMT", beginStockMap.get(map.get("HOSPITALCODE").toString()));
				map.put("ENDAMT", endStockMap.get(map.get("HOSPITALCODE").toString()));
			}
		}
		
		return purchases;
	}

	@Override
	@Transactional(readOnly = true)
	public HisStockDay getByDateAndProduct(String projectCode, String hospitalCode, String date, String productId) {
		return hisStockDayDao.getByDateAndProduct(hospitalCode, date, productId);
	}

	@Override
	@Transactional
	public void saveHisStock(String projectCode, List<JSONObject> list, String kcrq, Hospital hospital, Long datagramId, String ptdm) {
		for(JSONObject jo:list){
			String ypbm = jo.getString("ypbm"); 	//药品编码
			BigDecimal qckcsl = jo.getBigDecimal("qckcsl"); //期初库存数量
			BigDecimal qckcje = jo.getBigDecimal("qckcje"); //期初库存金额
			BigDecimal qmkcsl = jo.getBigDecimal("qmkcsl"); //期末库存数量
			BigDecimal qmkcje = jo.getBigDecimal("qmkcje"); //期末库存金额
			Product product = productService.getByCode(projectCode, ypbm);
			
			HisStockDay hisStockDay = hisStockDayDao.getByDateAndProduct(hospital.getCode(), kcrq, product.getCode());
			if(hisStockDay == null){
				hisStockDay = new HisStockDay();
				hisStockDay.setStockDate(kcrq);						//日期
				hisStockDay.setHospitalCode(hospital.getCode());		//医院code
				hisStockDay.setHospitalName(hospital.getFullName());//医院名称
				hisStockDay.setProductCode(product.getCode());		//产品code
				hisStockDay.setProjectCode(ptdm);
			}
			hisStockDay.setProductName(product.getName());		//产品名称
			hisStockDay.setBeginNum(qckcsl);					//起初库存数量
			hisStockDay.setBeginAmt(qckcje);					//起初库存金额
			hisStockDay.setEndNum(qmkcsl);						//期末库存数量
			hisStockDay.setEndAmt(qmkcje);						//期末库存金额
			hisStockDay.setDatagramId(datagramId);
			hisStockDayDao.saveOrUpdate(hisStockDay);
			
			Goods goods = goodsService.getByProductCodeAndHosiptal(projectCode, ypbm, hospital.getCode());
			goods.setStockNum(hisStockDay.getEndNum());
			goods.setStockSum(hisStockDay.getEndAmt());
			goodsService.update(projectCode, goods);
		}
	}

	@Override
	public BigDecimal getStockAmt(String projectCode, String stockDate, String hospitalCode, String productCode) {
		return hisStockDayDao.getStockAmt(stockDate, hospitalCode, productCode);
	}

	@Override
	public BigDecimal getStockAmt(String projectCode, String stockDate, String hospitalCode) {
		return hisStockDayDao.getStockAmt(stockDate, hospitalCode);
	}
	
	
}
