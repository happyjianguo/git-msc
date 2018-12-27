package com.shyl.msc.vendor.controller;

import java.math.BigDecimal;
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
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 退货Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/return")
public class VendorReturnController extends BaseController {
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IReturnsOrderService returnsOrderService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/return/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<DeliveryOrder> page(PageRequest pageable, @CurrentUser User user){
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
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);

		return deliveryOrderService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public List<DeliveryOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		List<DeliveryOrderDetail> list = deliveryOrderDetailService.list(user.getProjectCode(), pageable);
		return list;
	}
	
	/**
	 * 退货成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/return/success";
	}
	
	/**
	 * 生成退货单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkreturn")
	@ResponseBody
	public Message mkreturn(Long deliveryId,Long[] detailIds,BigDecimal[] nums,String[] reasons, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(deliveryId+","+detailIds+","+nums);
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			String ddbh = returnsOrderService.mkreturn(user.getProjectCode(), deliveryId,detailIds,nums,reasons,user);
			//String ddbh = "";
			//message.setMsg(msg);
			//System.out.println("ddbh="+pp.getCode());
			message.setData(ddbh);
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
