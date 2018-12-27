package com.shyl.msc.dm.controller;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.GpoProductList;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.service.IGpoProductListService;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/dm/GpoProductList")
public class GpoProductListController extends BaseController {

	@Resource
	private IGpoProductListService gpoProductListService;
	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IProductDetailService productDetailService;

	@Override
	protected void init(WebDataBinder arg0) {
	}
	
	@RequestMapping("")
	public String home() {
		return "/dm/GpoProductList/list";
	}
	
	@RequestMapping(value="/page",method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<GpoProductList> page(PageRequest page, @CurrentUser User user) {
		return gpoProductListService.query(user.getProjectCode(), page);
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public Message delete(String idStr, @CurrentUser User user) {
		Message msg = new Message();
		String[] ids = idStr.split(",");
		for (String id : ids) {
			gpoProductListService.delete(user.getProjectCode(), Long.valueOf(id));
		}
		return msg;
	}

	@RequestMapping(value="/exp")
	public void exp(HttpServletResponse resp)  {
		String[] heanders = {"药品编码","供应商编码","价格"};
		
		OutputStream out = null;
		Workbook wb = new HSSFWorkbook();
	 	try {
			 Sheet sh = wb.createSheet("sheet1");
			 Row row = sh.createRow(0);
			 for(int i = 0;i<heanders.length;i++) {
				 Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
				 cell.setCellValue(heanders[i]);
			 }

			 resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			 resp.setHeader("Content-Disposition", "attachment; filename=product.xls");
			 out = resp.getOutputStream();
			 wb.write(out);  
			 out.flush();
			 wb.close();
			 out.close();
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}  finally {
			if (wb!=null) {
		 		try {
					wb.close();
				} catch (IOException e) {
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@RequestMapping(value="/imp")
	@ResponseBody
	public Message imp(MultipartFile myfile,@CurrentUser User user)  {
		Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  String[][] upExcel = tranExcelToList(myfile);
				  gpoProductListService.doExcel(user.getProjectCode(),upExcel);
				  message.setMsg("导入成功");
			  }else {
				  message.setMsg("请用正确格式导入");
			  }
		 }  catch (RuntimeException e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  catch (Exception e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg("导入失败");
		 }  
		 return message;
	}

	// 将Excel转换成可以保存的List
	private String[][] tranExcelToList(MultipartFile file) throws Exception {
		String[][] upExcel = null;
		//一次只读500条
		InputStream in = null;
		Workbook wb = null;
		try{
			//获取文件流
			in = file.getInputStream();
			//防止修改文件扩展名
			try{
				wb = new HSSFWorkbook(in);
			}catch (Exception e){
				in = file.getInputStream();
				wb = new XSSFWorkbook(in);
			}
			Sheet sheet = wb.getSheetAt(0);
			if (sheet != null) {
				// i = 0 是标题栏
				for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
					Row row0 = sheet.getRow(0);
					Row row = sheet.getRow(i);
					if(row == null){
						break;
					}
					if (upExcel == null) {
						upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
					}
					for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
						Cell cell = row.getCell(j);
						String cellStr = ExcelUtil.getValue(cell);
						upExcel[i - 1][j] = cellStr;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导入失败");
		} finally {
			if (wb!=null) {
				try {
					wb.close();
				} catch (IOException e) {
				}
			}
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return upExcel;
	}

	
}
