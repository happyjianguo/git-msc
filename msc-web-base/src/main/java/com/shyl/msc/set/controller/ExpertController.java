package com.shyl.msc.set.controller;

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
import com.shyl.msc.set.entity.Expert;
import com.shyl.msc.set.service.IExpertService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/set/expert")
public class ExpertController extends BaseController {

	@Resource
	private IExpertService expertService;
	
	@RequestMapping("")
	public String list() {
		return "set/expert/list";
	}

	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Expert> page(PageRequest page, @CurrentUser User currUser) {
		DataGrid<Expert> data = expertService.query(currUser.getProjectCode(), page);
		return data;
	}
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String add(){
		return "set/expert/add";
	}
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@ResponseBody
	public Message add(Expert expert, @CurrentUser User user){
		Message msg = new Message();
		try {
			expertService.save(user.getProjectCode(), expert);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}

	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public String edit(){
		return "set/expert/edit";
	}
	
	@RequestMapping(value = "/edit",method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Expert expert, @CurrentUser User user){
		Message msg = new Message();
		try {
			expertService.update(user.getProjectCode(), expert);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}

	@RequestMapping(value = "/del",method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id,@CurrentUser User user){
		Message msg = new Message();
		try {
			expertService.delete(user.getProjectCode(), id);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}

	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
		
	}
}
