package com.shyl.msc.count.controller;

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
import com.shyl.msc.count.service.IOrderCService;
import com.shyl.msc.count.service.IOrderDetailCService;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.sys.entity.User;

/**
 * 7、品种交易汇总
 * 
 *
 */
@Controller
@RequestMapping("/count/goodstrade")
public class GoodsTradeCController extends BaseController {
	@Resource
	private IProductCService productCService;

	@Resource
	private IOrderDetailCService orderDetailCService;

	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home() {
		return "count/goodstrade/list";
	}

	/**
	 * 分页查询
	 * 
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(String dateS, String dateE, PageRequest pageable,
			@CurrentUser User user) {
		DataGrid<Map<String, Object>> page = productCService.reportCGoodsTrade(user.getProjectCode(), dateS, dateE,
				pageable);
		return page;
	}

	/**
	 * 明细查询
	 * 
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<Map<String, Object>> mxpage(Long id, String dateS, String dateE, PageRequest pageable,
			@CurrentUser User user) {
		DataGrid<Map<String, Object>> page = orderDetailCService.reportCGoodsTradeMX(user.getProjectCode(), id,
				dateS, dateE, pageable);
		return page;
	}

}
