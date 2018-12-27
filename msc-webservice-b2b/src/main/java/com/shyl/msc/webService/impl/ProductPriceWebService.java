package com.shyl.msc.webService.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IProductPriceWebService;
import com.shyl.sys.dto.Message;

/**
 * 药品价格实现
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="productPriceWebService",portName="productPricePort", targetNamespace="http://webservice.msc.shyl.com/")
public class ProductPriceWebService extends BaseWebService implements IProductPriceWebService {
	@Resource
	private IProductService productService;
	@Resource
	private IProductPriceService productPriceService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="send")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.productPrice_send, sign, dataType, data);
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
			String ptdm = converData.getString("ptdm");	//平台代码
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步新增报文信息
			message = savaDatagrame(ptdm, company.getCode(), company.getFullName(), data, dataType, DatagramType.productPrice_send);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第五步执行主逻辑
			message = sendMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject jObject) {
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
			productPriceService.saveProductPrice(list, ptdm);
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

	private Message checkData(JSONObject jObject) {
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
				message.setMsg("明细不能为空");
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
			}catch (Exception e) {
				message.setMsg("明细数据格式有误");
				return message;
			}
			for(JSONObject jo:list){
				int sxh = 1;
				String sxhString = jo.getString("sxh"); //顺序号
				if(StringUtils.isEmpty(sxhString)){
					message.setMsg("顺序号不能为空");
					return message;
				}
				try {
					sxh = jo.getIntValue("sxh");	
				} catch (Exception e) {
					message.setMsg("顺序号("+sxhString+")格式有误");
					return message;
				}
				String ypbm = jo.getString("ypbm");		//药品编码
				if(StringUtils.isEmpty(ypbm)){
					message.setMsg("第("+sxh+")笔：药品编码不能为空");
					return message;
				}
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("第("+sxh+")笔：药品编码有误");
					return message;
				}
				String gysbm = jo.getString("gysbm");//供应商编码
				if(StringUtils.isEmpty(gysbm)){
					message.setMsg("第("+sxh+")笔：供应商编码不能为空");
					return message;
				}
				Company vendor = companyService.findByCode(ptdm, gysbm, "isVendor=1");
				if(vendor == null){
					message.setMsg("第("+sxh+")笔：供应商编码有误");
					return message;
				}
				String yybm = jo.getString("yybm");		//医院编码
				Hospital hospital = null;
				if(!StringUtils.isEmpty(yybm)){
					hospital = hospitalService.findByCode(ptdm, yybm);
					if(hospital == null){
						message.setMsg("第("+sxh+")笔：医院编码有误");
						return message;
					}
				}
				String zbj = jo.getString("zbj");		//中标价
				if(StringUtils.isEmpty(zbj)){
					message.setMsg("第("+sxh+")笔：中标价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("zbj");//中标价
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：中标价格式有误");
					return message;
				}
				String cjj = jo.getString("cjj");		//成交价
				if(StringUtils.isEmpty(cjj)){
					message.setMsg("成交价不能为空");
					return message;
				}
				try {
					jo.getDoubleValue("cjj");//成交价
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：成交价格式有误");
					return message;
				}
				String sxrq = jo.getString("sxrq");		//生效日期
				if(StringUtils.isEmpty(sxrq)){
					message.setMsg("生效日期不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(sxrq, 1)){
					message.setMsg("第("+sxh+")笔：生效日期格式错误");
					return message;
				}
				
				String yxqq = jo.getString("yxqq");		//有效期起
				if(StringUtils.isEmpty(yxqq)){
					message.setMsg("有效期起不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxqq, 1)){
					message.setMsg("第("+sxh+")笔：有效期起日期格式错误");
					return message;
				}
				
				String yxqz = jo.getString("yxqz");		//有效期止
				if(StringUtils.isEmpty(yxqz)){
					message.setMsg("有效期止不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(yxqz, 1)){
					message.setMsg("第("+sxh+")笔：有效期止日期格式错误");
					return message;
				}
				String sfjyString = jo.getString("sfjy");//是否禁用
				if(StringUtils.isEmpty(sfjyString)){
					message.setMsg("是否禁用不能为空");
					return message;
				}
				try {
					int sfjy = jo.getIntValue("sfjy");//是否禁用
					if(sfjy != 0 && sfjy != 1){
						message.setMsg("是否禁用应为0或1");
						return message;
					}
				} catch (Exception e) {
					message.setMsg("第("+sxh+")笔：是否禁用格式有误");
					return message;
				}
				String tyjgsxfs = jo.getString("tyjgsxfs");//统一价格生效方式 
				if(hospital == null){
					if(StringUtils.isEmpty(tyjgsxfs)){
						message.setMsg("统一价格生效方式 不能为空");
						return message;
					}
					try {
						int tyjgsxfs_i = jo.getIntValue("tyjgsxfs");//统一价格生效方式 
						if(tyjgsxfs_i != 0 && tyjgsxfs_i != 1){
							message.setMsg("统一价格生效方式 应为0或1");
							return message;
						}
					} catch (Exception e) {
						message.setMsg("第("+sxh+")笔统一价格生效方式 格式有误");
						return message;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("company", company);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}

	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		// TODO Auto-generated method stub
		return null;
	}
}
