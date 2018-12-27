package com.shyl.msc.vendor.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 红字发票Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/invoicered")
public class VendorInvoiceRedController extends BaseController {
	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IInvoiceService invoiceService;
	
	@RequestMapping("")
	public String home(ModelMap model,@CurrentUser User user){
		model.addAttribute("orgId", user.getOrganization().getOrgId());
			
		return "vendor/invoicered/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<ReturnsOrder> page(PageRequest pageable, @CurrentUser User user){
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
		query.put("t#isInvoiced_I_NE", 1);
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		return returnsOrderService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ReturnsOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<ReturnsOrderDetail> page = returnsOrderDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "vendor/invoicered/para";
	}
	/**
	 * 上传成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/invoicered/success";
	}
	/**
	 * 生成发票
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkinvoice")
	@ResponseBody
	public Message mkinvoice(Long returnId,String invoiceCode,String invoiceDate,BigDecimal taxRate, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(returnId+","+invoiceCode+","+invoiceDate+","+taxRate);
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			String ddbh = invoiceService.mkinvoiceRed(user.getProjectCode(), returnId,invoiceCode,invoiceDate,taxRate,user);
			
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
