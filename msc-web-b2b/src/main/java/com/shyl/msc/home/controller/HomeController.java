package com.shyl.msc.home.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.shyl.common.framework.annotation.CurrentUser;
import com.shyl.common.util.DateUtil;
import com.shyl.common.web.controller.BaseController;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.service.IDeliveryOrderService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Notice;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.INoticeService;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.FileManagement;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IFileManagementService;
/**
 * 首页Controller
 * 
 * @author a_Q
 *
 */
@Controller
@RequestMapping("/home")
public class HomeController extends BaseController {

	@Resource
	private IHospitalService hospitalService;
	
	@Resource
	private INoticeService noticeService;
	@Resource
	private IPurchaseOrderService purchaseOrderService;
	@Resource
	private IProductService productService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private  IFileManagementService fileManagementService;
	@Resource
	private IDeliveryOrderService deliveryOrderService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	protected void init(WebDataBinder binder) {

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("")
	public String home(@CurrentUser User user){
		if(user.getOrganization().getOrgType() != null &&user.getOrganization().getOrgType()== 9)
			return "sys/user/sql";
		if(user.getOrganization().getOrgId() != null && user.getOrganization().getOrgType()!= null ){
			if(user.getOrganization().getOrgType()== 1)
				return "home/homeForH";
			if(user.getOrganization().getOrgType()== 2)
				return "home/homeForG";
			if(user.getOrganization().getOrgType()== 4)
				return "home/homeForGC";
		}
		return "home/homeForM";
	}
	/**
	 * 系统消息
	 * @return
	 */
	@RequestMapping("/msg")
	public String msg(){
		return "home/msg";
	}
	/**
	 * 公告
	 * @return
	 */
	@RequestMapping("/notice")
	public String notice(Model model, @CurrentUser User user){
		PageRequest pageable = new PageRequest();
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#status_I_EQ", 1);
		pageable.setQuery(query);
		pageable.setSort(new Sort(Direction.DESC,"publishDate"));
		List<Notice> list =  noticeService.list(user.getProjectCode(), pageable);
		for (Notice notice : list) {
			List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
			if(l == null || l.size() == 0){
				notice.setFileManagement(null);
			}
		}
		while(list.size()>5){
			list.remove(5);
		}
		model.addAttribute("noticeList", list);
		return "home/notice";
	}
	/**
	 * 公告详情
	 * @return
	 */
	@RequestMapping("/notice/sub")
	public String noticeSub(Long id,Model model, @CurrentUser User user){
		System.out.println("id="+id);
		Notice notice = noticeService.getById(user.getProjectCode(), id);
		List<FileManagement> l =fileManagementService.findByKeyFlag(user.getProjectCode(), "notice_"+notice.getId());
		for (FileManagement fileManagement : l) {
			notice.getFileManagement().add(fileManagement);
		}
		if(notice.getFileManagement().size() == 0){
			notice.setFileManagement(null);
		}
		model.addAttribute("notice", notice) ;
		return "home/noticeSub";
	}
	
	/**
	 * 采购统计
	 * @return
	 */
	@RequestMapping("/total")
	public String total(Model model,@CurrentUser User user){
		model.addAttribute("name","未找到医院名称");
		if(user.getOrganization().getOrgId() != null){
			Hospital h = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			if(h != null){
				model.addAttribute("name",h.getFullName());
				Calendar cal=Calendar.getInstance(); 
				//今年采购量
				int year = cal.get(Calendar.YEAR);
				
				Map<String,Object> yearM = purchaseOrderService.orderSumByHospitalAndYear(user.getProjectCode(), h.getCode(),year+"");
				String yearSum = "0.00";
				if(yearM != null){
					yearSum = yearM.get("ORDERSUM")+"";
				}
				model.addAttribute("jnsum", formatSum(yearSum));
				//去年采购量
				int lastYear = year-1;
				Map<String,Object> lastYearM = purchaseOrderService.orderSumByHospitalAndYear(user.getProjectCode(), h.getCode(),lastYear+"");
				String lastYearSum = "0.00";
				if(lastYearM != null){
					lastYearSum = lastYearM.get("ORDERSUM")+"";
				}
				model.addAttribute("qnsum", formatSum(lastYearSum));
			}
		}
		return "home/total";
	}
	/**
	 * 采购统计 gpo
	 * @return
	 */
	@RequestMapping("/totalG")
	public String totalG(Model model,@CurrentUser User user){
		model.addAttribute("name","未找到供应商名称");
		if(user.getOrganization().getOrgId() != null){
			Company c = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
			if(c != null){
				model.addAttribute("name",c.getFullName());
				Calendar cal=Calendar.getInstance(); 
				//今年采购量
				int year = cal.get(Calendar.YEAR);
				
				Map<String,Object> yearM = purchaseOrderService.orderSumByGpoAndYear(user.getProjectCode(), c.getCode(),year+"");
				String yearSum = "0.00";
				if(yearM != null){
					yearSum = yearM.get("ORDERSUM")+"";
				}
				model.addAttribute("jnsum", formatSum(yearSum));
				//去年采购量
				int lastYear = year-1;
				Map<String,Object> lastYearM = purchaseOrderService.orderSumByGpoAndYear(user.getProjectCode(), c.getCode(),lastYear+"");
				String lastYearSum = "0.00";
				if(lastYearM != null){
					lastYearSum = lastYearM.get("ORDERSUM")+"";
				}
				model.addAttribute("qnsum", formatSum(lastYearSum));
			}
		}
		return "home/totalG";
	}
	
	private String formatSum(String num){
		String s = num;
		BigDecimal b = new BigDecimal(s);
		if(b.compareTo(new BigDecimal("9999"))>0){
			b = b.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP);
			s = b+"万元";
		}else{
			b = b.divide(new BigDecimal("1"), 2, BigDecimal.ROUND_HALF_UP);
			s = b+"元";
		}
		return s;
	}
	/**
	 * 收货
	 * @return
	 */
	@RequestMapping("/inout")
	public String inout(){
		return "home/inout";
	}
	
