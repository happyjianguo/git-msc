package com.shyl.msc.b2b.monitor.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.shyl.common.util.ExcelUtil;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
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
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 点单计划Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/purchaseOrderPlan")
public class PurchaseOrderPlanController extends BaseController {
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailService purchaseOrderPlanDetailService;
	
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code, String planCode){
		if(user.getOrganization().getOrgType() != null){
			if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 9){
				model.addAttribute("isMonitor", 1);
			}
		}
		model.addAttribute("code", code);
		model.addAttribute("planCode", planCode);
		return "b2b/monitor/purchaseOrderPlan/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<PurchaseOrderPlan> page(PageRequest pageable, @CurrentUser User user){
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
			query.put("t#status_S_EQ", PurchaseOrderPlan.Status.valueOf(status).ordinal());
		}
		query.put("t#status_S_NE", PurchaseOrderPlan.Status.hcancel.ordinal());
		Sort sort = new Sort(new Order(Direction.DESC,"code"));
		pageable.setSort(sort);
//		return purchaseOrderPlanService.query(user.getProjectCode(), pageable);
		return purchaseOrderPlanService.listBypurchaseOrderPlanAndDetail(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<PurchaseOrderPlanDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#status_S_NE", Status.hcancel.ordinal());
		String status = (String)pageable.getQuery().get("op#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("op#status_S_EQ", PurchaseOrderPlan.Status.valueOf(status).ordinal());
		}
		DataGrid<PurchaseOrderPlanDetail> page = purchaseOrderPlanDetailService.queryByCode(user.getProjectCode(), pageable);
		page.addFooter("productCode", "goodsNum","goodsSum");
		return page;
	}

	/**
	 * 导出Excel
	 * @param page
	 * @param user
	 * @param resp
	 */
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
			query.put("t#status_S_EQ", PurchaseOrderPlan.Status.valueOf(status).ordinal());
		}
		Sort sort = new Sort(new Order(Direction.ASC,"t.code"));
		page.setSort(sort);
		List<Map<String,Object>> list = purchaseOrderPlanService.listByUnEffect(user.getProjectCode(),page);
		String[] headers = {"CODE","DRUGCODE","NAME","DOSAGEFORMNAME","MODEL","PACKDESC","AUTHORIZENO","PRODUCERNAME","VENDORNAME","GOODSNUM","PRICE","GOODSSUM","DELIVERYGOODSNUM","INOUTBOUNDGOODSNUM","RETURNSGOODSNUM","INTERNALCODE","CONTRACTDETAILCODE"};
		String[] beanNames = {"订单计划编号","药品编码","药品名称","剂型","规格","包装规格","批准文号","生产厂家","供应商名称","采购数量","单价","采购金额","实际配送数量","入库数量","退货数量","医院内部编码","合同明细编号"};
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
	 * 审计划单
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	@ResponseBody
	public Message check(Long id,@CurrentUser User user){
		Message message = new Message();
		try{
			PurchaseOrderPlan p = purchaseOrderPlanService.getById(user.getProjectCode(), id);
			List<PurchaseOrderPlanDetail> dlist = purchaseOrderPlanDetailService.listByOrderPlanId(user.getProjectCode(), id);
			p.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.cancel);
			for (PurchaseOrderPlanDetail pd : dlist) {
				if(pd.getStatus().equals(com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status.normal)){
					p.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.effect);
				}
			}
			purchaseOrderPlanService.update(user.getProjectCode(), p);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 取消计划明细
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/cancelDetail", method = RequestMethod.POST)
	@ResponseBody
	public Message checkDetail(Long id,@CurrentUser User currentUser){
		Message message = new Message();
		try{
			if(currentUser.getOrganization().getOrgType() == 1){
				purchaseOrderPlanDetailService.checkDetailByH(currentUser.getProjectCode(), id);
			}else{
				throw new Exception("只有【医院角色】才能操作");
			}	
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	@Override
	protected void init(WebDataBinder binder) {

	}

}
