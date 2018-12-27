package com.shyl.msc.b2b.report.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.User;

/**
 * GPO药品采购情况
 * @author lenovo
 *
 */
@Controller
@RequestMapping("/b2b/report/productCentralized")
public class ProductCentralizedController extends BaseController {

	@Resource
	private IProductService	productService;
	
	@RequestMapping("")
	public String home(){
		return "b2b/report/productCentralized/chart";
	}

	@Override
	protected void init(WebDataBinder binder) {
		
	}

	/**
	 * GPO药品集中采购情况
	 * @return
	 */
	@RequestMapping("/chart")
	@ResponseBody
	
	public JSONObject chart(String year, String month,@CurrentUser User user){
		JSONObject chart = new JSONObject();
		chart.put("gpo", (BigDecimal)(productService.getGpoPercent(user.getProjectCode()).get("percent")));
		chart.put("centralized", productService.getCentralizedPercent(user.getProjectCode(), 0,year,month));
		return chart;
	}
}
