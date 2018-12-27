package com.shyl.msc.webService.impl;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.webService.IProductDetailWebService;
import com.shyl.sys.dto.Message;

/**
 * 
 * 供应商药品供应关系
 * @author a_Q
 *
 */
@WebService(serviceName="productDetailWebService",portName="productDetailPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ProductDetailWebService extends BaseWebService implements IProductDetailWebService{

	@Resource
	private ICompanyService companyService;
	@Resource
	private IProductDetailService productDetailService;
	
	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.productDetail_get, sign, dataType, data);
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
			Company company = (Company) map.get("company");
			Boolean isGPO = (Boolean) map.get("isGPO");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, isGPO);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message getMethod(JSONObject jObject, Boolean isGPO) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String jgbm = jObject.getString("jgbm");    //机构编码
			String yybm = jObject.getString("yybm");    //机构编码
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			Integer page = jObject.getInteger("page");    //机构编码
			if (page == null) {
				page = 1;
			}
			PageRequest pageable = new PageRequest(page, 300);
			Map<String, Object> query = new HashMap<>();
			DataGrid<ProductDetail> data = null;
			if(isGPO){
				query.put("product.gpoCode_S_EQ", jgbm);
			}else{
				query.put("product.vendorCode_S_EQ", jgbm);
			}
			if (StringUtils.isNotBlank(yybm)) {
				query.put("hospitalCode_S_EQ",yybm);
			}
			if (StringUtils.isNotBlank(scgxsj)) {
				query.put("modifyDate_D_GE", scgxsj);
			}
			pageable.setQuery(query);
			data = productDetailService.page(ptdm, pageable);
			JSONArray array = new JSONArray();
			for(ProductDetail productDetail:data.getRows()){
				JSONObject jo = new JSONObject();
				jo.put("ypbm", productDetail.getProduct().getCode());//药品编码
				jo.put("gysbm", productDetail.getVendorCode());//供应商编码
				jo.put("yybm", productDetail.getHospitalCode());//医院编码
				jo.put("jg", productDetail.getPrice());//价格
				
				array.add(jo);
			}
			int pageTotal = (int)data.getTotal()/300;
			if (data.getTotal()%300 > 0) {
				pageTotal++;
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			JSONObject data_js = new JSONObject();
			data_js.put("page", data.getPageNum());
			data_js.put("pageTotal", pageTotal);
			data_js.put("jls", array.size());
			data_js.put("mx", array);
			message.setData(data_js);
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		return message;
	}

	private Message getCheckData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Boolean isGPO = true;
		Company company = null;
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
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
