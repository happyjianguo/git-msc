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
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;

/**
 * 药品报量情况
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/b2b/report/productplan")
public class ProductPlanController extends BaseController {

	@Resource
	private IHospitalPlanService hospitalPlanService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "CITY");
		model.addAttribute("regionCodePId",attributeItem.getField3());
		return "b2b/report/productplan/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, String startDate, String endDate,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.DESC,"count(hp.id)"));
		pageable.setSort(sort);
		DataGrid<Map<String, Object>> page =  hospitalPlanService.reportForProductPlan(user.getProjectCode(), startDate, endDate, pageable);
		page.addFooter("HOSPITALNAME","", "COUNT");
		return page;
	}
	
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxlist(PageRequest pageable, String startDate, String endDate, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"dr.id"));
		pageable.setSort(sort);
		DataGrid<Map<String, Object>> page =  hospitalPlanService.reportDetailForProductPlan(user.getProjectCode(), startDate, endDate, pageable);
		return page;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
