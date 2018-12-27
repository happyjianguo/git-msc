package com.shyl.msc.supervise.controller;

import java.util.HashMap;

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
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.service.IMedicineHospitalService;
import com.shyl.msc.supervise.service.IMedicineService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/medicine")
public class MedicineController extends BaseController{
	
	@Resource
	private IMedicineHospitalService medicineHospitalService;
	@Resource
	private IMedicineService medicineService;

	@Override
	protected void init(WebDataBinder arg0) {
		
	}
	
	@RequestMapping("")
	public String index() {
		return "/supervise/medicine/index";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Medicine> page(PageRequest page, @CurrentUser User user) {
		if(page.getQuery() == null) {
			page.setQuery(new HashMap<String,Object>());
		}
		return medicineService.query(user.getProjectCode()+"_SUP",page);
	}
	
	@RequestMapping(value = "/setAuxiliary", method = RequestMethod.POST)
	@ResponseBody
	public Message setAuxiliary(Long productId,Integer isAuxiliary, @CurrentUser User user) {
		Message message = new Message();
		try {
			medicineService.updateAuxiliaryType(user.getProjectCode()+"_SUP",productId, isAuxiliary);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("设置失败");
		}
		return message;
	}

}
