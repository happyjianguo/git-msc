package com.shyl.msc.b2b.report.controller;


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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.stl.service.IAccountProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 3、交易汇总统计（含图）
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/trade")
public class TradeController extends BaseController {

	@Resource
	private IPurchaseOrderService purchaseOrderService;
	
	@Resource
	private IAccountProductService accountProductService;
	
	@Override
	protected void init(WebDataBinder binder) {
	}
	
	@RequestMapping("")
	public String home(){
		return "b2b/report/trade/list";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.GET)
	public String chart(String year,Model model){
		model.addAttribute("year", year);
		return "b2b/report/trade/chart";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart(String year, @CurrentUser User user){
		System.out.println("------year-----"+year);
		Message message = new Message();
		try {
			List<Map<String,Object>> emptylist = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < 12; i++) {
				Map<String,Object> m  =new HashMap<String,Object>();
				m.put("MONTH", year+"-"+((100+i+1)+"").substring(1, 3));
				m.put("ORDERSUM", 0);
				emptylist.add(m);
			}
			
			List<Map<String, Object>> list = accountProductService.reportTrade(user.getProjectCode(), year);
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

	@RequestMapping("/list")
	@ResponseBody
	public DataGrid<Map<String, Object>> list(String year, @CurrentUser User user){
		DataGrid<Map<String, Object>> page= new DataGrid<>();
		System.out.println("------year-----"+year);
		List<Map<String, Object>> list = accountProductService.reportTrade(user.getProjectCode(), year);
		page.setRows(list);
		page.setTotal(list.size());
		page.addFooter("MONTH", "ORDERSUM");
		return page;
	}
	
}
