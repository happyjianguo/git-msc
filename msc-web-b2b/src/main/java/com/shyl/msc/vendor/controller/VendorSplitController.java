package com.shyl.msc.vendor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 订单计划拆单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/split")
public class VendorSplitController extends BaseController {
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/split/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<PurchaseOrderPlan> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return null;
		}
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		query.put("t#vendorId_L_EQ", user.getOrganization().getOrgId());
		query.put("t#status_S_EQ", Status.effect);
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		return purchaseOrderPlanService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public List<PurchaseOrderPlanDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#status_S_EQ", com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status.normal);
		List<PurchaseOrderPlanDetail> list = purchaseOrderPlanDetailService.list(user.getProjectCode(), pageable);
		return list;
	}
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "vendor/split/para";
	}
	
	/**
	 * 确认下单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkorder")
	@ResponseBody
	public Message mkorder(Long[] detailIds, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");

			//String msg = purchaseOrderService.mkOrder(detailIds);

			
			//message.setMsg(msg);
			//System.out.println("ddbh="+pp.getCode());
			//message.setData(ddbh);
		} catch (Exception e) {
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
