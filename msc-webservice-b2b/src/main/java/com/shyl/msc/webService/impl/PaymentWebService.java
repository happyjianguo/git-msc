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

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.stl.entity.Payment;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.b2b.stl.service.IPaymentService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IPaymentWebService;
import com.shyl.sys.dto.Message;

@WebService(serviceName="paymentWebService",portName="paymentPort", targetNamespace="http://webservice.msc.shyl.com/")
public class PaymentWebService extends BaseWebService implements IPaymentWebService {
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
	private IPaymentService paymentService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.payment_send, sign, dataType, data);
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
			Settlement settlement = (Settlement) map.get("settlement");
			String ptdm = converData.getString("ptdm");	//平台代码
			
			//第三部验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.payment_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			
			//第五步执行主逻辑
			message = sendMethod(converData, gpo, vendor, hospital, settlement, Long.valueOf(message.getData().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject jObject, Company gpo, Company vendor, Hospital hospital, Settlement settlement, Long datagramId) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String fksj = jObject.getString("fksj");		//付款时间
			Integer fkfs = jObject.getInteger("fkfs");	//付款方式
			String yqfkdbh = jObject.getString("yqfkdbh");	//药企付款单编号
			String fkf = jObject.getString("fkf");			//付款方
			BigDecimal fkje = jObject.getBigDecimal("fkje");//付款金额
			String skf = jObject.getString("skf");			//收款方
			String yhmc = jObject.getString("yhmc");		//银行名称
			String zh = jObject.getString("zh");			//支行
			String yhzh = jObject.getString("yhzh");		//银行账号
			String jbr = jObject.getString("jbr");			//经办人
			String code = snService.getCode(ptdm, OrderType.payment);
			Payment payment = new Payment();
			payment.setCode(code);
			payment.setInternalCode(yqfkdbh);
			payment.setOrderDate(DateUtil.strToDate(fksj));
			if(gpo != null){
				payment.setGpoCode(gpo.getCode());
				payment.setGpoName(gpo.getFullName());
			}
			payment.setVendorCode(vendor.getCode());
			payment.setVendorName(vendor.getFullName());
			payment.setHospitalCode(hospital.getCode());
			payment.setHospitalName(hospital.getFullName());
			payment.setDatagramId(datagramId);
			payment.setSum(fkje);
			payment.setIsPass(0);
			payment.setPayer(fkf);
			payment.setReciever(skf);
			payment.setBankName(yhmc);
			payment.setBankBranch(zh);
			payment.setAccNo(yhzh);
			payment.setResponsor(jbr);
			payment.setPayDate(DateUtil.strToDate(fksj));
			payment.setPayMethod(fkfs);
			payment.setSettlementCode(settlement.getCode());
			payment.setProjectCode(ptdm);
			
			paymentService.savePayment(ptdm, payment);
			JSONObject reslut_jo = new JSONObject();
			reslut_jo.put("jkdbh", payment.getCode());
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
		Hospital hospital = new Hospital();
		Boolean isGPO = true;
		Company company = new Company();
		Company gpo = new Company();
		Company vendor = new Company();
		Settlement settlement = new Settlement();
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
			String fksj = jObject.getString("fksj");		//付款时间
			if(StringUtils.isEmpty(fksj)){
				message.setMsg("付款时间不能为空");
				return message;
			}
			if(!DateUtil.checkDateFMT(fksj, 2)){
				message.setMsg("付款时间格式有误");
				return message;
			}
			String fkfsString = jObject.getString("fkfs");		//付款方式
			if(StringUtils.isEmpty(fkfsString)){
				message.setMsg("付款方式不能为空");
				return message;
			}
			int fkfs = 0;
			try {
				fkfs = jObject.getIntValue("fkfs");
				if(fkfs != 1 && fkfs != 2){
					message.setMsg("付款方式应为1或2");
					return message;
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("付款方式格式有误");
				return message;
			}
			String fkje = jObject.getString("fkje");	//付款金额
			if(StringUtils.isEmpty(fkje)){
				message.setMsg("付款金额不能为空");
				return message;
			}
			try {
				jObject.getBigDecimal("fkje");
			} catch (Exception e) {
				e.printStackTrace();
				message.setMsg("付款金额格式有误");
				return message;
			}
			String jsdbh = jObject.getString("jsdbh");		//结算单编号
			if(StringUtils.isEmpty(jsdbh)){
				message.setMsg("结算单编号不能为空");
				return message;
			}
			settlement = settlementService.findByCode(ptdm, jsdbh);
			if(settlement == null){
				message.setMsg("结算单编号有误");
				return message;
			}
			if(isGPO){
				if(!settlement.getGpoCode().equals(gpo.getCode())){
					message.setMsg("供应商编码和结算单中供应商编码不匹配");
					return message;
				}
			}else{
				if(!settlement.getVendorCode().equals(vendor.getCode())){
					message.setMsg("供应商编码和结算单中供应商编码不匹配");
					return message;
				}
			}
			String yqfkdbh = jObject.getString("yqfkdbh");	//药企付款单编号
			if(StringUtils.isEmpty(yqfkdbh)){
				message.setMsg("药企付款单编号不能为空");
				return message;
			}
			Payment payment = paymentService.getByInternalCode(ptdm, yqfkdbh);
			if(payment != null){
				message.setMsgcode(Message.MsgCode.err01.toString());
				message.setMsg("药企付款单编号已存在");
				JSONObject r_jo = new JSONObject();
				r_jo.put("fkdbh", payment.getCode());
				message.setData(r_jo);
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
		map.put("settlement", settlement);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

}
