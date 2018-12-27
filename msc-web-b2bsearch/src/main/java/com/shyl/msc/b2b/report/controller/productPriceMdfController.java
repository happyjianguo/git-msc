package com.shyl.msc.b2b.report.controller;

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
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;
import com.shyl.msc.dm.service.IProductPriceRecordService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 药品价格异动统计
 * @author hefeng
 *
 */

@Controller
@RequestMapping("/b2b/report/productPriceMdf")
public class productPriceMdfController extends BaseController {

	@Resource
	private IProductPriceRecordService productPriceRecordService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;
	@Resource
	private IProductService productService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "b2b/report/productpricemdf/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductPriceRecord> page(PageRequest pageable,@CurrentUser User user){
		pageable.setSort(new Sort(new Order(Direction.DESC, "priceCount")));
		DataGrid<ProductPriceRecord> page = productPriceRecordService.query(user.getProjectCode(),pageable);
		for (int i = 0; i < page.getRows().size(); i++) {
			ProductPriceRecord ppr = page.getRows().get(i);
			Product p = productService.getByCode(user.getProjectCode(), ppr.getProductCode());
			Map<String, Object> m = new HashMap<>();
			m.put("product", p);
			ppr.setSegmentMap(m);
		}
		
		return page;
	}
	
	@RequestMapping("/detail")
	public String detail(String productCode,String gpoCode,Model model){
		model.addAttribute("productCode",productCode);
		model.addAttribute("gpoCode",gpoCode);
		return "b2b/report/productpricemdf/detail";
	}
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<ProductPriceRecordHis> mxpage(PageRequest pageable, @CurrentUser User user){

		Sort sort = new Sort(new Order(Direction.DESC,"t.createDate"));
		pageable.setSort(sort);
		DataGrid<ProductPriceRecordHis> page =  productPriceRecordHisService.query(user.getProjectCode(), pageable);
		return page;
	}
	

	@RequestMapping(value = "/chart", method= RequestMethod.GET)
	public String chart(String productCode,Model model){
		model.addAttribute("productCode",productCode);
		return "b2b/report/productpricemdf/chart";
	}
	@RequestMapping(value = "/chart", method= RequestMethod.POST)
	@ResponseBody
	public Message chart(PageRequest pageable, @CurrentUser User user){
		Message message = new Message();
		try {
			Sort sort = new Sort(new Order(Direction.ASC,"t.createDate"));
			pageable.setSort(sort);
			List<ProductPriceRecordHis> list =  productPriceRecordHisService.list(user.getProjectCode(), pageable);
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
