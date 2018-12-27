package com.shyl.msc.b2b.monitor.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 采购订单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/purchaseOrder")
public class PurchaseOrderController extends BaseController {
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IPurchaseClosedRequestService purchaseClosedRequestService;
	@Resource
	private IPurchaseClosedRequestDetailService purchaseClosedRequestDetailService;
	@Resource
	private ISnService snService;
	@Override
	protected void init(WebDataBinder binder) {

	}


	
	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code, String purchaseOrderPlanCode){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		model.addAttribute("purchaseOrderPlanCode", purchaseOrderPlanCode);
		return "b2b/monitor/purchaseOrder/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<PurchaseOrder> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		String filter = pageable.getFilterRules();
		if(user.getOrganization().getOrgType() == 1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 2){
			query.put("t#vendorCode_S_EQ", user.getOrganization().getOrgCode());
		}else if(user.getOrganization().getOrgType() == 6){
			query.put("t#gpoCode_S_EQ", user.getOrganization().getOrgCode());
		}
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", PurchaseOrder.Status.valueOf(status).ordinal());
		}
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);
		//return purchaseOrderService.query(user.getProjectCode(), pageable);
		return purchaseOrderService.listBypurchaseOrderAndDetail(pageable);
	}
	
	@RequestMapping(value="/export",method=RequestMethod.GET)
	public void export(PageRequest page, @CurrentUser User user, HttpServletResponse resp){ 
		Map<String, Object> query = page.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			page.setQuery(query);
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
			query.put("t#status_S_EQ", PurchaseOrder.Status.valueOf(status).ordinal());
		}
		Sort sort = new Sort(new Order(Direction.ASC,"t.code"));
		page.setSort(sort);
		List<Map<String,Object>> list = purchaseOrderService.listByNODelivery(page);
		String[] headers = {"CODE","DRUGCODE","NAME","DOSAGEFORMNAME","MODEL","PACKDESC","AUTHORIZENO","PRODUCERNAME","VENDORNAME","GOODSNUM","PRICE","GOODSSUM","DELIVERYGOODSNUM","INOUTBOUNDGOODSNUM","RETURNSGOODSNUM","INTERNALCODE","CONTRACTDETAILCODE"};
		String[] beanNames = {"订单编号","药品编码","药品名称","剂型","规格","包装规格","批准文号","生产厂家","供应商名称","采购数量","单价","采购金额","实际配送数量","入库数量","退货数量","医院内部编码","合同明细编号"};
		try{
			ExcelUtil util = new ExcelUtil(beanNames, headers);
			Workbook workbook = util.doExportXLS(list, "订单明细", false, true);
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=DrugAnalysis.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch(IOException e){
			
		}
	}
	
	/**
	 * 分页查询 未结案状态 医院
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/statuspage")
	@ResponseBody
	public DataGrid<PurchaseOrder> statuspage(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrder> page = new DataGrid<PurchaseOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 1 || user.getOrganization().getOrgId() == null){
			return page;
		}
//		Map<String, Object> query = new HashMap<String, Object>();
//		query.put("t#hospitalId_L_EQ", user.getOrganization().getOrgId());
//		query.put("t#status_I_NE", Status.forceClosed);
//		query.put("t#status_I_NE", Status.autoClosed);
//		pageable.setQuery(query);
		Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		page =  purchaseOrderService.queryUnClosed(user.getProjectCode(), hospital.getCode(),pageable);

		return page;
	}
	
	/**
	 * 分页查询 未结案状态 
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/statusGpage")
	@ResponseBody
	public DataGrid<PurchaseOrder> statusGpage(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrder> page = new DataGrid<PurchaseOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return page;
		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#vendorId_L_EQ", user.getOrganization().getOrgId());
		query.put("t#status_I_NE", Status.forceClosed);
		query.put("t#status_L_NE", Status.sent);
		pageable.setQuery(query);
		page =  purchaseOrderService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	@RequestMapping(value="/add", method = RequestMethod.GET)
	public String add(){
		return "b2b/monitor/purchaseOrder/add";
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	public String edit(){
		return "b2b/monitor/purchaseOrder/edit";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user){
		Message message = new Message();
		try {
			purchaseOrderService.delete(user.getProjectCode(), id);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<PurchaseOrderDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		String status = (String)pageable.getQuery().get("op#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("op#status_S_EQ", PurchaseOrder.Status.valueOf(status).ordinal());
		}
		DataGrid<PurchaseOrderDetail> page = purchaseOrderDetailService.queryBycode(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum","deliveryGoodsNum","inOutBoundGoodsNum","returnsGoodsNum");
		return page;
	}
	/**
	 * 申请结案
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/closed")
	@ResponseBody
	public Message closed(Long id, String reason, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType() !=null && user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					PurchaseOrder purchaseOrder = purchaseOrderService.getById(user.getProjectCode(), id);
					PurchaseClosedRequest purchaseClosedRequest = purchaseClosedRequestService.findByPurchaseOrderCode(user.getProjectCode(), purchaseOrder.getCode(), PurchaseClosedRequest.ClosedType.orderClosed);
					if(purchaseClosedRequest != null){
						message.setSuccess(false);
						message.setMsg("该订单已经提交申请结案");
						return message;
					}else{
						purchaseClosedRequest = new PurchaseClosedRequest();
					}
					purchaseClosedRequest.setPurchaseOrderCode(purchaseOrder.getCode());
					purchaseClosedRequest.setClosedMan(user.getName());
					purchaseClosedRequest.setClosedRequestDate(new Date());
					purchaseClosedRequest.setStatus(PurchaseClosedRequest.Status.unaudit);
					purchaseClosedRequest.setReason(reason);
					
					purchaseClosedRequest.setCode(snService.getCode(user.getProjectCode(), OrderType.orderClosedRequest));
					purchaseClosedRequest.setInternalCode(purchaseOrder.getInternalCode());
					purchaseClosedRequest.setOrderDate(purchaseOrder.getOrderDate());
					purchaseClosedRequest.setVendorCode(purchaseOrder.getVendorCode());
					purchaseClosedRequest.setVendorName(purchaseOrder.getVendorName());
					purchaseClosedRequest.setHospitalCode(purchaseOrder.getHospitalCode());
					purchaseClosedRequest.setHospitalName(purchaseOrder.getHospitalName());
					purchaseClosedRequest.setWarehouseCode(purchaseOrder.getWarehouseCode());
					purchaseClosedRequest.setWarehouseName(purchaseOrder.getWarehouseName());
					purchaseClosedRequest.setNum(purchaseOrder.getNum());
					purchaseClosedRequest.setSum(purchaseOrder.getSum());
					purchaseClosedRequest.setGpoCode(purchaseOrder.getGpoCode());
					purchaseClosedRequest.setGpoName(purchaseOrder.getGpoName());
					purchaseClosedRequest.setIsPass(0);
					purchaseClosedRequest.setIsAuto(0);
					purchaseClosedRequest.setOrderType(OrderType.orderClosedRequest);
					purchaseClosedRequest.setClosedType(PurchaseClosedRequest.ClosedType.orderClosed);
					
					purchaseClosedRequestService.save(user.getProjectCode(), purchaseClosedRequest);
					message.setMsg("申请结案成功");
				}else{
					message.setMsg("您不是医院身份登录");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("程序出错");
		}
		return message;
	}
	
	@RequestMapping(value="/closedDetail")
	@ResponseBody
	public Message closedDetail(Long id, String reason, String data, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType() !=null && user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					message = purchaseClosedRequestService.savePurchaseRequest(user.getProjectCode(), id, reason, data, user);
				}else{
					message.setMsg("您不是医院身份登录");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("程序出错");
		}
		return message;
	}
}
