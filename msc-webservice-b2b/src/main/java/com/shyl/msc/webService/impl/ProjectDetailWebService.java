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
import com.shyl.common.util.DateUtil;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.plan.service.IHospitalPlanDetailService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.common.CommonProperties;
import com.shyl.msc.dm.entity.Directory;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.msc.webService.IProjectDetailWebService;
import com.shyl.sys.dto.Message;

/**
 * 
 * 
 * @author a_Q
 *
 */
@WebService(serviceName="projectDetailWebService",portName="projectDetailPort", targetNamespace="http://webservice.msc.shyl.com/")
public class ProjectDetailWebService extends BaseWebService implements IProjectDetailWebService{

	@Resource
	private ICompanyService companyService;
	@Resource
	private IProjectService projectService;
	@Resource
	private IProjectDetailService projectDetailService;
	@Resource
	private IHospitalPlanService hospitalPlanService;
	@Resource
	private IHospitalPlanDetailService hospitalPlanDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@WebMethod(action="get")
	public String get(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.inoutbound_get, sign, dataType, data);
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

	private Message getMethod(JSONObject jObject, Boolean isCode) {
		Message message = new Message();
		message.setSuccess(false);
		try {
			String ptdm = jObject.getString("ptdm");	//平台代码
			String ny  = jObject.getString("ny");//年月
			String xmdh = jObject.getString("xmdh");
			List<ProjectDetail> list = new ArrayList<ProjectDetail>();
			if(isCode){
				list = projectDetailService.listByProjectCode(ptdm, xmdh);
			}else{
				list = projectDetailService.listByMonth(ptdm, ny);
			}
			JSONArray array = new JSONArray();
			for(ProjectDetail projectDetail:list){
				JSONObject jo = new JSONObject();
				Directory directory = projectDetail.getDirectory();
				jo.put("xmdh", String.valueOf(projectDetail.getProject().getId()));//项目代号
				jo.put("xmmxdh", String.valueOf(projectDetail.getId()));//项目明细代号
				jo.put("lxypmlid", directory.getId());//遴选药品目录id
				jo.put("tym", directory.getGenericName());//通用名
				jo.put("tjjx", directory.getRcDosageFormName());//推荐剂型
				jo.put("jx", directory.getDosageFormName());//剂型
				jo.put("gg", directory.getModel());//规格
				jo.put("sccj", directory.getProducerNames());//生产厂家
				jo.put("zlcc", directory.getQualityLevel());//质量层次
				jo.put("zxsydw", directory.getMinUnit());//最小使用单位
				jo.put("bz", directory.getNote());//备注
				array.add(jo);
			}
			message.setSuccess(true);
			message.setMsg("成功返回");
			message.setData(array);
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
			
			String ny  = jObject.getString("ny");//年月
			String xmdh = jObject.getString("xmdh");//项目代号
			if(StringUtils.isEmpty(xmdh)){
				isCode = false;
				if(StringUtils.isEmpty(ny )){
					message.setMsg("年月或项目代号不能为空");
					return message;
				}
				if(!DateUtil.checkDateFMT(ny, 3)){
					message.setMsg("年月格式有误");
					return message;
				}
			}else{
				Project project = projectService.getByCode(ptdm, xmdh);
				if(project == null){
					message.setMsg("项目代号有误");
					return message;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setMsg("服务器程序出错");
			return message;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("isGPO", isGPO);
		map.put("company", company);
		map.put("isCode", isCode);
		message.setData(map);
		message.setSuccess(true);
		return message;
	}
}
