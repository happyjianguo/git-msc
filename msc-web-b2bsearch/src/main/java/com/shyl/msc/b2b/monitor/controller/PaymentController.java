package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
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
import com.shyl.msc.b2b.stl.entity.Payment;
import com.shyl.msc.b2b.stl.service.IPaymentService;
import com.shyl.sys.entity.User;
/**
 * 付款Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/payment")
public class PaymentController extends BaseController {

	@Resource
	private  IPaymentService paymentService;

	@RequestMapping("")
	public String home(Model model, @CurrentUser User user, String settlementCode){	
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("settlementCode", settlementCode);
		return "b2b/monitor/payment/list";
	}

	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Payment> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Payment> page = new DataGrid<Payment>();
		
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
		page = paymentService.query(user.getProjectCode(), pageable);
		return page;
	}

	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
