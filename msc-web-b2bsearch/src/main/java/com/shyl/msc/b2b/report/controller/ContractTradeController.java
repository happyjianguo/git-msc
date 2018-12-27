package com.shyl.msc.b2b.report.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.sys.entity.User;
@Controller
@RequestMapping("/b2b/report/contracttrade")
public class ContractTradeController extends BaseController {

	@Resource
	private IContractDetailService contractDetailService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/contracttrade/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, String startDate, String endDate,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"p.code"));
		pageable.setSort(sort);
		DataGrid<Map<String, Object>> page =  contractDetailService.tradeByProduct(user.getProjectCode(), pageable, startDate, endDate);
		return page;
	}
	
	@RequestMapping("/detail")
	public String detail(String productCode, String startDate, String endDate, String hospitalCode, Model model){
		System.out.println("productCode=="+productCode);
		System.out.println("startDate=="+startDate);
		System.out.println("endDate=="+endDate);
		System.out.println("hospitalCode=="+hospitalCode);
		
		model.addAttribute("productCode", productCode==null?"":productCode);
		model.addAttribute("startDate", startDate==null?"":startDate);
		model.addAttribute("endDate", endDate==null?"":endDate);
		model.addAttribute("hospitalCode", hospitalCode==null?"":hospitalCode);
		return "b2b/report/contracttrade/detail";
	}
	
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<Map<String, Object>> mx(PageRequest pageable, String startDate, String endDate, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"p.code"));
		pageable.setSort(sort);
		DataGrid<Map<String, Object>> page =  contractDetailService.tradeDetailByProduct(user.getProjectCode(), pageable, startDate, endDate);
		return page;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
