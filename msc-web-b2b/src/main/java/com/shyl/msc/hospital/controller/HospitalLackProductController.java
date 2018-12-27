package com.shyl.msc.hospital.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.framework.util.StringUtil;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.hospital.entity.LackProduct;
import com.shyl.msc.b2b.hospital.service.ILackProductService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/hospital/lackProduct")
public class HospitalLackProductController extends BaseController {

	@Resource
	private ILackProductService lackProductService;
	@Resource
	private IProductService productService;
	
	@RequestMapping("")
	public String home() {
		return "hospital/lackProduct/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<LackProduct> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Order order1 = new Order(Direction.DESC,"t.month");
		Order order2 = new Order(Direction.DESC,"t.createDate");
		Sort sort  = new Sort(order1 ,order2); 
		page.setSort(sort);
		
		return lackProductService.query(user.getProjectCode(), page);
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "hospital/lackProduct/add";
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(LackProduct lackProduct, @CurrentUser User user) {
		Message msg = new Message();
		try{
			LackProduct obj = lackProductService.findUnique(user.getProjectCode(), lackProduct.getMonth(), lackProduct.getHospitalCode(), lackProduct.getProduct().getId());
			if(obj != null){
				throw new Exception("该药品已上报");
			}
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				lackProduct.setHospitalCode(user.getOrganization().getOrgCode());
				lackProduct.setHospitalName(user.getOrganization().getOrgName());		
			}	
			lackProduct.setCreateUser(user.getEmpId());
			lackProductService.save(user.getProjectCode(), lackProduct);
			msg.setMsg("添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("添加失败,"+e.getMessage());
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long id, @CurrentUser User user) {
		Message msg = new Message();
		try{			
			lackProductService.delete(user.getProjectCode(), id);
			msg.setMsg("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfile, @CurrentUser User user) {
		Message msg = new Message();
		try {
			if (user.getOrganization() != null && user.getOrganization().getOrgType() == 1) {
				String filename = myfile.getOriginalFilename();
				if (filename == null || "".equals(filename)) {
					throw new Exception("文件不能为空");
				}
				if (!filename.endsWith(".xls") && !filename.endsWith(".xlsx")) {				
					throw new Exception("请用正确模版格式导入");
				}
				//将Excel转换成可以保存的List
				List<LackProduct> lackProducts = tranExcelToList(myfile, user);
				
				lackProductService.saveBatch(user.getProjectCode(), lackProducts);
				msg.setMsg("上传成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}
		return msg;
	}
	
	//将Excel转换成可以保存的List
	private List<LackProduct> tranExcelToList(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		/*HSSFWorkbook workBook = new HSSFWorkbook(input);
		HSSFSheet sheet = workBook.getSheetAt(0);*/
		Workbook workBook = WorkbookFactory.create(input);
		Sheet sheet = workBook.getSheetAt(0);
		if (sheet != null) {
			// i = 0 是标题栏
			for (int i = 1; i <= sheet.getPhysicalNumberOfRows() - 1; i++) {
				Row row0 = sheet.getRow(0);
				Row row = sheet.getRow(i);
				if (upExcel == null) {
					upExcel = new String[sheet.getPhysicalNumberOfRows() - 1][row0.getPhysicalNumberOfCells()];
				}
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					Cell cell =  row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return tranArrayToList(upExcel, user);
	}
	
	private List<LackProduct> tranArrayToList(String[][] upExcel, User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		List<LackProduct> lackProducts = new ArrayList<LackProduct>();
		for (int i = 0; i < upExcel.length; i++) {
			String[] row = upExcel[i];
			if(StringUtils.isEmpty(row[0])&&StringUtils.isEmpty(row[1])){
				break;
			}
			if(!row[0].matches("^[1-9][0-9]{3}\\-((0[1-9])|(1[012]))$")){
				throw new Exception("第"+(i+1)+"笔数据异常，年月格式有误");
			}
			Product product = productService.getByCode(user.getProjectCode(), row[1]);
			if(product == null){
				throw new Exception("第"+(i+1)+"笔数据异常，药品编码有误");
			}/*else {
				if(!product.getName().equals(row[2])){
					throw new Exception("第"+(i+1)+"笔数据异常，药品名称有误");
				}
			}*/
			//剔除重复数据
			Object obj1 = map.get(row[0] + "#"+user.getOrganization().getOrgCode() + "#" + product.getId());
			if(obj1 != null){
				continue;
			}
			LackProduct obj2 = lackProductService.findUnique(user.getProjectCode(), row[0], user.getOrganization().getOrgCode(), product.getId());
			if(obj2 != null){
				continue;
			}
			LackProduct lackProduct = new LackProduct();
			lackProduct.setMonth(row[0]);		
			lackProduct.setProduct(product);
			lackProduct.setReason(row[3]);		
			lackProduct.setHospitalCode(user.getOrganization().getOrgCode());
			lackProduct.setHospitalName(user.getOrganization().getOrgName());
			lackProduct.setCreateUser(user.getEmpId());
			
			lackProducts.add(lackProduct);
			map.put(row[0] + "#"+user.getOrganization().getOrgCode() + "#" + product.getId(), "");
		}	
		return lackProducts;
	}
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
