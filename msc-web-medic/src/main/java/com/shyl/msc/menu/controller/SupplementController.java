package com.shyl.msc.menu.controller;

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
import com.shyl.msc.menu.entity.Supplement;
import com.shyl.msc.menu.service.ISupplementService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/menu/supplement")
public class SupplementController extends BaseController {

	@Resource
	private ISupplementService supplementService;
	
	@Override
	protected void init(WebDataBinder arg0) {
		
	}

	@RequestMapping("")
	public String home() {
		return "/menu/supplement/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Supplement> list(PageRequest pageable, @CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"id");
		pageable.setSort(sort);
		return supplementService.query(user.getProjectCode(), pageable);
	}

}
