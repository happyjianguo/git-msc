package com.shyl.msc.b2b.order.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderService;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.sys.entity.Msg;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.service.IMsgService;
import com.shyl.sys.service.IOrganizationService;

/**
 * 补货订单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class PurchaseOrderService extends BaseService<PurchaseOrder, Long> implements IPurchaseOrderService {
	private IPurchaseOrderDao purchaseOrderDao;

	public IPurchaseOrderDao getPurchaseOrderDao() {
		return purchaseOrderDao;
	}

	@Resource
	public void setPurchaseOrderDao(IPurchaseOrderDao purchaseOrderDao) {
		this.purchaseOrderDao = purchaseOrderDao;
		super.setBaseDao(purchaseOrderDao);
	}

	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private ICartService cartService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;	
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private ISnDao sndao;
	@Resource
	private IMsgService msgService;
	@Resource
	private IOrganizationService organizationService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource
	private IAttributeItemService attributeItemService;
	
	@Override
	@Transactional
	public void commit(String projectCode, Long id)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		PurchaseOrder purchaseOrder = purchaseOrderDao.getById(id);
		purchaseOrder.setStatus(Status.effect);
		super.updateWithInclude(projectCode, purchaseOrder, "status");
	}

	@Override
	@Transactional(readOnly = true)
	public List<PurchaseOrder> listByDate(String projectCode, String vendorCode, String startDate, String endDate) {
		return purchaseOrderDao.listByDate(vendorCode, startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public PurchaseOrder findByCode(String projectCode, String ddbh) {
		return purchaseOrderDao.findByCode(ddbh);
	}

	@Override
	@Transactional(readOnly = true)
	public DataGrid<Map<String, Object>> reportByYear(String projectCode, PageRequest pageable, String year) {
		return purchaseOrderDao.reportByYear(pageable, year);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> totalTradeByYear(String projectCode, String year) {
		return purchaseOrderDao.totalTradeByYear(year);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> totalTradeByMonth(String projectCode, String month) {
		return purchaseOrderDao.totalTradeByMonth(month);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> reportByYear(String projectCode, String year) {
		return purchaseOrderDao.reportByYear(year);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> reportVendorTrade(String projectCode, String vendorCode, String year) {
		return purchaseOrderDao.reportVendorTrade(vendorCode, year);
	}

	@Override
	@Transactional
	public void savePurchaseOrders(String projectCode, List<PurchaseOrder> purchaseOrders) {
		for (PurchaseOrder purchaseOrder : purchaseOrders) {
			purchaseOrderDao.save(purchaseOrder);

			int i = 0;
			for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrder.getPurchaseOrderDetails()) {
				i++;
				purchaseOrderDetail.setCode(purchaseOrder.getCode() + "-" + NumberUtil.addZeroForNum(i + "", 4));// 配送单明细编号
				purchaseOrderDetail.setPurchaseOrder(purchaseOrder);
				purchaseOrderDetailDao.save(purchaseOrderDetail);
				//处方外配情况
				AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
				if(attributeItem.getField3().equals("1")){
					orderMsgService.updateOrderMsg(projectCode, purchaseOrderDetail.getPurchaseOrderPlanDetailCode(), purchaseOrderDetail.getCode());
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DataGrid<PurchaseOrder> queryUnClosed(String projectCode, String hospitalCode, PageRequest pageable) {
		return purchaseOrderDao.queryUnClosed(hospitalCode, pageable);
	}	

	@Override
	@Transactional(readOnly = true)
	public PurchaseOrder getByInternalCode(String projectCode, String vendorCode, String internalCode) {
		return purchaseOrderDao.getByInternalCode(vendorCode, internalCode);
	}
	
	@Override
	@Transactional
	public void addOrder(String projectCode, PurchaseOrder purchaseOrder) {
		//新增purchaseOrder
		purchaseOrderDao.saveJDBC(purchaseOrder);
		
		Set<PurchaseOrderDetail> details = purchaseOrder.getPurchaseOrderDetails();
		StringBuffer detailStr = new StringBuffer();
		for(PurchaseOrderDetail detail:details){
			if (detailStr.length() != 0) {
				detailStr.append(",");
			}
			detailStr.append("'").append(detail.getContractDetailCode()).append("'");
		}

		List<ContractDetail> cds = contractDetailDao.listByCodes(purchaseOrder.getHospitalCode(), detailStr.toString());

		Map<String, ContractDetail> cdMap = new HashMap<>();
		for(ContractDetail cd:cds){
			cdMap.put(cd.getCode(), cd);
		}
		
		List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
		List<ContractDetail> contractDetails = new ArrayList<>();
		System.out.println("//更新合同下单量=========");
		for(PurchaseOrderDetail detail:details){
			detail.setPurchaseOrder(purchaseOrder);
			purchaseOrderDetails.add(detail);
			//purchaseOrderDetailDao.save(detail);
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem.getField3().equals("1")){
				orderMsgService.updateOrderMsg(projectCode, detail.getPurchaseOrderPlanDetailCode(), detail.getCode());
			}
			//更新合同下单量
			ContractDetail cd = cdMap.get(detail.getContractDetailCode());
			if(cd != null){
				System.out.println("下单量detail.getGoodsNum()"+detail.getGoodsNum());
				BigDecimal a = cd.getPurchaseNum() == null?new BigDecimal("0"):cd.getPurchaseNum();
				cd.setPurchaseNum(a.add(detail.getGoodsNum()));
				contractDetails.add(cd);
				//contractDetailService.update(cd);
			}else{
				System.out.println("进入else  detail.getContractDetailCode() = null");
			}
		}
		System.out.println("contractDetails .size="+contractDetails.size());
		System.out.println("//更新合同下单量===end======");
		//批量新增purchaseOrderDetail
		purchaseOrderDetailDao.saveBatch(purchaseOrderDetails);
		//批量修改contractDetail
		contractDetailDao.updateBatch(contractDetails);
	}
	
	@Transactional
	public String mkOrder(String projectCode, PurchaseOrderPlan pp,List<PurchaseOrderPlanDetail> ppdlist) {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		String code = sndao.getCode(OrderType.order);
		purchaseOrder.setCode(code);
		purchaseOrder.setInternalCode("");
		purchaseOrder.setOrderDate(new Date());
		purchaseOrder.setGpoCode(pp.getGpoCode());
		purchaseOrder.setGpoName(pp.getGpoName());
		purchaseOrder.setVendorCode(pp.getVendorCode());
		purchaseOrder.setVendorName(pp.getVendorName());
		purchaseOrder.setHospitalCode(pp.getHospitalCode());
		purchaseOrder.setHospitalName(pp.getHospitalName());
		purchaseOrder.setWarehouseCode(pp.getWarehouseCode());
		purchaseOrder.setWarehouseName(pp.getWarehouseName());
		purchaseOrder.setIsPass(0);
		purchaseOrder.setPurchaseOrderPlanCode(pp.getCode());
		purchaseOrder.setPurchasePlanCode(pp.getPurchasePlanCode());
		purchaseOrder.setRequireDate(pp.getRequireDate());
		purchaseOrder.setUrgencyLevel(pp.getUrgencyLevel());
		purchaseOrder.setIsManyDelivery(pp.getIsManyDelivery());
		purchaseOrder.setIsAuto(1);
		purchaseOrder.setStatus(Status.effect);


		//数量
		BigDecimal num = new BigDecimal(0);
		//金额
		BigDecimal sum = new BigDecimal(0);
		
		int i=0;
		for (PurchaseOrderPlanDetail ppd : ppdlist) {
			i++;
			PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
			String code_detail = purchaseOrder.getCode() + "-" + NumberUtil.addZeroForNum(i + "", 4);
			purchaseOrderDetail.setCode(code_detail);
			purchaseOrderDetail.setInternalCode("");
			purchaseOrderDetail.setOrderDate(purchaseOrder.getOrderDate());
			purchaseOrderDetail.setGoodsNum(ppd.getGoodsNum());
			purchaseOrderDetail.setGoodsSum(ppd.getGoodsSum());
			purchaseOrderDetail.setIsPass(0);
			purchaseOrderDetail.setNotes(ppd.getNotes());
			purchaseOrderDetail.setPurchaseOrderPlanDetailCode(ppd.getCode());
			purchaseOrderDetail.setProductCode(ppd.getProductCode());
			purchaseOrderDetail.setProductName(ppd.getProductName());
			purchaseOrderDetail.setProducerName(ppd.getProducerName());
			purchaseOrderDetail.setDosageFormName(ppd.getDosageFormName());
			purchaseOrderDetail.setModel(ppd.getModel());
			purchaseOrderDetail.setPackDesc(ppd.getPackDesc());
			purchaseOrderDetail.setUnit(ppd.getUnit());
			purchaseOrderDetail.setPrice(ppd.getPrice());
			purchaseOrderDetail.setContractDetailCode(ppd.getContractDetailCode());
			
			purchaseOrder.getPurchaseOrderDetails().add(purchaseOrderDetail);
			num = num.add(purchaseOrderDetail.getGoodsNum());
			sum = sum.add(purchaseOrderDetail.getGoodsSum());
		}
		
		purchaseOrder.setNum(num);
		purchaseOrder.setSum(sum);
		purchaseOrder.setDatagramId(null);
		
		this.addOrder(projectCode, purchaseOrder);
		System.out.println("订单编号"+purchaseOrder.getCode());
		return purchaseOrder.getCode();
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getGpoCentralizedByHospital(String projectCode, String year, String month, int maxsize) {
		return purchaseOrderDao.getGpoCentralizedByHospital(year, month, maxsize);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getGpoSumByHospital(String projectCode, String year, String month, int maxsize) {
		return purchaseOrderDao.getGpoSumByHospital(year, month, maxsize);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getGpoCentralizedByRegion(String projectCode, String year, String month, String parentCode, int maxsize) {
		return purchaseOrderDao.getGpoCentralizedByRegion(year, month, parentCode, maxsize);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Map<String, Object>> getGpoSumByRegion(String projectCode, String year, String month, String treepath, int maxsize) {
		return purchaseOrderDao.getGpoSumByRegion(year, month ,treepath, maxsize);
	}

	@Override
	@Transactional
	public String checkPlan(String projectCode, Long id) {
		//计划主档
		PurchaseOrderPlan pp = purchaseOrderPlanService.getById(projectCode, id);
		pp.setStatus(PurchaseOrderPlan.Status.cancel);
		//是否有一种药品无法供应
		int flag = 0;
		//计划明细
		List<PurchaseOrderPlanDetail> ppdlist = purchaseOrderPlanDetailDao.listByOrderPlanId(pp.getId());
		List<PurchaseOrderPlanDetail> ppdlist1 = new ArrayList<PurchaseOrderPlanDetail>();
		for (PurchaseOrderPlanDetail ppd : ppdlist) {
			PurchaseOrderPlanDetail.Status status = ppd.getStatus();
			OrderDetailStatus orderDetailStatus = null;
			if(status == null ){
				orderDetailStatus = OrderDetailStatus.agree;
				pp.setStatus(PurchaseOrderPlan.Status.effect);//计划主档状态
				ppd.setStatus(PurchaseOrderPlanDetail.Status.normal);
				purchaseOrderPlanDetailDao.update(ppd);
				ppdlist1.add(ppd);
			}else if(status.equals(PurchaseOrderPlanDetail.Status.normal)){
				orderDetailStatus = OrderDetailStatus.agree;
				pp.setStatus(PurchaseOrderPlan.Status.effect);//计划主档状态
				ppdlist1.add(ppd);
			}else{
				orderDetailStatus = OrderDetailStatus.disagree;
				flag = 1;
				//更新合同计划量
				ContractDetail cd = contractDetailDao.findByCode(ppd.getContractDetailCode());
				if(cd != null){
					BigDecimal a = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
					cd.setPurchasePlanNum(a.subtract(ppd.getGoodsNum()));
					contractDetailDao.update(cd);
				}
				//处方外配情况
				AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
				if(attributeItem.getField3().equals("1")){
					orderMsgService.saveOrderMsg(projectCode, ppd.getPurchasePlanDetailCode(), ppd.getCode(), orderDetailStatus);
				}
			}
		}
		pp.setAuditDate(new Date());
		pp = purchaseOrderPlanService.update(projectCode, pp);
		if(flag == 1){
			//发系统消息
			this.sendMsg(projectCode, pp);
		}
		if(pp.getStatus().equals(PurchaseOrderPlan.Status.cancel)){
			return "cancel";
		}
		
		
		JSONArray jsonArray = new JSONArray();
		String ddbh = this.mkOrder(projectCode, pp,ppdlist1);
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", ddbh);
		jsonArray.add(jo);
		System.out.println("ddbh="+jsonArray);
		return JSON.toJSONString(jsonArray);
	}

	private void sendMsg(String projectCode, PurchaseOrderPlan pp) {
		System.out.println("发送消息"+pp.getCode());
		Msg msg = new Msg();
		msg.setIds("00");
		msg.setCaty("0");
		if(pp.getStatus().equals(PurchaseOrderPlan.Status.cancel)){
			msg.setTitle("订单计划被取消。-- "+pp.getCode());
		}else{
			msg.setTitle("订单计划中部分药品无法供应。-- "+pp.getCode());
		}
		
		msg.setAttach("/b2b/monitor/purchaseOrderPlan.htmlx?code="+pp.getCode());
		Hospital hospital = hospitalService.findByCode(projectCode, pp.getHospitalCode());
		Company company = companyService.findByCode(projectCode,pp.getVendorCode(), "isVendor=1");
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
			System.out.println("发送完成"+pp.getCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public DataGrid<PurchaseOrder> queryByStatus(String projectCode, PageRequest pageable) {
		return purchaseOrderDao.queryByStatus(pageable);
	}

	@Override
	public PurchaseOrder getByPlanCode(String projectCode, String planCode) {
		return purchaseOrderDao.getByPlanCode(planCode);
	}

	@Override
	public JSONArray addOrder(String projectCode, PurchaseOrder purchaseOrder, List<JSONObject> list) {
		int i = 0;
		BigDecimal num = new BigDecimal(0);
		BigDecimal sum = new BigDecimal(0);
		
		JSONArray res_arr = new JSONArray();
		for(JSONObject jo:list){
			i++;
			int sxh = jo.getIntValue("sxh");
			String yqddmxbh = jo.getString("yqddmxbh");		//药企订单明细编号
			String ddjhmxbh = jo.getString("ddjhmxbh");		//订单计划明细编号
			BigDecimal cgsl = jo.getBigDecimal("cgsl");			//采购数量
			BigDecimal cgdj = jo.getBigDecimal("cgdj");		//采购单价
			String bzsm = jo.getString("bzsm");				//备注说明
			PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailDao.findByCode(ddjhmxbh);
			
			PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
			String code_detail = purchaseOrder.getCode() + "-" + NumberUtil.addZeroForNum(i + "", 4);
			purchaseOrderDetail.setCode(code_detail);
			purchaseOrderDetail.setInternalCode(yqddmxbh);
			purchaseOrderDetail.setOrderDate(purchaseOrder.getOrderDate());
			purchaseOrderDetail.setProductCode(purchaseOrderPlanDetail.getProductCode());
			purchaseOrderDetail.setProductName(purchaseOrderPlanDetail.getProductName());
			purchaseOrderDetail.setProducerName(purchaseOrderPlanDetail.getProducerName());
			purchaseOrderDetail.setDosageFormName(purchaseOrderPlanDetail.getDosageFormName());
			purchaseOrderDetail.setModel(purchaseOrderPlanDetail.getModel());
			purchaseOrderDetail.setPackDesc(purchaseOrderPlanDetail.getPackDesc());
			purchaseOrderDetail.setUnit(purchaseOrderPlanDetail.getUnit());
			purchaseOrderDetail.setPrice(cgdj);
			purchaseOrderDetail.setGoodsNum(cgsl);
			purchaseOrderDetail.setGoodsSum(cgdj.multiply(cgsl));
			purchaseOrderDetail.setIsPass(0);
			purchaseOrderDetail.setNotes(bzsm);
			purchaseOrderDetail.setPurchaseOrderPlanDetailCode(ddjhmxbh);
			purchaseOrderDetail.setContractDetailCode(purchaseOrderPlanDetail.getContractDetailCode());
			purchaseOrder.getPurchaseOrderDetails().add(purchaseOrderDetail);
			
			num = num.add(purchaseOrderDetail.getGoodsNum());
			sum = sum.add(purchaseOrderDetail.getGoodsSum());
			
			JSONObject res_arr_jo = new JSONObject();
			res_arr_jo.put("sxh", sxh);
			res_arr_jo.put("ddmxbh", code_detail);
			res_arr_jo.put("yqddmxbh", yqddmxbh);
			res_arr.add(res_arr_jo);
		}
		purchaseOrder.setNum(num);
		purchaseOrder.setSum(sum);
		this.addOrder(projectCode, purchaseOrder);
		return res_arr;
	}

	
	@Override
	public DataGrid<PurchaseOrder> listBypurchaseOrderAndDetail(PageRequest pageable) {
		return purchaseOrderDao.listBypurchaseOrderAndDetail(pageable);
	}
	
	@Override
	public List<PurchaseOrder> listByIsPass(String projectCode, int isPass){
		return purchaseOrderDao.listByIsPass(isPass);
	}
	
	@Override
	public List<PurchaseOrder> listByIsPassDelivery(String projectCode, int isPassDelivery){
		return purchaseOrderDao.listByIsPassDelivery(isPassDelivery);
	}

	@Override
	public DataGrid<PurchaseOrder> listByPurchaseOrderAndDetailStatus(String projectCode, PageRequest pageable) {
		return purchaseOrderDao.listByPurchaseOrderAndDetailStatus(pageable);
	}

	@Override
	public Map<String, Object> orderSumByHospitalAndYear(String projectCode, String hospitalCode, String year) {
		return purchaseOrderDao.orderSumByHospitalAndYear(hospitalCode,year);
	}

	@Override
	public Map<String, Object> orderSumByGpoAndYear(String projectCode, String VendorCode, String year) {
		return purchaseOrderDao.orderSumByGpoAndYear(VendorCode,year);
	}

	@Override
	public List<Map<String, Object>> listByNODelivery(PageRequest page) {
		return purchaseOrderDao.listByNODelivery(page);
	}

	
}
