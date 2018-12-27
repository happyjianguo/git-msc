package com.shyl.msc.set.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.annotation.Fastjson;
import com.shyl.common.web.controller.BaseController;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Expert;
import com.shyl.msc.set.entity.Project;
import com.shyl.msc.set.entity.ProjectExpert;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IExpertService;
import com.shyl.msc.set.service.IProjectExpertService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/set/projectExpert")
public class ProjectExpertController extends BaseController {

	@Resource
	private IProjectExpertService projectExpertService;
	@Resource
	private IExpertService expertService;
	@Resource
	private IProjectService projectService;
	
	
	@RequestMapping("")
	public String list() {
		return "set/projectExpert/list";
	}

	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProjectExpert> page(PageRequest page, @CurrentUser User currUser) {
		DataGrid<ProjectExpert> data = projectExpertService.query(currUser.getProjectCode(), page);
		return data;
	}
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String add(Long projectId,Model model, @CurrentUser User user){
		//List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo(user.getProjectCode(), "expert");
		Project project = projectService.getById(user.getProjectCode(),projectId);
		model.addAttribute("projectId", projectId);
		model.addAttribute("projectName", project.getName());
		List<Map<String, Object>> list = expertService.listForCourseCode(user.getProjectCode());
		System.out.println("list.size="+list.size());
		model.addAttribute("courseList", list);
		return "set/projectExpert/add";
	}
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@ResponseBody
	public Message add(Long projectId,@Fastjson String expertArr, @CurrentUser User user){
		Message msg = new Message();
		try {
			Map expertMap = JSON.parseObject(expertArr,Map.class);  
			projectExpertService.saveRdmCourse(user.getProjectCode(),projectId,expertMap);
			//projectExpertService.save(user.getProjectCode(), projectExpert);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/rdmCourse",method = RequestMethod.POST)
	@ResponseBody
	public Message rdmCourse(@Fastjson String expertArr,Long projectId, @CurrentUser User user){
		
		System.out.println("expertArr="+expertArr);
		Message msg = new Message();
		try {
			Map expertMap = JSON.parseObject(expertArr,Map.class);  
			List<Expert> list = projectExpertService.rdmCourse(user.getProjectCode(),projectId,expertMap);
			msg.setData(list);
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
