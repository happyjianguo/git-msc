package com.shyl.msc.supervise.controller;

import javax.annotation.Resource;

import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.sys.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.supervise.entity.Quota;
import com.shyl.msc.supervise.service.IQuotaService;
import com.shyl.sys.dto.Message;

@Controller
@RequestMapping("/supervise/quota")
public class QuotaController extends BaseController {
	
	@Resource
	public IQuotaService quotaService;

	@Override
	protected void init(WebDataBinder arg0) {
	}
	
	@RequestMapping("/index")
	public String index() {
		return "/supervise/quota/index";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Quota> page(PageRequest page, @CurrentUser User user) {
		return quotaService.query(user.getProjectCode()+"_SUP",page);
	}
	
	@RequestMapping("/add")
	public String add() {
		return "/supervise/quota/add";
	}
	
	@RequestMapping("/edit")
	public String edit() {
		return "/supervise/quota/add";
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public Message save(Quota quota, @CurrentUser User user) {
		Message msg = new Message();
		try {
			quotaService.save(user.getProjectCode()+"_SUP",quota);
		} catch (Exception e) {
			msg.setSuccess(false);
			msg.setMsg("保存失败");
		}
		return msg;
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public Message update(Quota quota, @CurrentUser User user) {

		Message msg = new Message();
		try {
			quotaService.update(user.getProjectCode()+"_SUP",quota);
		} catch (Exception e) {
			msg.setSuccess(false);
			msg.setMsg("保存失败");
		}
		return msg;
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public Message delete(Quota quota, @CurrentUser User user) {
		Message msg = new Message();
		try {
			quotaService.delete(user.getProjectCode()+"_SUP",quota);
		} catch (Exception e) {
			msg.setSuccess(false);
			msg.setMsg("删除失败");
		}
		return msg;
	}
	
}
