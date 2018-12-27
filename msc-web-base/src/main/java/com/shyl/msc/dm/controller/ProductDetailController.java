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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.msc.dm.entity.ProductPriceRecord;
import com.shyl.msc.dm.entity.ProductPriceRecord.Type;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.service.IProductDetailService;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;
import com.shyl.msc.dm.service.IProductPriceRecordService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/dm/productDetail")
public class ProductDetailController extends BaseController {

	@Resource
	private IProductService productService;
	@Resource
	private IProductDetailService productDetailService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IProductPriceRecordService productPriceRecordService;
	@Resource
	private IProductPriceRecordHisService productPriceRecordHisService;
	
	@RequestMapping("")
	public String home() {
		return "/dm/productDetail/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Product> page(PageRequest pageable,@CurrentUser User user) {
		Sort sort = new Sort(Direction.ASC,"code");
		pageable.setSort(sort);
		return productService.pageInAttribute(user.getProjectCode(), pageable);
	}

	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ProductDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		DataGrid<ProductDetail> page = productDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(String productId, Model model) {
		model.addAttribute("productId", productId);
		return "/dm/productDetail/add";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(ProductDetail productDetail,@CurrentUser User user) {
		Message msg = new Message();
		try{
			Hospital hospital = hospitalService.findByCode(user.getProjectCode(), productDetail.getHospitalCode());
			if(hospital == null){
				throw new MyException("医院编码有误");
			}
			ProductDetail productDetail2 = productDetailService.getByKey(user.getProjectCode(), productDetail.getProduct().getId(), productDetail.getVendorCode(), productDetail.getHospitalCode());
			if(productDetail2 != null){
				msg.setSuccess(false);
				msg.setMsg("医院和供应商已存在");
			}else{
				Sort sort = new Sort(Direction.DESC,"modifyDate");
				List<ProductDetail> productDetail3 = productDetailService.listByProductId(sort,productDetail.getProduct().getId());
				BigDecimal lastPrice = new BigDecimal(0);
				if(productDetail3.size() > 0){
					lastPrice = productDetail3.get(0).getPrice();
				}
				productDetailService.save(user.getProjectCode(), productDetail);
				//记录ProductPriceRecord
				recordPrice(user.getProjectCode(), productDetail, lastPrice);
				msg.setSuccess(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	private void recordPrice(String projectCode,ProductDetail productDetail, BigDecimal lastPrice) {
		Product product = productService.getById(projectCode,productDetail.getProduct().getId());
		PageRequest pageable = new PageRequest();
		
		pageable.getQuery().put("t#productCode_S_EQ", product.getCode());
		pageable.getQuery().put("t#gpoCode_S_EQ", product.getGpoCode());
		ProductPriceRecord ppr = productPriceRecordService.getByKey(projectCode,pageable);
		//flag=1 新增his表
		int flag = 0;
		//未记录
		if(ppr == null){
			flag = 1;
			ppr = new ProductPriceRecord();
			ppr.setProductCode(product.getCode());
			ppr.setProductName(product.getName());
			ppr.setGpoCode(product.getGpoCode());
			ppr.setGpoName(product.getGpoName());
			ppr.setVendorCode(productDetail.getVendorCode());
			ppr.setVendorName(productDetail.getVendorName());
			ppr.setFinalPrice(productDetail.getPrice());
			ppr.setLastPrice(productDetail.getPrice());
			ppr.setPriceCount(1);
			ppr.setType(Type.gpo);
			productPriceRecordService.save(projectCode,ppr);
		}else if(ppr.getFinalPrice().compareTo(productDetail.getPrice())!=0){
			flag = 1;
			ppr.setVendorCode(productDetail.getVendorCode());
			ppr.setVendorName(productDetail.getVendorName());
			ppr.setFinalPrice(productDetail.getPrice());
			ppr.setLastPrice(lastPrice);
			ppr.setPriceCount(ppr.getPriceCount() + 1);
			productPriceRecordService.update(projectCode,ppr);
		}
		if(flag == 1){
			ProductPriceRecordHis pprh = new ProductPriceRecordHis();
			pprh.setProductCode(product.getCode());
			pprh.setProductName(product.getName());
			pprh.setGpoCode(product.getGpoCode());
			pprh.setGpoName(product.getGpoName());
			pprh.setVendorCode(productDetail.getVendorCode());
			pprh.setVendorName(productDetail.getVendorName());
			pprh.setFinalPrice(productDetail.getPrice());
			pprh.setType(com.shyl.msc.dm.entity.ProductPriceRecordHis.Type.gpo);
			productPriceRecordHisService.save(projectCode,pprh);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try{
			productDetailService.delete(user.getProjectCode(), id);
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(HttpServletResponse resp, PageRequest pageRequest){
		try {
			Sort sort = new Sort(Direction.ASC,"code");
			pageRequest.setSort(sort);
			String name = "医院供应商导入模板";
			List<Map<String, Object>> datas = new ArrayList<>();
			String heanders [] = {"药品编码","供应商编码","医院编码","价格"};
			String beannames [] = {"productCode","vendorCode","hospitalCode","price"};
			Map<String, Boolean> lineMap = new HashMap<>();
			ExcelUtil excelUtil = new ExcelUtil(heanders, beannames, lineMap);
			Workbook workbook = excelUtil.doExportXLS(datas, name, false, true);
			
			resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Disposition", "attachment; filename=productDetailTemplate.xls");
			OutputStream out = resp.getOutputStream();
			workbook.write(out);  
	 		out.flush();
	 		workbook.close();
	 		out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfileDetail,@CurrentUser User user){  
		 Message message = new Message(); 
		 String filename = myfileDetail.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  String msg =  this.doExcel(myfileDetail,user);
				  
				  message.setMsg(msg);
			  }else {
				  message.setMsg("请用正确模版格式导入");
			  }
		 }  catch (Exception e) {  
			 	e.printStackTrace();
				message.setSuccess(false);
				message.setMsg(e.getMessage());
		 }  
		 return message;
	}
	private String doExcel(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		HSSFWorkbook workBook = new HSSFWorkbook(input);
		HSSFSheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			// i = 0 是标题栏
			for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
				HSSFRow row0 = sheet.getRow(0);
				HSSFRow row = sheet.getRow(i);
				if (upExcel == null) {
					upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
				}
				if(row == null){
					workBook.close();
					throw new MyException("第"+i+"行不能为空");
				}
				HSSFCell cell = row.getCell(0);
				String cellStr = ExcelUtil.getValue(cell);
				if(StringUtils.isEmpty(cellStr)){
					workBook.close();
					throw new MyException("第"+i+"行不能为空");
				}
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					cell = row.getCell(j);
					cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return productDetailService.importExcel(user.getProjectCode(), upExcel, user);
	}
	
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	

}
