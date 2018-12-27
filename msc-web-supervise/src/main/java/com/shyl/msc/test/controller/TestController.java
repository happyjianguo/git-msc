package com.shyl.msc.test.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/test")
public class TestController {
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model,@CurrentUser User user) {
//		System.out.println("test");
		return "/supervise/test/index";
	}
}
