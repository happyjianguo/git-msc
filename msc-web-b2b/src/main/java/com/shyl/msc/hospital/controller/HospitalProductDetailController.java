package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
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
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/productDetail")
public class HospitalProductDetailController extends BaseController {

	@Resource
	private IProductDetailService productDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@RequestMapping("")
	public String home() {
		return "hospital/productDetail/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ProductDetail> page(@CurrentUser User user, PageRequest pageRequest) {
		Map<String, Object> query = pageRequest.getQuery();
		DataGrid<ProductDetail> page = new DataGrid<>();
		Sort sort = new Sort(new Order(Direction.ASC,"product.code"));
		pageRequest.setSort(sort);
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
			page = productDetailService.query(user.getProjectCode(), pageRequest);
		}
		return page;
	}
	
	
	@RequestMapping("/exportExcel")
	public void exportExcel(@CurrentUser User user, HttpServletResponse resp, PageRequest pageRequest){
		try {
			if(user.getOrganization().getOrgType() == 1){
				Map<String, Object> query = pageRequest.getQuery();
				String name = user.getOrganization().getOrgName()+"合同药品目录模板";
				query.put("hospitalCode_S_EQ", user.getOrganization().getOrgCode());
				Sort sort = new Sort(Direction.ASC,"product.code");
				pageRequest.setSort(sort);
				List<ProductDetail> productDetails = productDetailService.list(user.getProjectCode(), pageRequest);
				String heanders [] = {"药品名称","通用名","剂型","规格","包装规格","生产企业","质量层次","数量","价格","单位","供应商名称","GPO名称","药品编码","供应商编码"};
				String beannames [] = {"product.name",
						"product.genericName", "product.dosageFormName",
						"product.model", "product.packDesc", "product.producerName", "product.qualityLevel","num",
						"price", "product.unitName", "vendorName", "product.gpoName","product.code", "vendorCode"};
				
				//质量层次格式化
				List<AttributeItem> items = attributeItemService.getItemSelect(user.getProjectCode(),null, "product_qualityLevel");
				Map<String, Map<Object,Object>> formatMap = new HashMap<>();
				Map<Object, Object> format = new HashMap<>();
				for(AttributeItem item : items) {
					format.put(item.getId().toString(), item.getField2());
				}
				formatMap.put("product.qualityLevel", format);

				//显示边框
				Map<String, Boolean> lineMap = new HashMap<>();
				lineMap.put("product.code", true);
				lineMap.put("vendorCode", true);
				lineMap.put("num", true);
				
				ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, lineMap, formatMap);
				
				Workbook workbook = excelUtil.doExportXLS(productDetails, name, true, true);
				
				resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				resp.setHeader("Content-Disposition", "attachment; filename=contractTemplate.xls");
				OutputStream out = resp.getOutputStream();
				workbook.write(out);  
		 		out.flush();
		 		workbook.close();
		 		out.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void init(WebDataBinder binder) {

	}

}
