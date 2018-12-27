package com.shyl.msc.hospital.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.annotation.Fastjson;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.Goods;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/goodsStock")
public class HospitalGoodsStockController extends BaseController {
	
	@Resource
	private IGoodsService goodsService;	



	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String goodsStock(){
		return "hospital/goodsStock/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/stockPage")
	@ResponseBody
	public DataGrid<Goods> stockPage(PageRequest pageable,@CurrentUser User user){
		DataGrid<Goods> page =  goodsService.pageByHospital(user.getProjectCode(), pageable,user.getOrganization().getOrgCode());
		return page;
	}
	
	/**
	 * 保存
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/setStock", method = RequestMethod.POST)
	@ResponseBody
	public Message setStock(@Fastjson String stockLimit, @CurrentUser User user){
		Message message = new Message();
		try{
			List<JSONObject> list = JSON.parseArray(stockLimit, JSONObject.class);
			for (JSONObject jo : list) {
				Long id = jo.getLong("id");
				BigDecimal stockNum = jo.getBigDecimal("stockNum");
				Integer stockUpLimit = jo.getInteger("stockUpLimit");
				Integer stockDownLimit = jo.getInteger("stockDownLimit");
				if(stockUpLimit == null && stockDownLimit == null){
					continue;
				}
				Goods goods = goodsService.getById(user.getProjectCode(), id);
				goods.setStockNum(stockNum);
				goods.setStockUpLimit(stockUpLimit);
				goods.setStockDownLimit(stockDownLimit);
				goodsService.update(user.getProjectCode(), goods);
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}
}
