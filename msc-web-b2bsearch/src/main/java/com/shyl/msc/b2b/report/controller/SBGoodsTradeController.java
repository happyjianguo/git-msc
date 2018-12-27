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
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 1、社保药品汇总统计（含图）
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/sbgoodstrade")
public class SBGoodsTradeController extends BaseController {
	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {
		
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/sbgoodstrade/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, String>> page(String year,PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String, String>> page =  purchaseOrderDetailService.report1(user.getProjectCode(), year,pageable);
		page.addFooter("YM", "SBSUM", "FSBSUM");
		return page;
	}

	@RequestMapping(value="/chart", method = RequestMethod.GET)
	public String chart(String year,Model model){
		model.addAttribute("year", year);
		return "b2b/report/sbgoodstrade/chart";
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
				m.put("YM", year+"-"+((100+i+1)+"").substring(1, 3));
				m.put("SBSUM", 0);
				m.put("FSBSUM", 0);
				emptylist.add(m);
			}
			
			List<Map<String, Object>> list = purchaseOrderDetailService.chart1(user.getProjectCode(), year);

			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> m  = list.get(i);
				String month = m.get("YM")+"";
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
}
