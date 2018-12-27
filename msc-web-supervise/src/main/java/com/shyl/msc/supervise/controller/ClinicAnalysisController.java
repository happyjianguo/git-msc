package com.shyl.msc.supervise.controller;
/*
 * 每诊疗人次药品费用
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.entity.ClinicDiagnosis;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.msc.supervise.service.IClinicDiagnosisService;
import com.shyl.msc.supervise.service.IClinicRecipeItemService;
import com.shyl.msc.supervise.service.IClinicRecipeService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONObject;


@Controller
@RequestMapping("/supervise/clinicanalysis")
public class ClinicAnalysisController extends BaseController{
	
	@Resource
	private IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IClinicRecipeService clinicRecipeService;
	@Resource
	private IClinicRecipeItemService clinicRecipeItemService;
	@Resource
	private IClinicDiagnosisService clinicDiagnosisService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Override
	protected void init(WebDataBinder arg0) {	
	}
	
	@RequestMapping(value="" ,method=RequestMethod.GET)
	public String index(ModelMap model, Integer queryType,String jsonStr,@CurrentUser User user){
		queryType = (queryType == null ?0:queryType);
		// 如果是卫计委，过滤掉通行的地址
		if(user.getOrganization().getOrgType() == 3) {
			String[] info = this.getTreePath(user);
			int minType = info[0].split(",").length;
			model.addAttribute("zoneName", info[1]);
			if (queryType<minType) {
				queryType = minType;
			}
			model.addAttribute("treePath", info[0]);
		} else if(user.getOrganization().getOrgType() == 1){
			if(StringUtils.isEmpty(jsonStr)){
				queryType=4;
				model.addAttribute("orgType", user.getOrganization().getOrgType());
			}	
		}
		JSONObject obj = new JSONObject();
		if (jsonStr!=null) {
			obj = JSONObject.fromObject(jsonStr);
		}
		if (obj.size() == 0) {
			String month = DateUtil.getDateAfterMonths(new Date(), 1,"yyyy-MM");
			obj.put("query['c#month_S_GE']", month);
			obj.put("query['c#month_S_LE']", month);
			obj.put("queryType", queryType);
			model.addAttribute("isFirst", 1);
		}
		try {
			model.addAttribute("jsonStr", URLEncoder.encode(obj.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/clinicAnalysis/index";
	}
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/clinicAnalysis/chart";
	}
	
	@RequestMapping(value="/clinicRecipe", method=RequestMethod.GET)
	public String clinicRecipe(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/clinicAnalysis/clinicRecipe";
	}
	
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno",outSno);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/supervise/clinicAnalysis/detail";
	}
	
	@RequestMapping(value="/recipeItem", method=RequestMethod.POST)
	@ResponseBody
	public List<ClinicRecipeItem> recipeItem(PageRequest page, @CurrentUser User user) {
		return clinicRecipeItemService.list(user.getProjectCode(),page);
	}
	
	@RequestMapping(value="/diagnosis", method=RequestMethod.POST)
	@ResponseBody
	public List<ClinicDiagnosis> diagnosis(PageRequest page, @CurrentUser User user) {
		return clinicDiagnosisService.list(user.getProjectCode(),page);
	}
	
	@RequestMapping(value="/query", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> query(PageRequest page, Integer sumType, @CurrentUser User user) {
		return clinicRecipeService.queryRecipeAndReg(user.getProjectCode()+"_SUP",page, sumType);
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model){
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "AVGDRUGSUM")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "AVGDRUGSUM")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("z#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		Integer clinicType=null;
		if(query.get("clinicType_L_EQ")!=null&&query.get("clinicType_L_EQ")!=""){
			clinicType = Integer.parseInt(query.get("clinicType_L_EQ").toString());
		}	
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryClinicAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String,Object> rel : result.getRows()) {
			rel.put("CLINICTYPE", clinicType);
			if(rel.get("DRUGSUM")==null&&rel.get("VISITORNUM")==null){
				result=new DataGrid<Map<String,Object>>();
			}
		}
		return result;
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page,Integer queryType,Integer orderType, @CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
		Integer orgType = user.getOrganization().getOrgType();
		
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "AVGDRUGSUM")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "AVGDRUGSUM")));
		}
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		//如果是医院，删选医院数据
		if(user.getOrganization().getOrgType() == 1){
			query.put("z#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			String treePath = getTreePath(user)[0];

			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		String[] headers = null;
		String[] beanNames = null;
		
		switch(queryType){
			case 0:
				headers = new String[]{"ZONENAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"区域","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 1:
				headers = new String[]{"PROVINCENAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"省名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 2:
				headers = new String[]{"CITYNAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"市名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 3:
				headers = new String[]{"COUNTYNAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"区名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"医院名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"医院名称","科室名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			case 6:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","CLINICTYPE","DRUGSUM","VISITORNUM","AVGDRUGSUM"};
				beanNames = new String[]{"医院名称","科室名称","医生名称","门诊类别","药品收入汇总","就诊人数","每门诊人次药品费用"};
				break;
			default :
				return;
		}
		/*JSONObject obj = JSONObject.fromObject(jsonStr);
		String[] headers = (String[])JSONArray.fromObject(obj.getString("headers")).toArray(new String[0]);
		String[] beanNames = (String[])JSONArray.fromObject(obj.getString("beanNames")).toArray(new String[0]);*/
		Integer clinicType=null;
		if(query.get("clinicType_L_EQ")!=null&&query.get("clinicType_L_EQ")!=""){
			clinicType = Integer.parseInt(query.get("clinicType_L_EQ").toString());
		}
		String clinicName = "";
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		if(queryType>=5){
			page.setPageSize(1000);
		}
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryClinicAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(result.getRows());
		if (result.getTotal()>page.getPageSize()) {
			int pageNo = (int)result.getTotal()/page.getPageSize();
			if (result.getTotal()%page.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				page.setPage(i);
				result =  clinicAnalysisService.queryClinicAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
				if (result.getRows().size() >0) {
					list.addAll(result.getRows());
				}
			}
		}
		String zoneName ="";
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "ZONENAME");
		if(attributeItem != null){
			  zoneName = attributeItem.getField3();
		}
		for (Map<String,Object> map : list) {
			map.put("ZONENAME", zoneName);
			if(clinicType==null){
				clinicName="全部";
			}else if(clinicType==0){
				clinicName="门诊";
			}else{
				clinicName="急诊";
			}
			map.put("CLINICTYPE", clinicName);
		}
		try{
			Workbook workbook = util.doExportXLS(list, "每诊疗人次费用", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch(IOException e){
			
		}
		
	}
	
	private String[] getTreePath(User user) {
		RegulationOrg regulationOrg=null;
		RegionCode regionCode=null;
		if (user.getOrganization().getOrgType() == 3) {
			regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
			regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
		}else if(user.getOrganization().getOrgType() == 1){
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
		}
		String treePath = regionCode.getTreePath();
		if (treePath == null) {
			treePath += regionCode.getId().toString();
		} else {
			treePath += "," + regionCode.getId();
		}
		return new String[]{treePath, regionCode.getName()};
	}
}
