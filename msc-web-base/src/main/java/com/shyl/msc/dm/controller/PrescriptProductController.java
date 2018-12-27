package com.shyl.msc.dm.controller;

import java.util.Date;
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
import com.shyl.msc.dm.entity.PrescriptProduct;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IPrescriptProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 外配处方药品
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/prescriptProduct")
public class PrescriptProductController extends BaseController {

	@Resource
	private IPrescriptProductService prescriptProductService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/prescriptProduct/list";
	}
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model){
		return "dm/prescriptProduct/add";
	}
	

	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<PrescriptProduct> page(PageRequest pageable, @CurrentUser User user){		

		DataGrid<PrescriptProduct> page = prescriptProductService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 新增
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(Long productId, @CurrentUser User user){
		Message message = new Message();
		try{
			PrescriptProduct prescriptProduct = prescriptProductService.findByProductId(user.getProjectCode(), productId);
			if(prescriptProduct == null){
				prescriptProduct = new PrescriptProduct();
				Product product = new Product();
				product.setId(productId);
				prescriptProduct.setProduct(product);
				prescriptProduct.setModifyDate(new Date());
				prescriptProductService.save(user.getProjectCode(), prescriptProduct);
				message.setSuccess(true);
				message.setMsg("新增成功");
			}else{
				message.setSuccess(false);
				message.setMsg("已经存在");
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		try{		
			prescriptProductService.delete(user.getProjectCode(), id);
			message.setSuccess(true);
			message.setMsg("删除成功");				
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@RequestMapping("/page2")
	@ResponseBody
	public DataGrid<Map<String, Object>> page2(PageRequest pageable,@CurrentUser User user){		
		DataGrid<Map<String, Object>> page = new DataGrid<Map<String, Object>>();

		page = prescriptProductService.pageByProductWithSelected(user.getProjectCode(), pageable);
		return page;
	}
	
	@RequestMapping(value = "/del2", method = RequestMethod.POST)
	@ResponseBody
	public Message del2(Long productId, @CurrentUser User user){
		Message message = new Message();
		try{		
			PrescriptProduct prescriptProduct = prescriptProductService.findByProductId(user.getProjectCode(), productId);
			if(prescriptProduct != null){
				prescriptProductService.delete(user.getProjectCode(), prescriptProduct);
				message.setSuccess(true);
				message.setMsg("删除成功");
			}									
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub

	}

}
