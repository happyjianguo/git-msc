package com.shyl.msc.b2b.report.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

/**
 * 6、在线交易药品查询
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/goodscount")
public class GoodsCountController extends BaseController {
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IHospitalService hospitalService;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/goodscount/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable,@CurrentUser User user){
		DataGrid<Map<String, Object>> page =  goodsService.queryHospitalAndProduct(user.getProjectCode(), pageable);
		return page;
	}

}
