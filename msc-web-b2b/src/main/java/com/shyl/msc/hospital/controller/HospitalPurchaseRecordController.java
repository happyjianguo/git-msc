package com.shyl.msc.hospital.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.hospital.entity.PurchaseRecord;
import com.shyl.msc.b2b.hospital.service.IPurchaseRecordService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;


@Controller
@RequestMapping("/hospital/purchaseRecord")
public class HospitalPurchaseRecordController extends BaseController {

	@Resource
	private IPurchaseRecordService purchaseRecordService;
	@Resource
	private IProductService productService;
	
	@RequestMapping("")
	public String home() {
		return "hospital/purchaseRecord/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<PurchaseRecord> page(PageRequest page, @CurrentUser User user) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Map<String, Object> query = page.getQuery();
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Order order1 = new Order(Direction.DESC,"t.month");
		Order order2 = new Order(Direction.DESC,"t.createDate");
		Sort sort  = new Sort(order1 ,order2); 
		page.setSort(sort);
		
		return purchaseRecordService.query(user.getProjectCode(), page);
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "hospital/purchaseRecord/add";
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(PurchaseRecord purchaseRecord, @CurrentUser User user) {
		Message msg = new Message();
		try{
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				purchaseRecord.setHospitalCode(user.getOrganization().getOrgCode());
				purchaseRecord.setHospitalName(user.getOrganization().getOrgName());		
			}	
			purchaseRecord.setCreateUser(user.getEmpId());
			purchaseRecordService.save(user.getProjectCode(), purchaseRecord);
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
			purchaseRecordService.delete(user.getProjectCode(), id);
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
				List<PurchaseRecord> purchaseRecords = tranExcelToList(myfile, user);
				
				purchaseRecordService.saveBatch(user.getProjectCode(), purchaseRecords);
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
	private List<PurchaseRecord> tranExcelToList(MultipartFile file, User user) throws Exception {
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
				for (int j = 0; j < row0.getPhysicalNumberOfCells(); j++) {
					HSSFCell cell = row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return tranArrayToList(upExcel, user);
	}
	
	private List<PurchaseRecord> tranArrayToList(String[][] upExcel, User user) throws Exception{
		List<PurchaseRecord> purchaseRecords = new ArrayList<PurchaseRecord>();
		for (int i = 0; i < upExcel.length; i++) {
			String[] row = upExcel[i];
			
			if(!row[0].matches("^[1-9][0-9]{3}\\-((0[1-9])|(1[012]))$")){
				throw new Exception("第"+(i+1)+"笔数据异常，年月格式有误");
			}
			Product product = productService.getByCode(user.getProjectCode(), row[1]);
			if(product == null){
				throw new Exception("第"+(i+1)+"笔数据异常，药品编码有误");
			}
			if(!row[3].equals("省") && !row[3].equals("市")){
				throw new Exception("第"+(i+1)+"笔数据异常，交易平台格式有误");
			}
			Boolean numFormat = false;
			BigDecimal num = null;
			try{
				num = new BigDecimal(row[4]);
				numFormat = num.compareTo(new BigDecimal(0)) > 0;
			}catch(Exception e){		
			}
			if(!numFormat){
				throw new Exception("第"+(i+1)+"笔数据异常，采购数量格式有误");
			}	
			Boolean amtFormat = false;
			BigDecimal amt = null;
			try{
				amt = new BigDecimal(row[5]);
				amtFormat = amt.compareTo(new BigDecimal(0)) > 0;
			}catch(Exception e){		
			}
			if(!amtFormat){
				throw new Exception("第"+(i+1)+"笔数据异常，采购金额格式有误");
			}
			PurchaseRecord purchaseRecord = new PurchaseRecord();
			purchaseRecord.setMonth(row[0]);		
			purchaseRecord.setProduct(product);
			purchaseRecord.setProductTranId(row[2]);
			purchaseRecord.setPlatform(row[3]);
			purchaseRecord.setNum(num);
			purchaseRecord.setAmt(amt);
			purchaseRecord.setHospitalCode(user.getOrganization().getOrgCode());
			purchaseRecord.setHospitalName(user.getOrganization().getOrgName());
			purchaseRecord.setCreateUser(user.getEmpId());
			
			purchaseRecords.add(purchaseRecord);
		}	
		return purchaseRecords;
	}
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
