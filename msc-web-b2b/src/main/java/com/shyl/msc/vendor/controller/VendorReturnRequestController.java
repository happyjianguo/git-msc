package com.shyl.msc.vendor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.msc.b2b.order.entity.ReturnsRequest.Status;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IReturnsOrderService;
import com.shyl.msc.b2b.order.service.IReturnsRequestDetailService;
import com.shyl.msc.b2b.order.service.IReturnsRequestService;
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
 * 退货审核Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/vendor/returnrequest")
public class VendorReturnRequestController extends BaseController {
	@Resource
	private IReturnsRequestService returnsRequestService;
	@Resource
	private IReturnsRequestDetailService returnsRequestDetailService;
	@Resource
	private IReturnsOrderService returnsOrderService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IMsgService msgService;
	
	@RequestMapping("")
	public String home(){
		return "vendor/returnrequest/list";
	}

	@RequestMapping(value="/page")
	@ResponseBody
	public DataGrid<ReturnsRequest> page(PageRequest pageable, @CurrentUser User user){
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return null;
		}
		
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		query.put("t#vendorCode_S_EQ", company.getCode());
		query.put("t#status_S_NE", Status.agree);
		query.put("t#status_L_NE", Status.disagree);
		Sort sort = new Sort(Direction.DESC,"createDate");
		pageable.setSort(sort);

		return returnsRequestService.query(user.getProjectCode(), pageable);
	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public List<ReturnsRequestDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		List<ReturnsRequestDetail> list = returnsRequestDetailService.list(user.getProjectCode(), pageable);
		return list;
	}
	/**
	 * 条件输入画面
	 * @return
	 */
	@RequestMapping(value = "/para", method = RequestMethod.GET)
	public String para(){
		return "vendor/returnrequest/para";
	}
	/**
	 * 退货成功画面
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(){
		return "vendor/returnrequest/success";
	}
	
	/**
	 * 同意退货，生成退货单
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mkreturn")
	@ResponseBody
	public Message mkreturn(Long returnrequestId,String explain, String data, @CurrentUser User user){
		Message message = new Message();
		try {
			System.out.println("----data----"+data);
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			String ddbh = returnsOrderService.mkreturn(user.getProjectCode(), returnrequestId,explain,data);
			//String ddbh = "";
			//message.setMsg(msg);
			//System.out.println("ddbh="+pp.getCode());
			message.setData(ddbh);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	/**
	 * 不同意退货
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/cancelreturn")
	@ResponseBody
	public Message cancelreturn(Long returnrequestId,String explain, @CurrentUser User user){
		Message message = new Message();
		try {
			
			if(user.getOrganization().getOrgType()!=2)
				throw new Exception("您不是供应商帐号");
			if(user.getOrganization().getOrgId() == null)
				throw new Exception("未绑定供应商");
			
			ReturnsRequest rr = returnsRequestService.getById(user.getProjectCode(), returnrequestId);
			if(rr != null){
				rr.setReply(explain);
				rr.setStatus(Status.disagree);
				returnsRequestService.update(user.getProjectCode(), rr);
				//发送系统消息
				this.sendMsg(user.getProjectCode(), rr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg(e.getMessage());
		}
		return message;
	}
	
	private void sendMsg(String projectCode, ReturnsRequest rr) {
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		msg.setTitle("退货审核未通过。-- "+rr.getCode());
		msg.setAttach("/b2b/monitor/returnsRequest.htmlx?code="+rr.getCode());
		
		Hospital hospital = hospitalService.findByCode(projectCode, rr.getHospitalCode());
		Company vendor = companyService.findByCode(projectCode,rr.getVendorCode(), "isVendor=1");
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
	
	@Override
	protected void init(WebDataBinder binder) {

	}

}
