package com.shyl.msc.b2b.monitor.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
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
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.hospital.entity.LackProduct;
import com.shyl.msc.b2b.hospital.service.ILackProductService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/b2b/monitor/lackProduct")
public class LackProductController extends BaseController {

	@Resource
	private ILackProductService lackProductService;
	
	@RequestMapping("")
	public String home() {
		return "b2b/monitor/lackProduct/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("a#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Sort sort = new Sort(new Order(Direction.DESC,"NUM"));
		page.setSort(sort);
		DataGrid<Map<String,Object>> result = lackProductService.queryByCount(user.getProjectCode(), page);
		return result;
	}
	
	@RequestMapping(value = "/mxpage", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> mxpage(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("a#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		page.setSort(new Sort(new Order(Direction.DESC,"a.hospitalCode"),new Order(Direction.DESC,"a.month")));
		return lackProductService.queryByMx(user.getProjectCode(), page);
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, HttpServletResponse resp, PageRequest page){
		page.setSort(new Sort(new Order(Direction.DESC,"a.month")));
		try {
			List<Map<String, Object>> datas = lackProductService.queryBy(user.getProjectCode(),page);
			String heanders [] = {"年月","药品编码","药品名称",
					"剂型","规格","包装规格","生产企业","是否GPO药品","上报次数"};
			String beannames [] = {"MONTH","CODE","NAME","DOSAGEFORMNAME","MODEL",
					"PACKDESC","PRODUCERNAME","ISGPOPURCHASE","NUM"};
			Map<String, Map<Object,Object>> ma = new HashMap<>();
			Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
			ISGPOPURCHASE.put(new BigDecimal(0), "否");
			ISGPOPURCHASE.put(new BigDecimal(1), "是");
			ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames,null, ma);
			Workbook workbook = excelUtil.doExportXLS(datas, "短缺药品汇总统计", false, true);
			
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

	}

}
