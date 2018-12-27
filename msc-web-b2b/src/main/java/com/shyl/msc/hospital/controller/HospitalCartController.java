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

import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.msc.enmu.UrgencyLevel;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/cart")
public class HospitalCartController extends BaseController {
	@Resource
	private  ICartService cartService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IPurchasePlanService purchasePlanService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(){
		return "hospital/cart/list";
	}

	/**
	 * 查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<Map<String, Object>> list(@CurrentUser User user){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(user.getOrganization().getOrgId() == null){
			return list;
		}
		Integer type = user.getOrganization().getOrgType();
		if(type != 1){
			return list;
		}
		list =  cartService.queryByHospital(user.getProjectCode(), user.getOrganization().getOrgCode());
		return list;
	}
	
	/**
	 * 确认下单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkorder")
	@ResponseBody
	public Message mkorder(PurchasePlan purchasePlan,Integer uLevel,Long[] cartids,Long[] goodspriceids, BigDecimal[] nums, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType()!=1)
				throw new Exception("您不是医院帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定医院");
			if(uLevel == 0)
				purchasePlan.setUrgencyLevel(UrgencyLevel.urgent);
			else
				purchasePlan.setUrgencyLevel(UrgencyLevel.noturgent);
			
			Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			purchasePlan.setHospitalCode(hospital.getCode());
			
			String ddbh = purchasePlanService.mkOrder(user.getProjectCode(), purchasePlan,cartids,goodspriceids,nums,user);
			System.out.println("ddbh="+ddbh);
			message.setData(ddbh);
			message.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "hospital/cart/para";
	}
	
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "hospital/cart/success";
	}
	
	/**
	 * 导入失败画面
	 * @return
	 */
	@RequestMapping(value = "/importErr", method = RequestMethod.GET)
	public String importErr(){
		return "hospital/cart/importErr";
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long[] cartids, @CurrentUser User user){
		Message message = new Message();
		
		try{
			for (Long id : cartids) {
				System.out.println("id="+id);
				cartService.delete(user.getProjectCode(), id);
			}

		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		
		return  message;
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
		return cartService.doExcelH(user.getProjectCode(), upExcel, user);
	}
	
	
}
