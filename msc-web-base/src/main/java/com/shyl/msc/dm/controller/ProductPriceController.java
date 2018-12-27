package com.shyl.msc.dm.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.service.IProductPriceService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.TradeType;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
/**
 * 商品Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/dm/productPrice")
public class ProductPriceController extends BaseController {
	@Resource
	private IProductPriceService productPriceService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IProductService productService;
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "dm/productPrice/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ProductPrice> page(String productCode,String vendorCode,PageRequest pageable,@CurrentUser User user){
		System.out.println(productCode+"*********"+vendorCode);
		DataGrid<ProductPrice> page =  productPriceService.queryByProductAndGpo(user.getProjectCode(), productCode,vendorCode,pageable);
		return page;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<ProductPrice> list(PageRequest pageable, @CurrentUser User user){
		List<ProductPrice> list =  productPriceService.list(user.getProjectCode(), pageable);
		return list;
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(String productCode, String vendorCode,Model model){
		model.addAttribute("productCode",productCode);
		model.addAttribute("vendorCode",vendorCode);
		return "dm/productPrice/add";
	}
	
	/**
	 * 新增
	 * @param productPrice
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(ProductPrice productPrice,Integer tType,@CurrentUser User user){
		Message message = new Message();
		try{
			if(tType== 0){//医院统一价格
				productPrice.setTradeType(TradeType.hospital);
				productPrice.setHospitalCode("");
				productPrice.setHospitalName("");
			}else if(tType== 1){//指定医院价格
				productPrice.setTradeType(TradeType.hospital);
			}else if(tType== 2){//个人价格
				productPrice.setTradeType(TradeType.patient);
				productPrice.setHospitalCode("");
				productPrice.setHospitalName("");
			}else{
				throw new Exception("tType不存在");
			}
			productPrice.setIsDisabled(0);
			setProductPriceData(user.getProjectCode(), productPrice);
			productPrice.setModifyDate(new Date());
			productPriceService.save(user.getProjectCode(), productPrice);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	private void setProductPriceData(String projectCode, ProductPrice productPrice){
		Company vendor = companyService.findByCode(projectCode,productPrice.getVendorCode(), "isVendor=1");
		Hospital hospital = hospitalService.findByCode(projectCode, productPrice.getHospitalCode());
		Product product = productService.getByCode(projectCode, productPrice.getProductCode());
			
		if(vendor != null){
			productPrice.setVendorName(vendor.getFullName());
		}
		
		if(hospital != null){
			productPrice.setHospitalName(hospital.getFullName());
		}
		if(product != null){
			productPrice.setProductCode(product.getCode());
			productPrice.setProductName(product.getName());
		}	
	}
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id,Model model, @CurrentUser User user){
		ProductPrice pp= productPriceService.getById(user.getProjectCode(), id);
		if(pp != null){
			if(pp.getHospitalName() == null){
				model.addAttribute("tType", "所有医院");
			}else{
				model.addAttribute("tType", pp.getHospitalName());
			}
		}
		
		return "dm/productPrice/edit";
	}
	/**
	 * 修改
	 * @param productPrice
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(ProductPrice productPrice, @CurrentUser User user){
		Message message = new Message();
		
		try{
			ProductPrice p = productPriceService.getById(user.getProjectCode(), productPrice.getId());
			p.setFinalPrice(productPrice.getFinalPrice());
			p.setBiddingPrice(productPrice.getBiddingPrice());
			p.setIsDisabled(productPrice.getIsDisabled());
			productPriceService.update(user.getProjectCode(), p);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id,@CurrentUser User user){
		Message message = new Message();
		
		try{
			ProductPrice productPrice = productPriceService.getById(user.getProjectCode(),id);
			if(null!=productPrice){
				if(productPrice.getIsEffected()==2){
					message.setMsg("已过期,不允许执行删除");
				}else{
					message.setMsg("删除成功");
				}
			}else{
				message.setMsg("删除成功");
			}
			productPriceService.deletePrice(user.getProjectCode(), id);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
	}
	
	@RequestMapping("/pageByGPO")
	@ResponseBody
	public DataGrid<ProductPrice> pageByGPO(PageRequest pageable,Long vendorId,@CurrentUser User user){
		Company vendor = companyService.getById(user.getProjectCode(), vendorId);
		DataGrid<ProductPrice> dataGrid = productPriceService.pageByGPO(user.getProjectCode(), pageable,vendor.getCode());
		return dataGrid;
	}
	
	@RequestMapping("/pageByEnabled")
	@ResponseBody
	public DataGrid<ProductPrice> pageByEnabled(PageRequest pageable,@CurrentUser User user){
		DataGrid<ProductPrice> dataGrid = productPriceService.pageByEnabled(user.getProjectCode(), pageable);
		return dataGrid;
	}

	
	@RequestMapping("/dotask")
	@ResponseBody
	public Message dotask(Long id,@CurrentUser User user){
		 Message message = new Message();
		try {
			ProductPrice productPrice = productPriceService.getById(user.getProjectCode(), id);
			if(productPrice.getIsEffected() != 0){
				throw new Exception("只能对未生效的价格进行操作！");
			}
			String today = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
			productPrice.setEffectDate(today);
			productPriceService.effectPrice(user.getProjectCode(), productPrice);
			
	        message.setMsg("执行完成");
		} catch (Exception e) {
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
        return message;
	}
	
	

	
	/**
	 * 【导入购物车数据】
	 * <p>
	 * @param myfile - 文件上传路径
	 * @return ""
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfile,@CurrentUser User user){  
		
		 Message message = new Message(); 
		 String filename = myfile.getOriginalFilename();  
		 if (filename == null || "".equals(filename)) {  
			 message.setMsg("文件不能为空");
			 return message;
		 }  
		 try {  
			  if(filename.endsWith(".xls") ||filename.endsWith(".xlsx")) {
				  //读取Excel
				  String msg =  this.doExcelH(myfile,user);
				  
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
	
	
	private String doExcelH(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		XSSFWorkbook workBook = new XSSFWorkbook(input);
		XSSFSheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			// i = 0 是标题栏
			for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
				XSSFRow row0 = sheet.getRow(0);
				XSSFRow row = sheet.getRow(i);
				if (upExcel == null) {
					upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
				}
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					XSSFCell cell = row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return productPriceService.doExcelH(user.getProjectCode(), upExcel, user);
	}
	
	
}
