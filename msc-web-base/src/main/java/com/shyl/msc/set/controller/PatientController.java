package com.shyl.msc.set.controller;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Patient;
import com.shyl.msc.set.service.IPatientService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/set/patient")
public class PatientController extends BaseController {
	
	@Resource
	private IPatientService	patientService;

	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 接口数据报文查询jsp
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model,@CurrentUser User user){
		return "set/patient/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Patient> page(PageRequest pageable, @CurrentUser User user) {
		return patientService.query(user .getProjectCode(), pageable);
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "set/patient/add";
	}


	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Patient patient,@CurrentUser User user){
		Message message = new Message();
		try{
			Patient patient0 = patientService.getByIdCode(user.getProjectCode(), patient.getIdCode());
			if (patient0 != null) {
				message.setSuccess(false);
				message.setMsg("身份证已存在");
				return message;
			}
			patientService.save(user.getProjectCode(), patient);
		}catch(Exception e){
			e.printStackTrace();
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
		return "set/patient/edit";
	}


	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Patient patient, @CurrentUser User user){
		Message message = new Message();
		try{
			patientService.update(user.getProjectCode(), patient);
		}catch(Exception e){
			e.printStackTrace();
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
			patientService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
}
