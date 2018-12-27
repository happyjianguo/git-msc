package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.common.cache.lock.IBaseLock;
import com.shyl.common.cache.lock.LockCollection;
import com.shyl.msc.b2b.order.entity.*;
import com.shyl.msc.b2b.order.service.*;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.webService.IDeliveryOrderWebService;
import com.shyl.sys.dto.Message;

/**
 * 配送单实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="deliveryOrderWebService",portName="deliveryOrderPort", targetNamespace="http://webservice.msc.shyl.com/")
public class DeliveryOrderWebService extends BaseWebService implements IDeliveryOrderWebService {

	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private ISnService snService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	@Resource
	private LockCollection lockCollection;
	/**
	 * GPO上传配送单
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.deliveryOrder_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String gpobm = converData.getString("gpobm");//gpobm编码
			String yqpsdbh = converData.getString("yqpsdbh");//内部编码
			String psdtxm = converData.getString("psdtxm");//配送单条形码
			IBaseLock baseLock = lockCollection.getLock(DeliveryOrder.class, gpobm+"-"+yqpsdbh+"-"+psdtxm);
			baseLock.lock();
			try{
				message = checkData(converData);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) message.getData();
				Hospital hospital = (Hospital) map.get("hospital");
				Warehouse warehouse = (Warehouse) map.get("warehouse");
				Company company = (Company) map.get("company");
				Company gpo = (Company) map.get("gpo");
				Company vendor = (Company) map.get("vendor");
				Company sender = (Company) map.get("sender");
				String ptdm = converData.getString("ptdm");	//平台代码

				//第三步验证签名
				message = checkSign(company.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第四步新增报文信息
				message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.deliveryOrder_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}

				Long datagramId = Long.valueOf(message.getData().toString());
				//第五步执行主逻辑
				message = sendMethod(converData, gpo, vendor, sender, hospital, warehouse, datagramId);
			}finally {
				baseLock.unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	/**
	 * 医院下载配送单
	 */
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.deliveryOrder_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData2(converData,1);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Hospital hospital = (Hospital) map.get("hospital");
			//第三步验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, hospital);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	/**
	 * 医院下载配送单清单
	 * @param sign
	 * @param dataType
	 * @param data
	 * @return
	 */
	@Override
	@WebMethod(action="list")
	@WebResult(name="getResult")
	public String list(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try{
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.deliveryOrder_getList, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData2(converData,2);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Hospital hospital = (Hospital) map.get("hospital");
			//第三步验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethodList(converData, hospital);
		}catch (Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	/**
	 *
	 * @param jObject
	 * @return
	 */
	private Message getMethod(JSONObject jObject, Hospital hospital) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yybm = jObject.getString("yybm");
			String psdtxm =  jObject.getString("psdtxm");
			DeliveryOrder d = deliveryOrderService.listByHospitalAndBarcode(ptdm, hospital.getCode(), psdtxm);
			
			JSONObject dObject = new JSONObject();
			dObject.put("yybm", yybm);					//医院编码
			dObject.put("psdbm", d.getWarehouseCode());	//配送点编码
			dObject.put("gysbm", d.getVendorCode());		//供应商编码
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanService.findByCode(ptdm, d.getPurchaseOrderPlanCode());
			dObject.put("cgjhbh", purchaseOrderPlan==null?"":purchaseOrderPlan.getInternalCode());//采购计划编号
			dObject.put("ddbh", d.getPurchaseOrderCode()==null?"":d.getPurchaseOrderCode());//订单编号
			dObject.put("psdbh", d.getCode());			//配送单编号
			dObject.put("txm",d.getBarcode());
			dObject.put("yqpsdbh", d.getInternalCode());	//药企配送单编号
			dObject.put("pssbm", d.getSenderCode());
			dObject.put("pssmc", d.getSenderName());
			dObject.put("pssj", DateUtil.dateToStr(d.getOrderDate()));//配送时间

			List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.listByDeliveryOrder(ptdm, d.getId());
			dObject.put("jls", deliveryOrderDetails.size());
			JSONArray detailArray = new JSONArray();
			int i=1;
			for(DeliveryOrderDetail dd:deliveryOrderDetails){
				JSONObject ddObject = new JSONObject();
				ddObject.put("sxh", i);
				ddObject.put("psdmxbh", dd.getCode());		//配送单明细编号
				ddObject.put("ypbm", dd.getProductCode());	//药品编码
				ddObject.put("scph", dd.getBatchCode()==null?"":dd.getBatchCode());	//生产批号
				ddObject.put("scrq", dd.getBatchDate()==null?"":dd.getBatchDate());	//生产日期
				ddObject.put("yxrq", dd.getExpiryDate()==null?"":dd.getExpiryDate());	//有效日期
				ddObject.put("txm", d.getBarcode()==null?"":d.getBarcode());		//条形码
				ddObject.put("dj", dd.getPrice());	
				ddObject.put("pssl", dd.getGoodsNum());		//配送数量
				ddObject.put("zljl", dd.getQualityRecord());				//质量记录
				ddObject.put("jybglj", dd.getInspectionReportUrl());				//检验报告链接
				ddObject.put("ddmxbh", dd.getPurchaseOrderDetailCode()==null?"":dd.getPurchaseOrderDetailCode());//订单明细编号
				ddObject.put("cgjhmxbh", dd.getPurchaseOrderPlanDetailCode()==null?"":dd.getPurchaseOrderPlanDetailCode());//采购计划明细编号
				detailArray.add(ddObject);
				i++;
			}
			dObject.put("mx", detailArray);

			if(d.getIsRead()==0){
				d.setIsRead(1);
				deliveryOrderService.updateWithInclude(ptdm, d, "isRead");
			}
			message.setSuccess(true);
			message.setData(dObject);
			message.setMsg("返回成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器端程序出错！");
			return message;
		}
		return message;
	}

	private Message getMethodList(JSONObject jObject, Hospital hospital){
		Message message = new Message();
		message.setSuccess(false);
		try{
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yybm = jObject.getString("yybm");
			String cxkssj = jObject.getString("cxkssj");
			String cxjssj = jObject.getString("cxjssj");
			List<DeliveryOrder> deliveryOrders = deliveryOrderService.listByDate(ptdm,hospital.getCode(),cxkssj,cxjssj);
			JSONObject dObject = new JSONObject();

			JSONArray arr = new JSONArray();
			int i= 0;
			for(DeliveryOrder deliveryOrder : deliveryOrders){
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh", i + ""); // 顺序号
				js.put("yybm",deliveryOrder.getHospitalCode() == null ?"" : deliveryOrder.getHospitalCode());
				js.put("psdbm",deliveryOrder.getWarehouseCode() == null ? "" : deliveryOrder.getWarehouseCode());
				js.put("gysbm",deliveryOrder.getVendorCode() == null?"":deliveryOrder.getVendorCode());
				js.put("cgjhbh",deliveryOrder.getPurchasePlanCode() == null?"":deliveryOrder.getPurchasePlanCode());
				js.put("ddbh",deliveryOrder.getPurchaseOrderCode() == null?"":deliveryOrder.getPurchaseOrderCode());
				js.put("psdbh",deliveryOrder.getCode() == null?"":deliveryOrder.getCode());
				js.put("txm",deliveryOrder.getBarcode());
				js.put("yqpsdbh",deliveryOrder.getInternalCode()==null?"":deliveryOrder.getInternalCode());
				js.put("pssbm",deliveryOrder.getSenderCode()==null?"":deliveryOrder.getSenderCode());
				js.put("pssmc",deliveryOrder.getSenderName()==null?"":deliveryOrder.getSenderName());
				js.put("pssj",DateUtil.dateToStr(deliveryOrder.getOrderDate()));
				List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.listByDeliveryOrderId(ptdm,deliveryOrder.getId());

				JSONArray arrDetail = new JSONArray();
				int j=0;
				for(DeliveryOrderDetail deliveryOrderDetail : deliveryOrderDetails){
					j++;
					JSONObject jsDetail = new JSONObject();
					jsDetail.put("sxh", j + "");
					jsDetail.put("psdmxbh",deliveryOrderDetail.getCode());
					jsDetail.put("ypbm",deliveryOrderDetail.getProductCode());
					jsDetail.put("scph",deliveryOrderDetail.getBatchCode());
					jsDetail.put("scrq",deliveryOrderDetail.getBatchDate());
					jsDetail.put("yxrq",deliveryOrderDetail.getExpiryDate());
					jsDetail.put("txm",deliveryOrderDetail.getBarcode() == null ? "" : deliveryOrderDetail.getBarcode());
					jsDetail.put("dj",deliveryOrderDetail.getPrice());
					jsDetail.put("pssl",deliveryOrderDetail.getGoodsNum());
					jsDetail.put("zljl",deliveryOrderDetail.getQualityRecord());
					jsDetail.put("jybglj",deliveryOrderDetail.getInspectionReportUrl());
					jsDetail.put("ddmxbh",deliveryOrderDetail.getPurchaseOrderDetailCode() == null ? "" : deliveryOrderDetail.getPurchaseOrderDetailCode());
					jsDetail.put("cgjhmxbh",deliveryOrderDetail.getPurchaseOrderPlanDetailCode() == null ? "" : deliveryOrderDetail.getPurchaseOrderPlanDetailCode());
					arrDetail.add(jsDetail);
				}
				js.put("jls",deliveryOrderDetails.size());
				js.put("mx",arrDetail);
				arr.add(js);
			}

			dObject.put("bcgxsj",DateUtil.getToday19());
			dObject.put("jls",deliveryOrders.size());
			dObject.put("mx",arr);
			message.setSuccess(true);
			message.setData(dObject);
			message.setMsg("返回成功");
		}catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器端程序出错！");
			return message;
		}
		return message;
	}

	private Message checkData2(JSONObject jObject,Integer type) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = null;
		try {
			String a = CommonProperties.IS_TO_SZ_PROJECT;
			String ptdm = jObject.getString("ptdm");	//平台代码
			if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")){
				if(StringUtils.isEmpty(ptdm)){
					message.setMsg("平台代码不能为空");
					return message;
				}
				List<String> projectList = new ArrayList<>(Arrays.asList(CommonProperties.DB_PROJECTCODE.split(",")));
				if(!projectList.contains(ptdm)){
					message.setMsg("平台代码有误");
					return message;
				}
			}
			String yybm = jObject.getString("yybm");//医院编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("没有找到相应的医院");
				return message;
			}
			if(type.equals(1)){
				String psdtxm = jObject.getString("psdtxm");//配送单条形码
				if(StringUtils.isEmpty(psdtxm)){
					message.setMsg("配送单条形码不能为空");
					return message;
				}
				DeliveryOrder d = deliveryOrderService.listByHospitalAndBarcode(ptdm, hospital.getCode(), psdtxm);
				if(d == null){
					message.setMsg("配送单条形码不存在");
					return message;
				}
			}else if(type.equals(2)){
				String cxkssj = jObject.getString("cxkssj");// 上次更新时间
				if (StringUtils.isEmpty(cxkssj)) {
					message.setMsg("查询开始时间不能为空");
					return message;
				}
				if (!DateUtil.checkDateFMT(cxkssj, 2)) {
					message.setMsg("查询开始时间格式错误");
					return message;
				}
				String cxjssj = jObject.getString("cxjssj");// 上次更新时间
				if (StringUtils.isEmpty(cxjssj)) {
					message.setMsg("查询结束时间不能为空");
					return message;
				}
				if (!DateUtil.checkDateFMT(cxjssj, 2)) {
					message.setMsg("查询结束时间格式错误");
					return message;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}


	/**
	 * 
	 * @param jObject
	 * @return
	 */
	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Company sender, Hospital hospital, Warehouse warehouse, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			//解析数据
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yqpsdbh = jObject.getString("yqpsdbh");//药企配送单编号
			String psdtxm = jObject.getString("psdtxm");//配送单条形码
			String cjsj = jObject.getString("cjsj");//创建日期
			String sdsj = jObject.getString("sdsj");//送达时间
			String ddbh = jObject.getString("ddbh");//订单编号
			String mx = jObject.getString("mx");	//明细
			
			PurchaseOrder purchaseOrder = purchaseOrderService.findByCode(ptdm, ddbh);
			
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			
			//主档新增 d
			DeliveryOrder deliveryOrder = new DeliveryOrder();
			deliveryOrder.setCode(snService.getCode(ptdm, OrderType.delivery));
			deliveryOrder.setInternalCode(yqpsdbh);
			deliveryOrder.setHospitalCode(hospital.getCode());
			deliveryOrder.setHospitalName(hospital.getFullName());
			if(gpo != null){
				deliveryOrder.setGpoCode(gpo.getCode());
				deliveryOrder.setGpoName(gpo.getFullName());
			}
			deliveryOrder.setVendorCode(vendor.getCode());
			deliveryOrder.setVendorName(vendor.getFullName());
			deliveryOrder.setSenderCode(sender.getCode());
			deliveryOrder.setSenderName(sender.getFullName());
			deliveryOrder.setWarehouseCode(warehouse.getCode());
			deliveryOrder.setWarehouseName(warehouse.getName());
			deliveryOrder.setReceiveDate(DateUtil.strToDate(cjsj));
			deliveryOrder.setOrderDate(DateUtil.strToDate(sdsj));
			deliveryOrder.setPurchaseOrderCode(ddbh);
			if(purchaseOrder != null) {
				deliveryOrder.setPurchaseOrderPlanCode(purchaseOrder.getPurchaseOrderPlanCode());
				deliveryOrder.setPurchasePlanCode(purchaseOrder.getPurchasePlanCode());
			}		
			deliveryOrder.setBarcode(psdtxm);
			deliveryOrder.setStatus(DeliveryOrder.Status.unreceive);
			deliveryOrder.setIsAuto(1);
			deliveryOrder.setIsPass(0);
			deliveryOrder.setDatagramId(datagramId);
			deliveryOrder.setProjectCode(ptdm);
			
			//明细新增
			List<JSONObject> arr = JSON.parseArray(mx, JSONObject.class);
			BigDecimal deliveryNum = new BigDecimal(0); 
			BigDecimal deliverySum = new BigDecimal(0); 
			int i=0;
			JSONArray res_arr = new JSONArray();
			for(JSONObject jo:arr){
				i++;
				int sxh = jo.getIntValue("sxh");		//顺序号
				String yqpsdmxbh = jo.getString("yqpsdmxbh");//药企配送单明细编号
				String ypbm = jo.getString("ypbm");		//药品编码
				String scph = jo.getString("scph");		//生产批号
				String scrq = jo.getString("scrq");		//生产日期
				String yxrq = jo.getString("yxrq");		//有效日期
				String txm = jo.getString("txm");		//条形码
				BigDecimal dj = jo.getBigDecimal("dj");	//单价
				BigDecimal pssl = jo.getBigDecimal("pssl");	//配送数量
				String ddmxbh = jo.getString("ddmxbh");	//订单明细编号
				String zljl = jo.getString("zljl");		//质量记录
				String jybglj = jo.getString("jybglj");	//检验报告链接

				Product product = productService.getByCode("", ypbm);
				PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailService.findByCode(ptdm, ddmxbh);

				DeliveryOrderDetail deliveryOrderDetail = new DeliveryOrderDetail();
				String detail_code = deliveryOrder.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
				deliveryOrderDetail.setIsPass(0);
				deliveryOrderDetail.setOrderDate(deliveryOrder.getOrderDate());
				deliveryOrderDetail.setCode(detail_code);//配送单明细编号
				deliveryOrderDetail.setInternalCode(yqpsdmxbh);
				deliveryOrderDetail.setDeliveryOrder(deliveryOrder);
				deliveryOrderDetail.setBatchCode(scph);
				deliveryOrderDetail.setBatchDate(scrq);
				deliveryOrderDetail.setBarcode(txm);
				deliveryOrderDetail.setExpiryDate(yxrq);
				deliveryOrderDetail.setProductCode(product.getCode());
				deliveryOrderDetail.setProductName(product.getName());
				deliveryOrderDetail.setProducerName(product.getProducerName());
				deliveryOrderDetail.setDosageFormName(product.getDosageFormName());
				deliveryOrderDetail.setModel(product.getModel());
				deliveryOrderDetail.setPackDesc(product.getPackDesc());
				deliveryOrderDetail.setUnit(product.getUnit());
				deliveryOrderDetail.setPrice(dj);
				deliveryOrderDetail.setGoodsNum(pssl);
				deliveryOrderDetail.setGoodsSum(deliveryOrderDetail.getPrice().multiply(pssl));	
				deliveryOrderDetail.setPurchaseOrderDetailCode(ddmxbh);
				deliveryOrderDetail.setProductCode(ypbm);
				deliveryOrderDetail.setQualityRecord(zljl);
				deliveryOrderDetail.setInspectionReportUrl(jybglj);
				if(purchaseOrderDetail != null) {
					deliveryOrderDetail.setContractDetailCode(purchaseOrderDetail.getContractDetailCode());
				}				
				deliveryOrder.getDeliveryOrderDetails().add(deliveryOrderDetail);
				
				deliveryNum = deliveryNum.add(deliveryOrderDetail.getGoodsNum());
				deliverySum = deliverySum.add(deliveryOrderDetail.getGoodsSum());
				
				JSONObject res_arr_jo = new JSONObject();
				res_arr_jo.put("sxh", sxh);
				res_arr_jo.put("psdmxbh", detail_code);
				res_arr_jo.put("yqpsdmxbh", yqpsdmxbh);
				res_arr.add(res_arr_jo);
			}
			deliveryOrder.setNum(deliveryNum);
			deliveryOrder.setSum(deliverySum);
			deliveryOrderService.saveDeliveryOrder(ptdm, deliveryOrder);

			JSONObject data_rtn = new JSONObject();//返回
			data_rtn.put("psdbh", deliveryOrder.getCode());
			data_rtn.put("yqpsdbh", yqpsdbh);
			data_rtn.put("mx", res_arr);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData(data_rtn);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}
	/**
	 * 检查data数据是否合法
	 * @param jObject
	 * @return
	 */
	private Message checkData(JSONObject jObject){
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Warehouse warehouse = new Warehouse();
		Boolean isGPO = true;
		Company company = new Company();
		Company gpo = new Company();
		Company vendor = new Company();
		Company sender = new Company();
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")){
				if(StringUtils.isEmpty(ptdm)){
					message.setMsg("平台代码不能为空");
					return message;
				}
				List<String> projectList = new ArrayList<>(Arrays.asList(CommonProperties.DB_PROJECTCODE.split(",")));
				if(!projectList.contains(ptdm)){
					message.setMsg("平台代码有误");
					return message;
				}
			}
			String yybm = jObject.getString("yybm");		//医院编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}
			String psdbm = jObject.getString("psdbm");		//配送地点编码
			if(StringUtils.isEmpty(psdbm)){
				message.setMsg("配送点编码不能为空");
				return message;
			}
			warehouse = warehouseService.queryByCodeAndPid(ptdm, psdbm, hospital.getId());
			if(warehouse == null){
				message.setMsg("配送点编码有误");
				return message;
			}
			String jglx = jObject.getString("jglx");	//机构类型
			if(StringUtils.isEmpty(jglx)){
				message.setMsg("机构类型不能为空");
				return message;
			}
			try {
				int jglx_i = jObject.getIntValue("jglx");
				if(jglx_i != 1 && jglx_i != 2){
					message.setMsg("机构类型应为1或2");
					return message;
				}
				if(jglx_i == 2){
					isGPO = false;
				} else {
					isGPO = true;
				}
			} catch (Exception e) {
				message.setMsg("机构类型格式有误");
				return message;
			}
			String jgbm = jObject.getString("jgbm");	//机构编码
			if(StringUtils.isEmpty(jgbm)){
				message.setMsg("机构编码不能为空");
				return message;
			}
			if(isGPO){
				company = companyService.findByCode(ptdm, jgbm,"isGPO=1");
			}else{
				company = companyService.findByCode(ptdm, jgbm,"isVendor=1");
			}
			if(company == null){
				message.setMsg("机构编码有误");
				return message;
			}
			String gpobm = jObject.getString("gpobm");//GPO编码
			if(!gpobm.equals("")){
				gpo = companyService.findByCode(ptdm, gpobm, "isGPO=1");//GPO
				if(gpo == null){
					message.setMsg("GPO编码有误");
					return message;
				}
			}
			String gysbm = jObject.getString("gysbm");//供应商编码
			if(StringUtils.isEmpty(gysbm)){
				message.setMsg("供应商编码不能为空");
				return message;
			}
			vendor = companyService.findByCode(ptdm, gysbm, "isVendor=1");//供应商
			if(vendor == null){
				message.setMsg("供应商编码有误");
				return message;
			}
			String yqpsdbh = jObject.getString("yqpsdbh");	//药企配送单编号
			if(StringUtils.isEmpty(yqpsdbh)){
				message.setMsg("药企配送单编号不能为空");
				return message;
			}
			DeliveryOrder deliveryOrder = null;
			if(isGPO){
				deliveryOrder = deliveryOrderService.getByInternalCode(ptdm, gpo.getCode(), yqpsdbh, isGPO);
			}else{
				deliveryOrder = deliveryOrderService.getByInternalCode(ptdm, vendor.getCode(), yqpsdbh, isGPO);
			}
			if(deliveryOrder != null){
				message.setMsgcode(Message.MsgCode.err01.toString());
				message.setMsg("药企配送单编号已存在");
				List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.listByDeliveryOrder(ptdm, deliveryOrder.getId());
				JSONArray res_arr = new JSONArray();
				int k = 0;
				for(DeliveryOrderDetail detail:deliveryOrderDetails){
					k++;
					JSONObject jo = new JSONObject();
					jo.put("sxh", k);
					jo.put("psdmxbh", detail.getCode());
					jo.put("yqpsdmxbh", detail.getInternalCode());
					res_arr.add(jo);
				}
				JSONObject r_jo = new JSONObject();
				r_jo.put("psdbh", deliveryOrder.getCode());
				r_jo.put("yqpsdbh", deliveryOrder.getInternalCode());
				r_jo.put("mx", res_arr);
				message.setData(r_jo);
				return message;
			}
			String psdtxm = jObject.getString("psdtxm");	//配送单条形码
			if(StringUtils.isEmpty(psdtxm)){
				message.setMsg("配送单条形码不能为空");
				return message;
			}
			deliveryOrder = deliveryOrderService.getByBarcode(ptdm, psdtxm);
			if(deliveryOrder != null){
				message.setMsg("配送单条形码已存在");
				return message;
			}
			PurchaseOrder purchaseOrder = new PurchaseOrder();
			String ddbh = null;
			AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "DELIVERY");
			if(!wsCheck.getField3().equals("0")){
				ddbh = jObject.getString("ddbh");		//订单编号
				if(StringUtils.isEmpty(ddbh)){
					message.setMsg("订单编码不能为空");
					return message;
				}
				purchaseOrder = purchaseOrderService.findByCode(ptdm, ddbh);
				if(purchaseOrder == null){
					message.setMsg("订单编号有误");
					return message;
				}
			
				if(!purchaseOrder.getHospitalCode().equals(hospital.getCode())){
					message.setMsg("医院编码和订单中医院编码不匹配");
					return message;
				}
				if(isGPO){
					if(!purchaseOrder.getGpoCode().equals(gpo.getCode())){
						message.setMsg("GPO编码和订单中GPO编码不匹配");
						return message;
					}
				}else{
					if(!purchaseOrder.getVendorCode().equals(vendor.getCode())){
						message.setMsg("供应商编码和订单中供应商编码不匹配");
						return message;
					}
				}
			}
			
			String pssbm = jObject.getString("pssbm");		//配送商编码
			if(StringUtils.isEmpty(pssbm)){
				message.setMsg("配送商编码不能为空");
				return message;
			}
			sender = companyService.findByCode(ptdm, pssbm, "isSender=1");
			if(sender == null){
				message.setMsg("配送商编码有误");
				return message;
			}
			String cjsj = jObject.getString("cjsj");		//创建时间
			if(StringUtils.isEmpty(cjsj)){
				message.setMsg("创建时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(cjsj, 2)){
				message.setMsg("创建时间格式错误");
				return message;
			}
			String sdsj = jObject.getString("sdsj");		//送达时间
			if(StringUtils.isEmpty(sdsj)){
				message.setMsg("送达时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(sdsj, 2)){
				message.setMsg("送达时间格式错误");
				return message;
			}
			String jls = jObject.getString("jls");			//记录数
			if(StringUtils.isEmpty(jls)){
				message.setMsg("记录数不能为空");
				return message;
			}
			try {
				jObject.getIntValue("jls");//记录数
			} catch (Exception e) {
				message.setMsg("记录数格式有误");
				return message;
			}
			String mx = jObject.getString("mx");			//明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("明细不能为空");
				return message;
			}

			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = new ArrayList<>();
			try {
				list = JSON.parseArray(mx, JSONObject.class);
				if (list != null && list.size() == 0) {
					message.setMsg("明细数据不能为空");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("明细数据格式有误");
				return message;
			}
			
			BigDecimal totalDeliveryNum = purchaseOrder.getDeliveryNum() == null ?new BigDecimal(0): purchaseOrder.getDeliveryNum();
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");		//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jObject.getIntValue("sxh");	//顺序号
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ypbm = jo.getString("ypbm");		//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				if(!wsCheck.getField3().equals("0")){
					String ddmxbh = jo.getString("ddmxbh");	//订单明细编号
					if(StringUtils.isEmpty(ddmxbh)){
						message.setMsg("第("+sxh+")笔：订单明细编号不能为空");
						return message;
					}
					PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailService.findByCode(ptdm, ddmxbh);
					if(purchaseOrderDetail == null){
						message.setMsg("第("+sxh+")笔：订单明细编号有误");
						return message;
					}
					if(!purchaseOrderDetail.getPurchaseOrder().getCode().equals(ddbh)){
						message.setMsg("第("+sxh+")笔：订单编号 和 订单明细编号不匹配");
						return message;
					}
					if(!purchaseOrderDetail.getProductCode().equals(ypbm)){
						message.setMsg("第("+sxh+")笔：药品编码和订单明细药品编码 不匹配");
						return message;
					}
				}
				String scph = jo.getString("scph");		//生产批号
				if(StringUtils.isEmpty(scph)){
					message.setMsg("第("+sxh+")笔：生产批号不能为空");
					return message;
				}
				String scrq = jo.getString("scrq");		//生产日期
				if(StringUtils.isEmpty(scrq)){
					message.setMsg("第("+sxh+")笔：生产日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scrq, 1)){
					message.setMsg("第("+sxh+")笔：生产日期格式有误");
					return message;
				}
				String yxrq = jo.getString("yxrq");		//有效日期
				if(StringUtils.isEmpty(yxrq)){
					message.setMsg("第("+sxh+")笔：有效日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxrq, 1)){
					message.setMsg("第("+sxh+")笔：有效日期格式有误");
					return message;
				}
				String dj = jo.getString("dj");		//单价
				if(StringUtils.isEmpty(dj)){
					message.setMsg("第("+sxh+")笔：单价不能为空");
					return message;
				}
				try {
					jo.getBigDecimal("dj");
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：单价有误");
					return message;
				}
				String pssl = jo.getString("pssl");		//配送数量
				if(StringUtils.isEmpty(pssl)){
					message.setMsg("第("+sxh+")笔：配送数量不能为空");
					return message;
				}
				try {
					BigDecimal pssl_int = jo.getBigDecimal("pssl");
					if(pssl_int.compareTo(new BigDecimal(0)) <= 0){
						message.setMsg("第("+sxh+")笔：配送数量不能小于零");
						return message;
					}
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：配送数量有误");
					return message;
				}
				totalDeliveryNum = totalDeliveryNum.add(jo.getBigDecimal("pssl"));
			}
			if(!wsCheck.getField3().equals("0")){
				if(totalDeliveryNum.compareTo(purchaseOrder.getNum()) > 0){
					message.setMsg("配送数量总和不能大于订单采购数量");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		map.put("warehouse", warehouse);
		map.put("company", company);
		map.put("gpo", gpo);
		map.put("vendor", vendor);
		map.put("sender", sender);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

}
