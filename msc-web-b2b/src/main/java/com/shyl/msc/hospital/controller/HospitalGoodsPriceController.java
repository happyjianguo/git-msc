package com.shyl.msc.hospital.controller;

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
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/goodsPrice")
public class HospitalGoodsPriceController extends BaseController {
	
	@Resource
	private IGoodsPriceService goodsPriceService;	
	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IProductPriceService productPriceService;
	
	@Resource
	private ICartService cartService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "hospital/goodsPrice/list";
	}

	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<GoodsPrice> page(PageRequest pageable,@CurrentUser User user){
		DataGrid<GoodsPrice> page =  goodsPriceService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 全部查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<GoodsPrice> list(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		List<GoodsPrice> list =  goodsPriceService.list(user.getProjectCode(), pageable);
		int i=0;
		for (GoodsPrice goodsPrice : list) {
			if(i == 0){
				goodsPrice.setSelected(true);
			}
			i++;
			Company company = companyService.findByCode(user.getProjectCode(),goodsPrice.getVendorCode(), "isVendor=1");
			//暂借priceShow 存放  价格＋供应商，用于 combox显示
			goodsPrice.setVendorName(company.getFullName());
			goodsPrice.setPriceShow(goodsPrice.getFinalPrice().setScale(2)+"--"+company.getFullName());
		}
		return list;
	}
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(String productCode,Long goodsId,Model model){
		model.addAttribute("productCode", productCode);
		model.addAttribute("goodsId", goodsId);
		return "hospital/goodsPrice/add";
	}
	

	/**
	 * 新增
	 * @param productid
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(GoodsPrice goodsPrice,@CurrentUser User user){
		Message message = new Message();
		try{
			GoodsPrice g = goodsPriceService.findByKey(user.getProjectCode(), goodsPrice.getProductCode(), goodsPrice.getVendorCode(), user.getOrganization().getOrgCode(),null,null,0);
			if(g != null){
				g.setBiddingPrice(goodsPrice.getFinalPrice());
				g.setFinalPrice(goodsPrice.getFinalPrice());
				g.setIsDisabledByH(0);
				g.setIsDisabled(0);
				goodsPriceService.update(user.getProjectCode(), g);
			}else{
				Company c = companyService.findByCode(user.getProjectCode(),goodsPrice.getVendorCode(), "isVendor=1");
				if(c != null){
					goodsPrice.setVendorCode(c.getCode());
				}

				goodsPrice.setHospitalCode(user.getOrganization().getOrgCode());
				goodsPrice.setProductCode(goodsPrice.getProductCode());		
				
				goodsPrice.setModifyDate(new Date());
				goodsPriceService.save(user.getProjectCode(), goodsPrice);
			}
			
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
	public Message del(GoodsPrice goodsPrice,@CurrentUser User user){
		Message message = new Message();
		try{
			GoodsPrice g = goodsPriceService.findByKey(user.getProjectCode(), goodsPrice.getProductCode(), goodsPrice.getVendorCode(), user.getOrganization().getOrgCode(),null,null,0);
			if(g != null){
				g.setIsDisabledByH(1);
				goodsPriceService.update(user.getProjectCode(), g);
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}
	
	/**
	 * 作废
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@ResponseBody
	public Message disable(Long id,@CurrentUser User user){
		Message message = new Message();
		try{
			GoodsPrice g = goodsPriceService.getById(user.getProjectCode(), id);
			if(g != null){
				g.setIsDisabledByH(1);
				goodsPriceService.update(user.getProjectCode(), g);
				message.setSuccess(true);
				message.setMsg("删除成功！");
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("删除失败："+e.getMessage());
		}
		
		return  message;
	}
	
}
