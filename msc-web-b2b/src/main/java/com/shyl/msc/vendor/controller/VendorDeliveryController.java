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
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 订单配送Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/delivery")
public class VendorDeliveryController extends BaseController {
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private ICompanyService companyService;
	
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/delivery/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<PurchaseOrder> page(PageRequest pageable, @CurrentUser User user){
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
		query.put("t#status_S_NE", Status.sent);
		query.put("t#status_L_NE", Status.forceClosed);
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);
		return purchaseOrderService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public List<PurchaseOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		List<PurchaseOrderDetail> list = purchaseOrderDetailService.list(user.getProjectCode(), pageable);
		//returnsGoodsNum 借用为剩余需配送的数量
		for (PurchaseOrderDetail pd : list) {
			BigDecimal d = pd.getDeliveryGoodsNum()==null?new BigDecimal(0):pd.getDeliveryGoodsNum();
			BigDecimal g = pd.getGoodsNum()==null?new BigDecimal(0):pd.getGoodsNum();
			System.out.println(d+","+g);
			BigDecimal r = g.subtract(d);
			r = r.compareTo(new BigDecimal(0))>0?r:new BigDecimal(0);
			pd.setReturnsGoodsNum(r);
		}
		return list;
	}
	
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "vendor/delivery/para";
	}
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/mxpara", method = RequestMethod.GET)
	public String mxpara(){
		return "vendor/delivery/mxpara";
	}
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/delivery/success";
	}
	
	/**
	 * 生成配送单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkdelivery")
	@ResponseBody
	public Message mkdelivery(Long orderId,String senderCode,String internalCode,Long[] detailIds,BigDecimal[] nums,String[] batchCodes,String[] batchDates,String[] expiryDates,String[] qualityRecords,String[] inspectionReportUrls, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println(orderId+","+detailIds+","+nums);
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			//条形码唯一性检查
			PageRequest pageable = new PageRequest();
			pageable.getQuery().put("t#barcode_S_EQ", internalCode);
			DeliveryOrder d = deliveryOrderService.getByKey(user.getProjectCode(),pageable);
			if(d != null){
				throw new Exception("条形码已使用，请检查");
			}
			
			String ddbh = deliveryOrderService.mkdelivery(user.getProjectCode(), orderId,senderCode,internalCode,detailIds,nums,batchCodes,batchDates,expiryDates,qualityRecords,inspectionReportUrls);
			message.setData(ddbh);
			//System.out.println("ddbh="+pp.getCode());
			//message.setData(ddbh);
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
