package com.shyl.msc.set.controller;


import java.util.List;

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
import com.shyl.msc.set.entity.Address;
import com.shyl.msc.set.service.IAddressService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/set/address")
public class AddressController extends BaseController {
	
	@Resource
	private IAddressService	addressService;

	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Address> page(PageRequest pageable, Long id,@CurrentUser User user) {
		return addressService.pageByPatient(user.getProjectCode(), pageable, id);
	}

	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Address> list(Long pid,@CurrentUser User user) {
		return addressService.listByPid(user.getProjectCode(), pid);
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "/set/patient/subadd";
	}


	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Address address, @CurrentUser User user){
		Message message = new Message();
		try{
			addressService.save(user.getProjectCode(), address);
			message.setMsg("新增成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setMsg("新增失败");
			message.setSuccess(false);
		}
		return  message;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(){
		return "/set/patient/subedit";
	}


	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Address address, @CurrentUser User user){
		Message message = new Message();
		try{
			addressService.update(user.getProjectCode(), address);
			message.setMsg("更新成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setMsg("更新失败");
			message.setSuccess(false);
		}
		return  message;
	}
	


	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user){
		Message message = new Message();
		try{
			addressService.delete(user.getProjectCode(), id);
			message.setMsg("删除成功");
		}catch(Exception e){
			e.printStackTrace();
			message.setMsg("删除失败");
			message.setSuccess(false);
		}
		return  message;
	}
}
