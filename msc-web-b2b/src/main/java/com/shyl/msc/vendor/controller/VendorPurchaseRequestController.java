package com.shyl.msc.vendor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/vendor/purchaseRequest")
public class VendorPurchaseRequestController extends BaseController {
	@Resource
	private IPurchaseClosedRequestService purchaseClosedRequestService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	
	@Override
	protected void init(WebDataBinder binder) {
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user){
		return "vendor/purchaseRequest/list";
	}

	@RequestMapping("/add")
	public String closeAdd(ModelMap model, @CurrentUser User user){
		model.addAttribute("user", user);
		return "vendor/purchaseRequest/add";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<PurchaseClosedRequest> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1){
			Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			query.put("t#hospitalCode_S_EQ", hospital.getCode());
		}else if(user.getOrganization().getOrgType() == 2){
			Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			query.put("t#vendorCode_S_EQ", company.getCode());
		}

		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", PurchaseClosedRequest.Status.valueOf(status));
		}
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);

		return purchaseClosedRequestService.query(user.getProjectCode(), pageable);
	}
	
	@RequestMapping("/commit")
	@ResponseBody
	public Message commit(Long id,String status,String reply, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			purchaseClosedRequestService.closeCommit(user.getProjectCode(), id, status, reply);
			
			message.setMsg("审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	
}
