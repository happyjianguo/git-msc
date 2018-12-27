package com.shyl.msc.count.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.sys.entity.User;

/**
 * 药品交易排名
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/count/productC1")
public class ProductC1Controller extends BaseController {
	
	@Resource
	IProductCService productCService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user){
		
		return "count/productC1/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductC> page(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.DESC,"t.purchaseNum"));
		pageable.setSort(sort);
		
		DataGrid<ProductC>  page = productCService.query(user.getProjectCode(),pageable);
		return page;
	}
	
	
	
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
