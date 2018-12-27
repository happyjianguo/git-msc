package com.shyl.msc.webService.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.shyl.msc.set.entity.Hospital;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import com.shyl.msc.b2b.stl.entity.TradeInvoice.Type;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceDetailService;
import com.shyl.msc.b2b.stl.service.ITradeInvoiceService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.ITradeInvoiceWebService;
import com.shyl.sys.dto.Message;

/**
 * 交易发票实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="tradeInvoiceWebService",portName="tradeInvoicePort", targetNamespace="http://webservice.msc.shyl.com/")
public class TradeInvoiceWebService extends BaseWebService implements ITradeInvoiceWebService {
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ITradeInvoiceService tradeInvoiceService;
	@Resource
	private ITradeInvoiceDetailService tradeInvoiceDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private ISnService snService;
	@Resource
	private IProductService productService;

	/**
	 * 抛送交易发票数据
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.tradeInvoice_send, sign, dataType, data);
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
			Company company = (Company) map.get("company");
			Company gpo = (Company) map.get("gpo");
			Company vendor = (Company) map.get("vendor");
			Company customer = (Company) map.get("customer");
			Hospital his = (Hospital) map.get("his");
			TradeInvoice.Type type = (TradeInvoice.Type)map.get("type");
			String ptdm = converData.getString("ptdm");	//平台代码

			//第三部验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.tradeInvoice_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = sendMethod(converData, gpo, vendor, customer,his,type, Long.valueOf(message.getData().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Company customer,Hospital his,TradeInvoice.Type type, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");
			Integer jglx = jObject.getIntValue("jglx");//机构类型
			String fph = jObject.getString("fph");//发票号
			String fprq = jObject.getString("fprq");//发票日期
			BigDecimal hszje = jObject.getBigDecimal("hszje");//含税总金额
			BigDecimal bhszje = jObject.getBigDecimal("bhszje");//不含税总金额
			Integer sfch = jObject.getInteger("sfch");//是否充红
			String fpbz = jObject.getString("fpbz");//发票备注
			int czfs_i = jObject.getIntValue("czfs");//操作方式
			String parentTradeInvoiceCode = jObject.getString("scfph");//上层发票编号

			if(czfs_i == 1){
				TradeInvoice tradeInvoice = new TradeInvoice();
				String code = snService.getCode(ptdm, OrderType.tradeInvoice);
				tradeInvoice.setCode(code);	// 发票编码
				tradeInvoice.setInternalCode(fph);
				tradeInvoice.setOrderDate(DateUtil.strToDate(fprq));
				tradeInvoice.setParentTradeInvoiceCode(parentTradeInvoiceCode);
				if(gpo != null){
					tradeInvoice.setGpoCode(gpo.getCode());
					tradeInvoice.setGpoName(gpo.getFullName());
				}
				if(vendor != null){
					tradeInvoice.setVendorCode(vendor.getCode());
					tradeInvoice.setVendorName(vendor.getFullName());
				}
				if(customer != null){
					tradeInvoice.setCustomerCode(customer.getCode());
					tradeInvoice.setCustomerName(customer.getFullName());
				}
				if(his != null){
					tradeInvoice.setCustomerCode(his.getCode());
					tradeInvoice.setCustomerName(his.getFullName());
					tradeInvoice.setHospitalCode(his.getCode());
					tradeInvoice.setHospitalName(his.getFullName());
				}
				tradeInvoice.setType(type);
				tradeInvoice.setSum(hszje);
				tradeInvoice.setNoTaxSum(bhszje);
				tradeInvoice.setIsRed(sfch);//是否冲红
				tradeInvoice.setIsAuto(1);
				tradeInvoice.setIsPass(0);
				tradeInvoice.setRemarks(fpbz);
				tradeInvoice.setDatagramId(datagramId);
				tradeInvoice.setProjectCode(ptdm);				
				
				String mx = jObject.getString("mx");//明细
				if(mx.startsWith("{\"e\"")){
					mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
				} else if(mx.startsWith("{")){
					mx = "[" + mx + "]";
				}
				List<JSONObject> arr = JSON.parseArray(mx, JSONObject.class);
				JSONArray res_arr = tradeInvoiceService.saveTradeInvoice(ptdm, tradeInvoice, arr);
				
				JSONObject data_rtn = new JSONObject();
				data_rtn.put("fpbh", tradeInvoice.getCode());
				data_rtn.put("mx", res_arr);
				message.setSuccess(true);
				message.setMsg("成功返回");
				message.setData(data_rtn);
			}else if(czfs_i == 2){
				TradeInvoice tradeInvoice = tradeInvoiceService.getByInternalCode(ptdm,fph,type);
				tradeInvoice.setInternalCode(tradeInvoice.getInternalCode()+"@del");
				tradeInvoiceService.update(ptdm, tradeInvoice);
				message.setSuccess(true);
				message.setMsg("成功返回");
				message.setData("");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器端出错！");
			return message;
		}
		return message;
	}

	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = new Company();
		Company gpo = new Company();
		Company vendor = new Company();
		Company customer = new Company();
		Hospital his = new Hospital();
		TradeInvoice.Type type = null;
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
			String gyqybm = jObject.getString("gyqybm");		//供应企业编码
			String xqqybm = jObject.getString("xqqybm");		//需求企业编码
			String fph = jObject.getString("fph");			//发票号
			String fprq = jObject.getString("fprq");		//发票日期
			String bhszje = jObject.getString("bhszje");	//不含税总金额
			String hszje = jObject.getString("hszje");		//含税总金额
			String sfchString = jObject.getString("sfch");	//是否冲红
			String czfs = jObject.getString("czfs");		//操作方式
			String scfph = jObject.getString("scfph");			//上层发票号
			if(StringUtils.isEmpty(czfs)){
				message.setMsg("操作方式不能为空");
				return message;
			}
			int czfs_i = 0;
			try {
				czfs_i = jObject.getIntValue("czfs");
				if(czfs_i != 1 && czfs_i != 2){
					message.setMsg("操作方式应为1或2");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("操作方式格式有误");
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
				gpo = company;
			}else{
				company = companyService.findByCode(ptdm, jgbm,"isVendor=1");
				vendor = company;
			}
			if(company == null){
				message.setMsg("机构编码有误");
				return message;
			}
			try {
				int fplx_i = jObject.getIntValue("fplx");
				if(fplx_i != 1 && fplx_i != 2 && fplx_i != 3 && fplx_i != 4 && fplx_i != 5){
					message.setMsg("发票类型应为1、2、3、4 或 5");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("发票类型格式有误");
				return message;
			}
			if(StringUtils.isEmpty(gyqybm)){
				message.setMsg("供应企业编码不能为空");
				return message;
			}
			if(StringUtils.isEmpty(xqqybm)){
				message.setMsg("需求企业编码不能为空");
				return message;
			}
			int fplx_i = jObject.getIntValue("fplx");
			TradeInvoice.Type boforeType = null;
			String pram1 = "";
			String pram2 = "";
			if(fplx_i == 1){
				type = Type.producerToGPO;
				pram1 = "isProducer=1";
				pram2 = "isGPO = 1";
			}else if(fplx_i == 2){
				type = Type.GPOToVendor;
				boforeType = Type.producerToGPO;
				pram1 = "isGPO = 1";
				pram2 = "isVendor = 1";
			}else if(fplx_i == 3){
				type = Type.producerToVendor;
				boforeType = Type.GPOToVendor;
				pram1 = "isProducer=1";
				pram2 = "isVendor = 1";
			}else if(fplx_i == 4){
				type = Type.vendorToHospital;
				boforeType = Type.producerToVendor;
				pram1 = "isVendor=1";
			}else{
				type = Type.producerToHospital;
				boforeType = Type.vendorToHospital;
				pram1 = "isProducer=1";
			}
			if("isGPO = 1".equals(pram1)){
				gpo = companyService.findByCode(ptdm, gyqybm,pram1);
			}else{
				vendor = companyService.findByCode(ptdm, gyqybm,pram1);
			}
			if(vendor == null){
				message.setMsg("供应企业编码有误");
				return message;
			}
			if(gpo == null){
				message.setMsg("供应企业编码有误");
				return message;
			}
			if(fplx_i == 1 || fplx_i == 2 || fplx_i == 3){
				customer = companyService.findByCode(ptdm, xqqybm,pram2);

			}else if(fplx_i == 4 || fplx_i == 5){
				his = hospitalService.findByCode(ptdm,xqqybm);
			}
			if(customer == null){
				message.setMsg("需求企业编码有误");
				return message;
			}
			if(his == null){
				message.setMsg("需求企业编码有误");
				return message;
			}
			if(StringUtils.isEmpty(fph)){
				message.setMsg("发票号不能为空");
				return message;
			}
			TradeInvoice tradeInvoice = tradeInvoiceService.getByInternalCode(ptdm,fph,type);
			if(czfs_i == 1 && tradeInvoice != null){
				message.setMsg("发票号已存在");
				return message;
			}else if(czfs_i == 2 && tradeInvoice == null){
				message.setMsg("不存在作废发票号");
				return message;
			}
			//上层发票号选填   填了之后要检查
			if(!StringUtils.isEmpty(scfph)){
				TradeInvoice beforeTradeInvoice = tradeInvoiceService.getByInternalCode(ptdm,scfph,boforeType);
				if(beforeTradeInvoice == null){
					message.setMsg("上层发票号不存在");
					return message;
				}
			}
			if(StringUtils.isEmpty(fprq)){
				message.setMsg("发票日期不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(fprq, 1)){
				message.setMsg("发票日期格式有误");
				return message;
			}
			if(StringUtils.isEmpty(bhszje)){
				message.setMsg("不含税总金额不能为空");
				return message;
			}
			try {
				jObject.getDoubleValue("bhszje");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("不含税总金额格式有误");
				return message;
			}
			if(StringUtils.isEmpty(hszje)){
				message.setMsg("含税总金额不能为空");
				return message;
			}
			try {
				jObject.getDoubleValue("hszje");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("含税总金额格式有误");
				return message;
			}
			if(StringUtils.isEmpty(sfchString)){
				message.setMsg("是否冲红不能为空");
				return message;
			}
			int sfch = 0;
			try {
				sfch = jObject.getIntValue("sfch");
				if(sfch != 0 && sfch != 1){
					message.setMsg("是否冲红应为0或1");
					return message;
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("是否冲红格式有误");
				return message;
			}


			String jls = jObject.getString("jls");//记录数
			if(StringUtils.isEmpty(jls)){
				message.setMsg("记录数不能为空");
				return message;
			}
			try {
				jObject.getIntValue("jls");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("记录数格式有误");
				return message;
			}
			String mx = jObject.getString("mx");//明细
			if(StringUtils.isEmpty(mx)){
				message.setMsg("发票明细不能为空");
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
				String ypbm = jo.getString("ypbm");		//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				String spsl = jo.getString("spsl");		//商品数量
				if(StringUtils.isEmpty(spsl)){
					message.setMsg("第("+sxh+")笔：商品数量不能为空");
					return message;
				}
				try {
					BigDecimal spsl_int = jo.getBigDecimal("spsl");
					if(spsl_int.compareTo(new BigDecimal(0)) <= 0){
						message.setMsg("第("+sxh+")笔：商品数量不能小于零");
						return message;
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：商品数量格式有误");
					return message;
				}
				
				String bhsdj = jo.getString("bhsdj");	//不含税单价
				if(StringUtils.isEmpty(bhsdj)){
					message.setMsg("第("+sxh+")笔：不含税单价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("bhsdj");//不含税单价
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：不含税单价格式有误");
					return message;
				}
				String hsdj = jo.getString("hsdj");		//含税单价
				if(StringUtils.isEmpty(hsdj)){
					message.setMsg("第("+sxh+")笔：含税单价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("hsdj");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：含税单价格式有误");
					return message;
				}
				String bhsje = jo.getString("bhsje");	//不含税金额
				if(StringUtils.isEmpty(bhsje)){
					message.setMsg("第("+sxh+")笔：不含税金额不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("bhsje");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：不含税金额格式有误");
					return message;
				}
				String hsje = jo.getString("hsje");		//含税金额
				if(StringUtils.isEmpty(hsje)){
					message.setMsg("第("+sxh+")笔：含税金额不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("hsje");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：含税金额格式有误");
					return message;
				}
				String sl = jo.getString("sl");			//税率
				if(StringUtils.isEmpty(sl)){
					message.setMsg("第("+sxh+")笔：税率不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("sl");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：税率格式有误");
					return message;
				}
				String ph = jo.getString("ph");	//批号
				if(StringUtils.isEmpty(ph)){
					message.setMsg("第("+sxh+")笔：批号不能为空");
					return message;
				}
				
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("company", company);
		map.put("gpo", gpo.getId() == null ? null : gpo);
		map.put("vendor", vendor.getId() == null ? null : vendor);
		map.put("customer", customer.getId() == null ? null : customer);
		map.put("his", his.getId() == null ? null : his);
		map.put("type",type);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
	
}
