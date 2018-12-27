package com.shyl.msc.b2b.report.controller;


import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.shyl.sys.dto.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.stl.service.IAccountProductService;
import com.shyl.sys.entity.User;

/**
 * 7、品种交易汇总
 * 
 *
 */
@Controller
@RequestMapping("/b2b/report/goodstrade")
public class GoodsTradeController extends BaseController {

	@Resource
	private IAccountProductService accountProductService;
	
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
		return "b2b/report/goodstrade/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */ 
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(String dateS, String dateE, PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String, Object>> page = accountProductService.reportGoodsTrade(user.getProjectCode(), dateS, dateE, pageable);
		return page;
	}
	
	/**
	 * 明细查询
	 * @param pageable
	 * @return
	 */ 
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxpage(Long id, String dateS, String dateE,PageRequest pageable, @CurrentUser User user){
		DataGrid<Map<String, Object>> page =  purchaseOrderDetailService.reportGoodsTradeMX(user.getProjectCode(), id, dateS, dateE, pageable);
		return page;
	}

	/**
	 * 统计
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/doCount", method = RequestMethod.POST)
	@ResponseBody
	public Message doCount(PageRequest pageable, @CurrentUser User user){
		Message message = new Message();
		try{
			accountProductService.passAccountBetter("01");
		}catch (Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}

}
