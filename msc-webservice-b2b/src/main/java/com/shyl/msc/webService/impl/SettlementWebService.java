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

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.stl.entity.Invoice;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.ISettlementWebService;
import com.shyl.sys.dto.Message;

@WebService(serviceName="settlementWebService",portName="settlementPort", targetNamespace="http://webservice.msc.shyl.com/")
public class SettlementWebService extends BaseWebService implements ISettlementWebService {

	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IInvoiceService invoiceService;
	@Resource
	private ISettlementService settlementService;
	@Resource
	private ISnService snService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.settlement_send, sign, dataType, data);
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
			Hospital hospital = (Hospital) map.get("hospital");
			String ptdm = converData.getString("ptdm");	//平台代码
			
			//第三部验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.settlement_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = sendMethod(converData, gpo, vendor, hospital, Long.valueOf(message.getData().toString()));
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
			String ptdm = jObject.getString("ptdm");	//平台代码
			String jssj = jObject.getString("jssj");		//结算时间
			String zqqsrq = jObject.getString("zqqsrq");	//账期起始日期
			String zqjsrq = jObject.getString("zqjsrq");	//账期结束日期
			BigDecimal jszje = jObject.getBigDecimal("jszje");		//结算总金额
			String yqjsdbh = jObject.getString("yqjsdbh");	//药企结算单编号
			Settlement settlement = new Settlement();
			String code = snService.getCode(ptdm, OrderType.settlement);
			settlement.setCode(code);
			settlement.setInternalCode(yqjsdbh);
			settlement.setOrderDate(DateUtil.strToDate(jssj));
			if(gpo != null){
				settlement.setGpoCode(gpo.getCode());
				settlement.setGpoName(gpo.getFullName());
			}
			settlement.setVendorCode(vendor.getCode());
			settlement.setVendorName(vendor.getFullName());
			settlement.setHospitalCode(hospital.getCode());
			settlement.setHospitalName(hospital.getFullName());
			settlement.setIsPass(0);
			settlement.setDatagramId(datagramId);
			settlement.setAccBeginDate(DateUtil.strToDate(zqqsrq));
			settlement.setAccEndDate(DateUtil.strToDate(zqjsrq));
			settlement.setSum(jszje);
			settlement.setStatus(Settlement.Status.unpay);
			settlement.setProjectCode(ptdm);
			
			String mx = jObject.getString("mx");//明细
			if(mx.startsWith("{\"e\"")){
				mx = "[" + jObject.getJSONObject("mx").getString("e") + "]";
			} else if(mx.startsWith("{")){
				mx = "[" + mx + "]";
			}
			List<JSONObject> arr = JSON.parseArray(mx, JSONObject.class);
			JSONArray res_arr = settlementService.saveSettlement(ptdm, settlement, arr);
			
			JSONObject reslut_jo = new JSONObject();
			reslut_jo.put("jsdbh", settlement.getCode());
			reslut_jo.put("mx", res_arr);
			message.setMsg("成功返回");
			message.setSuccess(true);
			message.setData(reslut_jo);
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
		Hospital hospital = new Hospital();
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
			String jssj = jObject.getString("jssj");		//结算时间
			if(StringUtils.isEmpty(jssj)){
				message.setMsg("结算时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(jssj, 2)){
				message.setMsg("发票日期格式有误");
				return message;
			}
			String zqqsrq = jObject.getString("zqqsrq");	//账期起始日期
			if(StringUtils.isEmpty(zqqsrq)){
				message.setMsg("账期起始日期不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(zqqsrq, 1)){
				message.setMsg("账期起始日期格式有误");
				return message;
			}
			String zqjsrq = jObject.getString("zqjsrq");	//账期结束日期
			if(StringUtils.isEmpty(zqjsrq)){
				message.setMsg("账期结束日期不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(zqjsrq, 1)){
				message.setMsg("账期结束日期格式有误");
				return message;
			}
			String jszje = jObject.getString("jszje");		//结算总金额
			if(StringUtils.isEmpty(jszje)){
				message.setMsg("结算总金额不能为空");
				return message;
			}
			try {
				jObject.getDoubleValue("jszje");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("结算总金额格式有误");
				return message;
			}
			String yqjsdbh = jObject.getString("yqjsdbh");	//药企结算单编号
			if(StringUtils.isEmpty(yqjsdbh)){
				message.setMsg("药企结算单编号不能为空");
				return message;
			}
			String code = "";
			if(isGPO){
				code = gpo.getCode();
			}else {
				code = vendor.getCode();
			}
			Settlement settlement = settlementService.getByInternalCode(ptdm, code, yqjsdbh, isGPO);
			if(settlement != null){
				message.setMsgcode(Message.MsgCode.err01.toString());
				message.setMsg("药企结算单编号已存在");
				List<SettlementDetail> settlementDetails = settlementService.listBySettlementId(ptdm, settlement.getId());
				JSONArray res_arr = new JSONArray();
				int k = 0;
				for(SettlementDetail detail:settlementDetails){
					k++;
					JSONObject jo = new JSONObject();
					jo.put("sxh", k);
					jo.put("jsdmxbh", detail.getCode());
					res_arr.add(jo);
				}
				JSONObject r_jo = new JSONObject();
				r_jo.put("jsdbh", settlement.getCode());
				r_jo.put("mx", res_arr);
				message.setData(r_jo);
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
				message.setMsg("结算单明细不能为空");
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
				message.setMsg("结算单明细数据格式有误");
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
				String fpbh = jo.getString("fpbh");		//发票编号
				if(StringUtils.isEmpty(fpbh)){
					message.setMsg("第("+sxh+")笔：发票编号不能为空");
					return message;
				}
				Invoice invoice = invoiceService.findByCode(ptdm, fpbh);
				if(invoice == null){
					message.setMsg("第("+sxh+")笔：发票编号有误");
					return message;
				}
				if(isGPO){
					if(!invoice.getGpoCode().equals(gpo.getCode())){
						message.setMsg("第("+sxh+")笔：GPO编码和发票中GPO编码不匹配");
						return message;
					}
				}else{
					if(!invoice.getVendorCode().equals(vendor.getCode())){
						message.setMsg("第("+sxh+")笔：供应商编码和发票中供应商编码不匹配");
						return message;
					}
				}
				if(!invoice.getHospitalCode().equals(hospital.getCode())){
					message.setMsg("第("+sxh+")笔：医院编码和发票中医院编码不匹配");
					return message;
				}
				String jsje = jo.getString("jsje");		//结算金额
				if(StringUtils.isEmpty(jsje)){
					message.setMsg("第("+sxh+")笔：结算金额不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("jsje");
				} catch (Exception e) {
					e.printStackTrace();
					message.setMsg("第("+sxh+")笔：结算金额格式有误");
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
		map.put("gpo", gpo);
		map.put("vendor", vendor);
		map.put("hospital", hospital);
		message.setData(map);
		return message;
	}

}
