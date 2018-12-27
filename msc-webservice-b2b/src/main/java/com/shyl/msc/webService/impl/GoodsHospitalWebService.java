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

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsHospital;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsHospitalService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IGoodsHospitalWebService;
import com.shyl.sys.dto.Message;

/***
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="goodsHospitalWebService",portName="goodsHospitalPort", targetNamespace="http://webservice.msc.shyl.com/")
public class GoodsHospitalWebService extends BaseWebService implements IGoodsHospitalWebService {

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IProductService productService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IGoodsHospitalService goodsHospitalService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.goodsHospital_send, sign, dataType, data);
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
			Hospital hospital = (Hospital) map.get("hospital");
			//第三部验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			String ptdm = converData.getString("ptdm");	//平台代码
			//第四步新增报文信息
			message = savaDatagrame(ptdm, hospital.getCode(), hospital.getFullName(), data, dataType, DatagramType.goodsHospital_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第五步执行主逻辑
			message = sendMethod(converData, hospital);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject jo, Hospital hospital) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jo.getString("ptdm");	//平台代码
			String mx = jo.getString("mx");//明细

			if(mx.startsWith("{\"e\"")){
				mx = "[" + jo.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
			for(JSONObject jod:list){
				String yyypbm = jod.getString("yyypbm");//医院药品编码
				String ypmc = jod.getString("ypmc");//药品名称
				String tym = jod.getString("tym");//通用名
				String jxmc = jod.getString("jxmc");//剂型名称
				String scqymc = jod.getString("scqymc");//生产企业名称
				String gg = jod.getString("gg");//规格
				String bzdw = jod.getString("bzdw");//包装单位
				String bzgg = jod.getString("bzgg");//包装规格
				String ypbm = jod.getString("ypbm");//药品编码
				int sfjy = jod.getIntValue("sfjy");//是否禁用
				
				Product product = productService.getByCode(ptdm, ypbm);
				Goods goods = goodsService.getByProductCodeAndHosiptal(ptdm, ypbm, hospital.getCode());
				
				if(goods == null){
					goods = new Goods();
					goods.setProduct(product);
					goods.setProductCode(ypbm);
					goods.setHospitalCode(hospital.getCode());
					goods.setStockUpLimit(0);
					goods.setStockDownLimit(0);
					goods.setStockNum(new BigDecimal(0));
					goods.setStockSum(new BigDecimal(0));
					goods.setIsDisabled(0);
					goods = goodsService.save(ptdm, goods);
					
				} else {
					if(!goods.getIsDisabled().equals(sfjy)){
						goods.setIsDisabled(sfjy);
						goodsService.update(ptdm, goods);
					}
				}
				GoodsHospital goodsHospital = goodsHospitalService.getByGoodsId(ptdm, goods.getId());
				if(goodsHospital == null){
					goodsHospital = new GoodsHospital();
					goodsHospital.setGoodsId(goods.getId());
					goodsHospital.setProductId(product.getId());
					
				}
				goodsHospital.setProductCode(ypbm);
				goodsHospital.setInternalCode(yyypbm);
				goodsHospital.setProductName(ypmc);
				goodsHospital.setHospitalCode(hospital.getCode());
				goodsHospital.setHospitalName(hospital.getFullName());
				goodsHospital.setGenericName(tym);
				goodsHospital.setDosageFormName(jxmc);
				goodsHospital.setModel(gg);
				goodsHospital.setUnitName(bzdw);
				goodsHospital.setPackDesc(bzgg);
				goodsHospital.setProducerName(scqymc);
				goodsHospital.setProjectCode(ptdm);
				goodsHospitalService.saveOrUpdate(ptdm, goodsHospital);
				
			}
			
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData("");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message checkData(JSONObject jo) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
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
				message.setMsg("明细不能为空");
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
				message.setMsg("明细数据格式有误");
				return message;
			}
			StringBuffer strBuf = new StringBuffer();
			net.sf.json.JSONArray nulls = new net.sf.json.JSONArray();
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
				String yyypbm = jod.getString("yyypbm");//医院药品编码
				if(StringUtils.isEmpty(yyypbm)){
					message.setMsg("第("+sxh+")笔：医院药品编码不能为空");
					return message;
				}
				String ypmc = jod.getString("ypmc");//药品名称
				if(StringUtils.isEmpty(ypmc)){
					message.setMsg("第("+sxh+")笔：药品名称不能为空");
					return message;
				}
				String tym = jod.getString("tym");//通用名
				if(StringUtils.isEmpty(tym)){
					message.setMsg("第("+sxh+")笔：通用名不能为空");
					return message;
				}
				String jxmc = jod.getString("jxmc");//剂型名称
				if(StringUtils.isEmpty(jxmc)){
					message.setMsg("第("+sxh+")笔：剂型名称不能为空");
					return message;
				}
				String scqymc = jod.getString("scqymc");//生产企业名称
				if(StringUtils.isEmpty(scqymc)){
					message.setMsg("第("+sxh+")笔：生产企业名称不能为空");
					return message;
				}
				String gg = jod.getString("gg");//规格
				if(StringUtils.isEmpty(gg)){
					message.setMsg("第("+sxh+")笔：规格不能为空");
					return message;
				}
				String bzdw = jod.getString("bzdw");//包装单位
				if(StringUtils.isEmpty(bzdw)){
					message.setMsg("第("+sxh+")笔：包装单位不能为空");
					return message;
				}
				String bzgg = jod.getString("bzgg");//包装规格
				if(StringUtils.isEmpty(bzgg)){
					message.setMsg("第("+sxh+")笔：包装规格不能为空");
					return message;
				}
				String ypbm = jod.getString("ypbm");//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				String sfjyString = jod.getString("sfjy");
				if(StringUtils.isEmpty(sfjyString)){
					message.setMsg("是否禁用不能为空");
					return message;
				}
				int sfjy = 0;
				try {
					sfjy = jod.getIntValue("sfjy");
					if(sfjy != 0 && sfjy != 1){
						message.setMsg("是否禁用应为0或1");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("是否禁用格式有误");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					nulls.add(ypbm);
					continue;
				}
			}
			if (nulls.size() > 0) {
				message.setMsg("药品编码不存在："+nulls.toString());
				return message;
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

	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.goodsHospital_get, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = getCheckData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			AttributeItem attributeItem = (AttributeItem) map.get("attributeItem");
			//第三部验证签名
			message = checkSign(attributeItem.getField3(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四部执行主逻辑
			message = getMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message getMethod(JSONObject jObject) {
		Message message = new Message();
		try {
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			List<Map<String, Object>> goodsHospitals = goodsHospitalService.listByDate("", scgxsj);
			String nowTime = DateUtil.getToday19();
			JSONObject data_js = new JSONObject();
			JSONArray arr = new JSONArray();
			int i=0;
			for(Map<String, Object> map:goodsHospitals){
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh",i+"");							//顺序号
				js.put("yyypbm", map.get("INTERNALCODE")==null?"":map.get("INTERNALCODE"));//医院药品编码
				js.put("ypmc", map.get("PRODUCTNAME")==null?"":map.get("PRODUCTNAME"));//药品名称
				js.put("tym", map.get("GENERICNAME")==null?"":map.get("GENERICNAME"));//通用名
				js.put("jxmc", map.get("DOSAGEFORMNAME")==null?"":map.get("DOSAGEFORMNAME"));//剂型名称
				js.put("scqymc", map.get("PRODUCERNAME")==null?"":map.get("PRODUCERNAME"));//生产企业名称
				js.put("gg", map.get("MODEL")==null?"":map.get("MODEL"));
				js.put("bzdw", map.get("UNITNAME")==null?"":map.get("UNITNAME"));
				js.put("bzgg", map.get("PACKDESC")==null?"":map.get("PACKDESC"));
				js.put("ypbm", map.get("PRODUCTCODE")==null?"":map.get("PRODUCTCODE"));
				js.put("sfjy", map.get("ISDISABLED")==null?"":map.get("ISDISABLED"));//TODO
				arr.add(js);
			}
			data_js.put("bcgxsj", nowTime);
			data_js.put("jls", goodsHospitals.size());
			data_js.put("mx", arr);
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(data_js);
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message getCheckData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		AttributeItem attributeItem = new AttributeItem();
		try {
			String wbxtbm = jObject.getString("wbxtbm");	//外部系统编码
			if(StringUtils.isEmpty(wbxtbm)){
				message.setMsg("外部系统编码不能为空");
				return message;
			}
			attributeItem = attributeItemService.queryByAttrAndItemNo("external_system", wbxtbm);
			if(attributeItem == null){
				message.setMsg("外部系统编码有误");
				return message;
			}
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			if(StringUtils.isEmpty(scgxsj)){
				message.setMsg("上次更新时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(scgxsj,2)){
				message.setMsg("上次更新时间格式错误");
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("attributeItem", attributeItem);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}


	@WebMethod(action = "getAll")
	@WebResult(name = "getResult")
	public String getAll(@WebParam(name = "sign") String sign, @WebParam(name = "dataType") String dataType,
						 @WebParam(name = "data") String data) {
		Message message = new Message();
		try {
			// 第一步检查数据类型是否合法
			message = checkDataType(DatagramType.product_getAll, sign, dataType, data);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkDataGetAll(converData);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			Map<String, Object> map = (Map<String, Object>) message.getData();
			AttributeItem attributeItem = (AttributeItem) map.get("attributeItem");
			// 第三部验证签名并且过滤IP地址白名单
			String ip = getIp();
			System.out.println(ip);
			String field4 = attributeItem.getField4();
			if (StringUtils.isEmpty(field4)||StringUtils.isNotEmpty(field4) && StringUtils.isNotEmpty(ip) && -1 != field4.indexOf(ip)) {
				message = checkSign(attributeItem.getField3(), data, sign);
			} else {
				message.setMsg("您的IP地址不在白名单范围之类,请检查！");
				message.setSuccess(false);
				message.setData("");
			}
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第四部执行主逻辑
			message = getAllMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message checkDataGetAll(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		AttributeItem attributeItem = new AttributeItem();
		try {
			String ptdm = jObject.getString("ptdm"); // 平台代码
			if (CommonProperties.IS_TO_SZ_PROJECT.equals("0")) {
				if (StringUtils.isEmpty(ptdm)) {
					message.setMsg("平台代码不能为空");
					return message;
				}
				List<String> projectList = new ArrayList<>(Arrays.asList(CommonProperties.DB_PROJECTCODE.split(",")));
				if (!projectList.contains(ptdm)) {
					message.setMsg("平台代码有误");
					return message;
				}
			}
			String wbxtbm = jObject.getString("wbxtbm"); // 外部系统编码
			if (StringUtils.isEmpty(wbxtbm)) {
				message.setMsg("外部系统编码不能为空");
				return message;
			}
			attributeItem = attributeItemService.queryByAttrAndItemNo("external_system", wbxtbm);
			if (attributeItem == null) {
				message.setMsg("外部系统编码有误");
				return message;
			}

			String scgxsj = jObject.getString("scgxsj"); // 外部系统编码
			if (StringUtils.isEmpty(scgxsj)) {
				message.setMsg("上次更新时间格式错误");
				return message;
			}
			if (!DateUtil.checkDateFMT(scgxsj, 2)) {
				message.setMsg("上次更新时间格式错误");
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("attributeItem", attributeItem);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	public Message getAllMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			// 解析数据
			String scgxsj = jObject.getString("scgxsj");// 查询时间
			String ptdm = jObject.getString("ptdm");// 平台代码
			String ypbm = jObject.getString("ypbm");// 药品编码
			String ypmc = jObject.getString("ypmc");// 药品名称
			String pzwh = jObject.getString("pzwh");// 批准文号
			String sccj = jObject.getString("sccj");// 生产厂家
			Integer page = jObject.getInteger("page");// 页码
			Integer pageSize = jObject.getInteger("pageSize");// 页码
			if (page == null) {
				page = 1;
			}
			if (pageSize == null || pageSize>200) {
				pageSize = 200;
			}

			PageRequest pageable = new PageRequest();
			pageable.setPage(page);
			pageable.setPageSize(pageSize);
			pageable.setMySort(new Sort(new Sort.Order(Sort.Direction.DESC, "isGPOPurchase")));
			Map<String, Object> m = new HashMap<>();
			if (!StringUtils.isEmpty(ypmc)) {
				m.put("g#hospitalCode_S_LK", ypmc);
			}
			if (!StringUtils.isEmpty(ypmc)) {
				m.put("t#name_S_LK", ypmc);
			}
			if (!StringUtils.isEmpty(pzwh)) {
				m.put("t#authorizeNo_S_LK", pzwh);
			}
			if (!StringUtils.isEmpty(sccj)) {
				m.put("t#producerName_S_LK", sccj);
			}
			if (!StringUtils.isEmpty(ypbm)) {
				m.put("t#code_S_EQ", ypbm);
			}

			pageable.setQuery(m);

			String nowTime = DateUtil.getToday19();
			DataGrid<Map<String, Object>> data = productService.queryByGoodsHospital(ptdm, scgxsj, pageable);
			JSONArray arr = new JSONArray();
			int i = 0;
			for (Map<String, Object> product : data.getRows()) {
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh", i + ""); // 顺序号
				js.put("ypbm", product.get("CODE")); // 药品编码
				js.put("tym", product.get("GENERICNAME")); // 通用名
				js.put("cpm", product.get("NAME")); // 产品名
				js.put("ywm", product.get("ENGLISHNAME") == null ? "" : product.get("ENGLISHNAME")); // 英文名
				js.put("spm", product.get("TRADENAME") == null ? "" : product.get("TRADENAME")); // 商品名
				js.put("jxmc", product.get("DOSAGEFORMNAME") == null ? "" : product.get("DOSAGEFORMNAME")); // 剂型名称
				js.put("gg", product.get("MODEL") == null ? "" : product.get("MODEL")); // 规格
				js.put("scqymc", product.get("PRODUCERNAME") == null ? "" : product.get("PRODUCERNAME")); // 生产企业名称
				if (product.get("AUTHORIZENO") != null) {
					js.put("pzwh", product.get("AUTHORIZENO")); // 批准文号
				} else {
					js.put("pzwh", product.get("IMPORTFILENO") == null ? "" : product.get("IMPORTFILENO")); // 注册证号
				}
				js.put("zxyfl", product.get("ZXYFL") == null ? "" : product.get("ZXYFL")); // 中西药分类
				js.put("bzcz", product.get("PACKAGEMATERIAL") == null ? "" : product.get("PACKAGEMATERIAL")); // 包装材质
				js.put("bzdw", product.get("UNITNAME") == null ? "" : product.get("UNITNAME")); // 包装单位
				js.put("bzsl", product.get("CONVERTRATIO") == null ? "" : product.get("CONVERTRATIO")); // 包装数量
				js.put("bzgg", product.get("PACKDESC") == null ? "" : product.get("PACKDESC")); // 包装规格
				js.put("dwzhb", product.get("CONVERTRATIO") == null ? "0" : product.get("CONVERTRATIO")); // 单位转换比

				js.put("zxzjdw", product.get("MINUNIT") == null ? "" : product.get("MINUNIT"));// 最小制剂单位
				js.put("jldw", product.get("DOSEUNIT") == null ? "" : product.get("DOSEUNIT"));// 剂量单位
				js.put("ybbh", product.get("YBDRUGSNO") == null ? "" : product.get("YBDRUGSNO"));// 医保编号
				js.put("yybm", product.get("HOSPITALCODE") == null ? "" : product.get("HOSPITALCODE"));// 医院编码
				js.put("yymc", product.get("HOSPITALNAME") == null ? "" : product.get("HOSPITALNAME"));// 医院名称
				js.put("tzms", product.get("NOTES") == null ? "" :  product.get("NOTES")); // 特征描述
				js.put("sfjy", product.get("ISDISABLED") == null ? "0" : product.get("ISDISABLED"));
				arr.add(js);
			}
			int pageTotal = (int) data.getTotal() / pageSize;
			if (data.getTotal() % pageSize > 0) {
				pageTotal++;
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			JSONObject data_js = new JSONObject();
			data_js.put("jls", arr.size());
			data_js.put("mx", arr);
			data_js.put("bcgxsj", nowTime);
			data_js.put("page", page);
			data_js.put("pageTotal", pageTotal);
			message.setData(data_js);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}
}
