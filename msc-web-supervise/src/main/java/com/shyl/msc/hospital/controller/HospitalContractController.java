package com.shyl.msc.hospital.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.ca.CommonCA;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.GridFSDAO;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

@Controller
@RequestMapping("/hospital/contract")
public class HospitalContractController extends BaseController {

	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private GridFSDAO gridFSDAO;
	
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user, String code) {
		model.addAttribute("code", code);
		/*model.addAttribute("oid", user.getClientCert());
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "ISCAUSED");
		model.addAttribute("ISCAUSED", attributeItem.getField3());*/
		return "hospital/contract/list";
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Contract> page(PageRequest page, @CurrentUser User user) {
		Map<String, Object> query = page.getQuery();
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", Contract.Status.valueOf(status));
		}
		query.put("t#status_S_NE", Contract.Status.noConfirm);
		if(user.getOrganization() != null && user.getOrganization().getOrgType()==1){
			query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		}
		Sort sort = new Sort(Direction.DESC,"code");
		page.setSort(sort);
		return contractService.pageContract(user.getProjectCode(), page);
	}
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ContractDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"product.code");
		pageable.setSort(sort);
		DataGrid<ContractDetail> page = contractDetailService.mxlist(user.getProjectCode(), pageable);
		return page;
	}

	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success() {
		return "hospital/contract/success";
	}


	/**
	 * 查看附件
	 * @param user
	 */
	@RequestMapping("/readfile")
	public void exportExcel(Long id,@CurrentUser User user, HttpServletResponse response){
//		response.setContentType("application/pdf");
//		response.setHeader("Content-Disposition", "attachment; filename=aaa.pdfss");
//		response.setHeader("Expires","0");

		Contract c = contractService.getById(user.getProjectCode(), id);
		response.setContentType("application/pdf;charset=UTF-8");
//		response.setHeader("Content-Disposition", "attachment; filename=pdfFilled.pdf");
//		response.setHeader("Expires","0");
		
		gridFSDAO.readFileByIdToOutputStream(c.getFilePath(), "contract", response);
	}
	


	
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
