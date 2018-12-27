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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.plan.service.IHospitalPlanDetailService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.webService.IHospitalPlanWebService;
import com.shyl.sys.dto.Message;


@WebService(serviceName="hospitalPlanWebService",portName="hospitalPlanPort", targetNamespace="http://webservice.msc.shyl.com/")
public class HospitalPlanWebService extends BaseWebService implements IHospitalPlanWebService {
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalPlanService hospitalPlanService;
	@Resource
	private IHospitalPlanDetailService hospitalPlanDetailService;
	@Resource
	private IProjectDetailService projectDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.hospitalPlan_get, sign, dataType, data);
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
			//第三步验证签名
			message = checkSign(company.getIocode(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第四步执行主逻辑
			message = getMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message getMethod(JSONObject jObject) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			long xmmxdh_l = jObject.getLongValue("xmmxdh");
			JSONArray jo_arr_r = hospitalPlanService.getToGPO(ptdm, xmmxdh_l);
			
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(jo_arr_r);
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
			String xmmxdh = jObject.getString("xmmxdh");	//项目明细代号
			if(StringUtils.isEmpty(xmmxdh)){
				message.setMsg("项目明细代号不能为空");
				return message;
			}
			try {
				long xmmxdh_l = jObject.getLongValue("xmmxdh");
				ProjectDetail projectDetail = projectDetailService.getById(ptdm, xmmxdh_l);
				if(projectDetail == null){
					message.setMsg("项目明细代号有误");
					return message;
				}
			} catch (Exception e) {
				message.setMsg("项目明细代号格式有误");
				return message;
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


}
