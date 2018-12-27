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

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.container.page.Page;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.webService.IGoodsPriceWebService;
import com.shyl.sys.dto.Message;

/**
 * 药品价格实现
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="goodsPriceWebService",portName="goodsPricePort", targetNamespace="http://webservice.msc.shyl.com/")
public class GoodsPriceWebService extends BaseWebService implements IGoodsPriceWebService {
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private IProductPriceService productPriceService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IProductService productService;
	/**
	 * 医院取药品编码
	 */
	@Override
	@WebMethod(action="getToHis")
	@WebResult(name="getResult")
	public String getToHis(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.goodsPrice_getToHis, sign, dataType, data);
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
			Boolean isCode = (Boolean) map.get("isCode");
			//第三部验证签名
			message = checkSign(hospital.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四部执行主逻辑
			message = getToHisMethod(converData, hospital, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	private Message getToHisMethod(JSONObject jObject, Hospital hospital, Boolean isCode ) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			String ypbm = jObject.getString("ypbm");
			String nowTime = DateUtil.getToday19();
			JSONObject data_js = new JSONObject();
			JSONArray arr = new JSONArray();
			int i=0;
			List<GoodsPrice> goodsPrices = new ArrayList<>();
			if(isCode){
				PageRequest page = new PageRequest();
				Map<String, Object> query = new HashMap<>();
				query.put("productCode_S_EQ", ypbm);
				query.put("hospitalCode_S_EQ", hospital.getCode());
				page.setQuery(query);
				goodsPrices = goodsPriceService.list(ptdm, page);
			}else{
				goodsPrices = goodsPriceService.listByHospital(ptdm, hospital.getCode(), scgxsj);
			}
			for (GoodsPrice price : goodsPrices) {
				Company company = companyService.findByCode(ptdm, price.getVendorCode(), "isVendor=1");
				
				JSONObject jo_price = new JSONObject();
				jo_price.put("sxh", Integer.toString(i+1));			
				jo_price.put("ypbm", price.getProductCode());  //药品编码					
				jo_price.put("gysbm", price.getVendorCode());	//供应商编码		
				jo_price.put("gysmc", company.getFullName());	//供应商名称
				jo_price.put("zbj", price.getBiddingPrice());	//中标价
				jo_price.put("cjj", price.getFinalPrice());	//成交价
				jo_price.put("sxrq", price.getEffectDate());	//生效日期
				jo_price.put("yxqqsrq", price.getBeginDate());	//有效期起始日期
				jo_price.put("yxqjzrq", price.getOutDate());	//有效期截止日期
				jo_price.put("sfjy", price.getIsDisabled());	//是否禁用
				arr.add(jo_price);
				i++;
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			data_js.put("bcgxsj", nowTime);
			data_js.put("jls", goodsPrices.size());
			data_js.put("mx", arr);
			message.setData(data_js);
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
		Hospital hospital = new Hospital();
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
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			String ypbm = jObject.getString("ypbm");//药品编码
			if(StringUtils.isEmpty(yybm)){
				message.setMsg("医院编码不能为空");
				return message;
			}
			if(!StringUtils.isEmpty(yybm)){
				hospital = hospitalService.findByCode(ptdm, yybm);
				if(hospital == null){
					message.setMsg("医院编码有误");
					return message;
				}
			}
			if(StringUtils.isEmpty(ypbm)){
				isCode = false;
				if(StringUtils.isEmpty(scgxsj)){
					message.setMsg("上次更新时间或药品编码不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scgxsj,2)){
					message.setMsg("上次更新时间格式错误");
					return message;
				}
			}else{
				Product product = productService.getByCode(ptdm, ypbm);
				if(product == null){
					message.setMsg("药品编码有误");
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
}
