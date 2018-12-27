package com.shyl.msc.vendor.controller;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.CompanyCertReg;
import com.shyl.msc.set.entity.CompanyCertReg.AuditStatus;
import com.shyl.msc.set.service.ICompanyCertRegService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 企业证照注册申请
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/companyCertReg")
public class VendorCertRegController extends BaseController {
	@Resource
	private ICompanyCertRegService companyCertRegService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private GridFSDAO gridFSDAO;
	
	@RequestMapping("")
	public String home(){
		return "vendor/companyCertReg/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<CompanyCertReg> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return null;
		}
		Map<String, Object> query = pageable.getQuery();	
		query.put("t#declarant.id_L_EQ", user.getOrganization().getOrgId());
		Sort sort = new Sort(new Order(Direction.DESC,"t.createDate"));
		pageable.setSort(sort);
		return companyCertRegService.query(user.getProjectCode(), pageable);
	}

	/**
	 * 新增画面
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(){
		return "vendor/companyCertReg/add";
	}
	
	/**
	 * 新增
	 * @param permission
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Message add(CompanyCertReg companyCertReg, @RequestParam("pic") CommonsMultipartFile pic, @CurrentUser User user){
		Message message = new Message();
		try{
			DiskFileItem fi = (DiskFileItem)pic.getFileItem(); 
		    File f = fi.getStoreLocation();
			String fileid = gridFSDAO.saveFile(f, pic.getOriginalFilename(), "companyCert");
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$"+fileid);
			companyCertReg.setImagePath(fileid);
			Company declarant = companyService.getById(user.getProjectCode(),user.getOrganization().getOrgId());
			companyCertReg.setDeclarant(declarant);
			companyCertReg.setAuditStatus(AuditStatus.create);
			companyCertRegService.save(user.getProjectCode(), companyCertReg);
			message.setSuccess(true);
		}catch(Exception e){		
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return  message;
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, Model model, @CurrentUser User user){	
		CompanyCertReg companyCertReg = companyCertRegService.getById(user.getProjectCode(), id);
		model.addAttribute("companyCertReg", companyCertReg);
		return "vendor/companyCertReg/edit";
	}
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(CompanyCertReg companyCertReg, @RequestParam("pic") CommonsMultipartFile pic, @CurrentUser User user){
		Message message = new Message();
		try{
			if(companyCertReg.getImagePath() == null || companyCertReg.getImagePath().equals("")){	
				DiskFileItem fi = (DiskFileItem)pic.getFileItem(); 
			    File f = fi.getStoreLocation();
				String fileid = gridFSDAO.saveFile(f, pic.getOriginalFilename(), "companyCert");
				companyCertReg.setImagePath(fileid);
			}
			Company company = companyService.getById(user.getProjectCode(), companyCertReg.getCompany().getId());
			companyCertReg.setCompany(company);
			companyCertRegService.updateWithExclude(user.getProjectCode(), companyCertReg, "declarant", "auditStatus");
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 发送
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	@ResponseBody
	public Message send(Long id, @CurrentUser User user){
		Message message = new Message();
		try{			
			CompanyCertReg companyCertReg = companyCertRegService.getById(user.getProjectCode(),id);
			companyCertReg.setAuditStatus(AuditStatus.send);
			companyCertReg.setAuditTime(new Date());
			companyCertRegService.update(user.getProjectCode(), companyCertReg);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	/**
	 * 删除
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Message del(Long id, @CurrentUser User user){
		Message message = new Message();
		
		try{
			companyCertRegService.delete(user.getProjectCode(), id);
			message.setSuccess(true);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		
		return  message;
	}
	
	/**
	 * 查看附件
	 * @param user
	 * @param resp
	 */
	@RequestMapping(value = "/readfile", method = RequestMethod.GET)
	public void exportExcel(String fileid, HttpServletResponse response){
		
		gridFSDAO.findFileByIdToOutputStream(fileid, "companyCert", response);
	}
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
