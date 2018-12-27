package com.shyl.msc.hospital.controller;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.shyl.msc.b2b.order.entity.CartContract;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.service.ICartContractService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.msc.enmu.UrgencyLevel;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/cartContract")
public class HospitalCartContractController extends BaseController {
	@Resource
	private  ICartContractService cartContractService;
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
	public String home(Model model,@CurrentUser User user){
		if(user.getOrganization().getOrgId() != null){
			Integer type = user.getOrganization().getOrgType();
			if(type == 1){
				String totalstr = getTotal(user.getProjectCode(), user.getOrganization().getOrgCode());
				model.addAttribute("totalstr", totalstr);
			}
		}
		return "hospital/cartContract/list";
	}


	private String getTotal(String projectCode, String orgCode) {
		Map<String, Object> map = cartContractService.getTotal(projectCode, orgCode);
		if(map == null){
			return "";
		}
		DecimalFormat myformat = new DecimalFormat();
		myformat.applyPattern("##,###.00");
		String count = "";
		String num = "";
		String sum = "";
		if(map.get("COUNT") != null){
			count = map.get("COUNT")+"";
		}
		if(map.get("NUM") != null){
			num = myformat.format(map.get("NUM"));
		}
		if(map.get("SUM") != null){
			sum = myformat.format(map.get("SUM"));
		}
		String s = "药品总类："+count+"		总数量："+num+"	总金额："+sum;
		return s;
	}

	/**
	 * 查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable,@CurrentUser User user){
		DataGrid<Map<String, Object>> page =  new DataGrid<Map<String, Object>>();
		if(user.getOrganization().getOrgId() == null){
			return page;
		}
		
		Integer type = user.getOrganization().getOrgType();
		if(type != 1){
			return page;
		}

		page =  cartContractService.queryByHospital(user.getProjectCode(), user.getOrganization().getOrgCode(),pageable);
		return page;
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(Integer goodsNum,Long cartId, @CurrentUser User user){
		System.out.println("-----cartId----"+cartId);
		System.out.println("-----goodsNum----"+goodsNum);
		System.out.println("-----user----"+user.getEmpId());
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					CartContract cc =  cartContractService.getById(user.getProjectCode(), cartId);
					if(cc == null){
						message.setSuccess(false);
						message.setMsg("该购物车不存在");
					}else{
						cc.setNum(goodsNum);
						cartContractService.update(user.getProjectCode(), cc);
						String s = this.getTotal(user.getProjectCode(), user.getOrganization().getOrgCode());
						message.setData(s);
						message.setSuccess(true);
					}
					
				}else{
					message.setSuccess(false);
					message.setMsg("不是医疗机构账号");
				}
			}else{
				message.setSuccess(false);
				message.setMsg("不是医疗机构账号");
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	/**
	 * 确认下单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkorder")
	@ResponseBody
	public Message mkorder(PurchasePlan purchasePlan,Integer uLevel,@CurrentUser User user){
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
			String ddbh = purchasePlanService.mkOrderContract(user.getProjectCode(), purchasePlan,user);
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
		return "hospital/cartContract/para";
	}
	
	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "hospital/cartContract/success";
	}
	
	/**
	 * 导入失败画面
	 * @return
	 */
	@RequestMapping(value = "/importErr", method = RequestMethod.GET)
	public String importErr(){
		return "hospital/cartContract/importErr";
	}
	
	/**
	 * 删除
	 * @param Long
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long[] cartids,@CurrentUser User user){
		Message message = new Message();
		
		try{
			cartContractService.delete(user.getProjectCode(), cartids);
			String s = this.getTotal(user.getProjectCode(), user.getOrganization().getOrgCode());
			message.setData(s);
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
		return cartContractService.doExcelH(user.getProjectCode(), upExcel, user);
		
	}
	
	
}
