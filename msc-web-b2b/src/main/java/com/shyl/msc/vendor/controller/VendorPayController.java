package com.shyl.msc.vendor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.shyl.msc.b2b.stl.entity.Payment;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.Settlement.Status;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.b2b.stl.service.IPaymentService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 付款Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/pay")
public class VendorPayController extends BaseController {
	@Resource
	private IInvoiceService invoiceService;
	@Resource
	private  ISettlementService settlementService;
	@Resource
	private  IPaymentService paymentService;
	@Resource
	private ICompanyService companyService;


	@RequestMapping("")
	public String home(){	
		return "vendor/pay/list";
	}

	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Settlement> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<Settlement> page = new DataGrid<Settlement>();
		if(user.getOrganization().getOrgType() == null ||user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null)
			return page;
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		query.put("t#vendorCode_S_EQ", company.getCode());
		query.put("t#status_S_NE", Status.paid);
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		page = settlementService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(Long hospitalId,Model model){
		model.addAttribute("hospitalId",hospitalId);
		return "vendor/pay/para";
	}
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/pay/success";
	}
	
	/**
	 * 生成订单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkpayment")
	@ResponseBody
	public Message mkpayment(Payment payment,Long settleId, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			String ddbh = paymentService.mkpayment(user.getProjectCode(), payment,settleId,user);
			//String ddbh = settlementService.mkAutoOrder(purchaseOrderPlan,goodsids,nums,user);
			System.out.println("ddbh="+ddbh);
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
