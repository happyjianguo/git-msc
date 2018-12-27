package com.shyl.msc.count.controller;

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
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.count.entity.VendorC;
import com.shyl.msc.count.entity.VendorProductC;
import com.shyl.msc.count.service.IHospitalCService;
import com.shyl.msc.count.service.IProductCService;
import com.shyl.msc.count.service.IVendorCService;
import com.shyl.msc.count.service.IVendorProductCService;
import com.shyl.msc.enmu.VendorMethod;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 供货质量分析
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/count/vendorC2")
public class VendorC2Controller extends BaseController {
	
	@Resource
	IVendorCService vendorCService;
	@Resource
	IVendorProductCService vendorProductCService;
	@Resource
	ICompanyService companyService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user){
		return "count/vendorC2/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<VendorC> page(PageRequest pageable,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"t.month"));
		pageable.setSort(sort);
		
		DataGrid<VendorC>  page = vendorCService.query(user.getProjectCode(),pageable);
		//平均有效天数
		for (int i = 0; i < page.getRows().size(); i++) {
			VendorC vc = page.getRows().get(i);
			PageRequest p = new PageRequest();
			p.getQuery().put("t#month_S_EQ", vc.getMonth());
			p.getQuery().put("t#vendorCode_S_EQ", vc.getVendorCode());
			List<VendorProductC> l = vendorProductCService.list(user.getProjectCode(), p);
			Integer dayCount = 0;
			for (int j = 0; j < l.size(); j++) {
				VendorProductC vp = l.get(j);
				dayCount += vp.getValidityDayCount();
			}
			if(l.size()>0){
				dayCount = dayCount/l.size();
			}
			vc.setSegmentStr(dayCount+"");
		}
		return page;
	}
	
	/**
	 * 图标1 医院纬度
	 * @return
	 */
	@RequestMapping(value="/chart1", method = RequestMethod.GET)
	public String chart1(String vendorCode,Model model,@CurrentUser User user){
		Company v = companyService.findByCode1(user.getProjectCode(), vendorCode);
		model.addAttribute("vendorCode", vendorCode);
		model.addAttribute("vendorName", v.getFullName());
		return "count/vendorC2/chart1";
	}
	@RequestMapping(value="/chart1", method = RequestMethod.POST)
	@ResponseBody
	public Message chart1(String vendorCode,PageRequest pageable,@CurrentUser User user){
		System.out.println("vendorName="+vendorCode);
		Message message = new Message();
		try {
			pageable.getQuery().put("t#vendorCode_S_EQ", vendorCode);
			Sort sort = new Sort(new Order(Direction.ASC,"t.month"));
			pageable.setSort(sort);
			
			List<VendorC>  list = vendorCService.list(user.getProjectCode(),pageable);
			//平均有效天数
			for (int i = 0; i < list.size(); i++) {
				VendorC vc = list.get(i);
				PageRequest p = new PageRequest();
				p.getQuery().put("t#month_S_EQ", vc.getMonth());
				p.getQuery().put("t#vendorCode_S_EQ", vc.getVendorCode());
				List<VendorProductC> l = vendorProductCService.list(user.getProjectCode(), p);
				Integer dayCount = 0;
				for (int j = 0; j < l.size(); j++) {
					VendorProductC vp = l.get(j);
					dayCount += vp.getValidityDayCount();
				}
				if(l.size()>0){
					dayCount = dayCount/l.size();
				}
				vc.setSegmentStr(dayCount+"");
			}
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
		return "count/vendorC2/chart2";
	}
	@RequestMapping(value="/chart2", method = RequestMethod.POST)
	@ResponseBody
	public Message chart2(String month,PageRequest pageable,@CurrentUser User user){
		System.out.println("month="+month);
		Message message = new Message();
		try {
			pageable.getQuery().put("t#month_S_EQ", month);
			Sort sort = new Sort(new Order(Direction.ASC,"t.contractPurchaseSum"));
			pageable.setSort(sort);
			
			List<VendorC>  list = vendorCService.list(user.getProjectCode(),pageable);
			//平均有效天数
			for (int i = 0; i < list.size(); i++) {
				VendorC vc = list.get(i);
				PageRequest p = new PageRequest();
				p.getQuery().put("t#month_S_EQ", vc.getMonth());
				p.getQuery().put("t#vendorCode_S_EQ", vc.getVendorCode());
				List<VendorProductC> l = vendorProductCService.list(user.getProjectCode(), p);
				Integer dayCount = 0;
				for (int j = 0; j < l.size(); j++) {
					VendorProductC vp = l.get(j);
					dayCount += vp.getValidityDayCount();
				}
				if(l.size()>0){
					dayCount = dayCount/l.size();
				}
				vc.setSegmentStr(dayCount+"");
			}
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
