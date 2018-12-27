package com.shyl.msc.set.controller;

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
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.VendorSender;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IVendorSenderService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 *  供应商 指定 配送商Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/vendorSender")
public class VendorSenderController extends BaseController {

	@Resource
	private IVendorSenderService vendorSenderService;
	@Resource
	private ICompanyService companyService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/vendorSender/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Company> page(PageRequest pageable,@CurrentUser User user){
		DataGrid<Company> page = new DataGrid<Company>();
		if(user.getOrganization().getOrgType() == 2 || user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 5 || user.getOrganization().getOrgType() == 9){
			Map<String, Object> query = new HashMap<String, Object>();
			query.put("t#isVendor_I_EQ", 1);
			query.put("t#isDisabled_I_EQ", 0);
			
			if(user.getOrganization().getOrgType() == 2){
				query.put("t#id_L_EQ", user.getOrganization().getOrgId());
			}
			System.out.println("query="+query);
			pageable.setQuery(query);
			page = companyService.query(user.getProjectCode(), pageable);
		}
		
		return page;
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Company> mxpage(String vendorCode,PageRequest pageable,@CurrentUser User user){
		DataGrid<Company> page = new DataGrid<Company>();
		Map<String, Object> query = pageable.getQuery();
		//query.put("t#id_L_EQ", user.getOrganization().getId());
		query.put("t#isSender_I_EQ", 1);
		query.put("t#isDisabled_I_EQ", 0);
		pageable.setQuery(query);
		page = companyService.query(user.getProjectCode(), pageable);
		
		for (Company company : page.getRows()) {
			VendorSender gs =  vendorSenderService.findByKey(user.getProjectCode(), vendorCode,company.getCode());
			if(gs != null){
				//借用 wbcode 栏位标注 是否已选择
				company.setWbcode("1");
			}
		}
		return page;
	}
	/**
	 * 全部查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<VendorSender> list(PageRequest pageable,@CurrentUser User user){
		if(user.getOrganization().getOrgCode() == null){
			return null;
		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#vendorCode_S_EQ", user.getOrganization().getOrgCode());
		pageable.setQuery(query);
		List<VendorSender>  list = vendorSenderService.list(user.getProjectCode(), pageable);
		return list;
	}
	
	/**
	 * 新增
	 * @param productid
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(VendorSender vendorSender,@CurrentUser User user){
		Message message = new Message();
		try{
			VendorSender gs = vendorSenderService.findByKey(user.getProjectCode(), vendorSender.getVendorCode(), vendorSender.getSenderCode());
			if(gs == null){
				Company vendor = companyService.findByCode(user.getProjectCode(),vendorSender.getVendorCode(), "isVendor=1");
				if(vendor != null){
					vendorSender.setVendorCode(vendor.getCode());
					vendorSender.setVendorName(vendor.getFullName());
				}
				Company sender = companyService.findByCode(user.getProjectCode(),vendorSender.getSenderCode(), "isSender=1");
				if(sender != null){
					vendorSender.setSenderCode(sender.getCode());
					vendorSender.setSenderName(sender.getFullName());
				}
				vendorSenderService.save(user.getProjectCode(), vendorSender);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(VendorSender vendorSender,@CurrentUser User user){
		Message message = new Message();
		try{
			VendorSender gs = vendorSenderService.findByKey(user.getProjectCode(), vendorSender.getVendorCode(), vendorSender.getSenderCode());
			if(gs != null){
				vendorSenderService.delete(user.getProjectCode(), gs);
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}

}
