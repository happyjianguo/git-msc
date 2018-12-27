package com.shyl.msc.supervise.controller;

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
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.supervise.entity.SuccessLog;
import com.shyl.msc.supervise.service.ISuccessLogService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/supervise/successLog")
public class SuccessLogController extends BaseController {

	@Resource
	public ISuccessLogService successLogService;

	@Override
	protected void init(WebDataBinder arg0) {
	}

	@RequestMapping("/index")
	public String index() {
		return "/supervise/successLog/index";
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<SuccessLog> page(PageRequest page, @CurrentUser User user) {
		page.setSort(new Sort(Direction.DESC,"id"));;
		return successLogService.query(user.getProjectCode()+"_SUP", page);
	}

}
