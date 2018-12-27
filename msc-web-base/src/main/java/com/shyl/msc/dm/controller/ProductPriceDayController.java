package com.shyl.msc.dm.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IProductPriceDayService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.entity.User;
/**
 * 商品每日价格Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/productPriceDay")
public class ProductPriceDayController extends BaseController {
	@Resource
	private IProductPriceDayService productPriceDayService;
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/productPriceDay/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/pageByProduct")
	@ResponseBody
	public List<Map<String, Object>> pageByProduct(String productCode,@CurrentUser User user){		
		String today = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		List<Map<String, Object>> list =  productPriceDayService.listByProduct(user.getProjectCode(), today, productCode);
		List<Map<String, Object>> listh =  productPriceDayService.listByProductAndHospital(user.getProjectCode(), today, productCode, user.getOrganization().getOrgCode());
		
		Map<String, Object> maph =new HashMap<String, Object>();
		for (Map<String, Object> map : listh) {
			String key = map.get("VENDORCODE")+"";
			String val = map.get("FINALPRICE")+"";
			maph.put(key, val);
		}
		//赋值指定医院价格
		for (Map<String, Object> map : list) {
			String key = map.get("VENDORCODE")+"";
			if(maph.get(key)!=null){
				map.put("FINALPRICE", maph.get(key));
			}
			Company company = companyService.findByCode(user.getProjectCode(),key, "isVendor=1");
			//设置是否选中
			GoodsPrice l= goodsPriceService.findByKey(user.getProjectCode(), productCode, company.getCode(), user.getOrganization().getOrgCode(),0,0,0);
			if(l != null && l.getIsDisabledByH() == 0){
				map.put("selected", true);
			}
		}
		return list;
	}
	
}
