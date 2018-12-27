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
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;

/**
 * 药品采购情况
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/b2b/report/productOrder")
public class productOrderController extends BaseController {

	@Resource
	private IHospitalPlanService hospitalPlanService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IContractService contractService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "CITY");
		model.addAttribute("regionCodePId",attributeItem.getField3());
		return "b2b/report/productorder/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, String startDate, String endDate,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"pd.productCode"));
		pageable.setSort(sort);
		
		DataGrid<Map<String, Object>> page = purchaseOrderDetailService.reportForProductOrder(user.getProjectCode(), startDate, endDate, pageable);
		return page;
	}
	
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxpage(PageRequest pageable, String startDate, String endDate, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"p.code"));
		pageable.setSort(sort);
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.reportDetailForProductOrder(user.getProjectCode(), startDate, endDate, pageable);
		return page;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
