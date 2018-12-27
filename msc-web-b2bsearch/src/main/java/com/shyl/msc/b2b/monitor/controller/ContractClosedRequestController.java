package com.shyl.msc.b2b.monitor.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractClosedRequestService;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.sys.entity.User;
/**
 * 退货单Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/b2b/monitor/contractClosedRequest")
public class ContractClosedRequestController extends BaseController {

	@Resource
	private IContractClosedRequestService contractClosedRequestService;
	@Resource
	private IContractDetailService contractDetailService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(ModelMap model, @CurrentUser User user,String code){
		model.addAttribute("code", code);
		return "b2b/monitor/contractClosedRequest/list";
	}
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<ContractClosedRequest> page(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		String status = (String)query.get("t#status_S_EQ");
		if(!StringUtils.isEmpty(status)){
			query.put("t#status_I_EQ", ContractClosedRequest.Status.valueOf(status).ordinal());
			query.remove("t#status_S_EQ");
		}
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
		Sort sort = new Sort(Direction.DESC,"t.code");
		pageable.setSort(sort);


		return contractClosedRequestService.queryByOrg(user.getProjectCode(), pageable,hospitalCode,vendorCode,gpoCode);
	}

//	/**
//	 * 明细分页查询
//	 * @param pageable
//	 * @return
//	 */
//	@RequestMapping("/mxlist")
//	@ResponseBody
//	public DataGrid<ReturnsRequestDetail> mxlist(PageRequest pageable){
//		List<ReturnsRequestDetail> list = returnsRequestDetailService.list(pageable);
//		DataGrid<ReturnsRequestDetail> page = new DataGrid<ReturnsRequestDetail>(list, null, list.size());
//		page.addFooter("productCode", "goodsNum","goodsSum");
//		return page;
//	}
	
	/**
	 * 明细分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/mxlist")
	@ResponseBody
	public DataGrid<ContractDetail> mxlist(Long id,PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(Direction.ASC,"product.code");
		pageable.setSort(sort);
		ContractClosedRequest ccr = contractClosedRequestService.getById(user.getProjectCode(), id);
		Map<String, Object> query = pageable.getQuery();
		query.put("t#contract.code_S_EQ", ccr.getContractCode());
		
		if(ccr.getClosedObject().equals(ClosedObject.contractDetail)){
			query.put("t#code_S_EQ", ccr.getContractDetailCode());
		}
		
		DataGrid<ContractDetail> page = contractDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
}
