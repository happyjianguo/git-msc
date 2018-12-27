package com.shyl.msc.b2b.report.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.entity.User;

/**
 * 企业查询
 * 
 *
 */
@Controller(value="reportCompayController")
@RequestMapping("/b2b/report/company")
public class CompanyController extends BaseController {

	@Resource
	private ICompanyService companyService;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}
	
	@RequestMapping("")
	public String list(){
		return "b2b/report/company/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> list(PageRequest pageable, String companyType, String name,@CurrentUser User user){
		System.out.println("------companyType-----"+companyType);
		System.out.println("------name-----"+name);
		DataGrid<Map<String, Object>> page = companyService.pageByType(user.getProjectCode(), pageable, name, companyType);
		return page;
	}

}
