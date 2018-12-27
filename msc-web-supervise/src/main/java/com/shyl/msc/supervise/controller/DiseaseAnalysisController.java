package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.entity.DiseaseAnalysisItem;
import com.shyl.msc.supervise.service.IDiseaseAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/diseaseAnalysis")
public class DiseaseAnalysisController extends BaseController {

	@Resource
	private IDiseaseAnalysisService diseaseAnalysisService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	

	@Override
	protected void init(WebDataBinder arg0) {
	}

	/**
	 * queryType 0全省，1全市，2全区 3 医院，4 科室，5医生
	 * @param model
	 * @param queryType
	 * @param jsonStr
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ?0:queryType);
		//如果是卫计委，过滤掉通行的地址
		if(user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			model.addAttribute("zoneName", regionCode.getName());
		} else if(user.getOrganization().getOrgType() == 1){
			if (StringUtils.isEmpty(jsonStr)) {
				jsonStr = "{queryType:" + 1 + "}";
				queryType = 1;
			}
			model.addAttribute("orgType",1);
			//	model.addAttribute("zoneName", regionCode.getName());
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
		return "/supervise/diseaseAnalysis/index";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			model.addAttribute("treePath", treePath);
		}
		model.addAttribute("searchType", 1);
		return "/supervise/diseaseAnalysis/search";
	}
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(ModelMap model,@CurrentUser User user) {
		return "/supervise/diseaseAnalysis/chart";
	}

	@RequestMapping(value="/page", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> query(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user) {
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
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
		if(query.get("a#month_S_GE")==null||query.get("a#month_S_GE")==""){
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", start);
			begindata=start;
		}else{
			begindata=(String)query.get("a#month_S_GE");
			enddata=(String)query.get("a#month_S_LE");
		}
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("c#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("c#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("c#treePath_S_RLK", treePath);
			}
		}
		DataGrid<Map<String,Object>> result = diseaseAnalysisService.queryDiseaseAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
		for (Map<String, Object> rel : result.getRows()) {
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
		}
		return result;
	}

	@RequestMapping(value="/export", method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp) {
		queryType = (queryType == null ?0:queryType);
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
		}
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
		if (user.getOrganization().getOrgType() != null) {
			RegulationOrg regulationOrg = null;
			RegionCode regionCode = null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			} else if (user.getOrganization().getOrgType() == 1) {
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(),
						user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				query.put("c#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("c#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("c#treePath_S_RLK", treePath);
			}
		}
		String[] headers = null;
		
		String[] beanNames = null;
		Map<String, Map<Object,Object>> map = new HashMap<>();
		Map<Object,Object> isOperation = new HashMap<>();
		isOperation.put(new BigDecimal(0), "否");
		isOperation.put(new BigDecimal(1), "是");
		map.put("ISOPERATION", isOperation);
		switch (queryType) {
			case 0:
				headers = new String[]{"DISEASECODE","DISEASENAME","ISOPERATION","SUM","DRUGNUM","TREATMENTTOTAL","TREATMENTRATE"};
				beanNames =new String[]{"疾病编码","疾病名称","是否手术","药品费用","品规数","诊疗人次","诊疗率"};
				break;
			case 1:
				headers = new String[]{"HOSPITALNAME","DISEASECODE","DISEASENAME","ISOPERATION","SUM","DRUGNUM","TREATMENTTOTAL","TREATMENTRATE"};
				beanNames =new String[]{"医院","疾病编码","疾病名称","是否手术","药品费用","品规数","诊疗人次","诊疗率"};
				break;
			default:
				return;
		}

		ExcelUtil util = new ExcelUtil(beanNames, headers, null, map);
		List<Map<String,Object>> list = diseaseAnalysisService.queryDiseaseAnalysisByAll(user.getProjectCode()+"_SUP",page, queryType);
		try {
			Workbook workbook = util.doExportXLS(list, "单病种统计", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=diseaseAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		} catch (IOException e) {
		}
	}
	
	
	@RequestMapping(value="/product", method=RequestMethod.GET)
	public String product(ModelMap model, String jsonStr) {
		model.addAttribute("jsonStr", jsonStr);
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/diseaseAnalysis/product";
	}
	
	
	@RequestMapping(value="/product", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<DiseaseAnalysisItem> product(PageRequest page,@CurrentUser User user) {
		return diseaseAnalysisService.groupProductBy(user.getProjectCode()+"_SUP",page);
	}
	
	
	@RequestMapping(value="/disease", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, String>> disease(PageRequest page,@CurrentUser User user) {
		return diseaseAnalysisService.groupDiseaseBy(user.getProjectCode()+"_SUP",page);
	}
}
