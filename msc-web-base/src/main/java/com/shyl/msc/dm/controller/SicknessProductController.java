package com.shyl.msc.dm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.SicknessProduct;
import com.shyl.msc.dm.service.ISicknessProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 疾病药品
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/sicknessProduct")
public class SicknessProductController extends BaseController {

	@Resource
	private ISicknessProductService sicknessProductService;
	
	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model, String sicknessCode){
		model.addAttribute("sicknessCode", sicknessCode);
		return "dm/sicknessProduct/add";
	}
	

	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, String sicknessCode,@CurrentUser User user){		
		DataGrid<Map<String, Object>> page = new DataGrid<Map<String, Object>>();
		if(StringUtils.isEmpty(sicknessCode)){
			return page;
		}
		page = sicknessProductService.pageByProductWithSelected(user.getProjectCode(), pageable, sicknessCode);
		return page;
	}
	
	/**
	 * 新增
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(String productCode, String sicknessCode,@CurrentUser User user){
		Message message = new Message();
		try{
			SicknessProduct sicknessProduct = sicknessProductService.findByKey(user.getProjectCode(), sicknessCode, productCode);
			if(sicknessProduct == null){
				sicknessProduct = new SicknessProduct();
				sicknessProduct.setSicknessCode(sicknessCode);
				sicknessProduct.setProductCode(productCode);
				
				sicknessProductService.save(user.getProjectCode(), sicknessProduct);
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
	public Message del(String productCode, String sicknessCode,@CurrentUser User user){
		Message message = new Message();
		try{
			SicknessProduct sicknessProduct = sicknessProductService.findByKey(user.getProjectCode(), sicknessCode, productCode);
			if(sicknessProduct == null){
				message.setSuccess(false);
				message.setMsg("已删除");
			}else{
				sicknessProductService.delete(user.getProjectCode(), sicknessProduct);
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
	
	@RequestMapping(value = "/delProduct", method = RequestMethod.POST)
	@ResponseBody
	public Message delProduct(String data, String sicknessCode,@CurrentUser User user){
		Message message = new Message();
		try{
			List<JSONObject> datas = JSON.parseArray(data, JSONObject.class);
			for(JSONObject jo:datas){
				String productCode = jo.getString("code");
				SicknessProduct sicknessProduct = sicknessProductService.findByKey(user.getProjectCode(), sicknessCode, productCode);
				if(sicknessProduct != null){
					sicknessProductService.delete(user.getProjectCode(), sicknessProduct);
					message.setSuccess(true);
					message.setMsg("删除成功");
				}
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
