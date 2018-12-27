package com.shyl.msc.vendor.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.service.IInvoiceService;
import com.shyl.msc.b2b.stl.service.ISettlementService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 结算Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/settle")
public class VendorSettleController extends BaseController {
	@Resource
	private IInvoiceService invoiceService;
	@Resource
	private  ISettlementService settlementService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private  ICompanyService companyService;
	
	@RequestMapping("")
	public String home(){	
		return "vendor/settle/list";
	}

	@RequestMapping(value="/list")
	@ResponseBody
	public List<Map<String,Object>> list(String hospitalCode, @CurrentUser User user){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType()!=2 || user.getOrganization().getOrgId() == null)
			return list;
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		list = invoiceService.listForSettle(user.getProjectCode(), hospitalCode, company.getCode());
		return list;
	}
	
	@RequestMapping(value="/hospitalPage")
	@ResponseBody
	public DataGrid<Map<String,Object>> hospitalPage(PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String,Object>> page = new DataGrid<Map<String,Object>>();
		if(user.getOrganization().getOrgType() == null ||user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null)
			return page;
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		page = invoiceService.hospitalListForSettle(user.getProjectCode(), company.getCode(),pageable);
		
		return page;
	}
	
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(Model model){
		return "vendor/settle/para";
	}
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/settle/success";
	}
	
	/**
	 * 生成订单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mksettle")
	@ResponseBody
	public Message mksettle(Settlement settlement,Long[] invoiceids, String hospitalCode, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(invoiceids+","+hospitalCode+",");
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");

			String ddbh = settlementService.mkSettlement(user.getProjectCode(), settlement,invoiceids,hospitalCode,user);
			//System.out.println("ddbh="+ddbh);
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
