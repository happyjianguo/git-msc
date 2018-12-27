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
import com.shyl.msc.b2b.hospital.entity.SupplyProduct;
import com.shyl.msc.b2b.hospital.service.ISupplyProductService;

import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/b2b/monitor/supplyProduct")
public class SupplyProductController extends BaseController {

	@Resource
	private ISupplyProductService supplyProductService;
	
	@RequestMapping("")
	public String home() {
		return "b2b/monitor/supplyProduct/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest page, @CurrentUser User user) {
		Order order1 = new Order(Direction.DESC,"l.month");
		Order order2 = new Order(Direction.ASC,"l.hospitalCode");
		Order order3 = new Order(Direction.DESC,"l.createDate");
		Sort sort  = new Sort(order1 ,order2,order3); 
		page.setSort(sort);
		DataGrid<Map<String, Object>> result = supplyProductService.queryByPage(user.getProjectCode(), page) ;
		return result;
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, String startMonth, String toMonth, String hospitalCode, HttpServletResponse resp, PageRequest pageable){
		try {
			List<Map<String, Object>> datas = supplyProductService.query(user.getProjectCode(), startMonth, toMonth, hospitalCode);
			String heanders [] = {"年月","医院名称","药品编码","药品名称",
					"剂型","规格","包装规格","生产企业","是否GPO药品","供应商编码","供应商名称","是否对照"};
			String beannames [] = {"MONTH","HOSPITALNAME","CODE","NAME","DOSAGEFORMNAME","MODEL",
					"PACKDESC","PRODUCERNAME","ISGPOPURCHASE","VENDORCODE","VENDORNAME","ISCOMPARE"};
			Map<String, Map<Object,Object>> ma = new HashMap<>();
			Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
			ISGPOPURCHASE.put(new BigDecimal(0), "否");
			ISGPOPURCHASE.put(new BigDecimal(1), "是");
			ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
			Map<Object,Object> ISCOMPARE = new HashMap<>();
			ISCOMPARE.put(new BigDecimal(0), "否");
			ISCOMPARE.put(new BigDecimal(1), "是");
			ma.put("ISCOMPARE", ISCOMPARE);
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, null,ma);
			Workbook workbook = excelUtil.doExportXLS(datas, "供应药品汇总统计", false, true);
			
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
