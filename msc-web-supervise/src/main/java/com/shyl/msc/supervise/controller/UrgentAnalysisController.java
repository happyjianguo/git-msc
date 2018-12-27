package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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

/*
 * 急诊药品使用分析
 */
@Controller
@RequestMapping("/supervise/urgentAnalysis")
public class UrgentAnalysisController extends BaseController {

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
		} else if (user.getOrganization().getOrgType() == 1) {
			if (StringUtils.isEmpty(jsonStr)) {
				jsonStr = "{queryType:" + 4 + "}";
				queryType = 4;
			}
		} else {
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
		return "/supervise/urgentAnalysis/index";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String seach(ModelMap model, @CurrentUser User user) {
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
			model.addAttribute("orgType", user.getOrganization().getOrgType());
		} else {
			return null;
		}
		model.addAttribute("searchType", 1);
		return "/supervise/urgentAnalysis/search";
	}

	@RequestMapping(value = "/clinicRecipe", method = RequestMethod.GET)
	public String index(ModelMap model, String jsonStr, @CurrentUser User user) {
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return "/supervise/urgentAnalysis/clinicRecipe";
	}

	@RequestMapping(value = "page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page, Integer queryType, Integer orderType,
			@CurrentUser User user, ModelMap model) {
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
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
				query.put("z#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("treePath_S_RLK", treePath);
			}
		} else {
			return null;
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
		query.put("clinicType_L_EQ", new Long(1));
		if (queryType > 4) {
			query.remove("treePath_S_RLK");
		}
		DataGrid<Map<String, Object>> result = clinicAnalysisService.queryClinicMedicineByPage(user.getProjectCode()+"_SUP",page, queryType);
		double visitorNum = 0;
		DecimalFormat df = new DecimalFormat("######0.00");
		for (Map<String, Object> rel : result.getRows()) {
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
			if (rel.get("VISITORNUM") != null) {
				visitorNum = Double.parseDouble(rel.get("VISITORNUM").toString());
				if (rel.get("DRUGNUM") != null) {
					rel.put("AVGDRUGNUM", df.format(Double.parseDouble(rel.get("DRUGNUM").toString()) / visitorNum));
				}
				if (rel.get("DRUGSUM") != null) {
					rel.put("AVGDRUGSUM", df.format(Double.parseDouble(rel.get("DRUGSUM").toString()) / visitorNum));
				}
				if (rel.get("REGRECIPENUM") != null) {
					rel.put("RECIPERATE",
							df.format(Double.parseDouble(rel.get("REGRECIPENUM").toString()) * 100 / visitorNum));
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(ModelMap model, @CurrentUser User user) {
		return "/supervise/urgentAnalysis/chart";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(ModelMap model, String hospitalCode, String outSno, @CurrentUser User user) {
		model.addAttribute("outSno", outSno);
		model.addAttribute("hospitalCode", hospitalCode);
		return "/supervise/urgentAnalysis/detail";
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,
			HttpServletResponse resp) {
		queryType = (queryType == null ? 0 : queryType);
		Integer orgType = user.getOrganization().getOrgType();
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		Date day = c.getTime();
		String start = new SimpleDateFormat("yyyy-MM").format(day);
		if (query.get("a#month_S_GE") == null || query.get("a#month_S_GE") == "") {
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
				query.put("z#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) query.get("treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				query.put("treePath_S_RLK", treePath);
			}
		}
		if (queryType > 4) {
			query.remove("treePath_S_RLK");
		}
		query.put("a#clinicType_L_EQ", new Long(1));
		String[] headers = null;
		String[] beanNames = null;

		switch (queryType) {
		case 0:
			headers = new String[] { "ZONENAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM", "SUM",
					"DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "区域", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		case 1:
			headers = new String[] { "PROVINCENAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM",
					"SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "省名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		case 2:
			headers = new String[] { "CITYNAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM", "SUM",
					"DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "市名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		case 3:
			headers = new String[] { "COUNTYNAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM",
					"SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "区名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		case 4:
			headers = new String[] { "HOSPITALNAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM",
					"SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "医院名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		case 5:
			headers = new String[] { "HOSPITALNAME", "DEPARTNAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM",
					"AVGDRUGSUM", "SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "医院名称", "科室名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入",
					"药品收入比(%)", "处方人次", "处方率(%)" };
			break;
		case 6:
			headers = new String[] { "HOSPITALNAME", "DEPARTNAME", "DOCTORNAME", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM",
					"DRUGSUM", "AVGDRUGSUM", "SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "医院名称", "科室名称", "医生名称", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入",
					"药品收入比(%)", "处方人次", "处方率(%)" };
			break;
		default:
			headers = new String[] { "HOSPITALCODE", "VISITORNUM", "DRUGNUM", "AVGDRUGNUM", "DRUGSUM", "AVGDRUGSUM",
					"SUM", "DRUGRATIO", "REGRECIPENUM", "RECIPERATE" };
			beanNames = new String[] { "医院编码", "总就诊人次", "药品品规数", "每就诊人均品规数", "药品费用", "每就诊人次药品费用", "总收入", "药品收入比(%)",
					"处方人次", "处方率(%)" };
			break;
		}
		ExcelUtil util = new ExcelUtil(beanNames, headers);
		query.put("clinicType_L_EQ", new Long(1));
		if (queryType > 4) {
			query.remove("treePath_S_RLK");
		}
		/*
		 * if(queryType>=5){ page.setPageSize(1000); }
		 */
		List<Map<String, Object>> result = clinicAnalysisService.queryClinicMedicineByPageAll(user.getProjectCode()+"_SUP",page, queryType);
		/*
		 * List<Map<String, Object>> list = new ArrayList<>();
		 * list.addAll(result.getRows()); if
		 * (result.getTotal()>page.getPageSize()) { int pageNo =
		 * (int)result.getTotal()/page.getPageSize(); if
		 * (result.getTotal()%page.getPageSize()!=0) { pageNo++; } for (int
		 * i=2;i<=pageNo;i++) { page.setPage(i); result =
		 * clinicAnalysisService.queryClinicMedicineByPage(page, queryType); if
		 * (result.getRows().size() >0) { list.addAll(result.getRows()); } } }
		 */
		double visitorNum = 0;
		DecimalFormat df = new DecimalFormat("######0.00");
		String zoneName = "";
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(), "publicUser",
				"ZONENAME");
		if (attributeItem != null) {
			zoneName = attributeItem.getField3();
		}
		for (Map<String, Object> rel : result) {
			if (rel.get("VISITORNUM") != null) {
				rel.put("ZONENAME", zoneName);
				visitorNum = Double.parseDouble(rel.get("VISITORNUM").toString());
				if (rel.get("DRUGNUM") != null) {
					rel.put("AVGDRUGNUM", df.format(Double.parseDouble(rel.get("DRUGNUM").toString()) / visitorNum));
				}
				if (rel.get("DRUGSUM") != null && rel.get("SUM") != null) {
					rel.put("AVGDRUGSUM", df.format(Double.parseDouble(rel.get("DRUGSUM").toString()) / visitorNum));
					rel.put("DRUGRATIO", df.format(Double.parseDouble(rel.get("DRUGSUM").toString())
							/ Double.parseDouble(rel.get("SUM").toString())));
				}
				if (rel.get("REGRECIPENUM") != null) {
					rel.put("RECIPERATE",
							df.format(Double.parseDouble(rel.get("REGRECIPENUM").toString()) * 100 / visitorNum));
				}
			}
		}
		try {
			Workbook workbook = util.doExportXLS(result, "急诊药品使用分析", false, true);
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
