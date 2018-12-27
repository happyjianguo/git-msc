package com.shyl.msc.vendor.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
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
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;
/**
 * 审单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/contract")
public class VendorContractController extends BaseController {
	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IMsgService msgService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/contract/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<Contract> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return null;
		}
		Map<String, Object> query = pageable.getQuery();
		
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		query.put("t#vendorCode_S_EQ", company.getCode());
		//query.put("t#status_S_EQ",Status.hospitalSigned);
		
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_S_EQ", Contract.Status.valueOf(status));
		}
		Sort sort = new Sort(new Order(Direction.DESC,"createDate"));
		pageable.setSort(sort);
		return contractService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ContractDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.DESC,"product.code"));
		pageable.setSort(sort);
		DataGrid<ContractDetail> page = contractDetailService.query(user.getProjectCode(), pageable);
		
		return page;
	}

	/**
	 * 下单成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/contract/success";
	}
	
	/**
	 * 审计划单
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	@ResponseBody
	public Message check(Long id,@CurrentUser User user){
		Message message = new Message();
		try{
			contractDetailService.gpoCheck(user.getProjectCode(), id);
			
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	/**
	 * 审计划单 拒绝
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/uncheck", method = RequestMethod.POST)
	@ResponseBody
	public Message uncheck(Long id,@CurrentUser User user){
		Message message = new Message();
		try{
			Contract c= contractService.getById(user.getProjectCode(), id);
			c.setStatus(Status.rejected);
			c = contractService.update(user.getProjectCode(), c);
			sendMsg(user.getProjectCode(), c);
		}catch(Exception e){
			e.printStackTrace();
			message.setSuccess(false);
		}
		return  message;
	}
	
	private void sendMsg(String projectCode, Contract c) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		if(c.getStatus().equals(Status.rejected)){
			msg.setTitle("合同被供应商驳回。-- "+c.getCode());
		}else {
			return;
		}
		
		msg.setAttach("/hospital/contract.htmlx?code="+c.getCode());
		Hospital hospital = hospitalService.findByCode(projectCode, c.getHospitalCode());
		Company company = companyService.findByCode(projectCode,c.getVendorCode(), "isVendor=1");
		//查询医院的orgId
		Organization oh = organizationService.getByOrgId(projectCode, hospital.getId(), 1);
		//查询供应商的orgId
		Organization og = organizationService.getByOrgId(projectCode, company.getId(), 2);
		if(og != null){
			msg.setOrganizationId(og.getId());
			msg.setOrgName(og.getOrgName());
		}
		Long ohId = Long.parseLong("-1");
		if(oh != null){
			ohId = oh.getId();
		}
		try {
			msgService.sendBySYSToOrg(projectCode, msg, ohId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
