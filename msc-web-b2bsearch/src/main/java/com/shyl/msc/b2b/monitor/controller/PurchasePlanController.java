package com.shyl.msc.b2b.monitor.controller;

import java.util.*;

import javax.annotation.Resource;

import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.sys.dto.Message;
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
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.IPurchasePlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.sys.entity.User;
/**
 * 点单计划Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/purchasePlan")
public class PurchasePlanController extends BaseController {
	@Resource
	private IPurchasePlanService purchasePlanService;
	@Resource
	private IPurchasePlanDetailService purchasePlanDetailService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		return "b2b/monitor/purchasePlan/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<PurchasePlan> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);
		//return purchasePlanService.query(user.getProjectCode(), pageable);
		return purchasePlanService.listByPurchasePlanAndDetail(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<PurchasePlanDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#status_S_NE", PurchasePlanDetail.Status.hcancel.ordinal());
		DataGrid<PurchasePlanDetail> page = purchasePlanDetailService.queryByPurchasePlanAndCode(user.getProjectCode(), pageable);
		page.addFooter("PRODUCTCODE", "GOODSNUM","GOODSSUM");
		return page;
	}

	/**
	 * 批量执行拆单
	 * @param user
	 * @return
	 */
	@RequestMapping("/doUpdateBatch")
	@ResponseBody
	public Message doUpdateBatch(@CurrentUser User user){
		Message message = new Message();
		try{
			System.out.println("------------进来了---------------");
			List<PurchasePlan> purchasePlans = purchasePlanService.queryByPlanCode(user.getProjectCode());
			System.out.println("------------purchasePlans---------------"+purchasePlans.size());
			if(purchasePlans.size() == 0){
				throw new Exception("没有需要拆分的采购计划！");
			}
			int i = 0;
			for(PurchasePlan purchasePlan : purchasePlans){
				List<PurchasePlanDetail> purchasePlanDetails = purchasePlanService.getDetailById(user.getProjectCode(),purchasePlan.getId());
				if(purchasePlanDetails != null && purchasePlanDetails.size() >0){
					Set<PurchasePlanDetail> purchasePlanDetailSet = new HashSet<>(purchasePlanDetails);
					purchasePlan.setPurchasePlanDetails(purchasePlanDetailSet);
				}
				System.out.println("------------purchasePlanDetails---------------"+purchasePlanDetails.size());
				purchaseOrderPlanService.savePurchaseOrderPlan(user.getProjectCode(),purchasePlan);
				System.out.println("第"+ ++i +"笔------------更新成功---------------");
				message.setMsg("拆分成功");
			}
			System.out.println("------------结束了---------------");
		}catch (Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
