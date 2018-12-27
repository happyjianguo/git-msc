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
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 9、生产厂家交易汇总统计（含图）
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/producertrade")
public class ProducerTradeController extends BaseController {

	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	
	@Override
	protected void init(WebDataBinder binder) {
	}
	@RequestMapping("list")
	public String list(){
		return "b2b/report/producertrade/list";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.GET)
	public String chart(Long producerId, String year, String producerName,Model model){
		System.out.println("------producerId-----"+producerId);
		System.out.println("------year-----"+year);
		System.out.println("------producerName-----"+producerName);
		model.addAttribute("producerId", producerId);
		model.addAttribute("year", year);
		model.addAttribute("producerName", producerName);
		return "b2b/report/producertrade/chart";
	}
	
	@RequestMapping(value="/chart", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart(Long producerId, String year, @CurrentUser User user){
		System.out.println("------producerId-----"+producerId);
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
			
			List<Map<String, Object>> list = purchaseOrderDetailService.reportProducerTrade(user.getProjectCode(), producerId, year);

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
		DataGrid<Map<String, Object>> page = purchaseOrderDetailService.reportProducerTrade(user.getProjectCode(), pageable, name, year);
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
