package com.shyl.msc.vendor.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 蓝字发票Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/invoiceblue")
public class VendorInvoiceBlueController extends BaseController {
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IInvoiceService invoiceService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/invoiceblue/list";
	}

	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/orderPage")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return null;
		}

		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		return deliveryOrderService.queryUninvoice(user.getProjectCode(), pageable,company.getCode());
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<DeliveryOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<DeliveryOrderDetail> list = deliveryOrderDetailService.query(user.getProjectCode(), pageable);
		for (DeliveryOrderDetail deliveryOrderDetail : list.getRows()) {
			BigDecimal sum = deliveryOrderDetail.getGoodsSum();
			BigDecimal isum = deliveryOrderDetail.getInvoiceGoodsSum();
			if(isum == null){
				isum = isum == null?new BigDecimal("0"):isum;
				deliveryOrderDetail.setInvoiceGoodsSum(isum);
			}
			//借用 退货金额栏位 存剩余开票金额
			deliveryOrderDetail.setReturnsGoodsSum(sum.subtract(isum));
		}
		return list;
	}
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "vendor/invoiceblue/para";
	}
	
	/**
	 * 上传成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/invoiceblue/success";
	}
	/**
	 * 生成发票
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkinvoice")
	@ResponseBody
	public Message mkinvoice(String orderCode,Long[] detailIds,BigDecimal[] sums,String invoiceCode,String invoiceDate,BigDecimal taxRate, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(sums+","+invoiceCode+","+invoiceDate);
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			String ddbh = invoiceService.mkinvoiceBlue(user.getProjectCode(), orderCode,detailIds,sums,invoiceCode,invoiceDate,taxRate,user);
			
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
