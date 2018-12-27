package com.shyl.msc.yunwei.controller;

import java.math.BigDecimal;
import java.util.Date;
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
@RequestMapping("/yunwei/serviceJudge")
public class YunweiServiceJudgeController extends BaseController {

	@Resource
	private IServiceJudgeService serviceJudgeService;
	
	
	@RequestMapping("")
	public String home() {
		return "yunwei/serviceJudge/list";
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
	
	
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String add() {
		return "yunwei/serviceJudge/para";
	}
	
	
	@RequestMapping(value = "/judge", method = RequestMethod.POST)
	@ResponseBody
	public Message judge(Long id,BigDecimal deduct,String statusYN, @CurrentUser User currentUser) {
		Message msg = new Message();
		try{
			ServiceJudge sj = serviceJudgeService.getById(currentUser.getProjectCode(), id);
			if(sj == null){
				throw new Exception("用户已撤销投诉，数据不存在");
			}
			sj.setDeduct(deduct);
			sj.setAuditor(currentUser.getName());
			sj.setAuditDate(new Date());
			if(statusYN.equals("Y")){
				sj.setStatus(Status.agree);
			}else if(statusYN.equals("N")){
				sj.setStatus(Status.disagree);
			}
			
			serviceJudgeService.update(currentUser.getProjectCode(), sj);
			
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
			serviceJudgeService.delete(user.getProjectCode(), id);
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
