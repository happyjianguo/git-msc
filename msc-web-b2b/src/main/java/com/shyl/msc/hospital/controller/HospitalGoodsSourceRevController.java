package com.shyl.msc.hospital.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.shyl.msc.dm.service.IGoodsHospitalSourceService;
import com.shyl.sys.entity.User;


/**
 * 复审数据
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/hospital/goodsSourceRev")
public class HospitalGoodsSourceRevController extends BaseController {

	@Resource
	private IGoodsHospitalSourceService goodsHospitalSourceService;
	
	
	protected void init(WebDataBinder binder) {

	}

	@RequestMapping("")
	public String home() {
		return "hospital/goodsSourceRev/list";
	}

	/**
	 * 医院就编码分页
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/sourcePage", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Map<String, Object>> sourcePage(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
			page.setQuery(query);
		}
		query.put("t#status_L_EQ", 99);
		Long hospitalId = -1L;
		if (user != null) {
			hospitalId = user.getOrganization().getOrgId();
		}
		query.put("t#hospitalId_L_EQ", hospitalId);
		return goodsHospitalSourceService.npquery(user.getProjectCode(), page);
	}
	
	/**
	 * 导入excel
	 * @param page
	 * @param resp
	 * @param user
	 * @param status
	 */
	@RequestMapping(value="/exportExcel")
	public void exportExcel(PageRequest page, HttpServletResponse resp, 
			@CurrentUser User user, Integer status)  {
		Sort sort = new Sort(new Order(Direction.ASC,"hospitalName"),
				new Order(Direction.DESC,"productCode"));
		page.setSort(sort);
	 	page.setPageSize(500);
	 	Map<String, Object> query = page.getQuery();
	 	if (query == null) {
	 		query = new HashMap<String, Object>();
	 		page.setQuery(query);
	 	}
	 	if (user.getOrganization().getOrgType() != null && user.getOrganization().getOrgType() == 1) {
	 		query.put("t#hospitalId_L_EQ", user.getOrganization().getOrgId());
	 	}
	 	
	 	DataGrid<Map<String,Object>> data = goodsHospitalSourceService.npquery(user.getProjectCode(), page);
	
	 	Workbook wb = new XSSFWorkbook();
	 	String[] title = {"医院名称","医院内部药品编码","用药监管平台编码","医保编码","批准文号","药交ID",
				 "药品本位码","物价ID","通用名","药品名称","药品规格","医院转换比","医院最小制剂单位","包装单位","药品厂家","药品剂型","零售价","供应企业","供应价","基本药物标识",
				 "监管平台编码","监管平台通用名","监管平台药品名称","监管平台药品规格","监管平台包装规格","监管平台厂家","监管转换比","监管国药准字","监管本位码","监管英文名","监管商品名",
				 "监管最小制剂单位","转换比率"};
		 
	 	if (data.getTotal() > 0) {
			 List<Map<String,Object>> list = data.getRows();
			 String hospitalName = (String)list.get(0).get("HOSPITALNAME");
			 Sheet sh = wb.createSheet(hospitalName);
			 Row row = sh.createRow(0);
			 for (int i=0;i<title.length;i++) {
				 Cell cell = row.createCell(i);
				 cell.setCellValue(title[i]);
			 }
			 Long total = data.getTotal();
			 Long pageNum = total/200;
			 if (total%200 != 0) {
				 pageNum++;
			 }
			 int rowIndex = 1;
			 Map<String,Object> source = null;
			 for(int rownum = 1; rownum <= list.size() ; rownum++) {
				 source = list.get(rownum-1);
				 if (!hospitalName.equals(source.get("HOSPITALNAME"))) {
					 hospitalName = (String)source.get("HOSPITALNAME");
					 rowIndex=1;
					 sh = wb.createSheet(hospitalName);
					 row = sh.createRow(0);
					 for (int i=0;i<title.length;i++) {
						 Cell cell = row.createCell(i);
						 cell.setCellValue(title[i]);
					 }
				 }
				 row = sh.createRow(rowIndex);
				 Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("HOSPITALNAME"));
				 cell = row.createCell(1, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("INTERNALCODE"));
				 cell = row.createCell(2, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("YYJGCODE"));
				 cell = row.createCell(3, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("YBDRUGSNO"));
				 cell = row.createCell(4, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("AUTHORIZENO"));
				 cell = row.createCell(5, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("YJCODE"));
				 cell = row.createCell(6, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("STANDARDCODE"));
				 cell = row.createCell(7, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRICEFILENO"));
				 cell = row.createCell(8, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("GENERICNAME"));
				 cell = row.createCell(9, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTNAME"));
				 cell = row.createCell(10, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MODEL"));
				 cell = row.createCell(11, Cell.CELL_TYPE_NUMERIC);
				 cell.setCellValue((String)source.get("CONVERTRATIO0"));
				 cell = row.createCell(12, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MINUNIT"));
				 cell = row.createCell(13, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("UNITNAME"));
				 cell = row.createCell(14, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCERNAME"));
				 cell = row.createCell(15, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("DOSAGEFORMNAME"));
				 cell = row.createCell(16, Cell.CELL_TYPE_NUMERIC);
				 if (source.get("FinalPrice") != null) {
					 cell.setCellValue(((BigDecimal)source.get("FINALPRICE")).doubleValue());
				 } else {
					 cell.setCellValue("");
				 }
				 cell = row.createCell(17, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("VENDORNAME"));
				 cell = row.createCell(18, Cell.CELL_TYPE_NUMERIC);
				 if (source.get("BIDDINGPRICE") != null) {
					 cell.setCellValue(((BigDecimal)source.get("BIDDINGPRICE")).doubleValue());
				 } else {
					 cell.setCellValue("");
				 }
				 cell = row.createCell(19, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("BASEMARK"));
				 //获取资源
				 cell = row.createCell(20, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTCODE"));
				 cell = row.createCell(21, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("GENERICNAME0"));
				 cell = row.createCell(22, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCTNAME"));
				 cell = row.createCell(23, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MODEL0"));
				 cell = row.createCell(24, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PACKDESC0"));
				 cell = row.createCell(25, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("PRODUCERNAME0"));
				 cell = row.createCell(26, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("CONVERTRATIO1"));
				 cell = row.createCell(27, Cell.CELL_TYPE_STRING);
				 if (StringUtils.isEmpty(source.get("AUTHORIZENO0"))) {
					 cell.setCellValue((String)source.get("IMPORTFILENO"));
				 } else {

					 cell.setCellValue((String)source.get("AUTHORIZENO0"));
				 }
				 cell = row.createCell(28, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("STANDARDCODE0"));
				 cell = row.createCell(29, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("ENGLISHNAME"));
				 cell = row.createCell(30, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("TRADENAME"));
				 cell = row.createCell(31, Cell.CELL_TYPE_STRING);
				 cell.setCellValue((String)source.get("MINUNIT0"));
				 cell = row.createCell(32, Cell.CELL_TYPE_STRING);
				 if (source.get("MINUNIT0").equals(source.get("MINUNIT"))) {
					 BigDecimal bd = BigDecimal.valueOf(Double.valueOf((String)source.get("CONVERTRATIO0")));
					 bd = bd.divide(BigDecimal.valueOf(Double.valueOf((String)source.get("CONVERTRATIO1"))), 3, BigDecimal.ROUND_DOWN);
					 cell.setCellValue(bd.doubleValue());
				 } else {
					 cell.setCellValue("");
				 }
				 if (page.getPage()  != pageNum
						 && rownum == list.size()) {
					 page.setPage(page.getPage()+1);
					 data = goodsHospitalSourceService.npquery(user.getProjectCode(), page);
					 list = data.getRows();
					 rownum=0;
				 }
				 rowIndex++;
			 }
		 } else {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for (int i=0;i<title.length;i++) {
				 Cell cell = row.createCell(i);
				 cell.setCellValue(title[i]);
			 }
		 }
		 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=product.xlsx");
		 OutputStream out;
		 try {
			 out = resp.getOutputStream();
			 wb.write(out);
			 out.flush();
			 wb.close();
			 out.close();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
	}
}
