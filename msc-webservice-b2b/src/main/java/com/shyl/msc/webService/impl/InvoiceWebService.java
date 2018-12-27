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
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.stl.entity.Invoice;
import com.shyl.msc.b2b.stl.entity.InvoiceDetail;
import com.shyl.msc.b2b.stl.service.IInvoiceDetailService;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IInvoiceWebService;
import com.shyl.sys.dto.Message;

/**
 * 发票实现类
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="invoiceWebService",portName="invoicePort", targetNamespace="http://webservice.msc.shyl.com/")
public class InvoiceWebService extends BaseWebService implements IInvoiceWebService {
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IInvoiceService invoiceService;
	@Resource
	private IInvoiceDetailService invoiceDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private ISnService snService;
	@Resource
	private IProductService productService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private LockCollection lockCollection;

	/**
	 * GPO抛送发票数据
	 */
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.invoice_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			String jgbm = converData.getString("jgbm");
			String fph = converData.getString("fph");  //发票号
			IBaseLock baseLock = lockCollection.getLock(Invoice.class, jgbm+"-"+fph);
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
				String ptdm = converData.getString("ptdm");	//平台代码

				//第三部验证签名
				message = checkSign(company.getIocode(), data, sign);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}
				//第四步新增报文信息
				message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.invoice_send);
				if(!message.getSuccess()){
					return resultMessage(message, dataType);
				}

				//第五步执行主逻辑
				message = sendMethod(converData, gpo, vendor, hospital, Long.valueOf(message.getData().toString()));
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

	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Hospital hospital, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");
			String fph = jObject.getString("fph");//发票号
			String fprq = jObject.getString("fprq");//发票日期
			BigDecimal hszje = jObject.getBigDecimal("hszje");//含税总金额
			BigDecimal bhszje = jObject.getBigDecimal("bhszje");//不含税总金额
			Integer sfch = jObject.getInteger("sfch");//是否充红
			String fpbz = jObject.getString("fpbz");//发票备注
			int czfs_i = jObject.getIntValue("czfs");//操作方式
			if(czfs_i == 1){

				Invoice invoice = new Invoice();
				String code = snService.getCode(ptdm, OrderType.invoice);
				invoice.setCode(code);	// 发票编码
				invoice.setInternalCode(fph);
				invoice.setOrderDate(DateUtil.strToDate(fprq));
				if(gpo != null){
					invoice.setGpoCode(gpo.getCode());
					invoice.setGpoName(gpo.getFullName());
				}
				invoice.setVendorCode(vendor.getCode());
				invoice.setVendorName(vendor.getFullName());
				invoice.setHospitalCode(hospital.getCode());
				invoice.setHospitalName(hospital.getFullName());
				invoice.setSum(hszje);
				invoice.setNoTaxSum(bhszje);
				invoice.setIsRed(sfch);//是否冲红
				invoice.setIsAuto(1);
				invoice.setIsPass(0);
				invoice.setRemarks(fpbz);
				invoice.setDatagramId(datagramId);
				invoice.setProjectCode(ptdm);
				
				String mx = jObject.getString("mx");//明细
				if(mx.startsWith("{\"e\"")){
					mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
				} else if(mx.startsWith("{")){
					mx = "[" + mx + "]";
				}
				List<JSONObject> arr = JSON.parseArray(mx, JSONObject.class);
				JSONArray res_arr = invoiceService.saveInvoice(ptdm, invoice, arr);
				
				JSONObject data_rtn = new JSONObject();
				data_rtn.put("fpbh", invoice.getCode());
				data_rtn.put("mx", res_arr);
				message.setSuccess(true);
				message.setMsg("成功返回");
				message.setData(data_rtn);
			}else if(czfs_i == 2){
				Invoice invoice = invoiceService.getByInternalCode(ptdm, vendor.getCode(), fph, false);
				invoice.setStatus(Invoice.Status.cancel);
				invoice.setInternalCode(invoice.getInternalCode()+"@del");
				invoiceService.update(ptdm, invoice);
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
		Hospital hospital = new Hospital();
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
			String yybm = jObject.getString("yybm");		//医院编码
			String fph = jObject.getString("fph");			//发票号
			String fprq = jObject.getString("fprq");		//发票日期
			String bhszje = jObject.getString("bhszje");	//不含税总金额
			String hszje = jObject.getString("hszje");		//含税总金额
			String sfchString = jObject.getString("sfch");	//是否冲红
			String czfs = jObject.getString("czfs");		//操作方式
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
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			hospital = hospitalService.findByCode(ptdm, yybm);
			if(hospital == null){
				message.setMsg("医院编码有误");
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
			if(StringUtils.isEmpty(fph)){
				message.setMsg("发票号不能为空");
				return message;
			}
			Invoice invoice = invoiceService.getByInternalCode(ptdm, vendor.getCode(), fph, isGPO);
			if(czfs_i == 1 && invoice != null){
				message.setMsg("发票号已存在");
				return message;
			}else if(czfs_i == 2 && invoice == null){
				message.setMsg("不存在作废发票号");
				return message;
			}else if(czfs_i == 2 && invoice != null && invoice.getStatus().equals(Invoice.Status.settle)){
				message.setMsg("发票已经结算不可作废");
				return message;
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
				/*String ph = jo.getString("ph");	//批号
				if(StringUtils.isEmpty(ph)){
					message.setMsg("第("+sxh+")笔：批号不能为空");
					return message;
				}*/
				AttributeItem wsCheck = attributeItemService.queryByAttrAndItemNo("", "WEBSERVICECHECK", "INVOICE");
				if(!wsCheck.getField3().equals("0")){
					String stdmxbh = jo.getString("stdmxbh");//送退单明细编号
					if(StringUtils.isEmpty(stdmxbh)){
						message.setMsg("第("+sxh+")笔：送退单明细编号不能为空");
						return message;
					}
					if(sfch == 0){
						DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailService.findByCode(ptdm, stdmxbh);
						if(deliveryOrderDetail == null){
							message.setMsg("第("+sxh+")笔：送退单明细编号有误,应为配送单明细编号");
							return message;
						}
						if(!deliveryOrderDetail.getProductCode().equals(ypbm)){
							message.setMsg("第("+sxh+")笔：药品编码和送退单明细中药品编码不匹配");
							return message;
						}
					}else if(sfch == 1){
						ReturnsOrderDetail returnsOrderDetail = returnsOrderDetailService.findByCode(ptdm, stdmxbh);
						if(returnsOrderDetail == null){
							message.setMsg("第("+sxh+")笔：送退单明细编号有误,应为退货单明细编号");
							return message;
						}
						if(!returnsOrderDetail.getProductCode().equals(ypbm)){
							message.setMsg("第("+sxh+")笔：药品编码和送退单明细中药品编码不匹配");
							return message;
						}
					}
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
		map.put("hospital", hospital);
		map.put("company", company);
		map.put("gpo", gpo);
		map.put("vendor", vendor);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
	
	/**
	 * 发票下载
	 */
	@Override
	@WebMethod(action="get")
	@WebResult(name="getResult")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.invoice_get, sign, dataType, data);
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
			Hospital hospital = (Hospital) map.get("hospital");
			Boolean isCode = (Boolean) map.get("isCode");
			//第三步验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, hospital, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message checkData2(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = null;
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
			
			String fpbh = jObject.getString("fpbh");	//发票编号
			String cxkssj = jObject.getString("cxkssj");//查询开始时间
			String cxjssj = jObject.getString("cxjssj");//查询结束时间
			if(StringUtils.isEmpty(fpbh)){
				isCode = false;
				if(StringUtils.isEmpty(cxkssj)){
					message.setMsg("查询开始时间或发票编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxkssj, 2)){
					message.setMsg("查询开始时间格式有误");
					return message;
				}
				if(StringUtils.isEmpty(cxjssj)){
					message.setMsg("查询结束时间或发票编号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(cxjssj, 2)){
					message.setMsg("查询结束时间格式有误");
					return message;
				}
			}else{
				Invoice invoice = invoiceService.getByInternalCode(ptdm, hospital.getCode(), fpbh);
				if(invoice == null){
					message.setMsg("发票编号有误");
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
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
	
	/**
	 * 
	 * @param jObject
	 * @return
	 */
	private Message getMethod(JSONObject jObject, Hospital hospital, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码	
			String cxkssj = jObject.getString("cxkssj");
			String cxjssj = jObject.getString("cxjssj");
			String fpbh = jObject.getString("fpbh");
			List<Invoice> invoices = new ArrayList<>();
			if(isCode){
				Invoice invoice = invoiceService.getByInternalCode(ptdm, hospital.getCode(), fpbh);
				invoices.add(invoice);
			}else{
				invoices = invoiceService.listByDate(ptdm, hospital.getCode(),cxkssj,cxjssj);
			}
			JSONArray jsonArray = new JSONArray();
			for(Invoice invoice:invoices){
				JSONObject jo = new JSONObject();
				jo.put("fpbh", invoice.getCode());			//发票编号
				jo.put("fph", invoice.getInternalCode());   //发票号
				jo.put("fprq", DateUtil.getToday10(invoice.getOrderDate()));   //发票日期
				jo.put("yybm", invoice.getHospitalCode());			//医院编码
				jo.put("gysbm", invoice.getVendorCode());		//供应商编码
				jo.put("bhszje", invoice.getNoTaxSum());			//不含税金额
				jo.put("hszje", invoice.getSum());		//含税总金额
				jo.put("sfch", invoice.getIsRed());			//是否冲红
				jo.put("fpbz", invoice.getRemarks());		//发票备注				
				jo.put("zt", invoice.getStatus().ordinal());		//状态
							//读取状态
				List<InvoiceDetail> invoiceDetails = invoiceDetailService.listByInvoiceId(ptdm, invoice.getId());
			
				jo.put("jls", invoiceDetails.size());    	//记录数
				JSONArray jods = new JSONArray();
				int sxh = 0;
				for(InvoiceDetail invoiceDetail:invoiceDetails){
					sxh++;
					JSONObject jod = new JSONObject();
					jod.put("sxh", sxh);					//顺序号
					jod.put("fpmxbh", invoiceDetail.getCode());		//发票明细编号	
					jod.put("ypbm", invoiceDetail.getProductCode());//药品编码
					jod.put("dw", invoiceDetail.getUnit());//单位
					jod.put("spsl", invoiceDetail.getGoodsNum());		//商品数量
					jod.put("bhsdj", invoiceDetail.getNoTaxPrice());		//不含税单价
					jod.put("hsdj", invoiceDetail.getPrice());		//含税单价
					jod.put("bhsje", invoiceDetail.getNoTaxSum());		//不含税金额
					jod.put("hsje", invoiceDetail.getGoodsSum());		//含税金额
					jod.put("sl", invoiceDetail.getTaxRate());//税率
					jod.put("stdbh", invoiceDetail.getDeliveryOrReturnsCode());	//送退单编号
					jod.put("stdmxbh", invoiceDetail.getDeliveryOrReturnsDetailCode());	//送退单明细编号
					jods.add(jod);
				}
				jo.put("mx", jods);
				jsonArray.add(jo);
				
				if(invoice.getIsRead() == null || invoice.getIsRead()==0){
					invoice.setIsRead(1);
					invoiceService.updateWithInclude(ptdm, invoice, "isRead");
				}
			}
			message.setSuccess(true);
			message.setData(jsonArray);
			message.setMsg("返回成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器端程序出错！");
			return message;
		}
		return message;
	}
}
