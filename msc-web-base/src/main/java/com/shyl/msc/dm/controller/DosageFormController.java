package com.shyl.msc.dm.controller;

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
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.DosageForm;
import com.shyl.msc.dm.service.IDosageFormService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 剂型
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/dosageForm")
public class DosageFormController extends BaseController {

	@Resource
	private IDosageFormService dosageFormService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/dosageForm/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<DosageForm> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<DosageForm> page =  dosageFormService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<DosageForm> list(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		List<DosageForm> list =  dosageFormService.list(user.getProjectCode(), pageable);
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/dosageForm/add";
	}
	
	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(DosageForm dosageForm, @CurrentUser User user){
		Message message = new Message();
		try{
			PageRequest pageable = new PageRequest();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("t#code_S_EQ", dosageForm.getCode());
			pageable.setQuery(m);
			List<DosageForm> list = dosageFormService.list(user.getProjectCode(), pageable);
			if(list.size()>0){
				throw new Exception("剂型代码"+dosageForm.getCode()+"已存在");
			}
			dosageFormService.save(user.getProjectCode(), dosageForm);
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
	public String edit(){
		return "dm/dosageForm/edit";
	}
	/**
	 * 修改
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(DosageForm dosageForm, @CurrentUser User user){
		Message message = new Message();
		try{
			dosageFormService.update(user.getProjectCode(), dosageForm);
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
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			dosageFormService.delete(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}

}
