package com.shyl.msc.b2b.monitor.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.stock.entity.HisStockDay;
import com.shyl.msc.b2b.stock.service.IHisStockDayService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.entity.User;

/**
 * 库存上传查询
 * @author zzm
 *
 */
@Controller
@RequestMapping("/b2b/monitor/hisStock")
public class HisStockDayController extends BaseController {

	@Resource
	private IHisStockDayService hisStockDayService;
	@Resource
	private IHospitalService hospitalService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 库存上传数据jsp
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model,@CurrentUser User user){
		model.addAttribute("orgType", user.getOrganization().getOrgType());
		return "/b2b/monitor/hisStock/list";
	}
	


	/**
	 * 库存上传数据分页查看
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<HisStockDay> page(PageRequest pageable,@CurrentUser User user) {
		//如果是医院，需要过滤只显示医院的数据
		if (user.getOrganization().getOrgType() != null &&  user.getOrganization().getOrgId() != null 
				&&user.getOrganization().getOrgType() == 1) {
			Map<String, Object> query = pageable.getQuery();
			if(query == null){
				query = new HashMap<String,Object>();
				pageable.setQuery(query);
			}
			Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			//设置医疗机构查询条件
			query.put("t#hospitalCode_S_EQ", hospital.getCode());
		}
		Sort sort = new Sort(new Order(Direction.DESC,"stockDate"));
		pageable.setSort(sort);
		return hisStockDayService.query(user.getProjectCode(), pageable);
	}
}
