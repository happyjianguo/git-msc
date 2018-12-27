package com.shyl.msc.hospital.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.shyl.common.cache.dao.TaskLockDAO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.ExcelUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.common.ContractPDF;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

@Controller
@RequestMapping("/hospital/contractDetail")
public class HospitalContractDetailController extends BaseController {

	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IProductService productService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IContractClosedRequestService contractClosedRequestService;
	@Resource
	private ISnService snService;
	@Resource
	private ContractPDF contractPDF;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private TaskLockDAO taskLockDAO;
	
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user) {
		AttributeItem ai = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "B");
		model.addAttribute("dateType", ai.getField3());
		return "hospital/contractDetail/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ContractDetail> page(@CurrentUser User user, PageRequest pageRequest) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			Sort sort = new Sort(new Order(Direction.ASC,"c.vendorCode"),new Order(Direction.ASC,"p.code"));;
			pageRequest.setSort(sort);
			return contractDetailService.pageByHospitalCode(user.getProjectCode(), pageRequest, user.getOrganization().getOrgCode());
		}else{
			return new DataGrid<>();
		}
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(@CurrentUser User user, Model model) {
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			model.addAttribute("hospitalCode", user.getOrganization().getOrgCode());
		}else{
			
		}
		return "hospital/contractDetail/add";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(@CurrentUser User user, Long productId, BigDecimal num, String gpoCode, String vendorCode) {
		Message msg = new Message();
		if(!taskLockDAO.lock("addContractCart"+user.getOrganization().getOrgCode())){
			msg.setSuccess(false);
			msg.setMsg("请等待其他合同加入完成");
			return msg;
		}
		try{
			AttributeItem aiA = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "A");
			if(!aiA.getField3().equals("1")){
				AttributeItem aiD = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "D");
				msg.setSuccess(false);
				msg.setMsg("已过合同签订日期("+aiD.getField3()+")");
				return msg;
			}
			
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				String hospitalCode = user.getOrganization().getOrgCode();
				Product product = productService.getById(user.getProjectCode(), productId);
				contractService.saveContract(user.getProjectCode(), hospitalCode, product, gpoCode, vendorCode, num);
				msg.setSuccess(true);
			}else{
				msg.setSuccess(false);
				msg.setMsg("不是医院账号");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}finally {
			taskLockDAO.unlock("addContractCart"+user.getOrganization().getOrgCode());
		}
		return msg;
	}
	
	@RequestMapping(value = "/addList", method = RequestMethod.POST)
	@ResponseBody
	public Message add(@CurrentUser User user, String data) {
		Message msg = new Message();
		if(!taskLockDAO.lock("addContractCartList"+user.getOrganization().getOrgCode())){
			msg.setSuccess(false);
			msg.setMsg("请等待其他合同加入完成");
			return msg;
		}
		try{
			AttributeItem aiA = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "A");
			if(!aiA.getField3().equals("1")){
				AttributeItem aiD = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "D");
				msg.setSuccess(false);
				msg.setMsg("已过合同签订日期("+aiD.getField3()+")");
				return msg;
			}
			
			if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
				String hospitalCode = user.getOrganization().getOrgCode();
				
				contractService.saveContractBatch(user.getProjectCode(), hospitalCode, data);
				msg.setSuccess(true);
			}else{
				msg.setSuccess(false);
				msg.setMsg("不是医院账号");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(false);
			msg.setMsg(e.getMessage());
		}finally {
			taskLockDAO.unlock("addContractCartList"+user.getOrganization().getOrgCode());
		}
		return msg;
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile myfile,@CurrentUser User user){  
		
		 Message message = new Message(); 
		 AttributeItem aiA = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "A");
			if(!aiA.getField3().equals("1")){
				AttributeItem aiD = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"contract", "D");
				message.setSuccess(false);
				message.setMsg("已过合同签订日期("+aiD.getField3()+")");
				return message;
			}
		 if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			 String filename = myfile.getOriginalFilename();  
			 if (filename == null || "".equals(filename)) {
				 message.setSuccess(false);
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
		 }else{
			 message.setSuccess(false);
			 message.setMsg("不是医院账号");
		 }
		 return message;
		
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Message delete(String ids[], @CurrentUser User user) {
		Message msg = new Message();
		try{
			if(null!=ids&&ids.length>0){
				for (int i = 0; i < ids.length; i++) {
					Long id =Long.valueOf(ids[i]);
					contractDetailService.delete(user.getProjectCode(), id);
				}
				msg.setMsg("删除成功");
			}
			msg.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("删除失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/deleteD", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteD(Long id,@CurrentUser User currentUser) {
		Message msg = new Message();
		try{
			ContractDetail cd = contractDetailService.getById(currentUser.getProjectCode(), id);
			if(cd != null ){
				Contract c = cd.getContract();
				if(c != null && (cd.getContract().getStatus().equals(Status.rejected) 
						|| cd.getContract().getStatus().equals(Status.unsigned))){
					contractDetailService.delete(currentUser.getProjectCode(), id);
					
			    	List<ContractDetail> lists = contractDetailService.findByPID(currentUser.getProjectCode(), c.getId());
			    	if (lists.size() == 0) {
			    		contractService.delete(currentUser.getProjectCode(), c);
			    	} else {
						msg.setMsg("删除成功");
						//重新产生pdf
						String pdfpath = contractPDF.makePdf(c, lists, currentUser);
						c.setFilePath(pdfpath);
						contractService.update(currentUser.getProjectCode(), c);
			    	}
				}
				
			}else{
				throw new Exception("数据异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg(e.getMessage());
			msg.setSuccess(false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	@ResponseBody
	public Message close(Long id,String reason, @CurrentUser User user) {
		System.out.println("id="+id);
		System.out.println("reason="+reason);
		Message msg = new Message();
		try{
			if(user.getOrganization().getOrgType() !=null && user.getOrganization().getOrgId() != null){
				Integer type = user.getOrganization().getOrgType();
				if(type == 1){
					ContractDetail cd = contractDetailService.getById(user.getProjectCode(), id);
					
					ContractClosedRequest ccr = contractClosedRequestService.findByContractDetail(user.getProjectCode(), cd.getCode());
					if(ccr != null){
						msg.setSuccess(false);
						msg.setMsg("该合同明细已经提交申请结案");
						return msg;
					}
					
					ccr = new ContractClosedRequest();
					ccr.setCode(snService.getCode(user.getProjectCode(), OrderType.contractClosedRequest));
					ccr.setContractCode(cd.getContract().getCode());
					ccr.setContractDetailCode(cd.getCode());
					ccr.setClosedMan(user.getName());
					ccr.setClosedObject(ClosedObject.contractDetail);
					ccr.setClosedRequestDate(new Date());
					ccr.setStatus(com.shyl.msc.b2b.plan.entity.ContractClosedRequest.Status.unaudit);
					ccr.setReason(reason);
					contractClosedRequestService.save(user.getProjectCode(), ccr);
					
					msg.setMsg("申请结案成功");
				}else{
					msg.setMsg("您不是医院身份登录");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("终止失败");
			msg.setSuccess(false);
		}
		return msg;
	}
	@Override
	protected void init(WebDataBinder binder) {

	}

	private String doExcelH(MultipartFile file, User user) throws Exception {
		String[][] upExcel = null;
		InputStream input = file.getInputStream();
		Workbook workBook = null;
		//文件名称
		String filename = file.getOriginalFilename();
		if(filename.endsWith(".xlsx")) {
			workBook = new XSSFWorkbook(input);
		} else {
			workBook = new HSSFWorkbook(input);
		}
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
					Cell cell = row.getCell(j);
					String cellStr = ExcelUtil.getValue(cell);
					upExcel[i - 1][j] = cellStr;
				}
			}
		}
		workBook.close();
		return contractService.importExcel(user.getProjectCode(), upExcel, user);
	}

	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public Message update(Long id, Integer goodsNum, @CurrentUser User currentUser) {
		Message msg = new Message();
		try{
			ContractDetail cd = contractDetailService.getById(currentUser.getProjectCode(), id);
			if(cd != null ){
				Contract c = cd.getContract();
				if(c != null && cd.getContract().getStatus().equals(Status.noConfirm)){
					cd.setContractNum(new BigDecimal(goodsNum));
					cd.setContractAmt(new BigDecimal(goodsNum).multiply(cd.getPrice()));
					contractDetailService.update(currentUser.getProjectCode(), cd);
					
			    	List<ContractDetail> lists = contractDetailService.findByPID(currentUser.getProjectCode(), c.getId());
					//重新产生pdf
					String pdfpath = contractPDF.makePdf(c, lists, currentUser);
					c.setFilePath(pdfpath);
					contractService.update(currentUser.getProjectCode(), c);
				}
				
			}else{
				throw new Exception("数据异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg(e.getMessage());
			msg.setSuccess(false);
		}
		return msg;
	}
}
