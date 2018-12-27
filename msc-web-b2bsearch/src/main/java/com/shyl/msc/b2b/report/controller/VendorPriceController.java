package com.shyl.msc.b2b.report.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.entity.User;
/**
 * 供应商价格浮动率
 * 
 *
 */
@Controller(value="reportVendorPriceController")
@RequestMapping("/b2b/report/vendorprice")
public class VendorPriceController extends BaseController{
	@Resource
	private ICompanyService companyService;
	@Resource
	private IProductService productService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;
	
	@Override
	protected void init(WebDataBinder binder) {
	}

	@RequestMapping("")
	public String list(){
		return "b2b/report/vendorprice/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Product> list(String vendorCode, String startDate, String endDate, PageRequest pageable, @CurrentUser User user){
		DataGrid<Product> page = productService.listByVendorAndDate(user.getProjectCode(), vendorCode, startDate, endDate, pageable);
		System.out.println(vendorCode);
		System.out.println(startDate);
		System.out.println(endDate);
		System.out.println(page.getRows().size());
		Company vendor = companyService.findByCode1(user.getProjectCode(), vendorCode);
		for(Product product:page.getRows()){
			List<ProductPriceRecordHis> list = productPriceRecordHisService.listByVendorAndDate(user.getProjectCode(), vendorCode, product.getCode(), startDate, endDate);
			Map<String, Object> map = new HashMap<String, Object>();
			ProductPriceRecordHis productPriceRecordHis1 = list.get(0);
			ProductPriceRecordHis productPriceRecordHis2 = list.get(list.size()-1);
			System.out.println(list.size());
			map.put("vendorName", vendor.getFullName());
			map.put("start", productPriceRecordHis1);
			map.put("end", productPriceRecordHis2);
			map.put("rate", ((productPriceRecordHis2.getFinalPrice().subtract(productPriceRecordHis1.getFinalPrice()))
					.divide(productPriceRecordHis1.getFinalPrice(), 2, BigDecimal.ROUND_HALF_UP))
			.toString() + " %");
			product.setSegmentMap(map);
		}
		
		return page;
	}
}
