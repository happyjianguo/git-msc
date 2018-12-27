package com.shyl.msc.set.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.set.entity.CompanyCertReg;
import com.shyl.msc.set.entity.CompanyCertReg.AuditStatus;
import com.shyl.msc.set.service.ICompanyCertRegService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

/**
 * 企业证照注册申请审核
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/set/companyCertReg")
public class CompanyCertRegController extends BaseController {
	@Resource
	private ICompanyCertRegService companyCertRegService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private GridFSDAO gridFSDAO;
	
	@RequestMapping("")
	public String home(){
		return "set/companyCertReg/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<CompanyCertReg> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();	
		query.put("t#auditStatus_L_NE", AuditStatus.create);
		Sort sort = new Sort(new Order(Direction.DESC,"t.sendTime"));
		pageable.setSort(sort);
		return companyCertRegService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 修改画面
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, Model model, @CurrentUser User user){	
		CompanyCertReg companyCertReg = companyCertRegService.getById(user.getProjectCode(), id);
		model.addAttribute("companyCertReg", companyCertReg);
		return "set/companyCertReg/edit";
	}
	
	/**
	 * 审核
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Message edit(CompanyCertReg companyCertReg, @CurrentUser User user){
		Message message = new Message();
		try{
			companyCertReg.setAuditTime(new Date());
			companyCertRegService.copy(user.getProjectCode(), companyCertReg);
			
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
