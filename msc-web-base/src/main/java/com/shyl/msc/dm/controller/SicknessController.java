package com.shyl.msc.dm.controller;

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
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.Sickness;
import com.shyl.msc.dm.service.ISicknessProductService;
import com.shyl.msc.dm.service.ISicknessService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 疾病
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/sickness")
public class SicknessController extends BaseController{

	@Resource
	private ISicknessService sicknessService;
	@Resource
	private ISicknessProductService sicknessProductService;
	
	@RequestMapping("")
	public String home(){
		return "dm/sickness/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Sickness> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Sickness> page = sicknessService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	@RequestMapping("/product/page")
	@ResponseBody
	public DataGrid<Product> productPage(PageRequest pageable, String sicknessCode,@CurrentUser User user){
		DataGrid<Product> page = sicknessProductService.pageBySicknessCode(user.getProjectCode(), pageable, sicknessCode);
		return page;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/sickness/add";
	}
	
	/**
	 * 新增
	 * @param group
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Sickness sickness, @CurrentUser User user){
		Message message = new Message();
		try{
			sicknessService.save(user.getProjectCode(), sickness);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(){
		return "dm/sickness/edit";
	}
	/**
	 * 修改
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Sickness sickness, @CurrentUser User user){
		Message message = new Message();
		try{
			sicknessService.update(user.getProjectCode(), sickness);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}

}
