package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IDrugAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/drugAnalysis")
public class HospitalDrugAnalysisController extends BaseController {

	@Resource
	private IDrugAnalysisService drugAnalysisService;
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
		queryType = (queryType == null || queryType <3 || queryType > 5 ?3:queryType);
		model.addAttribute("jsonStr", jsonStr);
		model.addAttribute("queryType", queryType);
		return "/hospital/drugAnalysis/index";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String search(ModelMap model, @CurrentUser User user) {
		model.addAttribute("searchType", 2);
		return "/hospital/drugAnalysis/search";
	}

	@RequestMapping(value="/page", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> query(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user) {
		queryType = (queryType == null || queryType <3 || queryType > 5 ?3:queryType);
		Integer orgType = user.getOrganization().getOrgType();
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		if(orgType == 1) {
			query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			return new DataGrid<>();
		}

		return drugAnalysisService.queryDrugAnalysisByPage(user.getProjectCode()+"_SUP",page, queryType);
	}

	@RequestMapping(value="/export", method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp) {
		queryType = (queryType == null || queryType <3 || queryType > 5 ?3:queryType);
		Integer orgType = user.getOrganization().getOrgType();
		Map<String, Object> query = page.getQuery();
		if (orderType == null || orderType== 0) {
			page.setSort(new Sort(new Order(Direction.DESC, "sum")));
		} else {
			page.setSort(new Sort(new Order(Direction.DESC, "num")));
		}
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		if(orgType == 1) {
			query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		} else {
			return;
		}
		
		String[] headers = null;
		
		String[] beanNames = null;
		switch (0) {
			case 0:
				headers = new String[]{"PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","DDD","DDDS","DDDC"};
				beanNames =new String[]{"药品名称","规格","包装","剂型","生产厂家","金额","DDD","DDDS","DDDC"};
				break;
			case 3:
				headers = new String[]{"HOSPITALNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","DDD","DDDS","DDDC"};
				beanNames =new String[]{"医院名称","药品名称","规格","包装","剂型","生产厂家","金额","DDD","DDDS","DDDC"};
				break;
			case 4:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","DDD","DDDS","DDDC"};
				beanNames =new String[]{"医院名称","部门名称","药品名称","规格","包装","剂型","生产厂家","金额","DDD","DDDS","DDDC"};
				break;
			case 5:
				headers = new String[]{"HOSPITALNAME","DEPARTNAME","DOCTORNAME","PRODUCTNAME","PACKDESC","MODEL","DOSAGEFORMNAME","PRODUCERNAME","SUM","DDD","DDDS","DDDC"};
				beanNames =new String[]{"医院名称","部门名称","医生名称","药品名称","规格","包装","剂型","生产厂家","金额","DDD","DDDS","DDDC"};
				break;
		}

		ExcelUtil util = new ExcelUtil(headers, beanNames);
		List<Map<String,Object>> list = drugAnalysisService.queryDrugAnalysisByAll(user.getProjectCode()+"_SUP",page, queryType);
		try {
			Workbook workbook = util.doExportXLS(list, "单品种统计", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		} catch (IOException e) {
		}
	}

}
