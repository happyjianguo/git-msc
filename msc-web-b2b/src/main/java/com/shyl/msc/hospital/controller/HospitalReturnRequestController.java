package com.shyl.msc.hospital.controller;

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
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IReturnsRequestService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 退货申请单
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/hospital/returnRequest")
public class HospitalReturnRequestController extends BaseController {
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;	
	@Resource
	private IReturnsRequestService returnsRequestService;
	@Resource
	private IHospitalService hospitalService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	@RequestMapping("")
	public String home(){
		return "hospital/returnRequest/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<DeliveryOrderDetail> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 1 || user.getOrganization().getOrgId() == null){
			return null;
		}
		
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		Sort sort = new Sort(new Order(Direction.DESC,"d.code"),new Order(Direction.ASC,"t.code"));
		pageable.setSort(sort);

		Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		return deliveryOrderDetailService.pageByHospital(user.getProjectCode(), pageable, hospital.getCode());
	}
	
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "hospital/returnRequest/success";
	}
	
	/**
	 * 生成退货单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkreturn")
	@ResponseBody
	public Message mkreturn(String data, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(data);
			
			if(user.getOrganization().getOrgType()!=1)
				throw new Exception("您不是医院帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定医院");
			
			String ddbh = returnsRequestService.mkreturn(user.getProjectCode(), data, user);
			message.setData(ddbh);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}

}
