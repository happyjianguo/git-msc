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
import com.shyl.msc.count.entity.OrderDetailC;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.service.IOrderDetailCService;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.sys.entity.User;

/**
 * 7、品种交易汇总
 * 
 *
 */
@Controller
@RequestMapping("/count/productC2")
public class ProductC2Controller extends BaseController {

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
	public String home(){
		return "count/productC2/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */ 
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductC> page(PageRequest pageable, @CurrentUser User user){
		DataGrid<ProductC> page = productCService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 明细查询
	 * @param pageable
	 * @return
	 */ 
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<OrderDetailC> mxpage(PageRequest pageable, @CurrentUser User user){
		DataGrid<OrderDetailC> page =  orderDetailCService.query(user.getProjectCode(), pageable);
		return page;
	}

}
