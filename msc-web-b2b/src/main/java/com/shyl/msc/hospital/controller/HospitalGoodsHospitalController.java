package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
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
import com.shyl.common.util.ExcelUtil;
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
	public DataGrid<Map<String,Object>> page(PageRequest pageable,@CurrentUser User user) {
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
		Sort sort = new Sort(new Order(Direction.ASC,"hospitalCode"),
				new Order(Direction.ASC,"productCode"),new Order(Direction.DESC,"t.createDate"));
		pageable.setSort(sort);
		return goodsHospitalService.queryByPage(user.getProjectCode(), pageable);
	}
	
	@RequestMapping("/export")
	public void export(PageRequest pageable,HttpServletResponse resp,@CurrentUser User user) {
		try {
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
			Sort sort = new Sort(new Order(Direction.ASC,"hospitalCode"),new Order(Direction.ASC,"productCode"));
			List<Map<String,Object>> result = goodsHospitalService.queryByAll(user.getProjectCode(), pageable);
			Map<String, Map<Object,Object>> ma = new HashMap<>();
			Map<Object,Object> ISGPOPURCHASE = new HashMap<>();
			ISGPOPURCHASE.put(new BigDecimal(0), "否");
			ISGPOPURCHASE.put(new BigDecimal(1), "是");
			ma.put("ISGPOPURCHASE", ISGPOPURCHASE);
			String[] headers = new String[]{"PRODUCTCODE","INTERNALCODE","PRODUCTNAME","GENERICNAME","DOSAGEFORMNAME","MODEL","UNITNAME","PACKDESC","PRODUCERNAME","ISGPOPURCHASE"};
			String[] beanNames = new String[]{"药品编码","医院内部编码","药品名称","通用名","剂型","规格","单位","包装规格","生产企业名称","是否gpo药品"};
			ExcelUtil excelUtil = new ExcelUtil(beanNames, headers,null,ma);
			
			Workbook workbook = excelUtil.doExportXLS(result, "药品对照查询", false, true);
			
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=contractTemplate.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
