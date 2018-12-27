package com.shyl.msc.set.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.msc.set.entity.WarehouseTemp;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.msc.set.service.IWarehouseTempService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 配送点审核
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/warehouseTemp")
public class WarehouseTempController extends BaseController {
	@Resource
	private IWarehouseTempService warehouseTempService;
	@Resource
	private IWarehouseService warehouseService;
	
	@RequestMapping("")
	public String home(){
		return "set/warehouseTemp/list";
	}
	
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<WarehouseTemp> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", WarehouseTemp.Status.valueOf(status));
		}
		Sort sort = new Sort(new Order(Direction.ASC,"status"),
				new Order(Direction.DESC,"id"));
		pageable.setSort(sort);
		return warehouseTempService.query(user.getProjectCode(), pageable);
	}
	
	@RequestMapping(value = "/audit", method = RequestMethod.GET)
	public String audit(@CurrentUser User user, Model model){
		return "set/warehouseTemp/audit";
	}
	
	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	@ResponseBody
	public Message audit(WarehouseTemp warehouseTemp, String type,@CurrentUser User user){
		Message message = new Message();		
		try{
			Warehouse obj = warehouseService.findByCode(user.getProjectCode(), warehouseTemp.getCode());
			if(obj == null){
				warehouseTemp.setStatus(WarehouseTemp.Status.audit);
				warehouseTempService.update(user.getProjectCode(), warehouseTemp);
				
				Warehouse warehouse = new Warehouse();
				warehouse.setCode(warehouseTemp.getCode());
				warehouse.setName(warehouseTemp.getName());
				warehouse.setAddr(warehouseTemp.getAddr());
				warehouse.setContact(warehouseTemp.getContact());
				warehouse.setPhone(warehouseTemp.getPhone());
				warehouse.setLongitude(warehouseTemp.getLongitude());
				warehouse.setLatitude(warehouseTemp.getLatitude());
				warehouse.setType(Warehouse.Type.valueOf(type));
				warehouse.setIsDisabled(warehouseTemp.getIsDisabled());
				warehouseService.save(user.getProjectCode(), warehouse);
				
				message.setMsg("编码"+warehouseTemp.getCode()+"添加成功");
			}else{
				message.setSuccess(false);
				message.setMsg("编码"+warehouseTemp.getCode()+"已经添加，不能重复添加");
			}
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return message;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
	}

}
