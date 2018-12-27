package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.shyl.sys.dto.Message;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrder.Status;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.sys.entity.User;

/**
 * 配送单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/deliveryOrder")
public class DeliveryOrderController extends BaseController {

	@Resource
	private IDeliveryOrderService deliveryOrderService;
	
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code, String purchaseOrderCode){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		model.addAttribute("purchaseOrderCode", purchaseOrderCode);
		return "b2b/monitor/deliveryOrder/list";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<DeliveryOrder> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(user.getOrganization().getOrgType() == 1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 2){
			query.put("t#vendorCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 6){
			query.put("t#gpoCode_S_EQ", user.getOrganization().getOrgCode());
		}
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", DeliveryOrder.Status.valueOf(status).ordinal());
		}
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);

//		return deliveryOrderService.query(user.getProjectCode(), pageable);
		return deliveryOrderService.listByDeliveryOrderAndDetail(pageable);
	}
	
	/**
	 * 分页查询 待收货状态,医院查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/statuspage")
	@ResponseBody
	public DataGrid<DeliveryOrder> statuspage(PageRequest pageable, @CurrentUser User user){
		DataGrid<DeliveryOrder> page = new DataGrid<DeliveryOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 1 || user.getOrganization().getOrgId() == null){
			return page;
		}
		
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode()+"");
		query.put("t#status_I_NE", Status.closed);
		Sort sort = new Sort(Direction.DESC,"receiveDate");
		pageable.setSort(sort);
		page =  deliveryOrderService.query(user.getProjectCode(), pageable);

		return page;
	}


	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<DeliveryOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		String status = (String)pageable.getQuery().get("op#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("op#status_S_EQ",DeliveryOrder.Status.valueOf(status).ordinal());
		}
		DataGrid<DeliveryOrderDetail> page = deliveryOrderDetailService.queryByCode(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum","inOutBoundGoodsNum","returnsGoodsNum");
		return page;
	}
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/moreinfo", method = RequestMethod.GET)
	public String moreinfo(Long id,Model model, @CurrentUser User user){
		DeliveryOrderDetail d = deliveryOrderDetailService.getById(user.getProjectCode(), id);
		model.addAttribute("reporturl",d.getInspectionReportUrl());
		return "b2b/monitor/deliveryOrder/moreinfo";
	}

	@RequestMapping(value = "/doDelete", method = RequestMethod.POST)
	@ResponseBody
	public Message doDelete (Long id,@CurrentUser User user){
		Message message = new Message();
		//回写  采购单  采购订单明显哦  采购计划  采购计划明细   合同的配送数量
		DeliveryOrder deliveryOrder = deliveryOrderService.getById(user.getProjectCode(),id);
		PageRequest pageRequest = new PageRequest();
		pageRequest.getQuery().put("t.deliveryOrder.id_L_EQ",deliveryOrder.getId());
		List<DeliveryOrderDetail> deliveryOrderDetails = deliveryOrderDetailService.list(user.getProjectCode(),pageRequest);
		try {
			deliveryOrderService.returnDeliveryBack(user.getProjectCode(),deliveryOrder,deliveryOrderDetails);
		}catch (Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return message;
	}
}
