package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.common.cache.lock.IBaseLock;
import com.shyl.common.cache.lock.LockCollection;
import com.shyl.common.framework.exception.MyException;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.order.service.IPurchasePlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
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
import com.shyl.msc.webService.IPurchaseOrderPlanWebService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 订单计划实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="purchaseOrderPlanWebService",portName="purchaseOrderPlanPort", targetNamespace="http://webservice.msc.shyl.com/")
public class PurchaseOrderPlanWebService extends BaseWebService implements IPurchaseOrderPlanWebService{
	@Resource
	private IProductService productService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IPurchasePlanService purchasePlanService;
	@Resource
	private IPurchasePlanDetailService purchasePlanDetailService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private ISnService snService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private LockCollection lockCollection;
	
	/**
	 * 医院上传订单计划
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrderPlan_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String hospitalCode = converData.getString("yybm");//医院编码
			String cgjhdbh = converData.getString("cgjhdbh");//采购计划单编号
			IBaseLock baseLock = lockCollection.getLock(PurchaseOrderPlan.class, hospitalCode+"-"+cgjhdbh);
	    	baseLock.lock();
			try {
				message = checkData(converData);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) message.getData();
				Hospital hospital = (Hospital) map.get("hospital");
				Warehouse warehouse = (Warehouse) map.get("warehouse");
				User user = (User) map.get("user");
				String field3 = (String)map.get("filed3");
				String ptdm = converData.getString("ptdm");	//平台代码
				//第三步验证签名
				message = checkSign(hospital.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第四步新增报文信息
				message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.purchaseOrderPlan_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第五步执行主逻辑
				message = sendMethod(converData, Long.valueOf(message.getData().toString()), hospital, warehouse,field3, user);
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
	 *  采购计划细项取消 
	 */
	@Override
	@WebMethod(action="cancel")
	@WebResult(name="cancelResult")
	public String cancel(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrderPlan_cancel, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = cancelCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Hospital hospital = (Hospital) map.get("hospital");
			User user = (User) map.get("user");
			String ptdm = converData.getString("ptdm");	//平台代码
			//第三步验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.purchaseOrderPlan_cancel);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第五步执行主逻辑
			message = cancelMethod(converData, hospital, user);

		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message cancelMethod(JSONObject jObject, Hospital hospital, User user) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			purchaseOrderPlanDetailService.cancelDetail(ptdm, list);
			message.setSuccess(true);
			message.setMsg("成功返回");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}
	private Message cancelCheckData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		User user = new User();
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
			String hospitalCode = jObject.getString("yybm");//医院编码
			if(StringUtils.isEmpty(hospitalCode)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, hospitalCode);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}
			String yhzh = jObject.getString("yhzh");//用户账号
			String yhzs = jObject.getString("yhzs");//用户证书
			String yhqm = jObject.getString("yhqm");//用户签名
			
			message = checkCA(ptdm, yhzh, hospital.getIocode(), yhzs, yhqm);
			if(!message.getSuccess()){
				return message;
			}
			user = (User) message.getData();
			message.setSuccess(false);
			message.setData("");
			String jls = jObject.getString("jls");//记录数
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
				message.setMsg("订单计划明细数据格式有误");
				return message;
			}
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");	//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("订单明细顺序号格式有误");
					return message;
				}
				String ddjhmxbh = jo.getString("ddjhmxbh");		//订单计划明细 编号
				if(StringUtils.isEmpty(ddjhmxbh)){
					message.setMsg("第("+sxh+")笔：订单计划明细 编号不能为空");
					return message;
				}
				PurchasePlanDetail purchasePlanDetail = purchasePlanDetailService.findByCode(ptdm, ddjhmxbh); 
				if(purchasePlanDetail == null){
					message.setMsg("第("+sxh+")笔：订单计划明细 编号有误");
					return message;
				}
				PurchaseOrderPlanDetail orderPlanDetail = purchaseOrderPlanDetailService.findByPlanCode(ptdm, ddjhmxbh);
				if(orderPlanDetail == null){
					message.setMsg("第("+sxh+")笔：订单计划明细 编号有误2");
					return message;
				}
				if(!PurchaseOrderPlan.Status.uneffect.equals(orderPlanDetail.getPurchaseOrderPlan().getStatus())){
					message.setMsg("第("+sxh+")笔：仅允许取消未生效的订单计划");
					return message;
					
				}
				PurchaseOrder purchaseOrder = purchaseOrderService.getByPlanCode(ptdm, orderPlanDetail.getPurchaseOrderPlan().getCode());
				if(purchaseOrder!=null){
					message.setMsg("第("+sxh+")笔：订单已不可取消");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		map.put("user", user);
		message.setData(map);
		return message;
	}
	/**
	 * 订单计划医院取消项下载
	 */
	@Override
	@WebMethod(action="getCancel")
	@WebResult(name="getResult")
	public String getCancel(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrderPlan_getCancel, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData4(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Boolean isGPO = (Boolean) map.get("isGPO");
			Company company = (Company) map.get("company");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getCancelMethod(converData, isGPO, company);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	private Message checkData4( JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = null;
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
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		message.setData(map);
		return message;
	}
	/**
	 * 
	 * @param jObject
	 * @param isGPO
	 * @param company
	 * @return
	 */
	private Message getCancelMethod(JSONObject jObject, Boolean isGPO, Company company) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			JSONArray jsonArray = purchaseOrderPlanDetailService.getCancel(ptdm, company, isGPO);
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}
	
	/**
	 * GPO下载订单计划
	 */
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrderPlan_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData2(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Boolean isGPO = (Boolean) map.get("isGPO");
			Company company = (Company) map.get("company");
			Boolean isCode = (Boolean) map.get("isCode");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, isGPO, company, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	/**
	 * GPO反馈订单计划
	 */
	@Override
	@WebMethod(action="fedback")
	@WebResult(name="getResult")
	public String fedback(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.purchaseOrderPlan_fedback, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData3(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Company company = (Company) map.get("company");
			String ptdm = converData.getString("ptdm");	//平台代码
			
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.purchaseOrderPlan_fedback);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = fedbackMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	private Message fedbackMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			purchaseOrderPlanService.fedback(ptdm, jObject);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData("");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message checkData3(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = new Company();
		
		PurchaseOrderPlan purchaseOrderPlan = new PurchaseOrderPlan();
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
			String ztString = jObject.getString("zt");		//状态
			String jls = jObject.getString("jls");	//记录数
			
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
			
			String ddjhbh = jObject.getString("ddjhbh");	//订单计划编号
			if(StringUtils.isEmpty(ddjhbh)){
				message.setMsg("订单计划编码不能为空");
				return message;
			}		
			purchaseOrderPlan = purchaseOrderPlanService.findByCode(ptdm, ddjhbh);
			if(purchaseOrderPlan == null){
				message.setMsg("订单计划编码有误！");
				return message;
			}
			
			if(StringUtils.isEmpty(ztString)){
				message.setMsg("状态不能为空");
				return message;
			}
			try {
				int zt = jObject.getIntValue("zt");//状态
				if(zt != 0 && zt != 1 && zt != 2){
					message.setMsg("状态应为0或1或2");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("状态格式有误");
				return message;
			}
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
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");		//顺序号
				String ddjhmxbh = jo.getString("ddjhmxbh");	//订单明细编号
				String cljgString = jo.getString("cljg");	//处理结果
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");	//顺序号
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cljgString)){
					message.setMsg("第("+sxh+")笔：处理结果不能为空");
					return message;
				}
				if(StringUtils.isEmpty(ddjhmxbh)){
					message.setMsg("第("+sxh+")笔：订单计划明细编号不能为空");
					return message;
				}
				PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailService.findByCode(ptdm, ddjhmxbh);
				if(purchaseOrderPlanDetail == null){
					message.setMsg("第("+sxh+")笔：订单计划明细编号有误");
					return message;
				}
				if(!purchaseOrderPlanDetail.getPurchaseOrderPlan().getCode().equals(ddjhbh)){
					message.setMsg("第("+sxh+")笔：订单计划明细对应的订单计划主档编号不对应");
					return message;
				}
				try {
					int cljg = jo.getIntValue("cljg");//处理结果
					if(cljg != 0 && cljg != 1){
						message.setMsg("第("+sxh+")笔：处理结果应为0或1");
						return message;
					}
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：处理结果格式有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("company", company);
		message.setData(map);
		return message;
	}
	/**
	 * 
	 * @param jObject
	 * @param isGPO
	 * @param isCode
	 * @return
	 */
	private Message getMethod(JSONObject jObject, Boolean isGPO, Company company, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			JSONArray jsonArray = purchaseOrderPlanService.getToGPO(ptdm, company, isGPO, isCode, jObject);
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}
	
	private Message checkData2( JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = null;
		Boolean isCode = true;
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
			String ddjhbh = jObject.getString("ddjhbh");	//订单计划编号
			String cxkssj = jObject.getString("cxkssj");	//查询开始时间
			String cxjssj = jObject.getString("cxjssj");	//查询结束时间
			if(StringUtils.isEmpty(ddjhbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或订单计划编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或订单计划编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanService.findByCode(ptdm, ddjhbh);
				if(purchaseOrderPlan == null){
					message.setMsg("订单计划编号有误");
					return message;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		return message;
	}
	/**
	 * 
	 * @param jObject
	 * @param datagramId 
	 * @param user 
	 * @return
	 */
	private Message sendMethod(JSONObject jObject, Long datagramId, Hospital hospital, Warehouse warehouse,String field3, User user) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yqphsj = jObject.getString("yqphsj");//要求配货时间
			int jjcd = jObject.getIntValue("jjcd");//紧急程度
			Integer dcpsbs = jObject.getIntValue("dcpsbs");//多次配送标识
			String cgjhdbh = jObject.getString("cgjhdbh");//采购计划单编号
			int jhlx = jObject.getIntValue("jhlx");//计划类型
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			UrgencyLevel urgencyLevel = UrgencyLevel.values()[jjcd];
			PurchasePlan plan = new PurchasePlan();
			String code = snService.getCode(ptdm, OrderType.plan);
			plan.setCode(code); // 订单编号
			plan.setInternalCode(cgjhdbh);
			plan.setOrderDate(new Date());
			plan.setHospitalCode(hospital.getCode()); // 医疗机构id
			plan.setHospitalName(hospital.getFullName()); // 医疗机构名称
			plan.setWarehouseCode(warehouse.getCode()); // 库房id
			plan.setWarehouseName(warehouse.getName()); // 库房名称
			plan.setIsPass(0);
			plan.setIsAuto(1);// 订单产生方式
			plan.setIsRead(0);
			plan.setDatagramId(datagramId);
			plan.setPlanType(jhlx);
			
			plan.setRequireDate(DateUtil.strToDate(yqphsj)); // 要求供货日期
			if(urgencyLevel != null){
				plan.setUrgencyLevel(urgencyLevel);
			}else{
				//TODO 没有传紧急程度时怎么处理
				plan.setUrgencyLevel(UrgencyLevel.noturgent);
			}
			plan.setIsManyDelivery(dcpsbs);//是否多次配送
			plan.setProjectCode(ptdm);
			
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			if(user != null){
				plan.setCreateUser(user.getEmpId());
			}
			String result = purchasePlanService.savePurchasePlan(ptdm, list, plan,field3);
			
			System.out.println("--purchasePlan返回data--"+result);
			JSONObject r_jo = JSONObject.parseObject(result);
			JSONObject rJsonObject = new JSONObject();
			rJsonObject.put("ddjhbh", r_jo.getString("ddjhbh"));
			//if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")){//非深圳项目
				rJsonObject.put("jls", r_jo.getString("jls"));
				rJsonObject.put("mx", r_jo.getJSONArray("mx"));

			//}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(rJsonObject);
		} catch (MyException e) {
			e.printStackTrace();
			message.setMsg("数据处理错误："+e.getMessage());
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}
	
	/**
	 * 检查数据
	 * @param jObject
	 * @return
	 */
	private Message checkData(JSONObject jObject){
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Warehouse warehouse = new Warehouse();
		User user = new User();
		AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "PURCHASEORDERPLAN");
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
			String hospitalCode = jObject.getString("yybm");//医院编码
			if(StringUtils.isEmpty(hospitalCode)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, hospitalCode);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}

			if(!"0".equals(wsCheck.getField3())){
				String yhzh = jObject.getString("yhzh");//用户账号
				String yhzs = jObject.getString("yhzs");//用户证书
				String yhqm = jObject.getString("yhqm");//用户签名
				message = checkCA(ptdm, yhzh, hospital.getIocode(), yhzs, yhqm);
				if(!message.getSuccess()){
					return message;
				}
				user = (User) message.getData();
			}
			message.setSuccess(false);
			message.setData("");
			String warehouseCode = jObject.getString("psdbm");//配送点编码
			if(StringUtils.isEmpty(warehouseCode)){
				message.setMsg("配送点编码不能为空");
				return message;
			}
			warehouse = warehouseService.queryByCodeAndPid(ptdm, warehouseCode, hospital.getId());
			if(warehouse == null){
				message.setMsg("配送点编码有误");
				return message;
			}
			String yqphsj = jObject.getString("yqphsj");//要求配货时间
			if(StringUtils.isEmpty(yqphsj)){
				message.setMsg("要求配货时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(yqphsj, 2)){
				message.setMsg("要求配货时间格式有误");
				return message;
			}
			if(DateUtil.compareDate(DateUtil.getToday19(), yqphsj) > 0){
				message.setMsg("要求配货时间不能早于当前时间");
				return message;
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

			if(!"0".equals(wsCheck.getField3())){
				String cgjhdbh = jObject.getString("cgjhdbh");//采购计划单编号
				if(StringUtils.isEmpty(cgjhdbh)){
					message.setMsg("采购计划单编号不能为空");
					return message;
				}
				Long count = purchaseOrderPlanService.getCountByInternalCode(ptdm, hospital.getCode(), cgjhdbh);
				if(count != null && count > 0){
					message.setMsg("采购计划单编号已存在");
					return message;
				}
			}
			if(!StringUtils.isEmpty(jObject.getString("jjcd"))){
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

			if(CommonProperties.IS_TO_SZ_PROJECT.equals("0")) {
				String jhlx = jObject.getString("jhlx");//计划类型
				if(StringUtils.isEmpty(jhlx)){
					message.setMsg("计划类型不能为空");
					return message;
				}
				try {
					int jhlx_i = jObject.getIntValue("jhlx");//计划类型
					if(jhlx_i != 0 && jhlx_i != 1){
						message.setMsg("计划类型应为0或1");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("计划类型格式有误");
					return message;
				}
			} else {
				//深圳
				jObject.put("jhlx", 0);
			}
			String jls = jObject.getString("jls");//记录数
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
				message.setMsg("订单计划明细数据格式有误");
				return message;
			}
			
			Map<String, JSONObject> slMap = new LinkedHashMap<>();
			StringBuffer strBuf = new StringBuffer();
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh");	//顺序号
				String ypbm = jo.getString("ypbm");		//药品编码
				String cgsl = jo.getString("cgsl");		//采购数量
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("订单明细顺序号格式有误");
					return message;
				}
				if(StringUtils.isEmpty(ypbm)){
					strBuf.append("第("+sxh+")笔：药品编码不能为空;");
					continue;
				}
				String gysbm = jo.getString("gysbm");	//供应商编码
				if(StringUtils.isEmpty(gysbm)){
					strBuf.append("第("+sxh+")笔：供应商编码不能为空;");
					continue;
				}
				Company vendor = companyService.findByCode(ptdm, gysbm,"isVendor=1");
				if(vendor == null){
					strBuf.append("第("+sxh+")笔：供应商编码有误;");
					continue;
				}
				if(StringUtils.isEmpty(cgsl)){
					strBuf.append("第("+sxh+")笔：采购数量不能为空;");
					continue;
				}
				BigDecimal cgsl_int = new BigDecimal(0);
				try {
					cgsl_int = jo.getBigDecimal("cgsl");
				} catch (Exception e) {
					e.printStackTrace();
					strBuf.append("第("+sxh+")笔：采购数量格式有误;");
					continue;
				}
				if(cgsl_int.compareTo(new BigDecimal(0)) <= 0){
					strBuf.append("第("+sxh+")笔：采购数量不能小于零;");
					continue;
				}
				int i = cgsl_int.multiply(new BigDecimal(100)).intValue() % 100;
				if (i > 0) {
					strBuf.append("第("+sxh+")笔：采购数量必须为整数;");
					continue;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					strBuf.append("第("+sxh+")笔：药品编码有误;");
					continue;
				}
				if(product.getIsDisabled() != null && product.getIsDisabled() == 1){
					strBuf.append("第("+sxh+")笔：药品被禁用;");
					continue;
				}
				Goods goods = goodsService.getByProductAndHospital(ptdm, ypbm, hospital.getCode(),0);
				if(goods == null){
					strBuf.append("第("+sxh+")笔：药品("+ypbm+")不在医院药品目录中;");
					continue;
				}
				String cgdj = jo.getString("cgdj");		//采购单价
				if(StringUtils.isEmpty(cgdj)){
					strBuf.append("第("+sxh+")笔：采购单价不能为空;");
					continue;
				}
				JSONObject jsonObject = slMap.get(ypbm+gysbm);
				if (jsonObject == null) {
					jsonObject = new JSONObject();
					jsonObject.put("cgsl",cgsl);
					jsonObject.put("ypbm",ypbm);
					jsonObject.put("gysbm",gysbm);
					jsonObject.put("ypmc",product.getName());
				} else {
					jsonObject.put("cgsl",jsonObject.getBigDecimal("cgsl").add(new BigDecimal(Double.valueOf(cgsl))));
				}
				slMap.put(ypbm+gysbm, jsonObject);
			}
			//校验合同数量
			if(!"0".equals(wsCheck.getField3())){
				String msg = contractDetailService.checkGPOContractNum(ptdm, hospitalCode, slMap);
				strBuf.append(msg);
			}
			//校验合同数量
			/*String msg = contractDetailService.checkGPOContractNum(ptdm, hospitalCode, slMap);
			strBuf.append(msg);*/
			
			if (strBuf.length() > 0) {
				message.setMsg(strBuf.toString());
				return message;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		message.setSuccess(true);
		Map<String, Object> map = new HashMap<>();
		map.put("hospital", hospital);
		map.put("warehouse", warehouse);
		map.put("user", user);
		map.put("field3",wsCheck.getField3());
		message.setData(map);
		return message;
	}

	
}
