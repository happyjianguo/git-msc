package com.shyl.msc.vendor.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
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

@Controller
@RequestMapping("/vendor/contractClosedRequest")
public class VendorContractClosedRequestController extends BaseController {
	@Resource
	private IContractClosedRequestService contractClosedRequestService;


	@Resource
	private IMsgService msgService;
	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IOrganizationService organizationService;
	
	
	@Override
	protected void init(WebDataBinder binder) {
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user){
		return "vendor/contractClosedRequest/list";
	}

	@RequestMapping("/add")
	public String closeAdd(ModelMap model, @CurrentUser User user){
		model.addAttribute("user", user);
		return "vendor/contractClosedRequest/add";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ContractClosedRequest> page(PageRequest pageable, @CurrentUser User user){
		//Map<String, Object> query = pageable.getQuery();
		String hospitalCode = null;
		String vendorCode = null;
		String gpoCode = null;
		if(user.getOrganization().getOrgType() == 1){
			hospitalCode = user.getOrganization().getOrgCode();
		}else if(user.getOrganization().getOrgType() == 2){
			vendorCode = user.getOrganization().getOrgCode();
		}else if(user.getOrganization().getOrgType() == 6){
			gpoCode = user.getOrganization().getOrgCode();
		}
		Sort sort = new Sort(Direction.DESC,"t.createDate");
		pageable.setSort(sort);

		return contractClosedRequestService.queryByOrg(user.getProjectCode(), pageable,hospitalCode,vendorCode,gpoCode);
	}
	
	@RequestMapping("/commit")
	@ResponseBody
	public Message commit(Long id,String status,String reply, @CurrentUser User user){
		Message message = new Message();
		try {
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			ContractClosedRequest ccr = contractClosedRequestService.getById(user.getProjectCode(), id);
			ccr.setReply(reply);
			if(status.equals("2")){
				ccr.setStatus(Status.disagree);
				//发送系统消息
				this.sendMsg(user.getProjectCode(), ccr); 
			}else if(status.equals("1")){
				ccr.setStatus(Status.agree);
				if(ccr.getClosedObject().equals(ClosedObject.contract)){
					Contract c = contractService.findByCode(user.getProjectCode(), ccr.getContractCode());
					c.setStatus(com.shyl.msc.b2b.plan.entity.Contract.Status.stop);
					contractService.updateWithInclude(user.getProjectCode(), c, "status");
				}else if(ccr.getClosedObject().equals(ClosedObject.contractDetail)){
					ContractDetail cd =  contractDetailService.findByCode(user.getProjectCode(), ccr.getContractDetailCode());
					cd.setStatus(com.shyl.msc.b2b.plan.entity.ContractDetail.Status.stop);
					contractDetailService.update(user.getProjectCode(), cd);
				}
				
			}
			contractClosedRequestService.update(user.getProjectCode(), ccr);
			message.setSuccess(true);
			message.setMsg("终止成功");
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	private void sendMsg(String projectCode, ContractClosedRequest ccr) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		msg.setTitle("合同终止未通过。-- "+ccr.getContractCode());
		msg.setAttach("/b2b/monitor/contractClosedRequest.htmlx?code="+ccr.getContractCode());
		
		Contract c = contractService.findByCode(projectCode, ccr.getContractCode());
		Hospital hospital = hospitalService.findByCode(projectCode, c.getHospitalCode());
		Company vendor = companyService.findByCode(projectCode,c.getVendorCode(), "isVendor=1");
		//查询医院的orgId
		Organization oh = organizationService.getByOrgId(projectCode, hospital.getId(), 1);
		//查询供应商的orgId
		Organization og = organizationService.getByOrgId(projectCode, vendor.getId(), 2);
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
}
