package com.shyl.msc.gpo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor;
import com.shyl.msc.b2b.plan.entity.DirectoryVendor.Status;
import com.shyl.msc.b2b.plan.service.IDirectoryVendorService;
import com.shyl.msc.dm.entity.DirectoryPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IDirectoryPriceService;
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.ProjectStus;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.CompanyCert;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectDetail;
import com.shyl.msc.set.entity.ProjectExpert;
import com.shyl.msc.set.service.ICompanyCertService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectExpertService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/gpo/projectEval")
public class ProjectEvalController extends BaseController {

	@Resource
	private IProjectService	projectService;
	@Resource
	private IProductService	productService;
	@Resource
	private IDirectoryPriceService	directoryPriceService;
	@Resource
	private IProjectDetailService	projectDetailService;
	@Resource
	private IDrugService	drugService;
	@Resource
	private IDirectoryVendorService	directoryVendorService;
	@Resource
	private IProjectExpertService	projectExpertService;
	@Resource
	private ICompanyCertService	companyCertService;
	@Resource
	private ICompanyService	companyService;
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping("")
	public String home(@CurrentUser User user) {
		return "gpo/projectEval/list";
	}
	
	@RequestMapping(value = "/projectComb", method = RequestMethod.POST)
	@ResponseBody
	public List<Project> projectComb(PageRequest page, @CurrentUser User user) {
		page.getQuery().put("t#projectStus_S_EQ", ProjectStus.eval);
		return projectService.list(user.getProjectCode(), page);
	}

	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ProjectDetail> page(PageRequest page, @CurrentUser User user) {
		DataGrid<ProjectDetail> data = projectDetailService.query(user.getProjectCode(), page);
		for (int i = 0; i < data.getRows().size(); i++) {
			ProjectDetail pd = data.getRows().get(i);
			PageRequest pageable = new PageRequest();
			pageable.getQuery().put("t#directory.id_L_EQ", pd.getDirectory().getId());
			pageable.setSort(new Sort(Direction.ASC,"price"));
			List<DirectoryPrice> l = directoryPriceService.list(user.getProjectCode(),pageable);
			if(l != null && l.size()>0){
				DirectoryPrice dp = l.get(0);
				pd.setSegmentStr(dp.getAreaName());
				pd.setSegmentBD(dp.getPrice());
			}
		}
		return data;
	}
	

	@RequestMapping(value="/pageMX",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<DirectoryVendor> pageMX(PageRequest page,@CurrentUser User user) {
		page.getQuery().put("t#status_L_NE", Status.undeclare);
		page.setSort(new Sort(Direction.ASC,"price"));
		DataGrid<DirectoryVendor> data = directoryVendorService.query(user.getProjectCode(), page);
		return data;
	}
	
	@RequestMapping(value = "/productComb")
	@ResponseBody
	public DataGrid<Product> productComb(String productName,String producerName,PageRequest page, @CurrentUser User user) {
		System.out.println("productName="+productName);
		page.getQuery().put("t#name_S_LK", productName);
		page.getQuery().put("t#producerName_S_LK", producerName);
		DataGrid<Product> data = productService.query(user.getProjectCode(),page);
		if(data.getTotal() == 0){
			page.getQuery().put("t#name_S_LK", "");
			page.getQuery().put("t#producerName_S_LK", "");
			data = productService.query(user.getProjectCode(),page);
		}
		return data;
	}
	
	@RequestMapping("/expert")
	public String expert(Long projectId,Model model) {
		model.addAttribute("projectId", projectId);
		return "gpo/projectEval/expert";
	}
	
	@RequestMapping(value="/expertList")
	@ResponseBody
	public List<ProjectExpert> expertList(PageRequest page,@CurrentUser User user) {
		List<ProjectExpert> data = projectExpertService.list(user.getProjectCode(), page);
		System.out.println("data===="+data.size());
		return data;
	}
	
	@RequestMapping(value = "/directoryPrice",method = RequestMethod.GET)
	public String directoryPrice(Long directoryId,Model model){
		model.addAttribute("directoryId", directoryId);
		return "gpo/projectEval/directoryPrice";
	}

	@RequestMapping(value = "/companyCert",method = RequestMethod.GET)
	public String companyCert(String vendorCode,Model model,@CurrentUser User user){
		PageRequest page = new PageRequest();
		page.getQuery().put("t#code_S_EQ", vendorCode);
		Company company = companyService.getByKey(user.getProjectCode(),page);
		model.addAttribute("company", company);
		return "gpo/projectEval/companyCert";
	}
	@RequestMapping(value = "/companyCertList", method = RequestMethod.POST)
	@ResponseBody
	public List<CompanyCert> companyCertList(PageRequest page, @CurrentUser User user) {
		List<CompanyCert> list = companyCertService.list(user.getProjectCode(), page);
		return list;
	}
	
	
	/**
	 * 定标
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/dochoose", method = RequestMethod.POST)
	@ResponseBody
	public Message dochoose(Long id ,Long productId,String status,BigDecimal score, @CurrentUser User user){
		Message message = new Message();
		System.out.println("id = "+id);
		System.out.println("status = "+status);
		try{
			directoryVendorService.dochoose(user.getProjectCode(),id,productId,status,score);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
}
