package com.shyl.msc.webService.impl;

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
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.dm.service.IProductVendorService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IProductWebService;
import com.shyl.sys.dto.Message;

/**
 * 药品信息下载
 * 
 * @author a_Q
 *
 */
@WebService(serviceName = "productWebService", portName = "productPort", targetNamespace = "http://webservice.msc.shyl.com/")
public class ProductWebService extends BaseWebService implements IProductWebService {
	@Resource
	private IProductService productService;
	@Resource
	private IProductVendorService productVendorService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private WebServiceContext wscontext;

	@Override
	@WebMethod
	@WebResult(name = "sendResult")

	public String send(@WebParam(name = "sign") String sign, @WebParam(name = "dataType") String dataType,
			@WebParam(name = "data") String data) {

		return null;
	}

	/**
	 * 根据条件药品信息下载
	 */
	@Override
	@WebMethod(action = "get")
	@WebResult(name = "getResult")
	public String get(@WebParam(name = "sign") String sign, @WebParam(name = "dataType") String dataType,
			@WebParam(name = "data") String data) {
		Message message = new Message();
		try {
			// 第一步检查数据类型是否合法
			message = checkDataType(DatagramType.product_get, sign, dataType, data);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData(converData);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}

			// 第三部执行主逻辑
			message = getMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	/**
	 * 医院取药品编码
	 */
	@Override
	@WebMethod(action = "getToHis")
	@WebResult(name = "getResult")
	public String getToHis(@WebParam(name = "sign") String sign, @WebParam(name = "dataType") String dataType,
			@WebParam(name = "data") String data) {
		Message message = new Message();
		try {
			// 第一步检查数据类型是否合法
			message = checkDataType(DatagramType.product_getToHis, sign, dataType, data);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData2(converData);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Hospital hospital = (Hospital) map.get("hospital");
			// 第三部验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第四部执行主逻辑
			message = getToHisMethod(converData, hospital);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	/**
	 * GPO取药品编码
	 */
	@Override
	@WebMethod
	@WebResult(name = "getResult")
	public String getToCom(@WebParam(name = "sign") String sign, @WebParam(name = "dataType") String dataType,
			@WebParam(name = "data") String data) {
		Message message = new Message();
		try {
			// 第一步检查数据类型是否合法
			message = checkDataType(DatagramType.product_getToCom, sign, dataType, data);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData3(converData);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message.getData();
			Boolean isGPO = (Boolean) map.get("isGPO");
			Company company = (Company) map.get("company");
			// 第三部验证签名
			message = checkSign(company.getIocode(), data, sign);
			if (!message.getSuccess()) {
				return resultMessage(message, dataType);
			}
			// 第四部执行主逻辑
			message = getToComMethod(converData, isGPO, company);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	@Override
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
			@SuppressWarnings("unchecked")
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

	private Message getAllMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm"); // 平台代码
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			String page = jObject.getString("page");// 上次更新时间
			if (StringUtils.isBlank(page)) {
				page = "1";
			}
			PageRequest request = new PageRequest();
			Map<String, Object> query = new HashMap<>();
			query.put("modifyDate_D_GE", scgxsj);
			request.setQuery(query);
			request.setPage(Integer.valueOf(page));
			request.setPageSize(200);

			String nowTime = DateUtil.getToday19();
			JSONObject data_js = new JSONObject();

			DataGrid<Product> data = productService.query(ptdm, request);
			JSONArray arr = new JSONArray();
			int i = 0;
			for (Product product : data.getRows()) {
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh", i + ""); // 顺序号
				js.put("ypbm", product.getCode()); // 药品编码
				js.put("tym", product.getGenericName()); // 通用名
				js.put("cpm", product.getName()); // 产品名
				js.put("ywm", product.getEnglishName() == null ? "" : product.getEnglishName()); // 英文名
				js.put("spm", product.getTradeName() == null ? "" : product.getTradeName()); // 商品名
				js.put("jxmc", product.getDosageFormName() == null ? "" : product.getDosageFormName()); // 剂型名称
				js.put("gg", product.getModel() == null ? "" : product.getModel()); // 规格
				js.put("scqymc", product.getProducerName() == null ? "" : product.getProducerName()); // 生产企业名称
				if (product.getAuthorizeNo() != null) {
					js.put("pzwh", product.getAuthorizeNo()); // 批准文号
				} else {
					js.put("pzwh", product.getImportFileNo() == null ? "" : product.getImportFileNo()); // 注册证号
				}
				AttributeItem attributeItem = attributeItemService.getById(ptdm, product.getDrugType());
				js.put("zxyfl", attributeItem.getField1()); // 中西药分类
				js.put("bzcz", product.getPackageMaterial() == null ? "" : product.getPackageMaterial()); // 包装材质
				js.put("bzdw", product.getUnitName() == null ? "" : product.getUnitName()); // 包装单位
				js.put("bzsl", product.getConvertRatio() == null ? "" : product.getConvertRatio()); // 包装数量
				js.put("bzgg", product.getPackDesc() == null ? "" : product.getPackDesc()); // 包装规格
				js.put("dwzhb", product.getConvertRatio() == null ? "0" : product.getConvertRatio()); // 单位转换比
				js.put("tzms", product.getNotes() == null ? "" : product.getNotes()); // 特征描述
				js.put("sfjy", product.getIsDisabled() == null ? "0" : product.getIsDisabled());
				js.put("zxzjdw", product.getMinunit() == null ? "" : product.getMinunit());// 最小制剂单位
				js.put("jldw", product.getDoseUnit() == null ? "" : product.getDoseUnit());// 最小剂量单位
				js.put("ybbh", product.getYbdrugsNO() == null ? "" : product.getYbdrugsNO());// 医保编号
				arr.add(js);
			}
			int pageTotal = (int) data.getTotal() / request.getPageSize();
			if (data.getTotal() % request.getPageSize() > 0) {
				pageTotal++;
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			data_js.put("bcgxsj", nowTime);
			data_js.put("page", page);
			data_js.put("pageTotal", pageTotal);
			data_js.put("jls", arr.size());
			data_js.put("mx", arr);
			message.setData(data_js);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message checkDataGetAll(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		AttributeItem attributeItem = new AttributeItem();
		try {
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
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			if (StringUtils.isEmpty(scgxsj)) {
				message.setMsg("上次更新时间不能为空");
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

	private Message getToComMethod(JSONObject jObject, boolean isGPO, Company company) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			// 解析数据
			String ptdm = jObject.getString("ptdm"); // 平台代码
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			String nowTime = DateUtil.getToday19();
			// 业务处理 -- 查询规则 修改日期大于等于scgxsj
			JSONObject data_js = new JSONObject();
			List<Map<String, Object>> list = null;
			if (isGPO) {
				list = productService.listByGPO(ptdm, company.getId(), scgxsj);
			} else {
				Company vendor = companyService.getById(ptdm, company.getId());
				list = productVendorService.listByVendor(ptdm, vendor.getCode(), scgxsj);
			}
			JSONArray arr = new JSONArray();
			int i = 0;
			for (Map<String, Object> map : list) {
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh", i + ""); // 顺序号
				js.put("ypbm", map.get("CODE") == null ? "" : map.get("CODE")); // 药品编码
				js.put("tym", map.get("GENERICNAME") == null ? "" : map.get("GENERICNAME")); // 通用名
				js.put("cpm", map.get("NAME") == null ? "" : map.get("NAME")); // 产品名
				js.put("ywm", map.get("ENGLISHNAME") == null ? "" : map.get("ENGLISHNAME")); // 英文名
				js.put("spm", map.get("TRADENAME") == null ? "" : map.get("TRADENAME")); // 商品名
				js.put("jxmc", map.get("DOSAGEFORMNAME") == null ? "" : map.get("DOSAGEFORMNAME")); // 剂型名称
				js.put("gg", map.get("MODEL") == null ? "" : map.get("MODEL")); // 规格
				js.put("scqymc", map.get("PRODUCERNAME") == null ? "" : map.get("PRODUCERNAME")); // 生产企业名称
				AttributeItem attributeItem = attributeItemService.getById(ptdm,
						new Long(map.get("DRUGTYPE").toString()));
				js.put("zxyfl", attributeItem.getField1()); // 中西药分类
				if (map.get("AUTHORIZENO") != null) {
					js.put("pzwh", map.get("AUTHORIZENO")); // 批准文号
				} else {
					js.put("pzwh", map.get("IMPORTFILENO") == null ? "" : map.get("IMPORTFILENO")); // 注册证号
				}

				js.put("bzcz", map.get("PACKAGEMATERIAL") == null ? "" : map.get("PACKAGEMATERIAL")); // 包装材质
				js.put("bzdw", map.get("UNITNAME") == null ? "" : map.get("UNITNAME")); // 包装单位
				js.put("bzsl", map.get("CONVERTRATIO") == null ? "0" : map.get("CONVERTRATIO")); // 包装数量
				js.put("bzgg", map.get("PACKDESC") == null ? "" : map.get("PACKDESC")); // 包装规格
				js.put("dwzhb", map.get("CONVERTRATIO") == null ? "0" : map.get("CONVERTRATIO")); // 单位转换比
				js.put("tzms", map.get("NOTES") == null ? "" : map.get("NOTES")); // 特征描述
				String isGPOPurchase = map.get("ISGPOPURCHASE") == null ? "" : map.get("ISGPOPURCHASE").toString();
				js.put("sfgpo", isGPOPurchase); // 是否gpo
				Object gpoid = map.get("GPOID");
				if (isGPOPurchase.equals("1") && gpoid != null) {
					Company gpo = companyService.getById(ptdm, new Long(map.get("GPOID").toString()));
					js.put("gpobm", gpo.getCode()); // gpo编码
				} else {
					js.put("gpobm", ""); // gpo编码
				}
				String p_isdisabled = map.get("ISDISABLED").toString();
				if (!isGPO) {
					String g_isdisabled = map.get("PG_ISDISABLED").toString();
					if (p_isdisabled.equals("0") && g_isdisabled.equals("0")) {
						js.put("sfjy", "0");
					} else {
						js.put("sfjy", "1");
					}
				} else {
					if (p_isdisabled.equals("0")) {
						js.put("sfjy", "0");
					} else {
						js.put("sfjy", "1");
					}
				}

				arr.add(js);
			}
			data_js.put("jls", list.size()); // 记录数
			data_js.put("bcgxsj", nowTime); // 本次更新时间
			data_js.put("mx", arr); // 明细

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

	private Message checkData3(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
		Boolean isGPO = true;
		Company company = new Company();
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
			String jglx = jObject.getString("jglx"); // 机构类型
			if (StringUtils.isEmpty(jglx)) {
				message.setMsg("机构类型不能为空");
				return message;
			}
			try {
				int jglx_i = jObject.getIntValue("jglx");
				if (jglx_i != 1 && jglx_i != 2) {
					message.setMsg("机构类型应为1或2");
					return message;
				}
				if (jglx_i == 2) {
					isGPO = false;
				} else {
					isGPO = true;
				}
			} catch (Exception e) {
				message.setMsg("机构类型格式有误");
				return message;
			}
			String jgbm = jObject.getString("jgbm"); // 机构编码
			if (StringUtils.isEmpty(jgbm)) {
				message.setMsg("机构编码不能为空");
				return message;
			}
			if (isGPO) {
				company = companyService.findByCode(ptdm, jgbm, "isGPO=1");
			} else {
				company = companyService.findByCode(ptdm, jgbm, "isVendor=1");
			}
			if (company == null) {
				message.setMsg("机构编码有误");
				return message;
			}
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			if (StringUtils.isEmpty(scgxsj)) {
				message.setMsg("上次更新时间或药品编码不能为空");
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
		map.put("hospital", hospital);
		map.put("isGPO", isGPO);
		map.put("company", company);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	private Message getToHisMethod(JSONObject jObject, Hospital hospital) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm"); // 平台代码
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			String nowTime = DateUtil.getToday19();
			JSONObject data_js = new JSONObject();
			List<Map<String, Object>> list = goodsService.listByHospital(ptdm, hospital.getCode(), scgxsj);

			JSONArray arr = new JSONArray();
			int i = 0;
			for (Map<String, Object> map : list) {
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh", i + ""); // 顺序号
				js.put("ypbm", map.get("CODE") == null ? "" : map.get("CODE")); // 药品编码
				js.put("tym", map.get("GENERICNAME") == null ? "" : map.get("GENERICNAME")); // 通用名
				js.put("cpm", map.get("NAME") == null ? "" : map.get("NAME")); // 产品名
				js.put("ywm", map.get("ENGLISHNAME") == null ? "" : map.get("ENGLISHNAME")); // 英文名
				js.put("spm", map.get("TRADENAME") == null ? "" : map.get("TRADENAME")); // 商品名
				js.put("jxmc", map.get("DOSAGEFORMNAME") == null ? "" : map.get("DOSAGEFORMNAME")); // 剂型名称
				js.put("gg", map.get("MODEL") == null ? "" : map.get("MODEL")); // 规格
				js.put("scqymc", map.get("PRODUCERNAME") == null ? "" : map.get("PRODUCERNAME")); // 生产企业名称
				AttributeItem attributeItem = attributeItemService.getById(ptdm,
						new Long(map.get("DRUGTYPE").toString()));
				js.put("zxyfl", attributeItem.getField1()); // 中西药分类
				if (map.get("AUTHORIZENO") != null) {
					js.put("pzwh", map.get("AUTHORIZENO")); // 批准文号
				} else {
					js.put("pzwh", map.get("IMPORTFILENO") == null ? "" : map.get("IMPORTFILENO")); // 注册证号
				}
				js.put("bzcz", map.get("PACKAGEMATERIAL") == null ? "" : map.get("PACKAGEMATERIAL")); // 包装材质
				js.put("bzdw", map.get("UNITNAME") == null ? "" : map.get("UNITNAME")); // 包装单位
				js.put("bzsl", map.get("CONVERTRATIO") == null ? "0" : map.get("CONVERTRATIO")); // 包装数量
				js.put("bzgg", map.get("PACKDESC") == null ? "" : map.get("PACKDESC")); // 包装规格
				js.put("dwzhb", map.get("CONVERTRATIO") == null ? "0" : map.get("CONVERTRATIO")); // 单位转换比
				js.put("tzms", map.get("NOTES") == null ? "" : map.get("NOTES")); // 特征描述

				String isGPOPurchase = map.get("ISGPOPURCHASE") == null ? "" : map.get("ISGPOPURCHASE").toString();
				js.put("sfgpo", isGPOPurchase); // 是否gpo
				Object gpoid = map.get("GPOID");
				if (isGPOPurchase.equals("1") && gpoid != null) {
					Company company = companyService.getById(ptdm, new Long(map.get("GPOID").toString()));
					js.put("gpobm", company.getCode()); // gpo编码
				} else {
					js.put("gpobm", ""); // gpo编码
				}

				String p_isdisabled = map.get("P_ISDISABLED").toString();
				String g_isdisabled = map.get("G_ISDISABLED").toString();
				if (p_isdisabled.equals("0") && g_isdisabled.equals("0")) {
					js.put("sfjy", "0");
				} else {
					js.put("sfjy", "1");
				}
				arr.add(js);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			data_js.put("bcgxsj", nowTime);
			data_js.put("jls", list.size());
			data_js.put("mx", arr);
			message.setData(data_js);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错！");
			return message;
		}
		return message;
	}

	private Message checkData2(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
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
			String yybm = jObject.getString("yybm");// 医院编码

			if (StringUtils.isEmpty(yybm)) {
				message.setMsg("医院编码不能为空");
				return message;
			}
			if (!StringUtils.isEmpty(yybm)) {
				hospital = hospitalService.findByCode(ptdm, yybm);
				if (hospital == null) {
					message.setMsg("医院编码有误");
					return message;
				}
			}
			String scgxsj = jObject.getString("scgxsj");// 上次更新时间
			if (StringUtils.isEmpty(scgxsj)) {
				message.setMsg("上次更新时间不能为空");
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
		map.put("hospital", hospital);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
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
			String ypbm = jObject.getString("ypbm");// 药品编码
			if (!StringUtils.isEmpty(ypbm)) {
				Product product = productService.getByCode(ptdm, ypbm);
				if (product == null) {
					message.setMsg("药品编码有误");
					return message;
				}
			}
			String ypmc = jObject.getString("ypmc");// 药品名称
			String pzwh = jObject.getString("pzwh");// 批准文号
			if (StringUtils.isEmpty(ypbm) && StringUtils.isEmpty(ypmc) && StringUtils.isEmpty(pzwh)) {
				message.setMsg("药品编码、药品名称、批准文号不能都为空");
				return message;
			}

		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}

		message.setSuccess(true);
		return message;
	}

	/**
	 * 
	 * @param jObject
	 * @return
	 */
	private Message getMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			// 解析数据
			String ptdm = jObject.getString("ptdm");// 平台代码
			String ypbm = jObject.getString("ypbm");// 药品编码
			String ypmc = jObject.getString("ypmc");// 药品名称
			String pzwh = jObject.getString("pzwh");// 批准文号
			String sccj = jObject.getString("sccj");// 生产厂家

			// 业务处理 -- 查询规则 修改日期大于等于scgxsj
			JSONObject data_js = new JSONObject();
			PageRequest pageable = new PageRequest();
			pageable.setMySort(new Sort(new Sort.Order(Direction.DESC, "isGPOPurchase")));
			Map<String, Object> m = new HashMap<String, Object>();
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
			List<Product> list = productService.list("", pageable);
			JSONArray arr = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Product product = (Product) list.get(i);

				JSONObject js = new JSONObject();
				js.put("sxh", i + "");
				js.put("ypbm", product.getCode() == null ? "" : product.getCode());
				js.put("tym", product.getGenericName() == null ? "" : product.getGenericName());
				js.put("cpm", product.getName() == null ? "" : product.getName());
				js.put("ywm", product.getEnglishName() == null ? "" : product.getEnglishName());
				js.put("spm", product.getTradeName() == null ? "" : product.getTradeName());
				js.put("jxmc", product.getDosageFormName() == null ? "" : product.getDosageFormName());
				js.put("gg", product.getModel() == null ? "" : product.getModel());
				js.put("scqymc", product.getProducerName() == null ? "" : product.getProducerName());
				if (product.getAuthorizeNo() != null) {
					js.put("pzwh", product.getAuthorizeNo()); // 批准文号
				} else {
					js.put("pzwh", product.getImportFileNo() == null ? "" : product.getImportFileNo()); // 注册证号
				}
				AttributeItem attributeItem = attributeItemService.getById(ptdm, product.getDrugType());
				js.put("zxyfl", attributeItem.getField1()); // 中西药分类
				js.put("bzcz", product.getPackageMaterial() == null ? "" : product.getPackageMaterial());
				js.put("bzdw", product.getUnitName() == null ? "" : product.getUnitName());
				js.put("bzsl", product.getConvertRatio() == null ? "0" : product.getConvertRatio());
				js.put("bzgg", product.getPackDesc() == null ? "" : product.getPackDesc());
				js.put("dwzhb", product.getConvertRatio() == null ? "0" : product.getConvertRatio());
				js.put("tzms", product.getNotes() == null ? "" : product.getNotes());
				js.put("jl", product.getDose() == null ? "" : product.getDose());
				js.put("jldw", product.getDoseUnit() == null ? "" : product.getDoseUnit());
				String isGPOPurchase = product.getIsGPOPurchase() == null ? "0" : product.getIsGPOPurchase().toString();
				js.put("sfgpo", isGPOPurchase); // 是否gpo
				String gpoCode = product.getGpoCode();
				if (isGPOPurchase.equals("1") && gpoCode != null) {
					js.put("gpobm", gpoCode);
				} else {
					js.put("gpobm", "");
				}

				js.put("sfjy", product.getIsDisabled() == null ? "0" : product.getIsDisabled());
				arr.add(js);
			}
			data_js.put("jls", list.size());
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

	// 获取访问机远程IP地址
	public String getIP() {
		MessageContext ctx = wscontext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
		return request.getRemoteAddr();
	}

}
