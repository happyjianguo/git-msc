package com.shyl.msc.vendor.controller;


import java.math.BigDecimal;
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
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.enmu.UrgencyLevel;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 补货计划Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/autoorder")
public class VendorAutoOrderController extends BaseController {
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;

	
	@RequestMapping("")
	public String home(){	
		return "vendor/autoorder/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest pageable,String hospitalCode, @CurrentUser User user){
		DataGrid<Map<String,Object>> page = new DataGrid<Map<String,Object>>();
		if(user.getOrganization().getOrgType()!=2 || user.getOrganization().getOrgId() == null)
			return page;
		page = goodsService.listForAutoOrder(user.getProjectCode(), hospitalCode,user.getOrganization().getOrgCode(),pageable);
		for (Map<String, Object> map : page.getRows()) {
			try{
				Integer stockUpLimit = Integer.parseInt(map.get("STOCKUPLIMIT")+"") ;
				Integer stockNum = Integer.parseInt(map.get("STOCKNUM")+"") ;
				Integer num = stockUpLimit - stockNum;
				map.put("NUM", num);
			}catch(Exception e){
				map.put("NUM", 0);
			}
			
		}
		return page;
	}
	
	@RequestMapping(value="/hospitalPage")
	@ResponseBody
	public DataGrid<Map<String,Object>> hospitalPage(PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String,Object>> page = new DataGrid<Map<String,Object>>();
		if(user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null)
			return page;
		page = goodsService.hospitalListForAutoOrder(user.getProjectCode(), user.getOrganization().getOrgCode(),pageable);
		
		return page;
	}
	
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(String hospitalCode,Model model,@CurrentUser User user){
		Hospital hospital = hospitalService.findByCode(user.getProjectCode(), hospitalCode);
		model.addAttribute("hospitalId", hospital.getId());
		return "vendor/autoorder/para";
	}
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/autoorder/success";
	}
	
	/**
	 * 生成订单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkorder")
	@ResponseBody
	public Message mkorder(PurchaseOrderPlan purchaseOrderPlan,Integer uLevel,Long[] goodspriceids, BigDecimal[] nums, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(uLevel+","+goodspriceids+","+nums);
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			if(uLevel == 0)
				purchaseOrderPlan.setUrgencyLevel(UrgencyLevel.urgent);
			else
				purchaseOrderPlan.setUrgencyLevel(UrgencyLevel.noturgent);
			purchaseOrderPlan.setStatus(Status.uneffect);
			String ddbh = purchaseOrderPlanService.mkAutoOrder(user.getProjectCode(), purchaseOrderPlan,goodspriceids,nums,user);
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
