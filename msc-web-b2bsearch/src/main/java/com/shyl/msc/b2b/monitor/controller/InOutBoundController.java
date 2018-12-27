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
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.sys.entity.User;
/**
 * 出入库单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/inOutBound")
public class InOutBoundController extends BaseController {

	@Resource
	private IInOutBoundService inOutBoundService;
	
	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user,String code, String deliveryOrderCode){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		model.addAttribute("deliveryOrderCode", deliveryOrderCode);
		return "b2b/monitor/inOutBound/list";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<InOutBound> page(PageRequest pageable, @CurrentUser User user){
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
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);

//		return inOutBoundService.query(user.getProjectCode(), pageable);
		return inOutBoundService.listByInOutBoundAndDetail(pageable);
	}

	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<InOutBoundDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<InOutBoundDetail> page = inOutBoundDetailService.queryByCode(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum");
		return page;
	}
}
