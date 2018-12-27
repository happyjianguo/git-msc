package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
import com.shyl.msc.supervise.service.IMedicineHospitalService;
import com.shyl.sys.entity.User;

@RequestMapping("/supervise/baseDrugTotal")
@Controller
public class BaseDrugTotalController extends BaseController {
	
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
				model.addAttribute("jsonStr", URLEncoder.encode(jsonStr,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		model.addAttribute("queryType", queryType);
		return "/supervise/baseDrugTotal/index";
	}

	@RequestMapping(value="/page", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> query(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user) {
		queryType = (queryType == null ?0:queryType);
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
		return medicineHospitalService.countBaseDrugBy(user.getProjectCode()+"_SUP",page,queryType);
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String seach(ModelMap model,@CurrentUser User user) {
		if(user.getOrganization().getOrgType()!=null){
			RegulationOrg regulationOrg=null;
			RegionCode regionCode=null;
			if (user.getOrganization().getOrgType() == 3) {
				regulationOrg = regulationOrgService.findByCode(user.getProjectCode(),user.getOrganization().getOrgCode());
				regionCode = regionCodeService.getById(user.getProjectCode(), regulationOrg.getRegionCode());
			}else if(user.getOrganization().getOrgType() == 1){
				Hospital hospital = hospitalService.findByCode(user.getProjectCode(), user.getOrganization().getOrgCode());
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
		}else {
			return null;
		}
		model.addAttribute("searchType", 1);
		model.addAttribute("orgType", user.getOrganization().getOrgType());
		return "/supervise/baseDrugTotal/search";
	}

	@RequestMapping(value="/export", method=RequestMethod.GET)
	public void export(PageRequest page, Integer queryType, Integer orderType, @CurrentUser User user, HttpServletResponse resp) {
		queryType = (queryType == null ?0:queryType);
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
			case 0:
				headers = new String[]{"BASEDRUGNUM","NUM","JYZB"};
				beanNames =new String[]{"基药总数","药品总数","基药占比"};
				break;
			case 1:
				headers = new String[]{"COUNTYNAME","BASEDRUGNUM","NUM","JYZB"};
				beanNames =new String[]{"区域","基药总数","药品总数","基药占比"};
				break;
			case 2:
				headers = new String[]{"HOSPITALNAME","BASEDRUGNUM","NUM","JYZB"};
				beanNames =new String[]{"医院名称","基药总数","药品总数","基药占比"};
				break;
			default:
				return;
		}

		ExcelUtil util = new ExcelUtil(beanNames, headers);
		page.setPageSize(1000);
		DataGrid<Map<String,Object>> list = medicineHospitalService.countBaseDrugBy(user.getProjectCode()+"_SUP",page,queryType);
		try {
			Workbook workbook = util.doExportXLS(list.getRows(), "基药品规", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=baseDrugTotal.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		} catch (IOException e) {
		}
	}
}
