package com.shyl.msc.supervise.controller;
/*
 *抗菌药处方数占比
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.service.IAttributeService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/absRecipeRatio")
public class AbsRecipeRatioController extends BaseController{
	
	@Resource
	private IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
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
		//如果是卫计委，过滤掉通行的地址
		if(user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			String treePath = regionCode.getTreePath();
			if(treePath == null){
				treePath += regionCode.getId().toString();
			}else{
				treePath += "," + regionCode.getId();
			}
			model.addAttribute("treePath", treePath);
			int minType = 0;
			if (regionCode.getParentId() != null) {
				minType = regionCode.getTreePath().split(",").length+1;
			}
			model.addAttribute("zoneName", regionCode.getName());
			if (queryType<minType) {
				queryType = minType;
			}
		}else if(user.getOrganization().getOrgType() == 1){
			if(StringUtils.isEmpty(jsonStr)){
				jsonStr = "{queryType:"+4+"}";
				queryType=4;
				model.addAttribute("orgType", user.getOrganization().getOrgType());
			}	
		} else {
			return null;
		}
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		model.addAttribute("queryType", queryType);
		System.out.println("queryType"+queryType);
		return "/supervise/absRecipeRatio/index";
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model){
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "drugSum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "drugSum")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);
		String begindata = "";
		String enddata="";
		if(query.get("c#month_S_GE")==null||query.get("c#month_S_GE")==""){
			query.put("c#month_S_GE", start);
			query.put("c#month_S_LE", start);
			begindata=start;
		}else{
			begindata=(String)query.get("c#month_S_GE");
			enddata=(String)query.get("c#month_S_LE");
		}
		if(user.getOrganization().getOrgType()!=null){
			RegulationOrg regulationOrg=null;
			RegionCode regionCode=null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			}else if(user.getOrganization().getOrgType() == 1){
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("z#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		}else {
			return null;
		}
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		Integer clinicType=null;
		if(query.get("clinicType_L_EQ")!=null&&query.get("clinicType_L_EQ")!=""){
			clinicType = Integer.parseInt(query.get("clinicType_L_EQ").toString());
		}
		Integer absType=null;
		if(query.get("absType_L_EQ")!=null&&query.get("absType_L_EQ")!=""){
			absType = Integer.parseInt(query.get("absType_L_EQ").toString());
			query.remove("absType_L_EQ");
		}
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryClinicAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String,Object> rel : result.getRows()) {
			rel.put("CLINICTYPE", clinicType);
			rel.put("ABSTYPE", absType);
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
			if(rel.get("ABSRECIPENUM")==null&&rel.get("RECIPENUM")==null){
				result=new DataGrid<Map<String,Object>>();
			}
		}
		return result;
	}
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/absRecipeRatio/chart";
	}
	
	@RequestMapping(value="/clinicRecipe", method=RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/absRecipeRatio/clinicRecipe";
	}
	
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno",outSno);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/supervise/absRecipeRatio/detail";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page,Integer queryType,Integer orderType, @CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		if(query.get("c#month_S_GE")==null||query.get("c#month_S_GE")==""){
			query.put("c#month_S_GE", start);
			query.put("c#month_S_LE", start);
		}
		if(user.getOrganization().getOrgType()!=null){
			RegulationOrg regulationOrg=null;
			RegionCode regionCode=null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			}else if(user.getOrganization().getOrgType() == 1){
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("z#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("z#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("z#treePath_S_RLK", treePath);
			}
		} 
		if (queryType>4) {
			query.remove("z#treePath_S_RLK");
		}
		Integer clinicType= null;
		Integer absType= null;
		if(query.get("clinicType_L_EQ")!=null&&query.get("clinicType_L_EQ")!=""){
			clinicType = Integer.parseInt(query.get("clinicType_L_EQ").toString());
		}
		if(query.get("absType_L_EQ")!=null&&query.get("absType_L_EQ")!=""){
			absType = Integer.parseInt(query.get("absType_L_EQ").toString());
			query.remove("absType_L_EQ");
		}
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
		String[] headers = null;
		String[] beanNames = null;
		
		switch(queryType){
			case 0:
				headers = new String[]{"ZONENAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"区域","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 1:
				headers = new String[]{"PROVINCENAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"省名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 2:
				headers = new String[]{"CITYNAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"市名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 3:
				headers = new String[]{"COUNTYNAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"区名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"医院名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"医院名称","科室名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			case 6:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","CLINICTYPE","ABSTYPE","ABSRECIPENUM","RECIPENUM","ABSRECIPENUMRATIO"};
				beanNames = new String[]{"医院名称","科室名称","医生名称","门诊类别","抗菌药物类别","抗菌药物处方数","总处方数","抗菌药物处方数占比(%)"};
				break;
			default :
				return;
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		String clinicName = "";
		String absTypeName = "";
		double ABSRECIPENUM=0;
		double RECIPENUM = 0;
		DecimalFormat df=new DecimalFormat("######0.0"); 
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
			if(map.get("RECIPENUM")!=null){
				RECIPENUM = Double.parseDouble(map.get("RECIPENUM").toString());	
			}
				if(absType==null){
					absTypeName="全部";
					if(map.get("ABSRECIPENUM")!=null){
						ABSRECIPENUM=Double.parseDouble(map.get("ABSRECIPENUM").toString());
					}
				}else if(absType==0){
					absTypeName="限制级";
					if(map.get("ABSRECIPE1NUM")!=null){
						ABSRECIPENUM=Double.parseDouble(map.get("ABSRECIPE1NUM").toString());
					}				
				}else if(absType==1){
					absTypeName="非限制级";
					if(map.get("ABSRECIPE2NUM")!=null){
						ABSRECIPENUM=Double.parseDouble(map.get("ABSRECIPE2NUM").toString());
					}	
				}else{
					absTypeName="特殊级";
					if(map.get("ABSRECIPE3NUM")!=null){
						ABSRECIPENUM=Double.parseDouble(map.get("ABSRECIPE3NUM").toString());
					}
			}
			if(RECIPENUM!=0){
				map.put("ABSRECIPENUMRATIO", df.format(ABSRECIPENUM*100/RECIPENUM));
			}else{
				map.put("ABSRECIPENUMRATIO", 0);
			}
			map.put("CLINICTYPE", clinicName);
			map.put("ABSTYPE", absTypeName);
			map.put("ABSRECIPENUM", ABSRECIPENUM);
			
		}
		try{
			Workbook workbook = util.doExportXLS(list, "抗菌药物处方数占比", false, true);
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
	
}
