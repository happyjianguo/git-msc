package com.shyl.msc.hospital.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.annotation.Fastjson;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Group;
import com.shyl.sys.entity.User;

/**
 * 收货
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/hospital/delivery")
public class HospitalDeliveryController extends BaseController {
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private IInOutBoundService inOutBoundService;
	@Resource
	private IHospitalService hospitalService;

	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping(value="/list")
	public String list(ModelMap map, String code){
		map.addAttribute("code",code);
		return "/hospital/delivery/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<DeliveryOrderDetail> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<DeliveryOrderDetail> page = new DataGrid<DeliveryOrderDetail>();
		if(user.getOrganization().getOrgId() != null){
			Integer type = user.getOrganization().getOrgType();
			Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			if(type == 1){
				page = deliveryOrderDetailService.pageByHospitalNotIn(user.getProjectCode(), pageable,hospital.getCode());
			}
		}
		return page;
	}
	
	@RequestMapping(value="/takeDelivery", method = RequestMethod.POST)
	@ResponseBody
	public Message takeDelivery(@Fastjson List<DeliveryOrderDetail> fastjson, @CurrentUser User user){
		Message message = new Message();
		try {
			List<InOutBound> list = inOutBoundService.takeInOutBound(user.getProjectCode(), fastjson, user);
			message.setSuccess(true);
			message.setData(list);
			message.setMsg("收货成功！");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}

	/**
	 * 收货成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "hospital/delivery/success";
	}
}
