package com.shyl.msc.menu.controller;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.client.service.IProductCloudService;

/**
 * 云数据查询
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/menu/productCloud")
public class ProductCloudController extends BaseController {

	@Resource
	private IProductCloudService productCloudService;
	
	@RequestMapping(value="/cloud",method=RequestMethod.GET)
	public String cloud() {
		return "/menu/productCloud/cloud";
	}
	
	@RequestMapping(value="/cloud",method=RequestMethod.POST)
	public void cloud(PageRequest page,String callback, HttpServletResponse response) {
		
		PrintWriter write = null;
		try {
			JSONObject data = productCloudService.queryCloudByPage(page);
			
	        response.setHeader("Content-type", "text/html;charset=UTF-8");
			write = response.getWriter();
			//回写数据
			if (StringUtils.isEmpty(callback)) {
				write.write(data.toJSONString());
			} else {
				write.write(callback+"("+data.toJSONString()+")");
			}
			write.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (write!= null) {
				write.close();
			}
		}
	}

	@Override
	protected void init(WebDataBinder arg0) {
	}
}
