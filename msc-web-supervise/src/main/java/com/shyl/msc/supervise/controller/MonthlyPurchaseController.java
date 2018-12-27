package com.shyl.msc.supervise.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.shyl.msc.supervise.service.IMonthlyPurchaseService;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/supervise/monthlyPurchase")
public class MonthlyPurchaseController extends BaseController{

	@Resource
	private IMonthlyPurchaseService monthlyPurchaseService;
	
	@RequestMapping("")
	public String home() {
		return "/supervise/monthlyPurchase/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("l#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Order order1 = new Order(Direction.DESC,"l.month");
		Sort sort  = new Sort(order1); 
		page.setSort(sort);
		DataGrid<Map<String,Object>> result = monthlyPurchaseService.queryByPage(user.getProjectCode()+"_SUP", page);
		return result;
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user,HttpServletResponse resp, PageRequest page){
		try {
			List<Map<String, Object>> datas = monthlyPurchaseService.query(user.getProjectCode()+"_SUP",page);
			String heanders [] = {"年月","医院名称","药品编码","药品名称","剂型","规格","包装规格","生产企业","采购数量","gpo采购数量","非gpo采购数量","采购金额","gpo采购金额","非gpo采购金额","上月出库数量","单价","上月出库总金额","采购增长率(%)","是否gpo药品"};
			String beannames [] = {"MONTH","HOSPITALNAME","CODE","NAME","DOSAGEFORMNAME","MODEL","PACKDESC","PRODUCERNAME","NUM","GPONUM","NOTGPONUM","AMT","GPOAMT","NOTGPOAMT","OUTNUM","PRICE","OUTSUM","INRATIO","ISGPOPURCHASE"};
			Map<String, Map<Object,Object>> ma = new HashMap<>();
			Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
				ISGPOPURCHASE.put(new BigDecimal(0), "否");
				ISGPOPURCHASE.put(new BigDecimal(1), "是");
				ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
				ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, null,ma);
				DecimalFormat df=new DecimalFormat("######0.00");
				for (Map<String, Object> map : datas) {
					BigDecimal PRICE = new BigDecimal(0d); //单价
					BigDecimal INRATIO = new BigDecimal(0d);//采购增长率
					BigDecimal OUTSUM =(BigDecimal)map.get("OUTSUM");
					BigDecimal OUTNUM =(BigDecimal)map.get("OUTNUM");
					BigDecimal AMT = (BigDecimal)map.get("AMT");
				if(OUTNUM!=null&&OUTNUM.compareTo(BigDecimal.ZERO)!=0){
					PRICE=OUTSUM.divide(OUTNUM, 2, BigDecimal.ROUND_HALF_UP);
				}
				map.put("PRICE",df.format(PRICE.doubleValue()));
				if(OUTSUM!=null&&OUTNUM.compareTo(BigDecimal.ZERO)!=0){
					INRATIO= (AMT.subtract(OUTSUM)).divide(OUTSUM, 2, BigDecimal.ROUND_HALF_UP);
				}
				map.put("INRATIO",df.format(INRATIO.doubleValue()));
			}
			Workbook workbook = excelUtil.doExportXLS(datas, "采购记录汇总统计", false, true);
			
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
	protected void init(WebDataBinder arg0) {
	}

}
