package com.shyl.msc.menu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.menu.entity.BaseDrug;
import com.shyl.msc.menu.entity.Stupefacient;
import com.shyl.msc.menu.service.IBaseDrugService;
import com.shyl.msc.menu.service.IStupefacientService;
import com.shyl.msc.task.BaseDrugFoundAsyncTask;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 麻醉药品
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/menu/stupefacient")
public class StupefacientController extends BaseController{
	@Resource
	private IStupefacientService stupefacientService;
	
	@Override
	protected void init(WebDataBinder arg0) {		
	}
	
	@RequestMapping("")
	public String home() {
		return "/menu/stupefacient/list";
	}	
	
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Stupefacient> list(PageRequest pageable, @CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"id");
		pageable.setSort(sort);
		return stupefacientService.query(user.getProjectCode(), pageable);
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	public Message stop() {
		Message msg = new Message();
		BaseDrugFoundAsyncTask.status = 0;
		msg.setMsg("同步信息线程已停止");
		return msg;
	}
}
