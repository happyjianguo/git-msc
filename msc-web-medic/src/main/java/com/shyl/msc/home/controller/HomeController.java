package com.shyl.msc.home.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

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
		return "home/homeForY";
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
		Sort sort = new Sort(Direction.DESC,"publishDate");
		PageRequest pageable = new PageRequest();
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#status_I_EQ", 1);
		pageable.setQuery(query);
		pageable.setSort(sort);
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
	
}
