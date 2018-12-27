package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.supervise.entity.ClinicDiagnosis;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;
import com.shyl.msc.supervise.entity.HisDiagnosis;
import com.shyl.msc.supervise.entity.HisRegItem;
import com.shyl.msc.supervise.service.IClinicDiagnosisService;
import com.shyl.msc.supervise.service.IClinicRecipeItemService;
import com.shyl.msc.supervise.service.IClinicRecipeService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/clinicRecipe")
public class ClinicRecipeController extends BaseController {

	@Resource
	private IClinicRecipeService clinicRecipeService;
	@Resource
	private IClinicRecipeItemService clinicRecipeItemService;
	@Resource
	private IClinicDiagnosisService clinicDiagnosisService;
	@Resource
	private IProductService productService;

	@Override
	protected void init(WebDataBinder arg0) {

	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/clinicRecipe/index";
	}

	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer sumType, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		page.setSort("cdate,patientId");
		page.setOrder("desc,asc");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		Date day = c.getTime();
		String month = new SimpleDateFormat("yyyy-MM").format(day);
		String start = month + "-01";
		// String start = new SimpleDateFormat("yyyy-MM").format(new
		// Date())+"-01";
		String date = month + "-30";
		String begindata = "";
		String enddata = "";
		if (query.get("month_S_GE") != null || query.get("month_S_LE") != null) {
			query.put("cdate_D_GE", query.get("month_S_GE")+"-01");
			query.put("cdate_D_LE", query.get("month_S_LE")+"-31");
			query.remove("month_S_GE");
			query.remove("month_S_LE");
			begindata = (String) query.get("month_S_GE");
			enddata = (String) query.get("month_S_LE");
		} else if (query.get("cdate_D_GE") == null || query.get("cdate_D_LE") == "") {
			query.put("cdate_D_GE", start);
			query.put("cdate_D_LE", date);
			begindata = month;
			enddata=month;
		} else {
			begindata = ((String) query.get("cdate_D_GE")).substring(0, 7);
			enddata = ((String) query.get("cdate_D_LE")).substring(0, 7);
		}
		Integer clinicType = null;
		String hospitalCode = (String) query.get("a#hospitalCode_S_EQ");
		DataGrid<Map<String, Object>> result = null;
		if (!StringUtils.isEmpty(hospitalCode)) {
			System.out.println("连接分库数据源：sup_" + hospitalCode);
			result = clinicRecipeService.queryRecipeAndReg("sup_" + hospitalCode, page, sumType);
		}
		if (null != result) {
			for (Map<String, Object> rel : result.getRows()) {
				rel.put("begindata", begindata);
				rel.put("enddata", enddata);
			}
		}
		return result;
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public void export(PageRequest page, Integer sumType, @CurrentUser User user, HttpServletResponse resp) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		if (query.get("cdate_D_GE") == null || query.get("cdate_D_GE") == "") {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			Date d = cal.getTime();
			String cdate = new SimpleDateFormat("yyyy-MM").format(d);
			String startDate = cdate + "-01";
			String endDate = cdate + "-31";
			query.put("cdate_D_GE", startDate);
			query.put("cdate_D_LE", endDate);
		}

		String[] headers = new String[] { "病人姓名", "类型", "门诊/出院日期", "金额", "品规数", "药品费用", "医疗费用" };

		String[] beanNames = new String[] { "PATIENNAME", "TYPE", "CDATE", "SUM", "DRUGNUM", "DRUGSUM", "OTHERSUM" };
		ExcelUtil util = new ExcelUtil(headers, beanNames);
		String hospitalCode = (String) query.get("a#hospitalCode_S_EQ");
		List<Map<String, Object>> list = null;
		if (!StringUtils.isEmpty(hospitalCode)) {
			System.out.println("连接分库数据源：sup_" + hospitalCode);
			list = clinicRecipeService.queryRecipeAndRegAll("sup_" + hospitalCode, page, sumType);
		}
		try {
			Workbook workbook = util.doExportXLS(list, "病人详情", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=PatientDetail.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);
			out.flush();
			workbook.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, String rpSno, @CurrentUser User user) {
		model.addAttribute("outSno", outSno);
		model.addAttribute("rpSno", rpSno);
		model.addAttribute("hospitalCode", hospitalCode);
		return "/supervise/clinicRecipe/detail";
	}

	@RequestMapping(value = "/recipeItem", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> recipeItem(PageRequest page, @CurrentUser User user) {
		Map<String, Object> q = page.getQuery();
		DataGrid<Map<String, Object>> data = null;
		String hospitalCode = (String) q.get("hospitalCode_S_EQ");
		if (!StringUtils.isEmpty(hospitalCode)) {
			System.out.println("连接分库数据源：sup_" + hospitalCode);
			data = clinicRecipeItemService.queryByPage("sup_" + hospitalCode, page);
		}
		return data;
	}

	@RequestMapping(value = "/diagnosis", method = RequestMethod.POST)
	@ResponseBody
	public List<ClinicDiagnosis> diagnosis(PageRequest page, @CurrentUser User user) {
		Map<String, Object> q = page.getQuery();
		List<ClinicDiagnosis> result = null;
		String hospitalCode = (String) q.get("hospitalCode_S_EQ");
		System.out.println("连接分库数据源：sup_" + hospitalCode);
		result = clinicDiagnosisService.list("sup_" + hospitalCode, page);
		return result;
	}

}
