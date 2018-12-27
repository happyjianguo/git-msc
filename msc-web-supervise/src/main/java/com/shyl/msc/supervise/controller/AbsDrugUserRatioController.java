package com.shyl.msc.supervise.controller;

/*
 * 抗菌药物占药品使用比例
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.sys.entity.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/supervise/absDrugUserRatio")
public class AbsDrugUserRatioController extends BaseController {

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

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ? 0 : queryType);
		// 如果是卫计委，过滤掉通行的地址
		if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
					user.getOrganization().getOrgCode());

			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			model.addAttribute("treePath", treePath);
			int minType = 0;
			if (regionCode.getParentId() != null) {
				minType = regionCode.getTreePath().split(",").length + 1;
			}
			model.addAttribute("zoneName", regionCode.getName());
			if (queryType < minType) {
				queryType = minType;
			}
		}else if(user.getOrganization().getOrgType() == 1){
			if(StringUtils.isEmpty(jsonStr)){
				jsonStr = "{queryType:"+4+"}";
				queryType=4;
				model.addAttribute("orgType", user.getOrganization().getOrgType());
			}	
		}else{
			return null;
		}
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		model.addAttribute("queryType", queryType);
		System.out.println("queryType" + queryType);
		return "/supervise/absDrugUserRatio/index";
	}

	@RequestMapping(value = "page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page, Integer queryType, Integer orderType, Integer countType,
			@CurrentUser User user, ModelMap model) {
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		Date day = c.getTime();
		String start = new SimpleDateFormat("yyyy-MM").format(day);
		String begindata = "";
		String enddata="";
		if (query.get("a#month_S_GE") == null || query.get("a#month_S_GE") == "") {
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", start);
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
		if (queryType > 4) {
			query.remove("b#treePath_S_RLK");
		}
		Integer absType = null;
		if (query.get("absType_L_EQ") != null) {
			if (query.get("absType_L_EQ") != "") {
				absType = Integer.parseInt(query.get("absType_L_EQ").toString());
			}
			query.remove("absType_L_EQ");
		}
		DataGrid<Map<String, Object>> result = clinicAnalysisService.queryAbsDrugUserByPage(user.getProjectCode()+"_SUP",page, queryType, countType);
		for (Map<String, Object> rel : result.getRows()) {
			if(queryType<5){
				rel.put("COUNTTYPE", countType);
			}
			rel.put("ABSTYPE", absType);
			if(rel.get("ABSDRUGSUM")==null&&rel.get("ABSTYPE1SUM")==null&&rel.get("ABSTYPE2SUM")==null&&rel.get("ABSTYPE3SUM")==null&&rel.get("DRUGSUM")==null&&rel.get("OTHERSUM")==null){
				result = new DataGrid<Map<String,Object>>();
			}
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
		}
		return result;
	}

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(ModelMap model, @CurrentUser User user) {
		return "/supervise/absDrugUserRatio/chart";
	}

	@RequestMapping(value = "/clinicRecipe", method = RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/absDrugUserRatio/clinicRecipe";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno", outSno);
		model.addAttribute("hospitalCode", hospitalCode);
		return "/supervise/absDrugUserRatio/detail";
	}

	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page,Integer queryType,Integer countType, @CurrentUser User user, HttpServletResponse resp){ 
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);  
		Date day =  c.getTime();  
		String start=  new SimpleDateFormat("yyyy-MM").format(day);  
		if(query.get("a#month_S_GE")==null||query.get("a#month_S_GE")==""){
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", start);
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
		}
		if (queryType>4) {
			query.remove("b#treePath_S_RLK");
		}
		
		String[] headers = null;
		String[] beanNames = null;
		
		switch(queryType){
			case 0:
				headers = new String[]{"ZONENAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"区域","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 1:
				headers = new String[]{"PROVINCENAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"省名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 2:
				headers = new String[]{"CITYNAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"市名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 3:
				headers = new String[]{"COUNTYNAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"区名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"医院名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"医院名称","科室名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			case 6:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","COUNTTYPE","ALLABSDRUGSUM","ALLDRUGSUM","ALLABSDRUGSUMRATIO"};
				beanNames = new String[]{"医院名称","科室名称","医生名称","统计类别","抗菌药物使用总金额","所有药品使用总金额","抗菌药物金额占比(%)"};
				break;
			default :
				return;
		}
		Integer absType= null;
		if(query.get("absType_L_EQ")!=null&&query.get("absType_L_EQ")!=""){
			absType = Integer.parseInt(query.get("absType_L_EQ").toString());
			query.remove("absType_L_EQ");
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		if(queryType>=5){
			page.setPageSize(1000);
		}
		DataGrid<Map<String,Object>> result = clinicAnalysisService.queryAbsDrugUserByPage(user.getProjectCode()+"_SUP",page, queryType,countType);
		List<Map<String, Object>> list = new ArrayList<>();
		list.addAll(result.getRows());
		if (result.getTotal()>page.getPageSize()) {
			int pageNo = (int)result.getTotal()/page.getPageSize();
			if (result.getTotal()%page.getPageSize()!=0) {
				pageNo++;
			}
			for (int i=2;i<=pageNo;i++) {
				page.setPage(i);
				result =  clinicAnalysisService.queryAbsDrugUserByPage(user.getProjectCode()+"_SUP",page, queryType,countType);
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
		DecimalFormat df=new DecimalFormat("######0.0"); 
		if(list.size()!=0){
			for (Map<String,Object> map : list) {
				double drugsum = 0;
				double ALLABSDRUGSUM = 0;
				map.put("ZONENAME", zoneName);
				if(queryType<5){
					if(countType==null){
						map.put("COUNTTYPE", "全部");
					}else if(countType==0){
						map.put("COUNTTYPE", "门诊");
					}else{
						map.put("COUNTTYPE", "住院");
					}
				}
				if(map.get("DRUGSUM")!=null){
					drugsum = Double.parseDouble(map.get("DRUGSUM").toString());
				}
				if(map.get("ABSDRUGSUM")!=null&&map.get("ABSTYPE1SUM")!=null&&map.get("ABSTYPE2SUM")!=null&&map.get("ABSTYPE3SUM")!=null){
						if(absType==null){
							ALLABSDRUGSUM = Double.parseDouble(map.get("ABSDRUGSUM").toString());
						}else if(absType==0){
							ALLABSDRUGSUM = Double.parseDouble(map.get("ABSTYPE1SUM").toString());
						}else if(absType==1){
							ALLABSDRUGSUM = Double.parseDouble(map.get("ABSTYPE2SUM").toString());
						}else{
							ALLABSDRUGSUM = Double.parseDouble(map.get("ABSTYPE3SUM").toString());
						}
					
				}
				map.put("ALLDRUGSUM", drugsum);
				map.put("ALLABSDRUGSUM", ALLABSDRUGSUM);
				map.put("ALLABSDRUGSUMRATIO", df.format(ALLABSDRUGSUM*100/drugsum));
			}
		}	
		try{
			Workbook workbook = util.doExportXLS(list, "抗菌药物占药品使用比例", false, true);
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
