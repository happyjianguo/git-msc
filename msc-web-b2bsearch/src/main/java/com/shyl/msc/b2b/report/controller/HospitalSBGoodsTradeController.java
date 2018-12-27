package com.shyl.msc.b2b.report.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.sys.entity.User;

/**
 * 2、医疗机构社保药统计
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/hospitalsbgoodstrade")
public class HospitalSBGoodsTradeController extends BaseController {
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
		System.out.println("inin");
		return "b2b/report/hospitalsbgoodstrade/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, String>> page(String name,String yearS,String monthS,String yearE,String monthE,PageRequest pageable,@CurrentUser User user){
		name = name == null?"":name;
		String dataS = yearS+monthS;
		String dataE = yearE+monthE;
		DataGrid<Map<String, String>> page =  purchaseOrderDetailService.report2(user.getProjectCode(), name, dataS,dataE,pageable);
		return page;
	}
	

}
