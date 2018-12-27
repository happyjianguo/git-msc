package com.shyl.msc.b2b.report.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.sys.entity.User;

/**
 * 5、医疗机构采购情况
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/hospitaltrade")
public class HospitalTradeController extends BaseController {

	@Resource
	private IPurchaseOrderService purchaseOrderService;
	
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/hospitaltrade/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(String name,String year,String month,PageRequest pageable,@CurrentUser User user){
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.report5(user.getProjectCode(), name, year+month,pageable);
		Map<String, Object> totalmap = purchaseOrderService.totalTradeByMonth(user.getProjectCode(), year+month);
		BigDecimal total = new BigDecimal("0");
		if(totalmap!=null && totalmap.containsKey("TOTALSUM") && totalmap.get("TOTALSUM") != null
				&& !StringUtils.isEmpty(totalmap.get("TOTALSUM").toString())){
			total = new BigDecimal(totalmap.get("TOTALSUM").toString());
			for (Map<String, Object> m : page.getRows()) {
				BigDecimal d = new BigDecimal(m.get("JE")+"");
				BigDecimal per = d.multiply(new BigDecimal("100")).divide(total, 2, BigDecimal.ROUND_HALF_UP);
				m.put("PER", per+"%");
			}
		}
		page.addFooter("HOSPITALNAME", "SL", "JE");
		return page;
	}
	
	/**
	 * 明细查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxpage(String hospitalcode,String year,String month,PageRequest pageable,@CurrentUser User user){
		System.out.println("hospitalcode:"+hospitalcode);
		System.out.println("year:"+year);
		System.out.println("month:"+month);
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.report5mx(user.getProjectCode(), hospitalcode, year+month,pageable);
		return page;
	}
	
	/**
	 * 导出
	 * @return
	 */
	@RequestMapping("/export")
	public void export(String name,String year,String month,PageRequest page,@CurrentUser User user ,HttpServletResponse resp){
		List<Map<String, Object>> list = purchaseOrderDetailService.reportAll(user.getProjectCode(), name, year+month,page);
		Map<String, Object> totalmap = purchaseOrderService.totalTradeByMonth(user.getProjectCode(), year+month);
		BigDecimal total = new BigDecimal("0");
		if(totalmap!=null && totalmap.containsKey("TOTALSUM") && totalmap.get("TOTALSUM") != null
				&& !StringUtils.isEmpty(totalmap.get("TOTALSUM").toString())){
			total = new BigDecimal(totalmap.get("TOTALSUM").toString());
			for (Map<String, Object> m : list) {
				BigDecimal d = new BigDecimal(m.get("JE")+"");
				BigDecimal per = d.multiply(new BigDecimal("100")).divide(total, 2, BigDecimal.ROUND_HALF_UP);
				m.put("PER", per+"%");
			}
		}
		String[] headers = new String[]{"HOSPITALNAME","SL","JE","PER"};
		String[] beanNames = new String[]{"医疗机构","采购品种数","采购金额（元）","占总采购额比例"};
		
		try{
			ExcelUtil util = new ExcelUtil(beanNames, headers);
			DecimalFormat df=new DecimalFormat("######0.00");
			BigDecimal sl = new BigDecimal(0d); //采购品种数
	 		BigDecimal je = new BigDecimal(0d);//采购金额
	 		for (Map<String, Object> map : list) {
				sl =sl.add((BigDecimal)map.get("SL"));
				je =je.add((BigDecimal)map.get("JE"));
			}
	 		Map<String, Object> map = new HashMap<String, Object>();
	 		map.put("HOSPITALNAME", "合计");
	 		map.put("SL", df.format(sl.doubleValue()));
	 		map.put("JE", df.format(je.doubleValue()));
	 		list.add(map);
			Workbook workbook = util.doExportXLS(list, "抗菌药物占药品使用比例", false, true);
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
}
