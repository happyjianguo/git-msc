package com.shyl.msc.set.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Blacklist;
import com.shyl.msc.set.service.IBlacklistService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 中标企业Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/blacklist")
public class BlacklistController extends BaseController {
	@Resource
	private IBlacklistService blacklistService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "set/blacklist/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Blacklist> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Blacklist> page =  blacklistService.query(user.getProjectCode(), pageable);
		return page;
	}
	

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/blacklist/add";
	}
	
	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Blacklist blacklist, @CurrentUser User user){
		Message message = new Message();
		try{
			blacklist.setIsDisabled(1);
			blacklistService.save(user.getProjectCode(), blacklist);
		}catch(Exception e){		
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id,Model model, @CurrentUser User user){
		Blacklist bl = blacklistService.getById(user.getProjectCode(), id);
		if(bl != null){
			model.addAttribute("fullName", bl.getCompany().getFullName());
			model.addAttribute("joinReason", bl.getJoinReason());
		}
		return "set/blacklist/edit";
	}
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Blacklist blacklist, @CurrentUser User user){
		Message message = new Message();
		try{
			Blacklist bl = blacklistService.getById(user.getProjectCode(), blacklist.getId());
			bl.setDisabledReason(blacklist.getDisabledReason());
			bl.setIsDisabled(0);
			blacklistService.update(user.getProjectCode(), bl);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	
}
