package com.shyl.msc.supervise.controller;

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
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/hisDrugIncomeRatio")
public class HisDrugIncomeRatioController extends BaseController{
	
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
	
	@RequestMapping(value="", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType,String jsonStr, @CurrentUser User user){
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
		} else if(user.getOrganization().getOrgType() == 1){
			if(StringUtils.isEmpty(jsonStr)){
				jsonStr = "{queryType:"+4+"}";
				queryType=4;
			}	
		}else {
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
		return "/supervise/hisDrugIncomeRatio/index";
	}
	
	@RequestMapping(value="page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model){
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		/*if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "DRUGRATIO")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "DRUGRATIO")));
		}*/
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));  
		Date day1 =  c.getTime();  
		String date=  new SimpleDateFormat("yyyy-MM").format(day1); 
		String begindata = "";
		String enddata="";
		if(query.get("a#month_S_GE")==null||query.get("a#month_S_GE")==""){
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", date);
			begindata=start;
		}else{
			begindata=(String)query.get("a#month_S_GE");
			enddata=(String)query.get("a#month_S_LE");
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
				query.put("b#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("b#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("b#treePath_S_RLK", treePath);
			}
		}else {
			return null;
		}
		if (queryType>4) {
			query.remove("b#treePath_S_RLK");
		}
		
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryHisDrugInByPage(user.getProjectCode()+"_SUP",page, queryType,null);
		for (Map<String,Object> rel : result.getRows()) {
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
		}
		return result;
	}
	
	@RequestMapping(value="show",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> show(PageRequest page, Integer queryType,Integer orderType, @CurrentUser User user,ModelMap model){
		queryType = (queryType == null ?4:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "SUM")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "SUM")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
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
				query.put("b#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("b#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("b#treePath_S_RLK", treePath);
			}
		}else {
			return null;
		}
		page.setPageSize(10);
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryHisDrugInByPage(user.getProjectCode()+"_SUP",page, queryType,null);
		return result;
	}

	
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/hisDrugIncomeRatio/chart";
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp){
		queryType = (queryType == null ?0:queryType);
		Integer orgType = user.getOrganization().getOrgType();
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		/*if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "DRUGRATIO")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "DRUGRATIO")));
		}*/
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));  
		Date day1 =  c.getTime();  
		String date=  new SimpleDateFormat("yyyy-MM").format(day1); 
		if(query.get("a#month_S_GE")==null||query.get("a#month_S_GE")==""){
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", date);
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
				query.put("b#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("b#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("b#treePath_S_RLK", treePath);
			}
		} if (queryType>4) {
			query.remove("b#treePath_S_RLK");
		}
		String[] headers = null;
		String[] beanNames = null;
		
		switch(queryType){
			case 0:
				headers = new String[]{"ZONENAME","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"区域","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			case 1:
				headers = new String[]{"PROVINCENAME","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"省名称","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			case 2:
				headers = new String[]{"CITYNAME","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"市名称","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			case 3:
				headers = new String[]{"COUNTYNAME","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"区名称","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"医院名称","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","MONTH","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"医院名称","月份","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
			/*case 6:
				headers = new String[]{"DOCTORNAME","RECIPENUM","INJECTIONNUM","INJECTIONPROPORTION","VISITORNUM","AVGINTRAN","INTRANPROPORTION"};
				beanNames = new String[]{"医生名称","总处方数","注射剂处方数","注射剂使用处方比例","总就诊人数","静脉输液人次","静脉输液处方比例"};
				break;*/
			default :
				headers = new String[]{"HOSPITALCODE","OTHERSUM","DRUGSUM","SUM","OTHERRATIO","DRUGRATIO"};
				beanNames = new String[]{"医院编码","医疗总收入","药品总收入","医院总收入","药品与医疗总收入(%)","药品与总收入比(%)"};
				break;
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		DecimalFormat df=new DecimalFormat("######0.0"); 
		if(queryType>=5){
			page.setPageSize(1000);
		}
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryHisDrugInByPage(user.getProjectCode()+"_SUP",page, queryType,null);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(result.getRows());
		if (result.getTotal()>page.getPageSize()) {
			int pageNo = (int)result.getTotal()/page.getPageSize();
			if (result.getTotal()%page.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				page.setPage(i);
				result =  clinicAnalysisService.queryHisDrugInByPage(user.getProjectCode()+"_SUP",page, queryType,null);
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
			map.put("ZONENAME",zoneName);
			if(map.get("DRUGSUM")!=null&&map.get("OTHERSUM")!=null&&map.get("SUM")!=null){
				map.put("OTHERRATIO", df.format(Double.parseDouble(map.get("DRUGSUM").toString())*100/Double.parseDouble(map.get("OTHERSUM").toString())));
				map.put("DRUGRATIO", df.format(Double.parseDouble(map.get("DRUGSUM").toString())*100/Double.parseDouble(map.get("SUM").toString())));
			}
		}
		try{
			Workbook workbook = util.doExportXLS(list, "医院药品收入比", false, true);
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
