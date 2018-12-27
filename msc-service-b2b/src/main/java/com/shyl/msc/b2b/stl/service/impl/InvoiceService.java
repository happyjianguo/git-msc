package com.shyl.msc.b2b.stl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.stl.dao.IInvoiceDao;
import com.shyl.msc.b2b.stl.dao.IInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.Invoice;
import com.shyl.msc.b2b.stl.entity.Invoice.Status;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.User;
/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class InvoiceService extends BaseService<Invoice, Long> implements IInvoiceService {
	private IInvoiceDao invoiceDao;
	@Resource
	private IInvoiceDetailDao invoiceDetailDao;
	public IInvoiceDao getInvoiceDao() {
		return invoiceDao;
	}

	@Resource
	public void setInvoiceDao(IInvoiceDao invoiceDao) {
		this.invoiceDao = invoiceDao;
		super.setBaseDao(invoiceDao);
	}
	
	@Resource
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IReturnsOrderDao returnsOrderDao;
	
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	private IReturnsOrderDetailDao returnsOrderDetailDao;
	@Resource
	private ISnDao snDao;
	@Resource
	private IProductService productService;
	
	@Override
	@Transactional
	public void saveInvoice(String projectCode, Invoice invoice){
		invoice.setStatus(Status.unsettle);
		//新增invoice
		invoiceDao.saveJDBC(invoice);
		List<InvoiceDetail> invoiceDetails = new ArrayList<>();
		for (InvoiceDetail invoiceDetail : invoice.getInvoiceDetails()) {
			invoiceDetail.setInvoice(invoice);
			invoiceDetails.add(invoiceDetail);
			//invoiceDetailDao.save(invoiceDetail);
		}
		//批量新增invoiceDetail
		invoiceDetailDao.saveBatch(invoiceDetails);
	}

	@Override
	@Transactional(readOnly = true)
	public Invoice getByInternalCode(String projectCode, String companyCode, String fph, boolean isGPO) {
		return invoiceDao.getByInternalCode(companyCode, fph, isGPO);
	}

	@Override
	@Transactional
	public String mkinvoiceBlue(String projectCode, String orderCode,Long[] detailIds,BigDecimal[] sums, String invoiceCode, String invoiceDate,BigDecimal taxRate, User user) {
		//DeliveryOrder d = deliveryOrderDao.getById(deliveryId);
		//d.setIsInvoiced(1);
		PurchaseOrder po = purchaseOrderDao.findByCode(orderCode);
		//deliveryOrderDao.update(d);
		
		Invoice invoice = new Invoice();
		String code = snDao.getCode(OrderType.invoice);
		invoice.setCode(code);	// 发票编码
		invoice.setInternalCode(invoiceCode);
		invoice.setOrderDate(DateUtil.strToDate(invoiceDate));//发票日期
		invoice.setGpoCode(po.getGpoCode());
		invoice.setGpoName(po.getGpoName());
		invoice.setVendorCode(po.getVendorCode());
		invoice.setVendorName(po.getVendorName());
		invoice.setHospitalCode(po.getHospitalCode());
		invoice.setHospitalName(po.getHospitalName());
		invoice.setIsRed(0);//是否冲红
		invoice.setIsManual(0);//TODO json里没有
		invoice.setIsPass(0);
		invoice.setRemarks("");
		
		BigDecimal goodsSum = new BigDecimal(0);
		BigDecimal noTaxSum = new BigDecimal(0);
		BigDecimal num = new BigDecimal(0);
		//List<DeliveryOrderDetail> ddlist = deliveryOrderDetailDao.listByDeliveryOrder(deliveryId);
		for (int i = 0; i < detailIds.length; i++) {
			//配送单明细是否开票完成
			DeliveryOrderDetail dd = deliveryOrderDetailDao.getById(detailIds[i]);
			BigDecimal isum = dd.getInvoiceGoodsSum();
			isum = isum == null?new BigDecimal("0"):isum;
			dd.setInvoiceGoodsSum(sums[i].add(isum));
			if(dd.getInvoiceGoodsSum().compareTo(dd.getGoodsSum())>=0){
				dd.setIsInvoice(1);
			}
			deliveryOrderDetailDao.update(dd);
			//配送单主档是否开票完成
			DeliveryOrder d = deliveryOrderDao.getById(dd.getDeliveryOrder().getId());
			List<DeliveryOrderDetail> ddlist = deliveryOrderDetailDao.listByDeliveryOrder(dd.getDeliveryOrder().getId());
			Integer isinvoiceFlag = 1;
			for (DeliveryOrderDetail deliveryOrderDetail : ddlist) {
				isinvoiceFlag = isinvoiceFlag * deliveryOrderDetail.getIsInvoice();
				System.out.println("isinvoiceFlag======="+isinvoiceFlag);
			}
			if(isinvoiceFlag == 1){
				d.setIsInvoiced(1);
				deliveryOrderDao.update(d);
			}
			
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			String code_detail = invoice.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			invoiceDetail.setInvoice(invoice); //发票主档
			invoiceDetail.setPrice(dd.getPrice());//成交价
			invoiceDetail.setNoTaxPrice(invoiceDetail.getPrice().divide(new BigDecimal("1").add(taxRate.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP));//不含税单价
			invoiceDetail.setTaxRate(taxRate);//税率		
			invoiceDetail.setNoTaxSum(sums[i].divide(new BigDecimal("1").add(taxRate.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP));//不含税金额
			invoiceDetail.setCode(code_detail);//发票明细编码
			invoiceDetail.setGoodsNum(dd.getGoodsNum());//数量
			invoiceDetail.setGoodsSum(sums[i]);//含税金额
			invoiceDetail.setDeliveryOrReturnsDetailCode(dd.getCode());
			invoiceDetail.setDeliveryOrReturnsCode(dd.getDeliveryOrder().getCode());
			invoiceDetail.setProductCode(dd.getProductCode());
			invoiceDetail.setProductName(dd.getProductName());
			invoiceDetail.setProducerName(dd.getProducerName());
			invoiceDetail.setDosageFormName(dd.getDosageFormName());
			invoiceDetail.setModel(dd.getModel());
			invoiceDetail.setPackDesc(dd.getPackDesc());
			invoiceDetail.setUnit(dd.getUnit());
			
			
			invoice.getInvoiceDetails().add(invoiceDetail);
			
			goodsSum = goodsSum.add(invoiceDetail.getGoodsSum());
			noTaxSum = noTaxSum.add(invoiceDetail.getNoTaxSum());
			num = num.add(invoiceDetail.getGoodsNum());
		}
		
		invoice.setNum(num);
		invoice.setSum(goodsSum);
		invoice.setNoTaxSum(noTaxSum);
		
		this.saveInvoice(projectCode, invoice);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", invoice.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	@Transactional
	public String mkinvoiceRed(String projectCode, Long returnId, String invoiceCode, String invoiceDate, BigDecimal taxRate, User user) {
		ReturnsOrder r = returnsOrderDao.getById(returnId);
		r.setIsInvoiced(1);
		returnsOrderDao.update(r);
		
		Invoice invoice = new Invoice();
		String code = snDao.getCode(OrderType.invoice);
		invoice.setCode(code);	// 发票编码
		invoice.setInternalCode(invoiceCode);
		invoice.setOrderDate(DateUtil.strToDate(invoiceDate));//发票日期
		invoice.setGpoCode(r.getGpoCode());
		invoice.setGpoName(r.getGpoName());
		invoice.setVendorCode(r.getVendorCode());
		invoice.setVendorName(r.getVendorName());
		invoice.setHospitalCode(r.getHospitalCode());
		invoice.setHospitalName(r.getHospitalName());
		invoice.setIsRed(1);//是否冲红
		invoice.setIsManual(0);//TODO json里没有
		invoice.setIsPass(0);
		invoice.setRemarks("");	
		
		BigDecimal goodsSum = new BigDecimal(0);
		BigDecimal noTaxSum = new BigDecimal(0);
		BigDecimal num = new BigDecimal(0);
		List<ReturnsOrderDetail> rdlist = returnsOrderDetailDao.listByReturnId(returnId);
		for (int i = 0; i < rdlist.size(); i++) {
			ReturnsOrderDetail rd = rdlist.get(i);
			
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			String code_detail = invoice.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			invoiceDetail.setInvoice(invoice); //发票主档
			invoiceDetail.setPrice(rd.getPrice());//成交价
			invoiceDetail.setNoTaxPrice(invoiceDetail.getPrice().divide(new BigDecimal("1").add(taxRate.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP));//不含税单价
			invoiceDetail.setTaxRate(taxRate);//税率		
			invoiceDetail.setNoTaxSum(rd.getGoodsSum().divide(new BigDecimal("1").add(taxRate.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP));//不含税金额
			invoiceDetail.setCode(code_detail);//发票明细编码
			invoiceDetail.setGoodsNum(rd.getGoodsNum());//数量
			invoiceDetail.setGoodsSum(rd.getGoodsSum());//含税金额
			invoiceDetail.setDeliveryOrReturnsDetailCode(rd.getCode());
			invoiceDetail.setDeliveryOrReturnsCode(rd.getReturnsOrder().getCode());
			invoiceDetail.setProductCode(rd.getProductCode());
			invoiceDetail.setProductName(rd.getProductName());
			invoiceDetail.setProducerName(rd.getProducerName());
			invoiceDetail.setDosageFormName(rd.getDosageFormName());
			invoiceDetail.setModel(rd.getModel());
			invoiceDetail.setPackDesc(rd.getPackDesc());
			invoiceDetail.setUnit(rd.getUnit());
			
			invoice.getInvoiceDetails().add(invoiceDetail);
			goodsSum = goodsSum.add(invoiceDetail.getGoodsSum());
			noTaxSum = noTaxSum.add(invoiceDetail.getNoTaxSum());
			num = num.add(invoiceDetail.getGoodsNum());
		}
		invoice.setNum(num);
		invoice.setSum(goodsSum);
		invoice.setNoTaxSum(noTaxSum);
		
		this.saveInvoice(projectCode, invoice);
		JSONArray jsonArray = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", invoice.getCode());
		jsonArray.add(jo);
		return JSON.toJSONString(jsonArray);
	}

	@Override
	@Transactional
	public DataGrid<Map<String, Object>> hospitalListForSettle(String projectCode, String vendorCode, PageRequest pageable) {
		return invoiceDao.hospitalListForSettle(vendorCode, pageable);
	}

	@Override
	@Transactional
	public List<Map<String, Object>> listForSettle(String projectCode, String hospitalCode, String vendorCode) {
		return invoiceDao.listForSettle(hospitalCode, vendorCode);
	}

	@Override
	public Invoice findByCode(String projectCode, String code) {
		return invoiceDao.findByCode(code);
	}

	@Override
	public List<Invoice> listByDeliveryOrReturnsCode(String projectCode, String stdbh) {
		return invoiceDao.listByDeliveryOrReturnsCode(stdbh);
	}

	@Override
	@Transactional
	public JSONArray saveInvoice(String projectCode, Invoice invoice, List<JSONObject> arr) {
		BigDecimal goodsSum = new BigDecimal(0);
		BigDecimal num = new BigDecimal(0);
		int i = 0;
		JSONArray res_arr = new JSONArray();
		List<DeliveryOrderDetail> deliveryOrderDetails = new ArrayList<>();
		List<DeliveryOrder> deliveryOrders = new ArrayList<>();
		List<ReturnsOrder> returnsOrders = new ArrayList<>();
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
			String stdmxbh = jo.getString("stdmxbh");//送退单明细编号
			
			Product product = productService.getByCode(invoice.getProjectCode(), ypbm);
			
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			String code_detail = invoice.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			invoiceDetail.setInvoice(invoice); //发票主档
			invoiceDetail.setTaxRate(sl);//税率		
			invoiceDetail.setUnit(dw);
			invoiceDetail.setGoodsNum(spsl);//数量
			invoiceDetail.setPrice(hsdj);//含税单价
			invoiceDetail.setNoTaxPrice(bhsdj);//不含税单价
			invoiceDetail.setGoodsSum(hsje);//含税金额
			invoiceDetail.setNoTaxSum(bhsje);//不含税金额		
			invoiceDetail.setCode(code_detail);//发票明细编码
			invoiceDetail.setProduct(product); //商品
			invoiceDetail.setBatchCode(ph);//批号
			
			if(invoice.getIsRed().equals(0)){
				DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.findByCode(stdmxbh);
				invoiceDetail.setDeliveryOrReturnsDetailCode(deliveryOrderDetail.getCode());
				invoiceDetail.setDeliveryOrReturnsCode(deliveryOrderDetail.getDeliveryOrder().getCode());
				invoiceDetail.setProductCode(deliveryOrderDetail.getProductCode());
				invoiceDetail.setProductName(deliveryOrderDetail.getProductName());
				invoiceDetail.setProducerName(deliveryOrderDetail.getProducerName());
				invoiceDetail.setDosageFormName(deliveryOrderDetail.getDosageFormName());
				invoiceDetail.setModel(deliveryOrderDetail.getModel());
				invoiceDetail.setPackDesc(deliveryOrderDetail.getPackDesc());
				invoiceDetail.setUnit(deliveryOrderDetail.getUnit());
				//处理开票标志
				deliveryOrderDetail.setIsInvoice(1);
				deliveryOrderDetails.add(deliveryOrderDetail);
				//deliveryOrderDetailDao.update(deliveryOrderDetail);
				
				DeliveryOrder deliveryOrder = deliveryOrderDetail.getDeliveryOrder();
				deliveryOrder.setIsInvoiced(1);
				deliveryOrders.add(deliveryOrder);
				//deliveryOrderDao.update(deliveryOrder);
			}else{
				ReturnsOrderDetail returnsOrderDetail = returnsOrderDetailDao.findByCode(stdmxbh);
				invoiceDetail.setDeliveryOrReturnsDetailCode(returnsOrderDetail.getCode());
				invoiceDetail.setDeliveryOrReturnsCode(returnsOrderDetail.getReturnsOrder().getCode());
				invoiceDetail.setProductCode(returnsOrderDetail.getProductCode());
				invoiceDetail.setProductName(returnsOrderDetail.getProductName());
				invoiceDetail.setProducerName(returnsOrderDetail.getProducerName());
				invoiceDetail.setDosageFormName(returnsOrderDetail.getDosageFormName());
				invoiceDetail.setModel(returnsOrderDetail.getModel());
				invoiceDetail.setPackDesc(returnsOrderDetail.getPackDesc());
				invoiceDetail.setUnit(returnsOrderDetail.getUnit());

				//处理开票标志
				ReturnsOrder order = returnsOrderDetail.getReturnsOrder();
				order.setIsInvoiced(1);
				returnsOrders.add(order);
				//returnsOrderDao.update(order);
			}
			deliveryOrderDetailDao.updateBatch(deliveryOrderDetails);
			deliveryOrderDao.updateBatch(deliveryOrders);
			returnsOrderDao.updateBatch(returnsOrders);
			
			invoice.getInvoiceDetails().add(invoiceDetail);
			goodsSum = goodsSum.add(invoiceDetail.getGoodsSum());
			num = num.add(invoiceDetail.getGoodsNum());
			
			JSONObject res_arr_jo = new JSONObject();
			res_arr_jo.put("sxh", sxh);
			res_arr_jo.put("fpmxbh", code_detail);
			res_arr.add(res_arr_jo);
		}
		invoice.setNum(num);
		invoice.setSum(goodsSum);
		
		this.saveInvoice(projectCode, invoice);
		return res_arr;
	}
	
	@Override
	public List<Invoice> listByDate(String projectCode, String hospitalCode, String startDate, String endDate) {
		return invoiceDao.listByDate(hospitalCode, startDate, endDate);
	}

	@Override
	public Invoice getByInternalCode(String projectCode, String hospitalCode, String code) {
		return invoiceDao.getByInternalCode(hospitalCode, code);
	}
}
