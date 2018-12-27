package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.shyl.msc.supervise.service.IMedicineHospitalService;
import com.shyl.sys.entity.User;

/**
 * 药品目录品规数
 * 
 * @author CHENJIN
 *
 */
@Controller
@RequestMapping("/supervise/drugCatalogDrugNum")
public class DrugCatalogDrugNumController extends BaseController {
	@Resource
	private IHisAnalysisService hisAnalysisService;
	@Resource
	private IMedicineHospitalService medicineHospitalService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;

	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping(value = "index", method = RequestMethod.GET)
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

		} else if (user.getOrganization().getOrgType() == 1) {
			if (StringUtils.isEmpty(jsonStr)) {
				jsonStr = "{queryType:" + 2 + "}";
				queryType = 2;
			}

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
		return "/supervise/drugCatalogDrugNum/index";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> query(PageRequest page, Integer queryType, Integer orderType,
			@CurrentUser User user, String countyName, String hospitalName, ModelMap model) {
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

		DataGrid<Map<String, Object>> data = medicineHospitalService.countDrugCatalogBy(user.getProjectCode()+"_SUP",page, queryType);
		List<Map<String, Object>> rows = data.getRows();
		for (Map<String, Object> map : rows) {
			if (null != countyName && !StringUtils.isEmpty(countyName)) {
				map.put("name", countyName);
			}
			if (null != hospitalName && !StringUtils.isEmpty(hospitalName)) {
				map.put("name", hospitalName);
			}

		}
		return data;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String seach(ModelMap model, @CurrentUser User user) {
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
		model.addAttribute("searchType", 1);
		return "/supervise/drugCatalogDrugNum/search";
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user,
			HttpServletResponse resp) {
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

		switch (queryType) {
		// String countyName, String hospitalName
		case 0:
			headers = new String[] { "NUM", "CHINADRUGNUM", "WESTDRUGNUM" };
			beanNames = new String[] { "药品目录品规数", "中成药品规数", "西药品规数" };
			break;
		case 1:
			headers = new String[] { "COUNTYNAME", "NUM", "CHINADRUGNUM", "WESTDRUGNUM" };
			beanNames = new String[] { "区域","药品目录品规数", "中成药品规数", "西药品规数" };
			break;
		case 2:
			headers = new String[] { "HOSPITALNAME", "NUM", "CHINADRUGNUM", "WESTDRUGNUM" };
			beanNames = new String[] { "医院名称", "药品目录品规数", "中成药品规数", "西药品规数" };
			break;
		default:
			return;
		}

		ExcelUtil util = new ExcelUtil(beanNames, headers);
		// ExcelUtil util = new ExcelUtil(headers, beanNames, null, map);
		page.setPageSize(1000);
		DataGrid<Map<String, Object>> list = medicineHospitalService.countDrugCatalogBy(user.getProjectCode()+"_SUP",page, queryType);

		try {
			Workbook workbook = util.doExportXLS(list.getRows(), "药品目录品规数", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=drugCatalogDrugNum.xls");
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
