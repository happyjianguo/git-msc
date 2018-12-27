package com.shyl.msc.b2b.report.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchasePlanDetailService;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.sys.entity.User;

/**
 * 各类单药品统计汇总
 * 
 * @author a_Q
 *
 */

@Controller
@RequestMapping("/b2b/report/productReport")
public class ProductReportController extends BaseController {

	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IDeliveryOrderDetailService deliveryOrderDetailService;
	@Resource
	private IInOutBoundDetailService inOutBoundDetailService;
	@Resource
	private IReturnsOrderDetailService returnsOrderDetailService;
	@Resource
	private IPurchasePlanDetailService purchasePlanDetailService;
	@RequestMapping("")
	public String home(){
		return "/b2b/report/productReport/list";
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable,String orderType,
			String startDate, String endDate, String hospitalCode, String productCode, String productName, @CurrentUser User user){
		System.out.println("---orderType--"+orderType);
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(orderType.equals("1")){
			if(user.getOrganization().getOrgType() == 1){//医院账号
				query.put("c#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}else if(user.getOrganization().getOrgType() == 2){//供应商账号
				query.put("c#vendorCode_S_EQ", user.getOrganization().getOrgCode());
			}
			Sort sort = new Sort(new Order(Direction.ASC,"b.code"));
			pageable.setSort(sort);
			query.put("c#createDate_D_GE", startDate);
			query.put("c#createDate_D_LE", endDate);
			query.put("b#code_S_LK", productCode);
			query.put("b#name_S_LK", productName);
		}else{
			if(user.getOrganization().getOrgType() == 1){//医院账号
				query.put("po#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}else if(user.getOrganization().getOrgType() == 2){//供应商账号
				query.put("po#vendorCode_S_EQ", user.getOrganization().getOrgCode());
			}
			Sort sort = new Sort(new Order(Direction.ASC,"b.code"));
			pageable.setSort(sort);
			query.put("po#createDate_D_GE", startDate);
			query.put("po#createDate_D_LE", endDate);
			query.put("b#code_S_LK", productCode);
			query.put("b#name_S_LK", productName);
		}
		if(orderType.equals("1")){
			return contractDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("2")){
			return purchaseOrderDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("3")){
			return deliveryOrderDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("4")){
			return inOutBoundDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("5")){
			return returnsOrderDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("6")){
			return purchasePlanDetailService.pageByProductReport(user.getProjectCode(), pageable);
		}
		return new DataGrid<>();
	}
	
	@RequestMapping("/detail")
	public String detail(String orderType, String productCode, String startDate, String endDate, String hospitalCode, Model model){
		model.addAttribute("productCode", productCode==null?"":productCode);
		model.addAttribute("startDate", startDate==null?"":startDate);
		model.addAttribute("endDate", endDate==null?"":endDate);
		model.addAttribute("hospitalCode", hospitalCode==null?"":hospitalCode);
		model.addAttribute("orderType", orderType);
		return "b2b/report/productReport/detail";
	}
	
	@RequestMapping("/mxPage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxPage(PageRequest pageable,String productCode, 
			String orderType, String startDate, String endDate, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		if(orderType.equals("1")){
			if(user.getOrganization().getOrgType() == 1){//医院账号
				query.put("c#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}else if(user.getOrganization().getOrgType() == 2){//供应商账号
				query.put("c#vendorCode_S_EQ", user.getOrganization().getOrgCode());
			}
			Sort sort = new Sort(new Order(Direction.ASC,"p.code"));
			pageable.setSort(sort);
			query.put("c#createDate_D_GE", startDate);
			query.put("c#createDate_D_LE", endDate);
			query.put("p#code_S_LK", productCode);
		}else{
			if(user.getOrganization().getOrgType() == 1){//医院账号
				query.put("po#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			}else if(user.getOrganization().getOrgType() == 2){//供应商账号
				query.put("po#vendorCode_S_EQ", user.getOrganization().getOrgCode());
			}
			Sort sort = new Sort(new Order(Direction.ASC,"p.PRODUCTCODE"));
			pageable.setSort(sort);
			query.put("po#createDate_D_GE", startDate);
			query.put("po#createDate_D_LE", endDate);
			query.put("p#PRODUCTCODE_S_LK", productCode);
		}
		if(orderType.equals("1")){
			return contractDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("2")){
			return purchaseOrderDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("3")){
			return deliveryOrderDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("4")){
			return inOutBoundDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("5")){
			return returnsOrderDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}else if(orderType.equals("6")){
			return purchasePlanDetailService.pageByProductDetailReport(user.getProjectCode(), pageable);
		}
		return new DataGrid<>();
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, String orderType, String startDate, String endDate,
			String hospitalCode, String productCode, String productName, HttpServletResponse resp, PageRequest pageable){
		try {
			Map<String, Object> query = pageable.getQuery();
			String name = "";
			if(orderType.equals("1")){
				if(user.getOrganization().getOrgType() == 1){//医院账号
					query.put("c#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
				}else if(user.getOrganization().getOrgType() == 2){//供应商账号
					query.put("c#vendorCode_S_EQ", user.getOrganization().getOrgCode());
				}
				Sort sort = new Sort(new Order(Direction.ASC,"b.code"));
				pageable.setSort(sort);
				query.put("c#createDate_D_GE", startDate);
				query.put("c#createDate_D_LE", endDate);
				query.put("b#code_S_LK", productCode);
				query.put("b#name_S_LK", productName);
			}else{
				if(user.getOrganization().getOrgType() == 1){//医院账号
					query.put("po#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
				}else if(user.getOrganization().getOrgType() == 2){//供应商账号
					query.put("po#vendorCode_S_EQ", user.getOrganization().getOrgCode());
				}
				Sort sort = new Sort(new Order(Direction.ASC,"b.code"));
				pageable.setSort(sort);
				query.put("po#createDate_D_GE", startDate);
				query.put("po#createDate_D_LE", endDate);
				query.put("b#code_S_LK", productCode);
				query.put("b#name_S_LK", productName);
			}
			List<Map<String, Object>> datas = new ArrayList<>();
			if(orderType.equals("1")){
				name += "合同药品汇总统计";
				datas = contractDetailService.listByProductReport(user.getProjectCode(), pageable);
			}else if(orderType.equals("2")){
				name += "订单药品汇总统计";
				datas = purchaseOrderDetailService.listByProductReport(user.getProjectCode(), pageable);
			}else if(orderType.equals("3")){
				name += "配送单药品汇总统计";
				datas = deliveryOrderDetailService.listByProductReport(user.getProjectCode(), pageable);
			}else if(orderType.equals("4")){
				name += "入库单药品汇总统计";
				datas = inOutBoundDetailService.listByProductReport(user.getProjectCode(), pageable);
			}else if(orderType.equals("5")){
				name += "退货单药品汇总统计";
				datas = returnsOrderDetailService.listByProductReport(user.getProjectCode(), pageable);
			}else if(orderType.equals("6")){
				name += "订单计划药品汇总统计";
				datas = purchasePlanDetailService.listByProductReport(user.getProjectCode(), pageable);
			}
			String heanders [] = {"药品编码","药品名称",
					"剂型","规格","单位","包装规格","国药准字","生产企业","数量","单价","金额","是否GPO药品"};
			String beannames [] = {"PRODUCTCODE","PRODUCTNAME","DOSAGEFORMNAME","MODEL",
					"UNITNAME","PACKDESC","AUTHORIZENO","PRODUCERNAME","NUM","PRICE","AMT","ISGPOPURCHASE"};
			Map<String, Map<Object,Object>> ma = new HashMap<>();
			Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
			ISGPOPURCHASE.put(new BigDecimal(0), "否");
			ISGPOPURCHASE.put(new BigDecimal(1), "是");
			ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, null,ma);
			Workbook workbook = excelUtil.doExportXLS(datas, name, false, true);
			
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=report.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub

	}

}
