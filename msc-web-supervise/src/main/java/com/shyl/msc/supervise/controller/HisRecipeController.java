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
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.supervise.entity.HisDiagnosis;
import com.shyl.msc.supervise.entity.HisRegItem;
import com.shyl.msc.supervise.service.IClinicDiagnosisService;
import com.shyl.msc.supervise.service.IClinicRecipeItemService;
import com.shyl.msc.supervise.service.IClinicRecipeService;
import com.shyl.msc.supervise.service.IHisDiagnosisService;
import com.shyl.msc.supervise.service.IHisRegItemService;
import com.shyl.sys.entity.User;

/**
 * 
 * 
 * 住院处方查询
 * 
 * @author CHENJIN
 *
 */
@Controller
@RequestMapping("/supervise/hisRecipe")
public class HisRecipeController extends BaseController {
	@Resource
	private IClinicRecipeService clinicRecipeService;
	@Resource
	private IClinicRecipeItemService clinicRecipeItemService;
	@Resource
	private IClinicDiagnosisService clinicDiagnosisService;
	@Resource
	private IHisRegItemService hisRegItemService;
	@Resource
	private IHisDiagnosisService hisDiagnosisService;
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "/supervise/hisReg/index";
	}

	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer sumType, @CurrentUser User user) {
		/** sumType为1时只查询住院信息 **/
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		page.setSort("cdate,patientId");
		page.setOrder("desc,asc");
		String begindata = "";
		String enddata="";
		if (query.get("cdate_D_GE") == null || query.get("cdate_D_GE") == "") {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			Date d = cal.getTime();
			String cdate = new SimpleDateFormat("yyyy-MM").format(d);
			String startDate = cdate + "-01";
			String endDate = cdate + "-31";
			query.put("cdate_D_GE", startDate);
			query.put("cdate_D_LE", endDate);
			begindata=cdate;
		}else{
			begindata=((String)query.get("cdate_D_GE")).substring(0, 7);
			enddata=((String)query.get("cdate_D_LE")).substring(0, 7);
		}
		String hospitalCode = (String)query.get("a#hospitalCode_S_EQ");
		DataGrid<Map<String, Object>> result=null;
		if(!StringUtils.isEmpty(hospitalCode)){
			System.out.println("连接分库数据源：sup_"+hospitalCode);
			sumType = 1;
			result = clinicRecipeService.queryRecipeAndReg("sup_"+hospitalCode,page, sumType);
		}
		if(null!=result){
			for (Map<String, Object> rel : result.getRows()) {
				rel.put("begindata", begindata);
				rel.put("enddata", enddata);
			}
		}
		return result;
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public void export(PageRequest page, Integer sumType, @CurrentUser User user,HttpServletResponse resp) {
		/** sumType为1时只查询住院信息 **/
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
		sumType = 1;
		
		String[] headers =new String[] { "病人姓名", "类型", "出院日期","金额", "品规数","药品费用", "医疗费用"};

		String[] beanNames = new String[] { "PATIENNAME", "TYPE", "CDATE","SUM","DRUGNUM","DRUGSUM","OTHERSUM" };
		ExcelUtil util = new ExcelUtil(headers, beanNames);
		String hospitalCode = (String)query.get("a#hospitalCode_S_EQ");
		List<Map<String, Object>> list=null;
		if(!StringUtils.isEmpty(hospitalCode)){
			System.out.println("连接分库数据源：sup_"+hospitalCode);
			list = clinicRecipeService.queryRecipeAndRegAll("sup_"+hospitalCode,page, sumType);
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
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String inSno, @CurrentUser User user) {
		model.addAttribute("inSno",inSno);
		model.addAttribute("hospitalCode",hospitalCode);
		return "/supervise/hisReg/detail";
	}

	@RequestMapping(value="/regItem", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> regItem(PageRequest page, @CurrentUser User user) {
		Map<String, Object> q = page.getQuery();
		DataGrid<Map<String,Object>> data = null;
		String hospitalCode = (String)q.get("hospitalCode_S_EQ");
		System.out.println("连接分库数据源：sup_"+hospitalCode);
		data = hisRegItemService.queryByPage("sup_"+hospitalCode,page);
		return data;
	}
	
	@RequestMapping(value="/diagnosis", method=RequestMethod.POST)
	@ResponseBody
	public List<HisDiagnosis> diagnosis(PageRequest page, @CurrentUser User user) {
		Map<String, Object> q = page.getQuery();
		List<HisDiagnosis> data = null;
		String hospitalCode = (String)q.get("hospitalCode_S_EQ");
		System.out.println("连接分库数据源：sup_"+hospitalCode);
		data = hisDiagnosisService.list("sup_"+hospitalCode,page);
		return data;
	}

}