	/**
	 * 分页查询 待收货状态,医院查询
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/inoutpage")
	@ResponseBody
	public DataGrid<DeliveryOrder> inoutpage(PageRequest pageable, @CurrentUser User user){
		DataGrid<DeliveryOrder> page = new DataGrid<DeliveryOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 1 || user.getOrganization().getOrgId() == null){
			return page;
		}
		
		Map<String, Object> query = pageable.getQuery();
		if(query == null){
			query = new HashMap<String,Object>();
			pageable.setQuery(query);
		}
		query.put("t#hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		query.put("t#status_I_NE", com.shyl.msc.b2b.order.entity.DeliveryOrder.Status.closed);
		
		page =  deliveryOrderService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * 订单追踪 医院
	 * @return
	 */
	@RequestMapping("/orderH")
	public String orderH(){
		return "home/orderH";
	}
	
	/**
	 * 订单追踪 gpo
	 * @return
	 */
	@RequestMapping("/orderG")
	public String orderG(){
		return "home/orderG";
	}
	/**
	 * 合同执行情况
	 * @return
	 */
	@RequestMapping(value="/contractH", method = RequestMethod.GET)
	public String contractH(){
		System.out.println("in===");
		return "home/contractH";
	}
	@RequestMapping(value="/contractH", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ContractDetail> contractH(PageRequest pageable,@CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#contract.hospitalCode_S_EQ", user.getOrganization().getOrgCode());
		query.put("t#contract.status_S_EQ", com.shyl.msc.b2b.plan.entity.Contract.Status.signed);
		Sort sort = new Sort(Direction.DESC,"contract.code");
		pageable.setSort(sort);
		DataGrid<ContractDetail> page = contractDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 合同执行情况
	 * @return
	 */
	@RequestMapping(value="/contractM", method = RequestMethod.GET)
	public String contractM(){
		return "home/contractM";
	}
	@RequestMapping(value="/contractM", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<ContractDetail> contractM(PageRequest pageable, @CurrentUser User user){
		Map<String, Object> query = pageable.getQuery();
		query.put("t#contract.status_S_EQ", Contract.Status.signed);
		Sort sort = new Sort(Direction.DESC,"contract.code");
		pageable.setSort(sort);
		DataGrid<ContractDetail> page = contractDetailService.query(user.getProjectCode(), pageable);
		return page;
	}
	
	/**
	 * 图表 医院
	 * @return
	 */
	@RequestMapping("/chart")
	public String chart(Model model,@CurrentUser User user){
		return "home/chart";
	}
	
	/**
	 * 图表 gpo
	 * @return
	 */
	@RequestMapping("/chartG")
	public String chartG(Model model,@CurrentUser User user){
		return "home/chartG";
	}
	
	/**
	 * 监管者 基本信息统计
	 * @return
	 */
	@RequestMapping("/monitorTotal")
	public String monitorTotal(Model model,@CurrentUser User user){
		Map<String , Object> productMap = productService.count(user.getProjectCode());
		Map<String, Object> hospitalMap = hospitalService.count(user.getProjectCode());
		Map<String, Object> producerMap = companyService.count(user.getProjectCode(), "isProducer=1");
		Map<String, Object> vendorMap = companyService.count(user.getProjectCode(), "isVendor=1");
		
		model.addAttribute("productCount", productMap.get("COUNT"));
		model.addAttribute("hospitalCount", hospitalMap.get("COUNT"));
		model.addAttribute("producerCount", producerMap.get("COUNT"));
		model.addAttribute("vendorCount", vendorMap.get("COUNT"));
		return "home/monitorTotal";
	}
	/**
	 * 收货
	 * @return
	 */
	@RequestMapping(value="/abnormalOrder", method = RequestMethod.GET)
	public String abnormalOrder(){
		return "home/abnormalOrder";
	}
	
	/**
	 * 收货
	 * @return
	 */
	@RequestMapping("/abnormalOrder")
	@ResponseBody
	public DataGrid<PurchaseOrder> abnormalOrder(PageRequest pageable, @CurrentUser User user){
		return purchaseOrderService.query(user.getProjectCode(),pageable);
	}
	
	
	
	
	
	/**
	 * 交易情况图表
	 * @return
	 */
	@RequestMapping("/tradeChart")
	public String tradeChart(Model model,@CurrentUser User user){
		Calendar cal=Calendar.getInstance(); 
		int year = cal.get(Calendar.YEAR);
		model.addAttribute("year", year);
		return "home/tradeChart";
	}
	
	
	
	
	/**
	 * 订单追踪 未结案状态 医院
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/statusHpage")
	@ResponseBody
	public DataGrid<PurchaseOrder> statusHpage(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrder> page = new DataGrid<PurchaseOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 1 || user.getOrganization().getOrgId() == null){
			return page;
		}
		Map<String, Object> query = new HashMap<String, Object>();
		Hospital hospital = hospitalService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		query.put("t#hospitalCode_S_EQ", hospital.getCode());
		query.put("t#status_I_NE", Status.forceClosed);
		query.put("t#status_L_NE", Status.sent);
		pageable.setQuery(query);
		Sort sort = new Sort(Direction.DESC,"code");
		pageable.setSort(sort);
		page = purchaseOrderService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * 分页查询 未结案状态 
	 * @param pageable
	 * @return
	 */
	@RequestMapping("/statusGpage")
	@ResponseBody
	public DataGrid<PurchaseOrder> statusGpage(PageRequest pageable, @CurrentUser User user){
		DataGrid<PurchaseOrder> page = new DataGrid<PurchaseOrder>();
		if(user.getOrganization().getOrgType() == null || user.getOrganization().getOrgType() != 2 || user.getOrganization().getOrgId() == null){
			return page;
		}
		Company company = companyService.getById(user.getProjectCode(), user.getOrganization().getOrgId());
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("t#vendorCode_S_EQ", company.getCode());
		query.put("t#status_I_NE", Status.forceClosed);
		query.put("t#status_L_NE", Status.sent);
		pageable.setQuery(query);
		page =  purchaseOrderService.query(user.getProjectCode(), pageable);

		return page;
	}
	
	/**
	 * index
	 * @return
	 */
	@RequestMapping("/index")
	public String index(@CurrentUser User user,Model model){
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(user.getProjectCode(),"publicUser", "B2B_HOMETITLE");
		if(attributeItem != null){
			model.addAttribute("hometitle", attributeItem.getField3());
		}
		AttributeItem logo = attributeItemService.queryByAttrAndItemNo("", "LOGININFO", "LOGO");
		if(logo != null){
			model.addAttribute("logo", logo.getField3());
		}
		return "index";
	}
	/**
	 * center
	 * @return
	 */
	@RequestMapping("/center")
	public String center(@CurrentUser User user,Model model){
		return "center";
	}
}
