package com.shyl.msc.b2b.report.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.msc.b2b.plan.service.IHospitalPlanService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;

/**
 * 医院合同签订情况
 * @author hefeng
 *
 */

@Controller("reporthospitalContract")
@RequestMapping("/b2b/report/hospitalContract")
public class HospitalContractController extends BaseController {

	@Resource
	private IPurchaseOrderDetailService purchaseOrderDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private IContractService contractService;
	@Resource
	private IContractDetailService contractDetailService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(Model model,@CurrentUser User user){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "CITY");
		model.addAttribute("regionCodePId",attributeItem.getField3());
		return "b2b/report/hospitalcontract/list";
	}
	
	/**
	 * 分页查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/page")
	@ResponseBody
	public DataGrid<Map<String, Object>> page(PageRequest pageable, String startDate, String endDate,@CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.DESC,"t.hospitalCode"));
		pageable.setSort(sort);
		
		DataGrid<Map<String, Object>> page = contractService.reportForHospitalContract(user.getProjectCode(), startDate, endDate, pageable);
		String hospitalCodes = this.getHospitalCodes(page);
		if(hospitalCodes != null){
			//药品品种数 productNum,合同金额 contractAmt
			Map<String, Object> productNumMap = new HashMap<String, Object>();
			Map<String, Object> contractAmtMap = new HashMap<String, Object>();
			List<Map<String, Object>> list1 = contractService.reportForHospitalContract1(user.getProjectCode(), hospitalCodes,startDate, endDate, pageable);
			for (Map<String, Object> m : list1) {
				productNumMap.put(m.get("HOSPITALCODE")+"", m.get("PRODUCTNUM"));
				contractAmtMap.put(m.get("HOSPITALCODE")+"", m.get("CONTRACTAMT"));
			}
//			System.out.println("productNumMap="+productNumMap);
//			System.out.println("contractAmtMap="+contractAmtMap);
			//已采购药品品种数 productOrderNum，已采购总金额 purchaseAmt
			Map<String, Object> productOrderNumMap = new HashMap<String, Object>();
			Map<String, Object> purchaseAmtMap = new HashMap<String, Object>();
			List<Map<String, Object>> list2 = contractService.reportForHospitalContract2(user.getProjectCode(), hospitalCodes,startDate, endDate, pageable);
			for (Map<String, Object> m : list2) {
				productOrderNumMap.put(m.get("HOSPITALCODE")+"", m.get("PRODUCTORDERNUM"));
				purchaseAmtMap.put(m.get("HOSPITALCODE")+"", m.get("PURCHASEAMT"));
			}
			//塞入page
			for (Map<String, Object> m : page.getRows()) {
				String hospital = m.get("HOSPITALCODE")+"";
				m.put("PRODUCTNUM", productNumMap.get(hospital));
				m.put("CONTRACTAMT", contractAmtMap.get(hospital));
				m.put("PRODUCTORDERNUM", productOrderNumMap.get(hospital));
				m.put("PURCHASEAMT", purchaseAmtMap.get(hospital));
				if(hospital.equals("TT20161102")){
					System.out.println("TT20161102===="+m);
				}
			}
		}
		page.addFooter("HOSPITALNAME", "CONTRACTNUM","PRODUCTNUM","PRODUCTORDERNUM","CONTRACTAMT","PURCHASEAMT");
		return page;
	}
	
	private String getHospitalCodes(DataGrid<Map<String, Object>> page) {
		if(page.getTotal() == 0)
			return null;
		String s = "(";
		for (Map<String, Object> m : page.getRows()) {
			s += "'"+m.get("HOSPITALCODE")+"',";
		}
		s = s.substring(0, s.length()-1);
		s+=")";
		System.out.println("s = "+s);
		return s;
	}
	
	@RequestMapping("/mxpage")
	@ResponseBody
	public DataGrid<ContractDetail> mxlist(PageRequest pageable, @CurrentUser User user){
		Sort sort = new Sort(new Order(Direction.ASC,"t.contract.id"));
		pageable.setSort(sort);
		DataGrid<ContractDetail> page =  contractDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	@RequestMapping("/page2")
	@ResponseBody
	public DataGrid<PurchaseOrderDetail> page2(PageRequest pageable, @CurrentUser User user){
//		Sort sort = new Sort(new Order(Direction.ASC,"t.contract.id"));
//		pageable.setSort(sort);
		DataGrid<PurchaseOrderDetail> page =  purchaseOrderDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	@Override
	protected void init(WebDataBinder binder) {
		// TODO Auto-generated method stub
	}
	
}
