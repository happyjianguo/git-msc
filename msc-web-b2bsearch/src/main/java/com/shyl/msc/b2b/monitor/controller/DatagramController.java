package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.common.web.util.XmlFormat;
import com.shyl.msc.b2b.order.entity.Datagram;
import com.shyl.msc.b2b.order.service.IDatagramService;
import com.shyl.sys.entity.User;

/**
 * 接口报文数据查询
 * @author zzm
 *
 */
@Controller
@RequestMapping("/b2b/monitor/datagram")
public class DatagramController extends BaseController {

	@Resource
	private IDatagramService datagramService;
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 接口数据报文查询jsp
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model,@CurrentUser User user){
		return "b2b/monitor/datagram/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Datagram> page(PageRequest pageable, @CurrentUser User user) {
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		String status = (String)query.get("t#datagramType_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#datagramType_S_EQ", Datagram.DatagramType.valueOf(status));
		}
		Sort sort = new Sort(new Order(Direction.DESC,"id"));
		pageable.setSort(sort);
		return datagramService.query(user.getProjectCode(), pageable);
	}

	/**
	 * 接口数据报文查询jsp
	 * @return
	 */
	@RequestMapping(value="/data", method = RequestMethod.GET)
	public String data(ModelMap model, Long id){
		model.addAttribute("dataId", id);
		return "b2b/monitor/datagram/data";
	}
	
	/**
	 * 根据ID序号查询报文数据
	 * 1、数据类型为json则格式化为json排版，使用fastjson提供的方式
	 * 2、如果为XML。使用SAXReader进行格式化
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/data", method = RequestMethod.POST, produces = "application/text; charset=utf-8")
	@ResponseBody
	public String data(Long id, @CurrentUser User user) {
		Datagram data =	datagramService.getById(user.getProjectCode(), id);
		//获取报文数据
		String dataStr = data.getData();
		//1:json 2:xml
		if (data.getDataType() ==1) {
			//格式化json数据，按显示{\n"a":\"1\",\n"b":"2"\n}\n排版
			dataStr = JSON.toJSONString(JSON.parse(dataStr), true); 
		} else {
			//格式化xml格式数据,排版显示
			dataStr = XmlFormat.format(dataStr);
		}
		return dataStr;
	}
}
