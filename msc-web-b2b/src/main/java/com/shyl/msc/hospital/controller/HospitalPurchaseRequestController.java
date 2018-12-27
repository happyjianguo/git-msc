package com.shyl.msc.hospital.controller;

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
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/purchaseRequest")
public class HospitalPurchaseRequestController extends BaseController {
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IPurchaseClosedRequestService purchaseClosedRequestService;
	@Resource
	private IPurchaseClosedRequestDetailService purchaseClosedRequestDetailService;
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
		return "hospital/purchaseRequest/list";
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
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<PurchaseClosedRequestDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		return purchaseClosedRequestDetailService.query(user.getProjectCode(), pageable);
	}
	@RequestMapping(value="/other/page")
	@ResponseBody
	public DataGrid<PurchaseOrder> page2(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1){
			Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			query.put("t#hospitalCode_S_EQ", hospital.getCode());
			String status = (String)query.get("t#status_S_EQ");
			if(!StringUtils.isEmpty(status)){
				query.put("t#status_S_EQ", PurchaseOrder.Status.valueOf(status).ordinal());
			}
			
			Sort sort = new Sort(Direction.DESC,"createDate");
			pageable.setSort(sort);
		}
//		return purchaseOrderService.queryByStatus(user.getProjectCode(), pageable);
		return purchaseOrderService.listByPurchaseOrderAndDetailStatus(user.getProjectCode(), pageable);
	}
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/other/mxlist")
	@ResponseBody
	public DataGrid<PurchaseOrderDetail> othermxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrderDetail> page = purchaseOrderDetailService.query(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum","deliveryGoodsNum","inOutBoundGoodsNum","returnsGoodsNum");
		return page;
	}
	
	@RequestMapping("/other")
	public String other(){
		return "hospital/purchaseRequest/other/list";
	}
	
	@RequestMapping("/other/add")
	public String closeAdd(ModelMap model, @CurrentUser User user){
		model.addAttribute("user", user);
		return "hospital/purchaseRequest/other/add";
	}
}
