package com.shyl.msc.count.controller;

import java.math.BigDecimal;
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
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.count.entity.HospitalC;
import com.shyl.msc.count.entity.HospitalProductC;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.service.IHospitalCService;
import com.shyl.msc.count.service.IHospitalProductCService;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 库存周转率分析
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/count/hospitalC6")
public class HospitalC6Controller extends BaseController {
	
	@Resource
	IHospitalCService hospitalCService;
	@Resource
	IHospitalService hospitalService;
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<HospitalC> page(PageRequest pageable,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"t.month"));
		pageable.setSort(sort);
		
		DataGrid<HospitalC>  page = hospitalCService.query(user.getProjectCode(),pageable);
		
		
		return page;
	}
	
	/**
	 * 图标1 医院纬度
	 * @return
	 */
	@RequestMapping(value="/chart1", method = RequestMethod.GET)
	public String chart1(String hospitalCode,String hospitalName,Model model,@CurrentUser User user){
		Hospital h = hospitalService.findByCode(user.getProjectCode(), hospitalCode);
		model.addAttribute("hospitalCode", hospitalCode);
		model.addAttribute("hospitalName", h.getFullName());
		return "count/hospitalC6/chart1";
	}
	@RequestMapping(value="/chart1", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart1(String hospitalCode,PageRequest pageable,@CurrentUser User user){
		System.out.println("hospitalName="+hospitalCode);
		Message message = new Message();
		try {
			pageable.getQuery().put("t#hospitalCode_S_EQ", hospitalCode);
			Sort sort = new Sort(new Order(Direction.ASC,"t.month"));
			pageable.setSort(sort);
			
			List<HospitalC>  list = hospitalCService.list(user.getProjectCode(),pageable);
			
			
			message.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return message;
	}
	
	/**
	 * 图标2 年月纬度
	 * @return
	 */
	@RequestMapping(value="/chart2", method = RequestMethod.GET)
	public String chart2(String month,Model model,@CurrentUser User user){
		model.addAttribute("month", month);
		return "count/hospitalC6/chart2";
	}
	@RequestMapping(value="/chart2", method = RequestMethod.POST)
	@ResponseBody
	public Message Chart2(String month,PageRequest pageable,@CurrentUser User user){
		System.out.println("month="+month);
		Message message = new Message();
		try {
			pageable.getQuery().put("t#month_S_EQ", month);
			Sort sort = new Sort(new Order(Direction.ASC,"t.contractPurchaseSum"));
			pageable.setSort(sort);
			
			List<HospitalC>  list = hospitalCService.list(user.getProjectCode(),pageable);
			
			message.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return message;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
