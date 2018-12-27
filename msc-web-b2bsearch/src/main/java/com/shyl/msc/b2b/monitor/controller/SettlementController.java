package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.Settlement.Status;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;
import com.shyl.msc.b2b.stl.service.ISettlementDetailService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.sys.entity.User;
/**
 * 结算Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/settlement")
public class SettlementController extends BaseController {

	@Resource
	private  ISettlementService settlementService;
	@Resource
	private  ISettlementDetailService settlementDetailService;

	@RequestMapping("")
	public String home(Model model, @CurrentUser User user, String code){	
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		return "b2b/monitor/settlement/list";
	}

	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Settlement> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Settlement> page = new DataGrid<Settlement>();
		
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
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", Status.valueOf(status));
		}
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);
		page = settlementService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	
	@RequestMapping(value="/mxlist")
	@ResponseBody
	public DataGrid<SettlementDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<SettlementDetail> page = settlementDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
