package com.shyl.msc.supervise.controller;

/**
 * 每出院人次药品费用分析
 */
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
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IHisAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/hisOutDrugAnalysis")
public class HisOutDrugAnalysisController extends BaseController {
	@Resource
	private IHisAnalysisService hisAnalysisService;

	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;

	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model, Integer queryType, String jsonStr, @CurrentUser User user) {
		queryType = (queryType == null ? 0 : queryType);
		// 如果是卫计委，过滤掉通行的地址
		if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
					user.getOrganization().getOrgCode());
			if (regulationOrg.getRegionCode() != null) {
				RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
				String treePath = regionCode.getTreePath();
				if (treePath == null) {
					treePath += regionCode.getId().toString();
				} else {
					treePath += "," + regionCode.getId();
				}
				model.addAttribute("treePath", treePath);
			}
			RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
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
			model.addAttribute("orgType",1);
		} else {
			return null;
		}
		if (!StringUtils.isEmpty(jsonStr)) {
			try {
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/hisOutDrugAnalysis/index";
	}

	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public String chart(ModelMap model, @CurrentUser User user) {
		return "/supervise/hisOutDrugAnalysis/chart";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer queryType, @CurrentUser User user) {
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
		if (queryType > 4) {
			query.remove("c#treePath_S_RLK");
		}
		DataGrid<Map<String, Object>> queryHisAnalysisByPage = hisAnalysisService.queryHisAnalysisByPage(user.getProjectCode()+"_SUP",page,
				queryType);
		for (Map<String, Object> rel : queryHisAnalysisByPage.getRows()) {
			rel.put("begindata", begindata);
			rel.put("enddata", enddata);
		}
		return queryHisAnalysisByPage;
	}

	// 导出
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,
			HttpServletResponse resp) {
		queryType = (queryType == null ? 0 : queryType);
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		Date day = c.getTime();
		String start = new SimpleDateFormat("yyyy-MM").format(day);
		if (query.get("a#month_S_GE") == null || query.get("c#month_S_GE") == "") {
			query.put("a#month_S_GE", start);
			query.put("a#month_S_LE", start);
		}
		if (user.getOrganization().getOrgType() == 3) {
			RegulationOrg regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),
					user.getOrganization().getOrgCode());
			if (regulationOrg.getRegionCode() != null) {
				RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
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
		} else if ((user.getOrganization().getOrgType() == 1)) {
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
			if (hospital.getRegionCode() != null) {
				RegionCode regionCode = regionCodeService.getById(user.getProjectCode(), hospital.getRegionCode());
				String treePath = regionCode.getTreePath();
				if (treePath == null) {
					treePath += regionCode.getId().toString();
				} else {
					treePath += "," + regionCode.getId();
				}
				query.put("c#hospitalCode_S_EQ", hospital.getCode());
				String treePath0 = (String) query.get("c#treePath_S_RLK");
				if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
					query.put("c#treePath_S_RLK", treePath);
				}

			}
		}
		if (queryType > 4) {
			query.remove("c#treePath_S_RLK");
		}

		String[] headers = null;

		String[] beanNames = null;
		switch (queryType) {
		case 0:
			beanNames = new String[] { "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 1:
			beanNames = new String[] { "PROVINCENAME", "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "省名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 2:
			beanNames = new String[] { "CITYNAME", "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "市名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 3:
			beanNames = new String[] { "COUNTYNAME", "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "区名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 4:
			beanNames = new String[] { "HOSPITALNAME", "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "医院名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 5:
			beanNames = new String[] { "HOSPITALNAME", "DEPARTNAME", "DRUGSUM", "OUTNUM", "AVGOUTDRUGSUM" };
			headers = new String[] { "医院名称", "科室名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		case 6:
			beanNames = new String[] { "HOSPITALNAME", "DEPARTNAME", "DOCTORNAME", "DRUGSUM", "OUTNUM",
					"AVGOUTDRUGSUM" };
			headers = new String[] { "医院名称", "科室名称", "医生名称", "出院病人药品总费用", "出院病人人次", "每出院人次药品费用" };
			break;
		default:
			return;
		}

		ExcelUtil util = new ExcelUtil(headers, beanNames);
		List<Map<String, Object>> list = hisAnalysisService.queryHisAnalysisByPageAll(user.getProjectCode()+"_SUP",page, queryType);
		try {
			Workbook workbook = util.doExportXLS(list, "每出院人次药品费用统计", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=HisAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);
			out.flush();
			workbook.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
