package com.shyl.msc.dm.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.shyl.msc.dm.entity.ProductRegister;
import com.shyl.msc.dm.entity.ProductRegister.OrgType;
import com.shyl.msc.dm.entity.ProductRegisterDetail;
import com.shyl.msc.dm.service.IProductRegisterDetailService;
import com.shyl.msc.dm.service.IProductRegisterService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Controller
@RequestMapping("/dm/productRegister")
public class ProductRegisterController extends BaseController {

	@Resource
	private IProductRegisterService productRegisterService;
	@Resource
	private IProductRegisterDetailService productRegisterDetailService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IMsgService msgService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping("")
	public String home(){
		return "dm/productRegister/list";
	}

	@RequestMapping(value="/add", method = RequestMethod.GET)
	public String add(@CurrentUser User user,Model model){
		model.addAttribute("user", user);
		return "dm/productRegister/add";
	}
	

	@RequestMapping(value="/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(@CurrentUser User user,String rows){
		Message message = new Message();
		try {
			productRegisterService.add(user.getProjectCode(), user, rows);
			message.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user){
		Message message = new Message();
		try {
			productRegisterService.delete(user.getProjectCode(), id);
			message.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductRegister> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1 || user.getOrganization().getOrgType() == 2){
			query.put("t#orgId_L_EQ", user.getOrganization().getOrgId());
		}
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", ProductRegister.Status.valueOf(status));
		}
		pageable.setSort(new Sort(Direction.DESC,"createDate"));
		DataGrid<ProductRegister> page =  productRegisterService.query(user.getProjectCode(), pageable);
		return page;
	}
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public List<ProductRegisterDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		List<ProductRegisterDetail> list = productRegisterDetailService.list(user.getProjectCode(), pageable);
		return list;
	}
	
	@RequestMapping("/audit")
	public String audit(){
		return "dm/productRegister/audit/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/audit/page")
	@ResponseBody
	public DataGrid<ProductRegister> auditPage(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		pageable.setSort(new Sort(Direction.DESC,"createDate"));
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", ProductRegister.Status.valueOf(status));
		}
		DataGrid<ProductRegister> page =  productRegisterService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	@RequestMapping(value="/audit/add", method = RequestMethod.GET)
	public String addAudit(@CurrentUser User user,Model model){
		model.addAttribute("user", user);
		return "dm/productRegister/audit/add";
	}
	

	@RequestMapping(value="/audit/add", method = RequestMethod.POST)
	@ResponseBody
	public Message addAudit(@CurrentUser User user,Long id, String status, String suggestion){
		Message message = new Message();
		try {
			ProductRegister productRegister = productRegisterService.getById(user.getProjectCode(), id);
			productRegister.setAuditDate(new Date());
			productRegister.setAuditor(user.getName());
			productRegister.setStatus(ProductRegister.Status.values()[Integer.parseInt(status)]);
			productRegister.setSuggestion(suggestion);
			productRegisterService.update(user.getProjectCode(), productRegister);
			if(ProductRegister.Status.values()[Integer.parseInt(status)].equals(ProductRegister.Status.disagree)){
				//发送系统消息
				this.sendMsg(user.getProjectCode(), productRegister);
			}
			
			message.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	private void sendMsg(String projectCode, ProductRegister productRegister) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		msg.setTitle("备案审核未通过。-- "+productRegister.getId());
		msg.setAttach("/dm/productRegister.htmlx");
		
		//查询接收者orgId
		Integer orgType = 1;
		if(productRegister.getOrgType().equals(OrgType.hospital)){
			orgType = 1;
		}else if(productRegister.getOrgType().equals(OrgType.vendor)){
			orgType = 2;
		}
		Organization og = organizationService.getByOrgId(projectCode, productRegister.getOrgId(), orgType);
		//查询发送者orgId
		Organization oh = organizationService.getById(projectCode, Long.parseLong("2"));
		if(oh != null){
			msg.setOrganizationId(oh.getId());
			msg.setOrgName(oh.getOrgName());
		}

		try {
			msgService.sendBySYSToOrg(projectCode, msg, og.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
