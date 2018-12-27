package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.stl.dao.ITradeInvoiceDao;
import com.shyl.msc.b2b.stl.dao.ITradeInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
/**
 * 交易发票
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class TradeInvoiceService extends BaseService<TradeInvoice, Long> implements ITradeInvoiceService {
	private ITradeInvoiceDao tradeInvoiceDao;
	@Resource
	private ITradeInvoiceDetailDao tradeInvoiceDetailDao;
	public ITradeInvoiceDao getTradeInvoiceDao() {
		return tradeInvoiceDao;
	}

	@Resource
	public void setTradeInvoiceDao(ITradeInvoiceDao tradeInvoiceDao) {
		this.tradeInvoiceDao = tradeInvoiceDao;
		super.setBaseDao(tradeInvoiceDao);
	}
	
	@Resource
	private ISnDao snDao;
	@Resource
	private IProductService productService;
	
	@Override
	@Transactional
	public void saveTradeInvoice(String projectCode, TradeInvoice tradeInvoice){
		//新增tradeInvoice
		tradeInvoice = tradeInvoiceDao.saveJDBC(tradeInvoice);
		List<TradeInvoiceDetail> tradeInvoiceDetails = new ArrayList<>();
		Map<String,TradeInvoiceDetail> maps = new HashMap<>();
		for (TradeInvoiceDetail tradeInvoiceDetail : tradeInvoice.getTradeInvoiceDetails()) {
			tradeInvoiceDetail.setTradeInvoice(tradeInvoice);
			tradeInvoiceDetails.add(tradeInvoiceDetail);
			//tradeInvoiceDetailDao.save(tradeInvoiceDetail);
		}
		//批量新增tradeInvoiceDetail
		tradeInvoiceDetailDao.saveBatch(tradeInvoiceDetails);
	}

	@Override
	@Transactional(readOnly = true)
	public TradeInvoice getByInternalCode(String projectCode, String fph,TradeInvoice.Type type) {
		return tradeInvoiceDao.getByInternalCode(fph,type);
	}

	@Override
	public TradeInvoice getByCode(String projectCode, String conmpanyCode, String code, boolean isGPO) {
		return tradeInvoiceDao.getByCode(conmpanyCode,code,isGPO);
	}

	@Override
	@Transactional
	public JSONArray saveTradeInvoice(String projectCode, TradeInvoice tradeInvoice, List<JSONObject> arr) {
		BigDecimal goodsSum = new BigDecimal(0);
		BigDecimal num = new BigDecimal(0);
		int i = 0;
		JSONArray res_arr = new JSONArray();
		for(JSONObject jo:arr){//来筛选订单主档笔数
			i++;
			int sxh = jo.getIntValue("sxh");	//顺序号
			String ypbm = jo.getString("ypbm");//药品编码
			String dw = jo.getString("dw");//税率
			String ph = jo.getString("ph");//税率
			BigDecimal spsl = jo.getBigDecimal("spsl");//商品数量
			BigDecimal hsdj = jo.getBigDecimal("hsdj");//含税单价
			BigDecimal bhsdj = jo.getBigDecimal("bhsdj");//不含税单价
			BigDecimal hsje = jo.getBigDecimal("hsje");	//含税金额
			BigDecimal bhsje = jo.getBigDecimal("bhsje");	//不含税金额
			BigDecimal sl = jo.getBigDecimal("sl");//税率
			
			Product product = productService.getByCode(tradeInvoice.getProjectCode(), ypbm);
			
			TradeInvoiceDetail tradeInvoiceDetail = new TradeInvoiceDetail();
			String code_detail = tradeInvoice.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			tradeInvoiceDetail.setTradeInvoice(tradeInvoice); //发票主档
			tradeInvoiceDetail.setTaxRate(sl);//税率		
			tradeInvoiceDetail.setUnit(dw);
			tradeInvoiceDetail.setGoodsNum(spsl);//数量
			tradeInvoiceDetail.setPrice(hsdj);//含税单价
			tradeInvoiceDetail.setNoTaxPrice(bhsdj);//不含税单价
			tradeInvoiceDetail.setGoodsSum(hsje);//含税金额
			tradeInvoiceDetail.setNoTaxSum(bhsje);//不含税金额		
			tradeInvoiceDetail.setCode(code_detail);//发票明细编码
			tradeInvoiceDetail.setProduct(product); //商品
			tradeInvoiceDetail.setProductCode(product.getCode());
			tradeInvoiceDetail.setProductName(product.getName());
			tradeInvoiceDetail.setProducerName(product.getProducerName());
			tradeInvoiceDetail.setDosageFormName(product.getDosageFormName());
			tradeInvoiceDetail.setModel(product.getModel());
			tradeInvoiceDetail.setPackDesc(product.getPackDesc());
			tradeInvoiceDetail.setUnit(product.getUnit());
			tradeInvoiceDetail.setBatchCode(ph);
			tradeInvoiceDetail.setIsExistsParent(0);
			
			tradeInvoice.getTradeInvoiceDetails().add(tradeInvoiceDetail);
			goodsSum = goodsSum.add(tradeInvoiceDetail.getGoodsSum());
			num = num.add(tradeInvoiceDetail.getGoodsNum());
			
			JSONObject res_arr_jo = new JSONObject();
			res_arr_jo.put("sxh", sxh);
			res_arr_jo.put("fpmxbh", code_detail);
			res_arr.add(res_arr_jo);
		}
		tradeInvoice.setNum(num);
		tradeInvoice.setSum(goodsSum);
		
		this.saveTradeInvoice(projectCode, tradeInvoice);
		return res_arr;
	}
	
}
