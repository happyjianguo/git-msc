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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.webService.IWarehouseWebService;
import com.shyl.sys.dto.Message;

/**
 * 配送点下载实现
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="warehouseWebService",portName="warehousePort", targetNamespace="http://webservice.msc.shyl.com/")
public class WarehouseWebService extends BaseWebService implements IWarehouseWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	/**
	 * 取配送点信息
	 */
	@Override
	@WebMethod(action="getToCom")
	@WebResult(name="getResult")
	public String getToCom(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.warehouse_getToCom, sign, dataType, data);
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
			Boolean isCode = (Boolean) map.get("isCode");
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData, isCode);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}
	
	/**
	 * 
	 * @param jObject
	 * @param isCode 
	 * @return
	 */
	private Message getMethod(JSONObject jObject, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			String psdbm = jObject.getString("psdbm");//配送点编码
			String nowTime = DateUtil.getToday19();
			//业务处理 -- 查询规则 修改日期大于等于scgxsj
			JSONObject data_js = new JSONObject();
			PageRequest pageable = new PageRequest();
			Map<String, Object> m = new HashMap<String, Object>();
			if(isCode){
				m.put("t#code_S_EQ", psdbm);
			}else{
				m.put("t#modifyDate_D_GE", scgxsj);
			}
			m.put("t#isReceive_I_EQ", 1);
			m.put("t#hospital.isDisabled_I_EQ",0);
			pageable.setQuery(m);
			List<Warehouse> warehouses = warehouseService.list(ptdm, pageable);
			JSONArray arr = new JSONArray();
			int i = 0;
			for (Warehouse warehouse:warehouses) {
				i++;
				JSONObject js = new JSONObject();
				js.put("sxh",i+"");														//顺序号
				js.put("psdbm", warehouse.getCode());									//配送点编码
				js.put("psdmc", warehouse.getName());									//配送点名称
				js.put("psddz", warehouse.getAddr() == null?"":warehouse.getAddr());	//配送点地址
				js.put("lxr", warehouse.getContact() == null?"":warehouse.getContact());//联系人
				js.put("lxdh", warehouse.getPhone() == null?"":warehouse.getPhone());	//联系电话
				if(warehouse.getHospital() != null){
					js.put("yybm", warehouse.getHospital().getCode());						//医院编码
				}else{
					js.put("yybm", "");						//医院编码
				}
				arr.add(js);
			}
			data_js.put("jls", warehouses.size());
			data_js.put("bcgxsj", nowTime);
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

	private Message checkData(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		Hospital hospital = new Hospital();
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
		
			String scgxsj = jObject.getString("scgxsj");//上次更新时间
			String psdbm = jObject.getString("psdbm");	//配送点编码
			if(StringUtils.isEmpty(psdbm)){
				isCode = false;
				if(StringUtils.isEmpty(scgxsj)){
					message.setMsg("上次更新时间或配送点编码不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(scgxsj,2)){
					message.setMsg("上次更新时间格式错误");
					return message;
				}
			}else{
				Warehouse warehouse = warehouseService.findByCode(ptdm, psdbm);
				if(warehouse == null){
					message.setMsg("配送点编码有误");
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
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
