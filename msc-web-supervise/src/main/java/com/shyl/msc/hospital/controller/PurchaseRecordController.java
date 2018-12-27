package com.shyl.msc.hospital.controller;

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
import com.shyl.msc.b2b.hospital.entity.PurchaseRecord;
import com.shyl.msc.b2b.hospital.service.IPurchaseRecordService;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.entity.DrugAnalysisHospital;
import com.shyl.msc.supervise.service.IDrugAnalysisService;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/hospital/purchaseRecord")
public class PurchaseRecordController extends BaseController {

	@Resource
	private IPurchaseRecordService purchaseRecordService;
	@Resource
	private IDrugAnalysisService drugAnalysisService;
	
	@RequestMapping("")
	public String home() {
		return "/hospital/purchaseRecord/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String,Object>> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("l#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Order order1 = new Order(Direction.DESC,"l.month");
		Order order2 = new Order(Direction.DESC,"l.createDate");
		Sort sort  = new Sort(order1 ,order2); 
		page.setSort(sort);
		DataGrid<Map<String,Object>> result = purchaseRecordService.queryByPage(user.getProjectCode(), page);
		for (Map<String,Object> map : result.getRows()) {
			String month = map.get("MONTH").toString();
			Integer dd = Integer.parseInt(month.substring(6));
			String num = null;
			String sum = null;
			if(dd>1){
				dd=dd-1;
				month = month.substring(0, 6)+dd;
			}else{
				Integer ee = Integer.parseInt(month.substring(0,4));
				month = (ee-1)+"-12";
			}
			if(map.get("HOSPITALCODE")!=null&&map.get("CODE")!=null){
				System.out.println("code"+"---------"+map.get("CODE"));
				System.out.println("HOSPITALCODE"+"---------"+map.get("HOSPITALCODE"));
				System.out.println("month"+"---------"+month);
				List<Map<String,Object>> drugAnalysis = drugAnalysisService.queryByCode(user.getProjectCode()+"_SUP",month,map.get("HOSPITALCODE").toString(),map.get("CODE").toString());
				if(drugAnalysis!=null){
					for (Map<String, Object> map2 : drugAnalysis) {
						num = (String)map2.get("NUM");
						sum=(String)map2.get("SUM");
					}
				}
			}
			map.put("OUTNUM", num);
			map.put("OUTSUM", sum);
		}
		return result;
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, String startMonth, String toMonth, String hospitalCode, HttpServletResponse resp, PageRequest pageable){
		try {
			List<Map<String, Object>> datas = purchaseRecordService.query(user.getProjectCode(), startMonth, toMonth, hospitalCode);
			String heanders [] = {"年月","医院名称","药品编码","药品名称",
					"剂型","规格","包装规格","生产企业","交易平台","省药交产品ID","采购数量","采购金额"};
			String beannames [] = {"MONTH","HOSPITALNAME","CODE","NAME","DOSAGEFORMNAME","MODEL",
					"PACKDESC","PRODUCERNAME","PLATFORM","PRODUCTTRANID","NUM","AMT"};
			Map<String, Boolean> lineMap = new HashMap<>();
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, lineMap);
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
	protected void init(WebDataBinder binder) {

	}

}
