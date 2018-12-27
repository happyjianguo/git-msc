package com.shyl.msc.b2b.report.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 10、gpo交易汇总统计（含图）
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/gpotrade")
public class GPOTradeController extends BaseController {
	
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private ICompanyService companyService;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}

	@RequestMapping("list")
	public String list(){
		return "b2b/report/gpotrade/list";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.GET)
	public String chart(Long vendorId, String year, String vendorName,Model model){
		
		model.addAttribute("vendorId", vendorId);
		model.addAttribute("year", year);
		model.addAttribute("vendorName", vendorName);
		return "b2b/report/gpotrade/chart";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart(Long vendorId, String year, @CurrentUser User user){
		Message message = new Message();
		try {
			Company vendor = companyService.getById(user.getProjectCode(), vendorId);
			List<Map<String,Object>> emptylist = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < 12; i++) {
				Map<String,Object> m  =new HashMap<String,Object>();
				m.put("MONTH", year+"-"+((100+i+1)+"").substring(1, 3));
				m.put("ORDERSUM", 0);
				emptylist.add(m);
			}
			
			List<Map<String, Object>> list = purchaseOrderService.reportVendorTrade(user.getProjectCode(), vendor.getCode(), year);

			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> m  = list.get(i);
				String month = m.get("MONTH")+"";
				int flagi = Integer.parseInt(month.substring(month.length()-2,month.length()));
				emptylist.set(flagi-1, m); 
			}
			
			message.setData(emptylist);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return message;
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> list(PageRequest pageable, String name, String year, @CurrentUser User user){
		System.out.println("------name-----"+name);
		System.out.println("------year-----"+year);
		DataGrid<Map<String, Object>> page = purchaseOrderDetailService.reportVendorTrade(user.getProjectCode(), pageable, name, year);
		Map<String, Object> map = purchaseOrderService.totalTradeByYear(user.getProjectCode(), year);
		BigDecimal totalsum =  new BigDecimal(0);
		if(map.get("TOTALSUM") != null){
			totalsum = new BigDecimal(map.get("TOTALSUM").toString());
			System.out.println("------totalsum------"+totalsum);
		}
		
		List<Map<String, Object>> list = page.getRows();
		for(Map<String, Object> m:list){
			BigDecimal orderSum = new BigDecimal(m.get("ORDERSUM").toString());
			if(totalsum.compareTo(new BigDecimal(0)) != 0){
				String proportion = orderSum.multiply(new BigDecimal(100)).divide(totalsum, 2, BigDecimal.ROUND_HALF_UP) + "%";
				m.put("PROPORTION", proportion);
			}
		}
		return page;
	}

}
