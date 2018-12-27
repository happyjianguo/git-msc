package com.shyl.msc.hospital.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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
import com.shyl.msc.dm.entity.GoodsHospital;
import com.shyl.msc.dm.service.IGoodsHospitalService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/goodsHospital")
public class HospitalGoodsHospitalController extends BaseController {

	@Resource
	private IGoodsHospitalService goodsHospitalService;
	
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
		return "/hospital/goodsHospital/list";
	}

	/**
	 * 库存上传数据分页查看
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable,@CurrentUser User user) {
		//如果是医院，需要过滤只显示医院的数据
		if (user.getOrganization().getOrgType() == 1) {
			Map<String, Object> query = pageable.getQuery();
			if(query == null){
				query = new HashMap<String,Object>();
				pageable.setQuery(query);
			}
			//设置医疗机构查询条件
			query.put("t#hospitalCode_S_EQ",  user.getOrganization().getOrgCode());
		}
		Sort sort = new Sort(new Order(Direction.ASC,"t.hospitalCode"),
				new Order(Direction.ASC,"t.productCode"));
		pageable.setSort(sort);
		String sort1 = pageable.getSort();
		if(StringUtils.isNotEmpty(sort1) && sort1.contains("CREATEDATE")){
			sort1 = sort1.replaceAll("CREATEDATE","t.CREATEDATE");
		}
		pageable.setSort(sort1);
		return goodsHospitalService.queryByPage(user.getProjectCode(), pageable);
	}
}
