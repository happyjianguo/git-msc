package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.common.cache.lock.IBaseLock;
import com.shyl.common.cache.lock.LockCollection;
import com.shyl.sys.entity.AttributeItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.order.service.IReturnsRequestDetailService;
import com.shyl.msc.b2b.order.service.IReturnsRequestService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.webService.IReturnsRequestWebService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@WebService(serviceName="returnsRequestWebService",portName="returnsRequestPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ReturnsRequestWebService extends BaseWebService implements IReturnsRequestWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IReturnsRequestService returnsRequestService;
	@Resource
	private IReturnsRequestDetailService returnsRequestDetailService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private ISnService snService;
	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IProductService productService;
	@Resource
	private LockCollection lockCollection;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="getResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.returnsRequest_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String hospitalCode = converData.getString("yybm");//医院编码
			String yythsqdbh = converData.getString("yythsqdbh");//退货申请单编号
			IBaseLock baseLock = lockCollection.getLock(ReturnsRequest.class, hospitalCode+"-"+yythsqdbh);
			baseLock.lock();
			try{
				message = checkData3(converData);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) message.getData();
				Hospital hospital = (Hospital) map.get("hospital");
				Company gpo = (Company) map.get("gpo");
				User user = (User) map.get("user");
				String ptdm = converData.getString("ptdm");	//平台代码
				//第三步验证签名
				message = checkSign(hospital.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第四步新增报文信息
				message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.returnsRequest_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第五步执行主逻辑
				message = sendMethod(converData, gpo, hospital, user, Long.valueOf(message.getData().toString()));
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
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.returnsRequest_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData(converData);
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
	
	@Override
	@WebMethod(action="fedback")
	@WebResult(name="getResult")
	public String fedback(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.returnsRequest_fedback, sign, dataType, data);
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
	
	private Message sendMethod(JSONObject jObject, Company gpo, Hospital hospital,User user, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String yythsqdbh = jObject.getString("yythsqdbh");//医院退货申请单编号
			String thfqsj = jObject.getString("thfqsj");//退货发起时间
			String thfqr = jObject.getString("thfqr");//退货发起人
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			ReturnsRequest returnsRequest = new ReturnsRequest();
			returnsRequest.setCode(snService.getCode(ptdm, OrderType.returnsRequest));
			returnsRequest.setInternalCode(yythsqdbh);
			returnsRequest.setOrderDate(DateUtil.strToDate(thfqsj));
			returnsRequest.setVendorCode(gpo.getCode());
			returnsRequest.setVendorName(gpo.getFullName());
			returnsRequest.setHospitalCode(hospital.getCode());
			returnsRequest.setHospitalName(hospital.getFullName());
			returnsRequest.setIsPass(0);
			returnsRequest.setIsAuto(1);
			returnsRequest.setOrderType(OrderType.returnsRequest);
			returnsRequest.setDatagramId(datagramId);
			returnsRequest.setReturnsMan(thfqr);
			returnsRequest.setReturnsBeginDate(DateUtil.strToDate(thfqsj));
			returnsRequest.setStatus(ReturnsRequest.Status.unaudit);
			if(user != null){
				returnsRequest.setCreateUser(user.getEmpId());;
			}
			//returnsRequest.setProjectCode(ptdm);
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			System.out.println(returnsRequest);
			returnsRequestService.saveReturnsRequest(ptdm, returnsRequest, list);
			
			JSONObject r_ro = new JSONObject();
			r_ro.put("thsqdbh", returnsRequest.getCode());
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(r_ro);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message checkData3(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Company gpo = new Company();
		User user = new User();
		try {
			AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "RETURNSREQUEST");
			String field3 = wsCheck==null ?"":wsCheck.getField3();
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
			String yythsqdbh = jObject.getString("yythsqdbh");//医院退货申请单编号
			if(StringUtils.isEmpty(yythsqdbh)){
				message.setMsg("医院退货申请单编号不能为空");
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
			if(!"0".equals(field3)){
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
			String gysbm = jObject.getString("gysbm");//供应商编码
			if(StringUtils.isEmpty(gysbm)){
				message.setMsg("供应商编码不能为空");
				return message;
			}
			gpo = companyService.findByCode(ptdm, gysbm, "isVendor=1");
			if(gpo == null){
				message.setMsg("供应商编码有误");
				return message;
			}
			String thfqr = jObject.getString("thfqr");//退货发起人
			if(StringUtils.isEmpty(thfqr)){
				message.setMsg("退货发起人不能为空");
				return message;
			}
			String thfqsj = jObject.getString("thfqsj");//退货发起时间
			if(StringUtils.isEmpty(thfqsj)){
				message.setMsg("退货发起时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(thfqsj, 2)){
				message.setMsg("退货发起时间格式有误");
				return message;
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
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ypbm = jo.getString("ypbm");//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);

				String psdmxbh = jo.getString("psdmxbh");//配送单明细编号

				if(!"0".equals(field3)){
					if(product.getIsGPOPurchase() == 1){
						if(StringUtils.isEmpty(psdmxbh)){
							message.setMsg("第("+sxh+")笔：配送单明细编号不能为空");
							return message;
						}
					}
				}
				if(!StringUtils.isEmpty(psdmxbh)){
					DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailService.findByCode(ptdm, psdmxbh);
					if(deliveryOrderDetail == null){
						message.setMsg("第("+sxh+")笔：配送单明细编号有误");
						return message;
					}
					if(!ypbm.equals(deliveryOrderDetail.getProductCode())){
						message.setMsg("第("+sxh+")笔：药品编码和配送单明细中的药品编码不匹配");
						return message;
					}
				}
				String scph = jo.getString("scph");//生产批号
				if(StringUtils.isEmpty(scph)){
					message.setMsg("第("+sxh+")笔：生产批号不能为空");
					return message;
				}
				String scrq = jo.getString("scrq");//生产日期
				if(StringUtils.isEmpty(scrq)){
					message.setMsg("第("+sxh+")笔：生产日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scrq, 1)){
					message.setMsg("生产日期格式有误");
					return message;
				}
				String yxrq = jo.getString("yxrq");//有效日期
				if(StringUtils.isEmpty(yxrq)){
					message.setMsg("第("+sxh+")笔：有效日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxrq, 1)){
					message.setMsg("有效日期格式有误");
					return message;
				}
				String thsl = jo.getString("thsl");//退货数量
				if(StringUtils.isEmpty(thsl)){
					message.setMsg("第("+sxh+")笔：退货数量不能为空");
					return message;
				}
				try {
					jo.getBigDecimal("thsl");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：退货数量格式有误");
					return message;
				}
				if(StringUtils.isEmpty(psdmxbh)){
					String thdj = jo.getString("thdj");//退货单价
					if(StringUtils.isEmpty(thdj)){
						message.setMsg("第("+sxh+")笔：退货单价不能为空");
						return message;
					}
					try {
						jo.getBigDecimal("thdj");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：退货单价格式有误");
						return message;
					}
					String thje = jo.getString("thje");//退货金额
					if(StringUtils.isEmpty(thje)){
						message.setMsg("第("+sxh+")笔：退货金额不能为空");
						return message;
					}
					try {
						jo.getBigDecimal("thje");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：退货金额格式有误");
						return message;
					}
				}
				String thyy = jo.getString("thyy");//退货原因
				if(StringUtils.isEmpty(thyy)){
					message.setMsg("第("+sxh+")笔：退货原因不能为空");
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
		map.put("gpo", gpo);
		map.put("user", user);
		message.setData(map);
		return message;
	}

	
	
	private Message getMethod(JSONObject jObject, boolean isGPO, Company company, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String cxkssj = jObject.getString("cxkssj");
			String cxjssj = jObject.getString("cxjssj");
			String thsqdbh = jObject.getString("thsqdbh");
			List<ReturnsRequest> requests = new ArrayList<>();
			if(isCode){
				ReturnsRequest returnsRequest = returnsRequestService.findByCode(ptdm, thsqdbh);
				requests.add(returnsRequest);
			}else{
				requests = returnsRequestService.listByDate(ptdm, company.getCode(), cxkssj, cxjssj, isGPO);
			}
			JSONArray r_jos = new JSONArray();
			for(ReturnsRequest request:requests){				
				JSONObject r_jo = new JSONObject();
				r_jo.put("thsqdbh", request.getCode());		//退货申请单编号
				r_jo.put("yybm", request.getHospitalCode());		//医院编码
				if(request.getGpoCode() != null){
					r_jo.put("gpobm", request.getGpoCode());		//GPO编码
				}else{
					r_jo.put("gpobm", "");		//GPO编码
				}
				if(request.getVendorCode() != null){
					r_jo.put("gysbm", request.getVendorCode());
				}else{
					r_jo.put("gysbm", "");
				}
				r_jo.put("thfqr", request.getReturnsMan()); //退货发起人
				r_jo.put("thfqsj", DateUtil.dateToStr(request.getReturnsBeginDate()));//退货发起时间
				r_jo.put("yy", request.getReason());
				List<ReturnsRequestDetail> details = returnsRequestDetailService.listByPid(ptdm, request.getId());
				
				r_jo.put("jls", details.size());
				JSONArray r_jo_ds = new JSONArray();
				int sxh = 1;
				for(ReturnsRequestDetail detail:details){
					JSONObject r_jo_d = new JSONObject();
					r_jo_d.put("sxh", sxh);
					r_jo_d.put("thsqdmxbh", detail.getCode()); //退货申请单明细编号
					r_jo_d.put("ypbm", detail.getProductCode());//药品编码
					r_jo_d.put("scph", detail.getBatchCode());//生产批号
					r_jo_d.put("scrq", detail.getBatchDate());//生产日期
					r_jo_d.put("yxrq", detail.getExpiryDate());//有效日期
					r_jo_d.put("thsl", detail.getGoodsNum());//退货数量
					r_jo_d.put("thyy", detail.getReason());//退货原因
					r_jo_d.put("psdmxbh", detail.getDeliveryOrderDetailCode());//配送单明细编号
					sxh++;
					r_jo_ds.add(r_jo_d);
				}
				r_jo.put("mx", r_jo_ds);
				r_jos.add(r_jo);
				if(request.getIsRead() == null || request.getIsRead()==0){
					request.setIsRead(1);
					returnsRequestService.updateWithInclude(ptdm, request, "isRead");
				}
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(r_jos);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Warehouse warehouse = new Warehouse();
		Boolean isGPO = true;
		Company company = new Company();
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
			String cxkssj = jObject.getString("cxkssj");	//查询开始时间
			String cxjssj = jObject.getString("cxjssj");	//查询结束时间
			String thsqdbh = jObject.getString("thsqdbh");	//退货申请单编号
			if(StringUtils.isEmpty(thsqdbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或退货申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或退货申请单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				ReturnsRequest returnsRequest = returnsRequestService.findByCode(ptdm, thsqdbh);
				if(returnsRequest == null){
					message.setMsg("退货申请单编号有误");
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
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	private Message fedbackMethod(JSONObject jo) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jo.getString("ptdm");	//平台代码
			int zt = jo.getIntValue("zt");//状态
			String df = jo.getString("df");//说明
			String thsqdbh = jo.getString("thsqdbh");//退货申请单编号
			ReturnsRequest returnsRequest = returnsRequestService.findByCode(ptdm, thsqdbh);
			if(zt == 0){
				returnsRequest.setStatus(ReturnsRequest.Status.disagree);
			}else if(zt == 1){
				returnsRequest.setStatus(ReturnsRequest.Status.agree);
			}
			returnsRequest.setReply(df);
			returnsRequest.setReturnsRequestDetails(new HashSet<ReturnsRequestDetail>());
			String mx = jo.getString("mx");
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jo.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			for(JSONObject detail:list){
				String thsqdmxbh = detail.getString("thsqdmxbh");	//退货申请单明细编号
				BigDecimal dfthsl = detail.getBigDecimal("dfthsl");		//答复退货数量
				String dfmx = detail.getString("dfmx");				//答复明细
				ReturnsRequestDetail returnsRequestDetail = returnsRequestDetailService.findByCode(ptdm, thsqdmxbh);
				returnsRequestDetail.setReplyNum(dfthsl);
				returnsRequestDetail.setReply(dfmx);
				returnsRequest.getReturnsRequestDetails().add(returnsRequestDetail);
			}
			returnsRequestService.udateReturnsRequest(ptdm, returnsRequest);
			message.setSuccess(true);
			message.setMsg("返回成功");
			message.setData("");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message checkData2(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = new Company();
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
			
			String thsqdbh = jObject.getString("thsqdbh");//退货申请单编号
			if(StringUtils.isEmpty(thsqdbh)){
				message.setMsg("退货申请单编号不能为空");
				return message;
			}
			ReturnsRequest returnsRequest = returnsRequestService.findByCode(ptdm, thsqdbh);
			if(returnsRequest == null){
				message.setMsg("退货申请单编号有误");
				return message;
			}
			ReturnsOrder returnsOrder = returnsOrderService.findByRequestCode(ptdm, thsqdbh);
			if(returnsOrder != null){
				message.setMsg("退货申请单编号已经反馈");
				return message;
			}
			String ztString = jObject.getString("zt");//状态
			if(StringUtils.isEmpty(ztString)){
				message.setMsg("状态不能为空");
				return message;
			}
			try {
				int zt = jObject.getIntValue("zt");//状态
				if(zt != 0 && zt != 1){
					message.setMsg("状态应为0或1");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("状态格式有误");
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
			String mx = jObject.getString("mx");
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = new ArrayList<>();
			try {
				list = JSON.parseArray(mx, JSONObject.class);
			} catch (Exception e) {
				message.setMsg("明细数据格式有误");
				return message;
			}
			for(JSONObject detail:list){
				int sxh = 1;
				String sxhString = detail.getString("sxh");		//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = detail.getIntValue("sxh");	//顺序号
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String thsqdmxbh = detail.getString("thsqdmxbh");	//退货申请单明细编号
				if(StringUtils.isEmpty(thsqdmxbh)){
					message.setMsg("第("+sxh+")笔：退货申请单明细编号不能为空");
					return message;
				}
				ReturnsRequestDetail returnsRequestDetail = returnsRequestDetailService.findByCode(ptdm, thsqdmxbh);
				if(returnsRequestDetail == null){
					message.setMsg("第("+sxh+")笔：退货申请单明细编号有误");
					return message;
				}
				String dfthsl = detail.getString("dfthsl");			//答复退货数量
				if(StringUtils.isEmpty(dfthsl)){
					message.setMsg("第("+sxh+")笔：答复退货数量不能为空");
					return message;
				}
				if(!returnsRequestDetail.getReturnsRequest().getCode().equals(returnsRequest.getCode())){
					message.setMsg("第("+sxh+")笔：退货申请单明细编号跟退货申请单不匹配");
					return message;
				}
				BigDecimal dfthsl_int = new BigDecimal(0);
				try {
					dfthsl_int = detail.getBigDecimal("dfthsl");//退货数量
					if(dfthsl_int.compareTo(new BigDecimal(0)) < 0){
						message.setMsg("第("+sxh+")笔：答复退货数量不能小于零");
						return message;
					}
					if(dfthsl_int.compareTo(returnsRequestDetail.getGoodsNum()) > 0){
						message.setMsg("第("+sxh+")笔：答复退货数量不能大于退货申请数量");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：答复退货数量格式有误");
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
}
