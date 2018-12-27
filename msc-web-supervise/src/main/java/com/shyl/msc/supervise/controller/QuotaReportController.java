package com.shyl.msc.supervise.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.RegionCode;
import com.shyl.msc.set.entity.RegulationOrg;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IRegionCodeService;
import com.shyl.msc.set.service.IRegulationOrgService;
import com.shyl.msc.supervise.service.IClinicAnalysisService;
import com.shyl.msc.supervise.service.IHisAnalysisService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/quotaReport")
public class QuotaReportController extends BaseController {
	
	@Resource
	public IHisAnalysisService hisAnalysisService;
	@Resource
	public IClinicAnalysisService clinicAnalysisService;
	@Resource
	private IRegulationOrgService regulationOrgService;
	@Resource
	private IRegionCodeService regionCodeService;
	@Resource
	private IHospitalService hospitalService;

	@Override
	protected void init(WebDataBinder arg0) {
	}
	
	@RequestMapping("/index")
	public String index() {
		return "/supervise/quotaReport/index";
	}

	@RequestMapping(value="/groupHisByCounty",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> groupHisByCounty(PageRequest page, @CurrentUser User user) {
		setQuery(page, user);
		return hisAnalysisService.groupMonthByCountyCode(user.getProjectCode()+"_SUP",page);
	}

	@RequestMapping(value="/groupHisByHospital",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> groupHisByHospital(PageRequest page, @CurrentUser User user) {

		setQuery(page, user);
		return hisAnalysisService.groupMonthByHospital(user.getProjectCode()+"_SUP",page);
	}

	@RequestMapping(value="/groupClinicByCounty",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> groupClinicByCounty(PageRequest page, @CurrentUser User user) {
		setQuery(page, user);
		return clinicAnalysisService.groupMonthByCountyCode(user.getProjectCode()+"_SUP",page);
	}

	@RequestMapping(value="/groupClinicByHospital",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> groupClinicByHospital(PageRequest page, @CurrentUser User user) {
		setQuery(page, user);
		return clinicAnalysisService.groupMonthByHospital(user.getProjectCode()+"_SUP",page);
	}
	
	/**
	 * 设置QUERY值
	 * @param page
	 * @param user
	 */
	private void setQuery(PageRequest page, User user) {
		if (true) {
			return;
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
				page.getQuery().put("a#hospitalCode_S_EQ", hospital.getCode());
			}
			String treePath = regionCode.getTreePath();
			if (treePath == null) {
				treePath += regionCode.getId().toString();
			} else {
				treePath += "," + regionCode.getId();
			}
			String treePath0 = (String) page.getQuery().get("z#treePath_S_RLK");
			if (treePath0 == null || treePath0.indexOf(treePath) < 0) {
				page.getQuery().put("z#treePath_S_RLK", treePath);
			}
		}
	}
}
