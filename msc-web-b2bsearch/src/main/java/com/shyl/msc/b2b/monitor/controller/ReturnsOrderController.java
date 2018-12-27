package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.sys.entity.User;
/**
 * 退货单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/returnsOrder")
public class ReturnsOrderController extends BaseController {

	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user,String code){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		return "b2b/monitor/returnsOrder/list";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ReturnsOrder> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 2){
			query.put("t#vendorCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 6){
			query.put("t#gpoCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Sort sort = new Sort(new Order(Direction.DESC,"t.id"));
		pageable.setSort(sort);

		//return returnsOrderService.query(user.getProjectCode(), pageable);
		return returnsOrderService.listByReturnsOrderAndDetail(user.getProjectCode(), pageable);
	}

	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ReturnsOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<ReturnsOrderDetail> page = returnsOrderDetailService.query(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum");
		return page;
	}
}
