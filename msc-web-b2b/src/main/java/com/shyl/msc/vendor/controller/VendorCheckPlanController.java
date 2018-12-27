package com.shyl.msc.vendor.controller;

import java.util.HashMap;
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
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 审单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/check")
public class VendorCheckPlanController extends BaseController {
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private ICompanyService companyService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/check/list";
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
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		query.put("t#vendorCode_S_EQ", company.getCode());
		query.put("t#status_S_EQ", Status.uneffect);
	
		Sort sort = new Sort(new Order(Direction.ASC,"status"),new Order(Direction.DESC,"createDate"));
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
	public DataGrid<PurchaseOrderPlanDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrderPlanDetail> page = purchaseOrderPlanDetailService.query(user.getProjectCode(), pageable);
		return page;
	}

	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/check/success";
	}
	
	/**
	 * 审单笔药品
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/checkOne", method = RequestMethod.POST)
	@ResponseBody
	public Message checkOne(Long id,Integer status, @CurrentUser User user){
		Message message = new Message();
		try{
			System.out.println("id = "+id);
			PurchaseOrderPlanDetail pd = purchaseOrderPlanDetailService.getById(user.getProjectCode(), id);
			if(pd ==null){
				message.setSuccess(false);
				message.setMsg("数据不存在");
				return message;
			}
			if(status == 0){
				pd.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status.normal);
			}else{
				pd.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status.cancel);
			}
			
			purchaseOrderPlanDetailService.update(user.getProjectCode(), pd);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 审计划单
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	@ResponseBody
	public Message check(Long id,@CurrentUser User user){
		Message message = new Message();
		try{
			String ddbhs = purchaseOrderService.checkPlan(user.getProjectCode(), id);
			if(ddbhs.equals("cancel")){
				message.setSuccess(false);
			}else{
				message.setData(ddbhs);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
