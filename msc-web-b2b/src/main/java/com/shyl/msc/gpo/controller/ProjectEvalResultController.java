package com.shyl.msc.gpo.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.msc.b2b.plan.service.IDirectoryVendorService;
import com.shyl.msc.enmu.ProjectStus;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/gpo/projectEvalResult")
public class ProjectEvalResultController extends BaseController {

	@Resource
	private IDirectoryVendorService	directoryVendorService;
	@Resource
	private IProjectService	projectService;
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping("")
	public String home(@CurrentUser User user) {
		return "gpo/projectEvalResult/list";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<DirectoryVendor> page(PageRequest pageable, @CurrentUser User user) {
		Map<String, Object> query = pageable.getQuery();
		String status1 = (String)query.get("t#status_L_NE");
		if(!StringUtils.isEmpty(status1)){
			query.put("t#status_L_NE", DirectoryVendor.Status.valueOf(status1));
		}
		String status2 = (String)query.get("t#status_L_EQ");
		if(!StringUtils.isEmpty(status2)){
			query.put("t#status_L_EQ", DirectoryVendor.Status.valueOf(status2));
		}
		DataGrid<DirectoryVendor> directoryVendors = directoryVendorService.query(user.getProjectCode(), pageable);
		
		return directoryVendors;
	}

	@RequestMapping(value = "/projectComb", method = RequestMethod.POST)
	@ResponseBody
	public List<Project> projectComb(PageRequest page, @CurrentUser User user) {
		page.getQuery().put("t#projectStus_S_NE", ProjectStus.create);
		return projectService.list(user.getProjectCode(), page);
	}
}
