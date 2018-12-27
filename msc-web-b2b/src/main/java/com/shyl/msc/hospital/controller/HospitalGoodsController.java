package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
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
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.Cart;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/goods")
public class HospitalGoodsController extends BaseController {
	
	@Resource
	private IGoodsService goodsService;	
	@Resource
	private IGoodsPriceService goodsPriceService;	
	@Resource
	private IProductService productService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICartService cartService;
	@Resource
	private IProductPriceService productPriceService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "hospital/goods/list";
	}

	/**
	 * 导出医院药品目录
	 * @param user
	 * @param resp
	 */
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, HttpServletResponse resp){
		try {
			if(user.getOrganization().getOrgType() == 1){
				String hospitalCode = user.getOrganization().getOrgCode();
				String name = user.getOrganization().getOrgName()+"药品目录";
				List<Map<String, Object>> dataList = goodsService.listByHospital(user.getProjectCode(), hospitalCode);
				String heanders [] = {"药品编码","药品名称","通用名","剂型","规格","包装规格","生产企业",
						"供应商编码","供应商名称","价格","合同开始日期","合同截至日期"};
				String beannames [] = {"PRODUCTCODE","PRODUCTNAME","GENERICNAME","DOSAGEFORMNAME",
						"MODEL","PACKDESC","PRODUCERNAME","VENDORCODE","VENDORNAME",
						"FINALPRICE","BEGINDATE","OUTDATE"};
				ExcelUtil excelUtil = new ExcelUtil(heanders, beannames);
				Workbook workbook = excelUtil.doExportXLS(dataList, name, false, true);
				
				resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				resp.setHeader("Content-Disposition", "attachment; filename=hopitalMedicine.xls");
				OutputStream out = resp.getOutputStream();
				workbook.write(out);  
		 		out.flush();
		 		workbook.close();
		 		out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Goods> page(PageRequest pageable,@CurrentUser User user){
		pageable.setSort(new Sort(Direction.ASC, "productCode"));
		DataGrid<Goods> page =  goodsService.pageByHospital(user.getProjectCode(), pageable,user.getOrganization().getOrgCode());
		for (Goods g : page.getRows()) {
			System.out.println("**************");;
			List<GoodsPrice> l = goodsPriceService.listByGoods(user.getProjectCode(), g.getId());
			if(l != null && l.size() > 0){
				//借用isDisabled栏位 存 是否已选择供应商
				g.setIsDisabled(1);
			}else{
				g.setIsDisabled(0);
			}
		}
		return page;
	}
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "hospital/goods/add";
	}
	
	/**
	 * 新增
	 * @param productid
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Long productId,@CurrentUser User user){
		Message message = new Message();
		try{
			goodsService.addGoodsAndGoodsprice(user.getProjectCode(), productId,user.getOrganization().getOrgCode());
			
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
	public Message del(Long productId,@CurrentUser User user){
		Message message = new Message();
		
		try{
			Product product = productService.getById(user.getProjectCode(), productId);
			String hospitalCode = user.getOrganization().getOrgCode();
			Goods g = goodsService.getByProductAndHospital(user.getProjectCode(), product.getCode(), hospitalCode,0);
			if(g != null){
				g.setIsDisabled(1);
				goodsService.update(user.getProjectCode(), g);
				//删除购物车
				Cart c = cartService.findByHopitalAndGoods(user.getProjectCode(), user.getOrganization().getOrgCode(), g.getId());
				if(c != null)
					cartService.delete(user.getProjectCode(), c);
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}
	
	/**
	 * 未添加目录 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/newProductPage")
	@ResponseBody
	public DataGrid<Map<String, Object>> newProductPage(String code,String name,PageRequest pageable,@CurrentUser User user){		
		code = code == null?"":code;
		name = name == null?"":name;
		DataGrid<Map<String, Object>> page =  productService.npquery(user.getProjectCode(), code,name,pageable);
		if(user.getOrganization().getOrgType()==1){
			for(Map<String,Object> map:page.getRows()){
				Goods goods = goodsService.getByProductAndHospital(user.getProjectCode(), map.get("CODE").toString(),user.getOrganization().getOrgCode(),0);
				if(goods != null){
					map.put("selected", "true");
				}
			}
		}
		return page;
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/pageByProduct")
	@ResponseBody
	public List<ProductPrice> pageByProduct(String productCode,@CurrentUser User user){
		List<ProductPrice> list =  productPriceService.listByProduct(user.getProjectCode(), productCode);
		List<ProductPrice> listh =  productPriceService.listByProductAndHospital(user.getProjectCode(), productCode, user.getOrganization().getOrgCode());
		
		Map<String, Object> maph =new HashMap<String, Object>();
		for (ProductPrice pp : listh) {
			String key = pp.getVendorCode();
			maph.put(key, pp);
		}
		
		List<ProductPrice> rtnlist = new ArrayList<ProductPrice>();
		for (ProductPrice pp : list) {
			String key = pp.getVendorCode();
			ProductPrice val = new ProductPrice();
			if(maph.get(key)!=null){
				val = (ProductPrice)maph.get(key);
			}else{
				val = pp;
			}
			//设置是否选中
			GoodsPrice l= goodsPriceService.findByKey(user.getProjectCode(), productCode, val.getVendorCode(), user.getOrganization().getOrgCode(),0,0,0);
			if(l != null){
				val.setSelected(true);
			}
			rtnlist.add(val);
		}
		return rtnlist;
	}
	
}
