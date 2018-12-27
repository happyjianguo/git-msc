package com.shyl.msc.gpo.controller;

import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
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
import com.shyl.msc.dm.service.IDrugService;
import com.shyl.msc.enmu.ProjectStus;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Project;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IProjectDetailService;
import com.shyl.msc.set.service.IProjectService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/gpo/project")
public class ProjectController extends BaseController {

	@Resource
	private IProjectService	projectService;
	@Resource
	private IProjectDetailService	projectDetailService;
	@Resource
	private IDrugService	drugService;
	@Resource
	private IAttributeItemService	attributeItemService;
	
	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping("")
	public String home(@CurrentUser User user) {
		AttributeItem ai = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "TENDER_OPEN");
		if(ai != null && ai.getField3().equals("1")){
			//开启招标流程
			return "gpo/project/list";
		}
		//只有报量流程
		return "gpo/project/list1";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Project> page(PageRequest page, @CurrentUser User user) {
		page.getQuery().put("t#gpoCode_S_EQ", user.getOrganization().getOrgCode());
		String type = (String)page.getQuery().get("t#type_S_EQ");
		if(!StringUtils.isEmpty(type)){
			page.getQuery().put("t#type_S_EQ", Project.ProjectType.valueOf(type));
		}
		Sort sort = new Sort(Direction.DESC, "createDate");
		page.setSort(sort);
		return projectService.query(user.getProjectCode(), page);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "gpo/project/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Project project, @CurrentUser User user) {
		Message msg = new Message();
		try{
			if(user.getOrganization().getOrgType() != 6){
				throw new Exception("非GPO角色，无权操作");
			}
			project.setGpoCode(user.getOrganization().getOrgCode());
			project.setGpoName(user.getOrganization().getOrgName());
			project.setProjectStus(ProjectStus.create);
			projectService.save(user.getProjectCode(), project);
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@ResponseBody
	public Message report(Long id,@CurrentUser User user){
		Message message = new Message();
		try {
			Project project = projectService.getById(user.getProjectCode(),id);
			if(!project.getProjectStus().equals(ProjectStus.create)){
				throw new Exception("状态异常");
			}
			project.setProjectStus(ProjectStus.report);
			project = projectService.updateWithInclude(user.getProjectCode(), project,"projectStus");
			message.setSuccess(true);
			message.setMsg(project.getName()+"开始报量");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Long id,@CurrentUser User user){
		Message message = new Message();
		try {
			Project project = projectService.getById(user.getProjectCode(),id);
			if(!project.getProjectStus().equals(ProjectStus.report)){
				throw new Exception("状态异常");
			}
			project.setProjectStus(ProjectStus.edit);
			project = projectService.updateWithInclude(user.getProjectCode(), project,"projectStus");
			message.setSuccess(true);
			message.setMsg(project.getName()+"结束报量");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/tender", method = RequestMethod.POST)
	@ResponseBody
	public Message tender(Long id,@CurrentUser User user){
		Message message = new Message();
		try {
			Project project = projectService.getById(user.getProjectCode(),id);
			if(!project.getProjectStus().equals(ProjectStus.edit)){
				throw new Exception("状态异常");
			}
			project.setProjectStus(ProjectStus.tender);
			project = projectService.updateWithInclude(user.getProjectCode(), project,"projectStus");
			message.setSuccess(true);
			message.setMsg(project.getName()+"开始投标");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/eval", method = RequestMethod.POST)
	@ResponseBody
	public Message eval(Long id,@CurrentUser User user){
		Message message = new Message();
		try {
			Project project = projectService.getById(user.getProjectCode(),id);
			if(!project.getProjectStus().equals(ProjectStus.tender)){
				throw new Exception("状态异常");
			}
			project.setProjectStus(ProjectStus.eval);
			project = projectService.updateWithInclude(user.getProjectCode(), project,"projectStus");
			message.setSuccess(true);
			message.setMsg(project.getName()+"结束投标");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/done", method = RequestMethod.POST)
	@ResponseBody
	public Message done(Long id,@CurrentUser User user){
		Message message = new Message();
		try {
			Project project = projectService.getById(user.getProjectCode(),id);
			if(!project.getProjectStus().equals(ProjectStus.eval)){
				throw new Exception("状态异常");
			}
			project.setProjectStus(ProjectStus.done);
			project = projectService.updateWithInclude(user.getProjectCode(), project,"projectStus");
			message.setSuccess(true);
			message.setMsg(project.getName()+"招标完成");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id,@CurrentUser User user) {
		Message msg = new Message();
		try{
			projectDetailService.deleteByProjectId(user.getProjectCode(), id);
			projectService.delete(user.getProjectCode(), id);
			msg.setMsg("删除成功");
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
}
