package com.shyl.msc.hospital.controller;

import java.util.Date;
import java.util.List;

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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.GpoProductList;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.service.IGpoProductListService;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/productVendor")
public class HospitalProductVendorController extends BaseController {

	@Resource
	private IProductService productService;
	@Resource
	private IProductDetailService productDetailService;
	@Resource
	private IGpoProductListService gpoProductListService;
	
	@RequestMapping("")
	public String home() {
		return "hospital/productVendor/list";
	}
	
	@RequestMapping(value = "/page")
	@ResponseBody
	public DataGrid<Product> page(PageRequest pageable, Integer isInProductVendor, @CurrentUser User user) {
		if(user.getOrganization().getOrgType() != 1){
			return null;
		}
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		DataGrid<Product> page = productService.pageInProductVendor(user.getProjectCode(), 
				pageable, user.getOrganization().getOrgCode(), isInProductVendor);
		
		for (Product p : page.getRows()) {
			ProductDetail pd = productDetailService.getByKey2(user.getProjectCode(), p.getId(), user.getOrganization().getOrgCode());
			if(pd != null){
				p.setProjectCode(pd.getId()+"");
				//供应商
				p.setNotes(pd.getVendorName());
				p.setFinalPrice(pd.getPrice());
			}else{
				GpoProductList gp = gpoProductListService.getPrice(user.getProjectCode(), p.getId());
				if(gp!=null){
					p.setFinalPrice(gp.getPrice());
				}			
			}
		}
		return page;
	}
	
	@RequestMapping(value = "/choose")
	@ResponseBody
	public List<GpoProductList> choose(PageRequest pageable,@CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"productId");
		pageable.setSort(sort);
		return gpoProductListService.list(user.getProjectCode(), pageable);
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Long productId,@CurrentUser User user, Model model) {
		Product p = productService.getById(user.getProjectCode(), productId);
		model.addAttribute("productId", productId);
		model.addAttribute("name", p.getName());
		return "hospital/productVendor/add";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(@CurrentUser User user, Product product,Long gpoProductId) {
		Message msg = new Message();
		try{
			if(user.getOrganization() == null && user.getOrganization().getOrgType()!=1){
				msg.setSuccess(false);
				msg.setMsg("不是医院账号");
				return msg;
			}
			
			GpoProductList g = gpoProductListService.getById(user.getProjectCode(), gpoProductId);
			
			ProductDetail pd = productDetailService.getByKey2(user.getProjectCode(), product.getId(), user.getOrganization().getOrgCode());
			if(pd != null){
				pd.setModifyDate(new Date());
				pd.setVendorCode(g.getVendorCode());
				pd.setVendorName(g.getVendorName());
				pd.setPrice(g.getPrice());
				productDetailService.update(user.getProjectCode(), pd);
			}else{
				pd = new ProductDetail();
				pd.setModifyDate(new Date());
				pd.setProduct(product);
				pd.setHospitalName(user.getOrganization().getOrgName());
				pd.setHospitalCode(user.getOrganization().getOrgCode());
				pd.setVendorCode(g.getVendorCode());
				pd.setVendorName(g.getVendorName());
				pd.setPrice(g.getPrice());
				productDetailService.save(user.getProjectCode(), pd);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
