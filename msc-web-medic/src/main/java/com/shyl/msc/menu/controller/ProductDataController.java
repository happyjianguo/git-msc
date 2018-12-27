package com.shyl.msc.menu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.menu.entity.ProductData;
import com.shyl.msc.menu.service.IProductDataService;
import com.shyl.msc.task.ProductFoundAsyncTask;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 国产，进口药品数据
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/menu/productData")
public class ProductDataController extends BaseController {

	@Resource
	private IProductDataService productDataService;
	@Resource
	private ProductFoundAsyncTask productFoundAsyncTask;
	
	@Override
	protected void init(WebDataBinder arg0) {
		
	}

	@RequestMapping("")
	public String home() {
		return "/menu/productData/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductData> list(PageRequest pageable, @CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"id");
		pageable.setSort(sort);
		return productDataService.query(user.getProjectCode(), pageable);
	}
	
	@RequestMapping("/snyc")
	@ResponseBody
	public Message snyc(int tableid) {
		Message msg = new Message();
		try {
			if( ProductFoundAsyncTask.status != 0) {
				if (ProductFoundAsyncTask.tableid == 25) {
					throw new MyException("正在抓取国产药品数据，抓取完毕再操作");
				} else if (ProductFoundAsyncTask.tableid == 36) {
					throw new MyException("正在抓取进口药品数据，抓取完毕再操作");
				} else if (ProductFoundAsyncTask.tableid == 63) {
					throw new MyException("正在抓取进口药品数据，抓取完毕再操作");
				}
			}

			ProductFoundAsyncTask.status = 1;
			productFoundAsyncTask.syncProductData(tableid);
			msg.setMsg("同步信息线程开启");
		} catch (MyException e) {
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	public Message stop() {
		Message msg = new Message();
		ProductFoundAsyncTask.status = 0;
		msg.setMsg("同步信息线程已停止");
		return msg;
	}
	
	
	@RequestMapping(value="/add",method = RequestMethod.GET)
	public String add() {
		return "/menu/productData/add";
	}

}
