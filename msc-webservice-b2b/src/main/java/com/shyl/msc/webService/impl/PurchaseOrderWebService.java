package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.common.cache.lock.IBaseLock;
import com.shyl.common.cache.lock.LockCollection;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.enmu.UrgencyLevel;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.webService.IPurchaseOrderWebService;
import com.shyl.sys.dto.Message;

/**
 * 订单实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="purchaseOrderWebService",portName="purchaseOrderPort", targetNamespace="http://webservice.msc.shyl.com/")
public class PurchaseOrderWebService extends BaseWebService implements IPurchaseOrderWebService {

	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IProductService productService;
	@Resource
	private ISnService snService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private LockCollection lockCollection;
	/**
	 * GPO上传订单
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")

	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrder_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String gpobm = converData.getString("gpobm");  //gpo编码
			String ddjhbh = converData.getString("ddjhbh");  //订单计划编号
			IBaseLock baseLock = lockCollection.getLock(PurchaseOrder.class, gpobm+"-"+ddjhbh);
			baseLock.lock();
			try{
				message = checkData(converData);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) message.getData();
				Company company = (Company) map.get("company");
				Company gpo = (Company) map.get("gpo");
				Company vendor = (Company) map.get("vendor");
				Hospital hospital = (Hospital) map.get("hospital");
				Warehouse warehouse = (Warehouse) map.get("warehouse");
				String ptdm = converData.getString("ptdm");	//平台代码

				//第三步验证签名
				message = checkSign(company.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第四步新增报文信息
				message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.purchaseOrder_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}

				//第五步执行主逻辑
				message = sendMethod(converData, gpo, vendor, hospital, warehouse, Long.valueOf(message.getData().toString()));
			}finally {
				baseLock.unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	/**
	 * 
	 * @param dataType
	 * @param data
	 * @return
	 */
	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Hospital hospital, Warehouse warehouse, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			//String yqddbh = jObject.getString("yqddbh");
			String ptdm = jObject.getString("ptdm");	//平台代码
			String ddjhbh = jObject.getString("ddjhbh");//订单计划编码
			String yqddbh = jObject.getString("yqddbh");//药企订单编号
			String yqphsj = jObject.getString("yqphsj");	//要求配货时间
			int jjcd = jObject.getIntValue("jjcd");			//紧急程度
			Integer dcpsbs = jObject.getIntValue("dcpsbs");	//多次配送标识
			
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanService.findByCode(ptdm, ddjhbh);
			
			String mx = jObject.getString("mx");
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			UrgencyLevel urgencyLevel = UrgencyLevel.values()[jjcd];

			PurchaseOrder purchaseOrder = new PurchaseOrder();
			String code = snService.getCode(ptdm, OrderType.order);
			purchaseOrder.setCode(code);
			purchaseOrder.setInternalCode(yqddbh);
			purchaseOrder.setOrderDate(new Date());
			if(gpo != null){
				purchaseOrder.setGpoCode(gpo.getCode());
				purchaseOrder.setGpoName(gpo.getFullName());
			}
			purchaseOrder.setVendorCode(vendor.getCode());
			purchaseOrder.setVendorName(vendor.getFullName());
			purchaseOrder.setHospitalCode(hospital.getCode());
			purchaseOrder.setHospitalName(hospital.getFullName());
			purchaseOrder.setWarehouseCode(warehouse.getCode());
			purchaseOrder.setWarehouseName(warehouse.getName());
			purchaseOrder.setIsPass(0);
			purchaseOrder.setPurchaseOrderPlanCode(ddjhbh);
			purchaseOrder.setPurchasePlanCode(purchaseOrderPlan.getCode());
			purchaseOrder.setRequireDate(DateUtil.strToDate(yqphsj));
			purchaseOrder.setUrgencyLevel(urgencyLevel);
			purchaseOrder.setIsManyDelivery(dcpsbs);
			purchaseOrder.setIsAuto(1);
			purchaseOrder.setStatus(Status.effect);
			purchaseOrder.setDatagramId(datagramId);
			purchaseOrder.setProjectCode(ptdm);//平台代码
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			JSONArray res_arr = purchaseOrderService.addOrder(ptdm, purchaseOrder, list);
			
			JSONObject data_rtn = new JSONObject();
			data_rtn.put("ddbh", code);
			data_rtn.put("yqddbh", yqddbh);
			data_rtn.put("mx", res_arr);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData(data_rtn);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}


	/**
	 * 检查数据
	 * @param data
	 * @param data 
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
			String ddjhbh = jObject.getString("ddjhbh");//订单计划编码
			PurchaseOrderPlan purchaseOrderPlan = new PurchaseOrderPlan();
			AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "PURCHASEORDER");
			if(!wsCheck.getField3().equals("0")){
				if(StringUtils.isEmpty(ddjhbh)){
					message.setMsg("订单计划编码不能为空");
					return message;
				}
				purchaseOrderPlan = purchaseOrderPlanService.findByCode(ptdm, ddjhbh);
				if(purchaseOrderPlan == null){
					message.setMsg("订单计划编码有误");
					return message;
				}
				if(purchaseOrderPlan.getStatus().equals(PurchaseOrderPlan.Status.uneffect)){
					message.setMsg("订单计划未生效,请先反馈订单计划");
					return message;
				}
				if(purchaseOrderPlan.getStatus().equals(PurchaseOrderPlan.Status.cancel)){
					message.setMsg("订单计划已经取消,不能上传订单");
					return message;
				}
			}
			String yqddbh = jObject.getString("yqddbh");//药企订单编码
			if(StringUtils.isEmpty(yqddbh)){
				message.setMsg("药企订单编码不能为空");
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
			
			PurchaseOrder purchaseOrder = purchaseOrderService.getByPlanCode(ptdm, ddjhbh);
			if(purchaseOrder != null){
				message.setMsgcode(Message.MsgCode.err01.toString());
				message.setMsg("该订单计划已经下单");
				List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailService.listByOrderId(ptdm, purchaseOrder.getId());
				JSONArray res_arr = new JSONArray();
				int k = 0;
				for(PurchaseOrderDetail detail:purchaseOrderDetails){
					k++;
					JSONObject jo = new JSONObject();
					jo.put("sxh", k);
					jo.put("ddmxbh", detail.getCode());
					jo.put("yqddmxbh", detail.getInternalCode());
					res_arr.add(jo);
				}
				JSONObject r_jo = new JSONObject();
				r_jo.put("ddbh", purchaseOrder.getCode());
				r_jo.put("mx", res_arr);
				message.setData(r_jo);
				return message;
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
			String yybm = jObject.getString("yybm");//医院编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}
			if(!wsCheck.getField3().equals("0")){
				if(!purchaseOrderPlan.getHospitalCode().equals(yybm)){
					message.setMsg("医院编码和订单计划中医院编码不匹配");
					return message;
				}
			}
			String psdbm = jObject.getString("psdbm");//配送点编码
			if(StringUtils.isEmpty(psdbm)){
				message.setMsg("配送点编码不能为空");
				return message;
			}
			warehouse = warehouseService.queryByCodeAndPid(ptdm, psdbm, hospital.getId());
			if(warehouse == null){
				message.setMsg("配送点编码有误");
				return message;
			}
			String yqphsj = jObject.getString("yqphsj");//要求配货时间
			if(!DateUtil.checkDateFMT(yqphsj, 2)){
				message.setMsg("要求配货时间格式有误,yyyy-MM-dd hh:mm:ss");
				return message;
			}
			String jjcdString = jObject.getString("jjcd"); //紧急程度
			if(!StringUtils.isEmpty(jjcdString)){
				try {
					Integer jjcd = jObject.getIntValue("jjcd");//紧急程度
					UrgencyLevel urgencyLevel = UrgencyLevel.values()[jjcd];
					if(urgencyLevel == null){
						message.setMsg("没有找到相应的紧急程度");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("紧急程度格式有误");
					return message;
				}
			}
			String dcpsbsString = jObject.getString("dcpsbs");//多次配送标识
			if(StringUtils.isEmpty(dcpsbsString)){
				message.setMsg("多次配送标识不能为空");
				return message;
			}
			try {
				int dcpsbs = jObject.getIntValue("dcpsbs");//多次配送标识
				if(dcpsbs != 0 && dcpsbs != 1){
					message.setMsg("多次配送标识应为0或1");
					return message;
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("多次配送标识格式有误");
				return message;
			}
			String jls = jObject.getString("jls");	//记录数
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
			String mx = jObject.getString("mx");//明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("订单明细不能为空");
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
				e.printStackTrace();
				message.setMsg("订单明细数据格式有误");
				return message;
			}
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");		//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ddjhmxbh = jo.getString("ddjhmxbh");	//订单计划明细编号
				if(StringUtils.isEmpty(ddjhmxbh)){
					message.setMsg("第("+sxh+")笔：订单计划明细编号不能为空");
					return message;
				}
				
				String ypbm = jo.getString("ypbm");			//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				
				PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailService.findByCode(ptdm, ddjhmxbh);
				if(!wsCheck.getField3().equals("0")){
					if(purchaseOrderPlanDetail == null){
						message.setMsg("第("+sxh+")笔：订单计划明细编号有误");
						return message;
					}
					
					if(!purchaseOrderPlanDetail.getProductCode().equals(ypbm)){
						message.setMsg("第("+sxh+")笔：药品编码和订单计划明细药品编码不匹配");
						return message;
					}
				}
				String cgsl = jo.getString("cgsl");			//采购数量
				if(StringUtils.isEmpty(cgsl)){
					message.setMsg("第("+sxh+")笔：采购数量不能为空");
					return message;
				}
				BigDecimal cgsl_int = new BigDecimal(0);
				try {
					cgsl_int = jo.getBigDecimal("cgsl");
					if(cgsl_int.compareTo(new BigDecimal(0)) <= 0){
						message.setMsg("第("+sxh+")笔：采购数量不能小于");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：采购数量格式有误");
					return message;
				}
				if(!wsCheck.getField3().equals("0")){
					if(cgsl_int.compareTo(purchaseOrderPlanDetail.getGoodsNum()) > 0){
						message.setMsg("第("+sxh+")笔：采购数量不能大于采购计划数量");
						return message;
					}
				}
				String cgdj = jo.getString("cgdj");			//采购单价
				if(StringUtils.isEmpty(cgdj)){
					message.setMsg("第("+sxh+")笔：采购单价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("cgdj");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：采购单价格式有误");
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
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

}
