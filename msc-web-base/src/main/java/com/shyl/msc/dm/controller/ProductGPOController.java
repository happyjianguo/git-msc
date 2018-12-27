package com.shyl.msc.dm.controller;

import java.util.Date;
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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.ProductVendor;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.dm.service.IProductVendorService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 商品GPOController
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/productGPO")
public class ProductGPOController extends BaseController {
	@Resource
	private IProductVendorService productVendorService;
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
		return "dm/productGPO/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable,@CurrentUser User user){
		DataGrid<Map<String, Object>> page = new DataGrid<Map<String, Object>>();
		if(user.getOrganization().getOrgType() == 3 || user.getOrganization().getOrgType() == 5 || user.getOrganization().getOrgType() == 9){
			page =  productVendorService.mapByVendor(user.getProjectCode(), pageable,null);
		}else if(user.getOrganization().getOrgType() == 2){
			page = productVendorService.mapByVendor(user.getProjectCode(), pageable,user.getOrganization().getOrgCode());
		}
		return page;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<ProductVendor> list(PageRequest pageable, @CurrentUser User user){
		List<ProductVendor> list =  productVendorService.list(user.getProjectCode(), pageable);
		return list;
	}

	/**
	 * gpo未添加药品 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/productPage")
	@ResponseBody
	public DataGrid<Map<String, Object>> productPage(String vendorCode,PageRequest pageable,@CurrentUser User user){		
		System.out.println("vendorCode="+vendorCode);
		DataGrid<Map<String, Object>> page = new DataGrid<Map<String, Object>>();
		if(vendorCode == null){
			return page;
		}
		page =  productVendorService.findByStatus(user.getProjectCode(), vendorCode,0,pageable);
		
		return page;
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "dm/productGPO/add";
	}
	
	/**
	 * 新增
	 * @param productPrice
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(String productCode, String vendorCode,@CurrentUser User user){
		Message message = new Message();
		try{
			ProductVendor productVendor = productVendorService.findByKey(user.getProjectCode(), productCode, vendorCode);
			if(productVendor != null){//有 isDisabled = 1的资料
				productVendor.setIsDisabled(0);
				productVendorService.update(user.getProjectCode(), productVendor);
			}else{
				productVendor = new ProductVendor();
				productVendor.setProductCode(productCode);
				productVendor.setVendorCode(vendorCode);
				productVendor.setIsDisabled(0);
				productVendor.setModifyDate(new Date());
				productVendorService.save(user.getProjectCode(), productVendor);
			}
			
			//productVendorService.saveOrUpdate(productPrice);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long productId,Long vendorId,Long hospitalId,Model model){
//		if(productId!=null){
//			model.addAttribute("productId", productId);
//		}
//		if(vendorId!=null){
//			model.addAttribute("vendorId", vendorId);
//		}
//		if(hospitalId!=null){
//			model.addAttribute("hospitalId", hospitalId);
//		}
		return "dm/productGPO/edit";
	}
	/**
	 * 修改
	 * @param productPrice
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(ProductVendor productVendor, @CurrentUser User user){
		Message message = new Message();
		try{
			productVendorService.saveOrUpdate(user.getProjectCode(), productVendor);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(String productCode,String vendorCode,@CurrentUser User user){
		Message message = new Message();
		try{
			ProductVendor p = productVendorService.findByKey(user.getProjectCode(), productCode, vendorCode);
			if(p != null){
				if(p.getIsDisabled() == 0){
					p.setIsDisabled(1);
					productVendorService.update(user.getProjectCode(), p);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}
	

}
