package com.shyl.msc.home.controller;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Notice;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.INoticeService;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;
/**
 * 首页Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/home")
public class HomeController extends BaseController {

	@Resource
	private IHospitalService hospitalService;
	
	@Resource
	private INoticeService noticeService;
	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private  IFileManagementService fileManagementService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(@CurrentUser User user){
		if(user.getOrganization().getOrgType() != null &&user.getOrganization().getOrgType()== 9)
			return "sys/user/sql";
		if(user.getOrganization().getOrgId() != null && user.getOrganization().getOrgType()!= null ){
			if(user.getOrganization().getOrgType()== 1)
				return "home/homeForM";
			if(user.getOrganization().getOrgType()== 2)
				return "home/homeForG";
			if(user.getOrganization().getOrgType()== 4)
				return "home/homeForGC";
		}
		return "home/homeForM";
	}
	/**
	 * 系统消息
	 * @return
	 */
	@RequestMapping("/msg")
	public String msg(){
		return "home/msg";
	}
	/**
	 * 公告
	 * @return
	 */
	@RequestMapping("/notice")
	public String notice(Model model, @CurrentUser User user){
		PageRequest pageable = new PageRequest();
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#status_I_EQ", 1);
		pageable.setQuery(query);
		pageable.setSort(new Sort(Direction.DESC,"publishDate"));
		List<Notice> list =  noticeService.list(user.getProjectCode(), pageable);
		for (Notice notice : list) {
			List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
			if(l == null || l.size() == 0){
				notice.setFileManagement(null);
			}
		}
		while(list.size()>5){
			list.remove(5);
		}
		model.addAttribute("noticeList", list);
		return "home/notice";
	}
	/**
	 * 公告详情
	 * @return
	 */
	@RequestMapping("/notice/sub")
	public String noticeSub(Long id,Model model, @CurrentUser User user){
		System.out.println("id="+id);
		Notice notice = noticeService.getById(user.getProjectCode(), id);
		List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
		for (FileManagement fileManagement : l) {
			notice.getFileManagement().add(fileManagement);
		}
		if(notice.getFileManagement().size() == 0){
			notice.setFileManagement(null);
		}
		model.addAttribute("notice", notice) ;
		return "home/noticeSub";
	}
	
	/**
	 * 收货
	 * @return
	 */
	@RequestMapping("/inout")
	public String inout(){
		return "home/inout";
	}
	
	/**
	 * 订单追踪 医院
	 * @return
	 */
	@RequestMapping("/orderH")
	public String orderH(){
		return "home/orderH";
	}
	
	/**
	 * 订单追踪 gpo
	 * @return
	 */
	@RequestMapping("/orderG")
	public String orderG(){
		return "home/orderG";
	}
	/**
	 * 合同执行情况
	 * @return
	 */
	@RequestMapping(value="/contractH", method = RequestMethod.GET)
	public String contractH(){
		System.out.println("in===");
		return "home/contractH";
	}
	
	/**
	 * 合同执行情况
	 * @return
	 */
	@RequestMapping(value="/contractM", method = RequestMethod.GET)
	public String contractM(){
		return "home/contractM";
	}
	
	/**
	 * 图表 医院
	 * @return
	 */
	@RequestMapping("/chart")
	public String chart(Model model,@CurrentUser User user){
		return "home/chart";
	}
	
	/**
	 * 图表 gpo
	 * @return
	 */
	@RequestMapping("/chartG")
	public String chartG(Model model,@CurrentUser User user){
		return "home/chartG";
	}
	
	/**
	 * 监管者 基本信息统计
	 * @return
	 */
	@RequestMapping("/monitorTotal")
	public String monitorTotal(Model model,@CurrentUser User user){
		Map<String , Object> productMap = productService.count(user.getProjectCode());
		Map<String, Object> hospitalMap = hospitalService.count(user.getProjectCode());
		Map<String, Object> producerMap = companyService.count(user.getProjectCode(), "isProducer=1");
		Map<String, Object> vendorMap = companyService.count(user.getProjectCode(), "isVendor=1");
		
		model.addAttribute("productCount", productMap.get("COUNT"));
		model.addAttribute("hospitalCount", hospitalMap.get("COUNT"));
		model.addAttribute("producerCount", producerMap.get("COUNT"));
		model.addAttribute("vendorCount", vendorMap.get("COUNT"));
		return "home/monitorTotal";
	}
	/**
	 * 收货
	 * @return
	 */
	@RequestMapping(value="/abnormalOrder", method = RequestMethod.GET)
	public String abnormalOrder(){
		return "home/abnormalOrder";
	}
	
	
	/**
	 * 违约情况图表
	 * @return
	 */
	@RequestMapping(value="/contractChart", method = RequestMethod.GET)
	public String contractChart(Model model,@CurrentUser User user){
		Calendar cal=Calendar.getInstance(); 
		int year = cal.get(Calendar.YEAR);
		model.addAttribute("year", year);
		return "home/abnormalChart";
	}
	
	/**
	 * 交易情况图表
	 * @return
	 */
	@RequestMapping("/tradeChart")
	public String tradeChart(Model model,@CurrentUser User user){
		Calendar cal=Calendar.getInstance(); 
		int year = cal.get(Calendar.YEAR);
		model.addAttribute("year", year);
		return "home/tradeChart";
	}
	
	/**
	 * index
	 * @return
	 */
	@RequestMapping("/index")
	public String index(@CurrentUser User user,Model model){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "B2B_HOMETITLE");
		if(attributeItem != null){
			model.addAttribute("hometitle", attributeItem.getField3());
		}
		AttributeItem logo = attributeItemService.queryByAttrAndItemNo("", "LOGININFO", "LOGO");
		if(logo != null){
			model.addAttribute("logo", logo.getField3());
		}
		return "index";
	}
	/**
	 * center
	 * @return
	 */
	@RequestMapping("/center")
	public String center(@CurrentUser User user,Model model){
		return "center";
	}
	
	/*
	 * 医院药品收入比(图表)
	 */
	@RequestMapping("/hisDrugRatio")
	public String hisDrugChart(Model model,@CurrentUser User user){
		return "home/hisDrugChart";
	}
	
	/*
	 * 医疗机构采购金额
	 */
	@RequestMapping("/hospitaltradejbgoods")
	public String hospitaltradejbgoods(Model model,@CurrentUser User user){
		return "home/hospitaltradejbgoods";
	}
	
	/*
	 * 基药金额比例分析(图表)
	 */
	@RequestMapping("/BaseDrugProportion")
	public String BaseDrugProportion(Model model,@CurrentUser User user){
		return "home/BaseDrugProportion";
	}
}
