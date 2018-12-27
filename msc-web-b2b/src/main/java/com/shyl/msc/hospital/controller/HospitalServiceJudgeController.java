package com.shyl.msc.hospital.controller;

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
import com.shyl.msc.b2b.judge.entity.ServiceJudge;
import com.shyl.msc.b2b.judge.entity.ServiceJudge.Status;
import com.shyl.msc.b2b.judge.service.IServiceJudgeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/hospital/serviceJudge")
public class HospitalServiceJudgeController extends BaseController {

	@Resource
	private IServiceJudgeService serviceJudgeService;
	
	
	@RequestMapping("")
	public String home() {
		return "hospital/serviceJudge/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ServiceJudge> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("t#createUser_S_EQ", user.getEmpId());
		}
		Sort sort = new Sort(Direction.DESC,"createDate");
		page.setSort(sort);
		
		return serviceJudgeService.query(user.getProjectCode(), page);
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "hospital/serviceJudge/add";
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(ServiceJudge serviceJudge, @CurrentUser User currentUser) {
		Message msg = new Message();
		try{
			serviceJudge.setStatus(Status.unaudit);
			serviceJudge.setCreateUser(currentUser.getEmpId());
			serviceJudgeService.save(currentUser.getProjectCode(), serviceJudge);
			
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try{
			ServiceJudge sj = serviceJudgeService.getById(user.getProjectCode(), id);
			if(sj.getStatus().equals(Status.unaudit)){
				serviceJudgeService.delete(user.getProjectCode(), id);
			}else{
				throw new Exception("只能删除【未审核】的投诉");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
