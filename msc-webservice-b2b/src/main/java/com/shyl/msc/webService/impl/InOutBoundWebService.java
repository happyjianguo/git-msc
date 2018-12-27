package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.shyl.common.framework.util.StringUtil;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.InOutBound.IOType;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.msc.b2b.order.service.ISnService;
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
import com.shyl.msc.webService.IInOutBoundWebService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 入库单实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="inOutBoundWebService",portName="inOutBoundPort", targetNamespace="http://webservice.msc.shyl.com/")
public class InOutBoundWebService extends BaseWebService implements IInOutBoundWebService {
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IInOutBoundService inOutBoundService;
	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	@Resource
	private ISnService snService;
	@Resource
	private IProductService productService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private LockCollection lockCollection;
	/**
	 * 医院上传入库单
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.inoutbound_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String yybm = converData.getString("yybm"); //医院编码
			String rkdbh = converData.getString("rkdbh"); //入库单编码
			IBaseLock baseLock = lockCollection.getLock(InOutBound.class, yybm+"-"+rkdbh);
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
				Company vendor = (Company) map.get("vendor");
				User user = (User) map.get("user");
				DeliveryOrder deliveryOrder = (DeliveryOrder) map.get("deliveryOrder");
				@SuppressWarnings("unchecked")
				Map<String, DeliveryOrderDetail> detailMap = (Map<String, DeliveryOrderDetail>) map.get("detailMap");
				//第三步验证签名
				message = checkSign(hospital.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				String ptdm = converData.getString("ptdm");	//平台代码
				//第四步新增报文信息
				message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.inoutbound_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第五步执行主逻辑
				message = sendMethod(converData, detailMap, hospital, warehouse, vendor, Long.valueOf(message.getData().toString()), user, deliveryOrder);
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
	 * GPO下载入库单
	 */
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.inoutbound_get, sign, dataType, data);
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
			message = getMethod(converData, company, isGPO, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message getMethod(JSONObject jObject, Company company, boolean isGPO, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码	
			String cxkssj = jObject.getString("cxkssj");
			String cxjssj = jObject.getString("cxjssj");
			String rkdbh = jObject.getString("rkdbh");
			List<InOutBound> inOutBounds = new ArrayList<>();
			if(isCode){
				InOutBound inOutBound = inOutBoundService.findByCode(ptdm,rkdbh);
				inOutBounds.add(inOutBound);
			}else{
				inOutBounds = inOutBoundService.listByDate(ptdm, company.getCode(),cxkssj,cxjssj,isGPO);
			}
			JSONArray jsonArray = new JSONArray();
			for(InOutBound inOutBound:inOutBounds){
				JSONObject jo = new JSONObject();
				jo.put("yybm", inOutBound.getHospitalCode());			//医院编码
				jo.put("psdbm", inOutBound.getWarehouseCode());		//配送点编码
				if(inOutBound.getGpoCode() != null){
					jo.put("gpobm", inOutBound.getGpoCode());						//供应商编码
				}else{
					jo.put("gpobm", "");						//供应商编码
				}
				if(inOutBound.getVendorCode() != null){
					jo.put("gysbm", inOutBound.getVendorCode());						//供应商编码
				}else{
					jo.put("gysbm", "");						//供应商编码
				}
				jo.put("rkdbh", inOutBound.getCode());		//入库单编号
				jo.put("psdbh", inOutBound.getDeliveryOrderCode());//配送单编号
				jo.put("czsj", DateUtil.dateToStr(inOutBound.getOrderDate()));//操作时间
				jo.put("dqzt", inOutBound.getIsRead());						//读取状态
				List<InOutBoundDetail> inOutBoundDetails = inOutBoundDetailService.listByInOutBound(ptdm, inOutBound.getId());
			
				jo.put("jls", inOutBoundDetails.size());    	//记录数
				JSONArray jods = new JSONArray();
				int sxh = 0;
				for(InOutBoundDetail inOutBoundDetail:inOutBoundDetails){
					sxh++;
					JSONObject jod = new JSONObject();
					jod.put("sxh", sxh);					//顺序号
					jod.put("rkdmxbh", inOutBoundDetail.getCode());		//订单明细编号
					jod.put("psdmxbh", inOutBoundDetail.getDeliveryOrderDetailCode());	//配送单明细编号
					jod.put("ypbm", inOutBoundDetail.getProductCode());//药品编码
					jod.put("crksl", inOutBoundDetail.getGoodsNum());		//采购数量
					jod.put("scph", inOutBoundDetail.getBatchCode());//生产批号
					jods.add(jod);
				}
				jo.put("mx", jods);
				jsonArray.add(jo);
				
				if(inOutBound.getIsRead() == null || inOutBound.getIsRead()==0){
					inOutBound.setIsRead(1);
					inOutBoundService.updateWithInclude(ptdm, inOutBound, "isRead");
				}
			}
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
	
	private Message checkData2(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
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
			String rkdbh = jObject.getString("rkdbh");	//入库单编号
			String cxkssj = jObject.getString("cxkssj");//查询开始时间
			String cxjssj = jObject.getString("cxjssj");//查询结束时间
			if(StringUtils.isEmpty(rkdbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或入库单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或入库单编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				InOutBound inOutBound = inOutBoundService.findByCode(ptdm, rkdbh);
				if(inOutBound == null){
					message.setMsg("入库单编号有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
	/**
	 * 
	 * @param jObject
	 * @param detailMap 
	 * @param vendor 
	 * @return
	 */
	private Message sendMethod(JSONObject jObject, Map<String, DeliveryOrderDetail> detailMap, Hospital hospital, Warehouse warehouse, Company vendor, Long datagramId, User user, DeliveryOrder deliveryOrder) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String czsj = jObject.getString("czsj");//操作日期
			String czr = jObject.getString("czr");//操作人
			String psdbh = jObject.getString("psdbh");//配送单编号
			String rkdbh = jObject.getString("rkdbh");//入库单编号
			
			InOutBound inOutBound = new InOutBound();
			inOutBound.setCode(snService.getCode(ptdm, OrderType.inoutbound));
			inOutBound.setInternalCode(rkdbh);
			inOutBound.setHospitalCode(hospital.getCode());
			inOutBound.setHospitalName(hospital.getFullName());
			inOutBound.setWarehouseCode(warehouse.getCode());
			inOutBound.setWarehouseName(warehouse.getName());
			inOutBound.setVendorCode(vendor.getCode());
			inOutBound.setVendorName(vendor.getFullName());
			inOutBound.setDeliveryOrderCode(psdbh);
			inOutBound.setIoType(IOType.in);
			inOutBound.setOperator(czr);
			inOutBound.setOrderDate(DateUtil.strToDate(czsj));
			inOutBound.setIsRead(0);
			inOutBound.setIsAuto(1);
			inOutBound.setIsPass(0);
			inOutBound.setProjectCode(ptdm);
			
			String mx = jObject.getString("mx");
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			BigDecimal inOutBoundNum = new BigDecimal(0);
			BigDecimal inOutBoundSum = new BigDecimal(0);
			int i = 0;
			for(JSONObject jod:list){
				i++;
				String ypbm = jod.getString("ypbm");//药品编码
				BigDecimal crksl = jod.getBigDecimal("crksl");//出入库数量
				BigDecimal crkdj = jod.getBigDecimal("crkdj");//出入库单价
				BigDecimal crkje = jod.getBigDecimal("crkje");//出入库金额
				String psdmxbh = jod.getString("psdmxbh");//配送单明细编号
				String rkdmxbh = jod.getString("rkdmxbh");//入库单明细编号
				String scph = jod.getString("scph");//生产批号
				String scrq = jod.getString("scrq");//生产日期
				String yxrq = jod.getString("yxrq");//有效日期
				
				Product product = productService.getByCode(ptdm, ypbm);
				DeliveryOrderDetail deliveryOrderDetail = null;
				if (!StringUtils.isBlank(psdmxbh)) {
					deliveryOrderDetail = detailMap.get(psdmxbh);
				}
				InOutBoundDetail inOutBoundDetail = new InOutBoundDetail();
				String code_detail = inOutBound.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
				inOutBoundDetail.setCode(code_detail);
				inOutBoundDetail.setInternalCode(rkdmxbh);
				inOutBoundDetail.setIsPass(0);
				inOutBoundDetail.setOrderDate(inOutBound.getOrderDate());
				inOutBoundDetail.setInOutBound(inOutBound);
				inOutBoundDetail.setBatchCode(scph);
				inOutBoundDetail.setBatchDate(scrq);
				inOutBoundDetail.setExpiryDate(yxrq);
				inOutBoundDetail.setProductCode(product.getCode());
				inOutBoundDetail.setProductName(product.getName());
				inOutBoundDetail.setProducerName(product.getProducerName());
				inOutBoundDetail.setDosageFormName(product.getDosageFormName());
				inOutBoundDetail.setModel(product.getModel());
				inOutBoundDetail.setPackDesc(product.getPackDesc());
				inOutBoundDetail.setUnit(product.getUnit());
				inOutBoundDetail.setGoodsNum(crksl);
				if(deliveryOrderDetail != null){
					inOutBoundDetail.setPrice(deliveryOrderDetail.getPrice());
					inOutBoundDetail.setGoodsSum(inOutBoundDetail.getPrice().multiply(crksl));
					inOutBoundDetail.setDeliveryOrderDetailCode(psdmxbh);
					inOutBoundDetail.setContractDetailCode(deliveryOrderDetail.getContractDetailCode());
				} else {
					inOutBoundDetail.setPrice(crkdj);
					inOutBoundDetail.setGoodsSum(crkje);;
				}
				inOutBound.getInOutBoundDetails().add(inOutBoundDetail);
				inOutBoundNum = inOutBoundNum.add(inOutBoundDetail.getGoodsNum());
				inOutBoundSum = inOutBoundSum.add(inOutBoundDetail.getGoodsSum());
			}
			inOutBound.setNum(inOutBoundNum);
			inOutBound.setSum(inOutBoundSum);
			inOutBound.setDatagramId(datagramId);
			if(user != null){
				inOutBound.setCreateUser(user.getEmpId());;
			}
			if(deliveryOrder != null && deliveryOrder.getId() != null){
				inOutBound.setGpoCode(deliveryOrder.getGpoCode());
				inOutBound.setGpoName(deliveryOrder.getGpoName());
			}
			inOutBoundService.saveInOutBound(ptdm,inOutBound);
			
			JSONObject data_rtn = new JSONObject();//返回json对象	
			data_rtn.put("crkdbh", inOutBound.getCode());
			
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(data_rtn);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	/**
	 * 检查数据格式
	 * @param jo
	 * @return
	 */
	private Message checkData(JSONObject jo){
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Warehouse warehouse = new Warehouse();
		Company vendor = new Company();
		User user = new User();
		DeliveryOrder deliveryOrder = new DeliveryOrder();
		Map<String, DeliveryOrderDetail> detailMap = new HashMap<>();
		try {
			String ptdm = jo.getString("ptdm");	//平台代码
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
			String yybm = jo.getString("yybm");//医院编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
				return message;
			}

			AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "INOUTBOUND");

			if(!"0".equals(wsCheck.getField3())){
				String yhzh = jo.getString("yhzh");//用户账号
				String yhzs = jo.getString("yhzs");//用户证书
				String yhqm = jo.getString("yhqm");//用户签名
				message = checkCA(ptdm, yhzh, hospital.getIocode(), yhzs, yhqm);
				if(!message.getSuccess()){
					return message;
				}
				user = (User) message.getData();
			}

			message.setSuccess(false);
			message.setData("");
			String psdbm = jo.getString("psdbm");//配送点编码
			if(StringUtils.isEmpty(psdbm)){
				message.setMsg("配送点编码不能为空");
				return message;
			}
			warehouse = warehouseService.queryByCodeAndPid(ptdm, psdbm, hospital.getId());
			if(warehouse == null){
				message.setMsg("配送点编码有误");
				return message;
			}
			String rkdbh = jo.getString("rkdbh");//入库单编号
			if(StringUtils.isEmpty(rkdbh)){
				message.setMsg("入库单编号不能为空");
				return message;
			}
			InOutBound inOutBound = inOutBoundService.getByInternalCode(ptdm, hospital.getCode(), rkdbh);
			if(inOutBound != null){
				message.setMsg("入库单编号已存在");
				return message;
			}
			int gpobz_int = 1;
			String psdbh = jo.getString("psdbh");//配送单编号

			
			//xml解析如果为空，会解析为[]
			if (!StringUtils.isEmpty(psdbh)) {
				
				/*if(StringUtils.isEmpty(psdbh)) {
					message.setMsg("配送单编号不能为空");
					return message;
				}*/
				deliveryOrder = deliveryOrderService.findByCode(ptdm, psdbh);
				if(!wsCheck.getField3().equals("0")){
					if(deliveryOrder == null){
						message.setMsg("配送单编号有误");
						return message;
					}
					vendor = companyService.findByCode(ptdm, deliveryOrder.getVendorCode(), "isVendor=1");
				}
			}else{
				String gpobz = jo.getString("gpobz");//GPO标志
				if(StringUtils.isEmpty(gpobz)){
					message.setMsg("GPO标志不能为空");
					return message;
				}
				try {
					gpobz_int = jo.getIntValue("gpobz");//GPO标志
					if(gpobz_int != 0 && gpobz_int != 1){
						message.setMsg("GPO标志应为0或1");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("GPO标志格式有误");
					return message;
				}
				String gysbm = jo.getString("gysbm");//供应商编码
				if(StringUtils.isEmpty(gysbm)){
					message.setMsg("供应商编码不能为空");
					return message;
				}
				vendor = companyService.findByCode(ptdm, gysbm, "isVendor=1");
				if(vendor == null){
					message.setMsg("供应商编码有误");
					return message;
				}			
			}
			
			String czsj = jo.getString("czsj");//操作时间
			if(StringUtils.isEmpty(czsj)){
				message.setMsg("操作时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(czsj, 2)){
				message.setMsg("操作时间格式有误");
				return message;
			}
			String jls = jo.getString("jls");//记录数
			if(StringUtils.isEmpty(jls)){
				message.setMsg("记录数不能为空");
				return message;
			}
			try {
				jo.getIntValue("jls");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("记录数格式有误");
				return message;
			}
			String mx = jo.getString("mx");//明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("订单明细不能为空");
				return message;
			}
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jo.getJSONObject("mx").getString("e") + "]";
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
			BigDecimal totalInOutBoundNum = new BigDecimal(0);
			if (deliveryOrder != null && deliveryOrder.getId() != null) {
				totalInOutBoundNum = deliveryOrder.getInOutBoundNum() == null ?new BigDecimal(0) : deliveryOrder.getInOutBoundNum();
				List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.listByDeliveryOrder(ptdm, deliveryOrder.getId());
				for(DeliveryOrderDetail dd:deliveryOrderDetails){
					detailMap.put(dd.getCode(), dd);
				}
			}
			for(JSONObject jod:list){
				int sxh = 1;
				String sxhString = jod.getString("sxh");	//顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jod.getIntValue("sxh");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ypbm = jod.getString("ypbm");//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：入库单明细中药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				String scph = jod.getString("scph");//生产批号
				if(StringUtils.isEmpty(scph)){
					message.setMsg("第("+sxh+")笔：入库单明细中生产批号不能为空");
					return message;
				}
				String scrq = jod.getString("scrq");//生产日期
				if(StringUtils.isEmpty(scrq)){
					message.setMsg("第("+sxh+")笔：入库单明细中生产日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scrq, 1)){
					message.setMsg("第("+sxh+")笔：入库单明细中生产日期格式有误");
					return message;
				}
				String yxrq = jod.getString("yxrq");//有效日期
				if(StringUtils.isEmpty(yxrq)){
					message.setMsg("第("+sxh+")笔：入库单明细中有效日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxrq, 1)){
					message.setMsg("第("+sxh+")笔：入库单明细中有效日期格式有误");
					return message;
				}
				String crksl = jod.getString("crksl");//出入库数量
				if(StringUtils.isEmpty(crksl)){
					message.setMsg("第("+sxh+")笔：入库单明细中出入库数量不能为空");
					return message;
				}
				try {
					BigDecimal crksl_int = jod.getBigDecimal("crksl");
					if(crksl_int.compareTo(new BigDecimal(0)) <= 0){
						message.setMsg("第("+sxh+")笔：入库单明细中出入库数量不能小于零");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：入库单明细中出入库数量格式有误");
					return message;
				}
				totalInOutBoundNum = totalInOutBoundNum.add(jod.getBigDecimal("crksl"));
				if (product.getIsGPOPurchase() == null || product.getIsGPOPurchase() != 1) {
					String crkdj = jod.getString("crkdj");//出入库单价
					if(StringUtils.isEmpty(crkdj)){
						message.setMsg("第("+sxh+")笔：入库单明细中出入库单价不能为空");
						return message;
					}
					try {
						jod.getBigDecimal("crkdj");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：入库单明细中出入库单价格式有误");
						return message;
					}
					String crkje = jod.getString("crkje");//出入库金额
					if(StringUtils.isEmpty(crkje)){
						message.setMsg("第("+sxh+")笔：入库单明细中出入库金额不能为空");
						return message;
					}
					try {
						jod.getBigDecimal("crkje");
					} catch (Exception e) {
						e.printStackTrace();
						message.setMsg("第("+sxh+")笔：入库单明细中出入库金额格式有误");
						return message;
					}
				}
				String psdmxbh = jod.getString("psdmxbh");//配送单明细编号
				if(!wsCheck.getField3().equals("0")){
					if (!StringUtils.isEmpty(psdbh)) {
						if(product.getIsGPOPurchase() == 1){
							if(StringUtils.isEmpty(psdmxbh)){
								message.setMsg("第("+sxh+")笔：配送单明细编号不能为空");
								return message;
							}
						}
						if(!StringUtils.isEmpty(psdmxbh)){
							DeliveryOrderDetail deliveryOrderDetail = detailMap.get(psdmxbh);
							if(deliveryOrderDetail == null){
								message.setMsg("第("+sxh+")笔：配送单明细编号有误");
								return message;
							}
							if(!deliveryOrderDetail.getDeliveryOrder().getCode().equals(psdbh)){
								message.setMsg("第("+sxh+")笔：配送单编号 和 配送单明细编号不匹配");
								return message;
							}
							if(!deliveryOrderDetail.getProductCode().equals(ypbm)){
								message.setMsg("第("+sxh+")笔：药品编码("+ypbm+")和配送单明细药品编码 不匹配");
								return message;
							}
						}
					}
				}
			}
			if(!wsCheck.getField3().equals("0")){
				if(deliveryOrder != null  && deliveryOrder.getId() != null && totalInOutBoundNum.compareTo(deliveryOrder.getNum()) > 0){
					message.setMsg("入库数量总和不能大于配送单的配送数量");
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
		map.put("vendor", vendor);
		map.put("user", user);
		map.put("deliveryOrder", deliveryOrder);
		map.put("detailMap", detailMap);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
