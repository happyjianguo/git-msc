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
import com.shyl.common.util.DateUtil;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.msc.b2b.order.service.ISnService;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.b2b.plan.service.IContractDetailService;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsPriceService;
import com.shyl.msc.dm.service.IGoodsService;
import com.shyl.msc.dm.service.IProductService;
import com.shyl.msc.enmu.CreateMethod;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.entity.Warehouse;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.msc.set.service.IHospitalService;
import com.shyl.msc.set.service.IWarehouseService;
import com.shyl.sys.entity.User;

/**
 * 订单计划Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class PurchaseOrderPlanService extends BaseService<PurchaseOrderPlan, Long> implements IPurchaseOrderPlanService {
	@Resource
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;
	@Resource
	private ICompanyService companyService;
	@Resource
	private ISnService snService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IGoodsService goodsService;
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private IProductService productService;
	@Resource
	private ICartService cartService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IPurchasePlanService purchasePlanService;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IContractDetailService contractDetailService;
	@Resource
	private IAttributeItemService attributeItemService;
	
	private IPurchaseOrderPlanDao purchaseOrderPlanDao;
	
	public IPurchaseOrderPlanDao getPurchaseOrderPlanDao() {
		return purchaseOrderPlanDao;
	}

	@Resource
	public void setPurchaseOrderPlanDao(IPurchaseOrderPlanDao purchaseOrderPlanDao) {
		this.purchaseOrderPlanDao = purchaseOrderPlanDao;
		super.setBaseDao(purchaseOrderPlanDao);
	}


	@Override
	@Transactional(readOnly = true)
	public List<PurchaseOrderPlan> listByDate(String projectCode, String companyCode, String startDate,
			String endDate , boolean isGPO) {
		return purchaseOrderPlanDao.listByDate(companyCode, startDate, endDate, isGPO);
	}

	@Override
	@Transactional(readOnly = true)
	public PurchaseOrderPlan findByCode(String projectCode, String code) {
		return purchaseOrderPlanDao.findByCode(code);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PurchaseOrderPlan getByInternalCode(String projectCode, String hospitalCode, String internalCode) {
		return purchaseOrderPlanDao.getByInternalCode(hospitalCode, internalCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long getCountByInternalCode(String projectCode, String hospitalCode, String internalCode) {
		return purchaseOrderPlanDao.getCountByInternalCode(hospitalCode, internalCode);
	}

	@Override
	@Transactional
	public String mkAutoOrder(String projectCode, PurchaseOrderPlan purchaseOrderPlan, Long[] goodspriceids, BigDecimal[] nums, User user) {
		Hospital hospital = hospitalService.findByCode(projectCode, purchaseOrderPlan.getHospitalCode());
		if (hospital != null) {
			purchaseOrderPlan.setHospitalName(hospital.getFullName());
		}
		if(purchaseOrderPlan.getWarehouseCode() != null){
			Warehouse wh = warehouseService.findByCode(projectCode, purchaseOrderPlan.getWarehouseCode());
			purchaseOrderPlan.setWarehouseName(wh.getName());
		}
		Company vendor = companyService.getById(projectCode, user.getOrganization().getOrgId());
		if(vendor != null){
			purchaseOrderPlan.setVendorCode(vendor.getCode());
			purchaseOrderPlan.setVendorName(vendor.getFullName());
		}
		//purchaseOrderPlan.setGpoId(gpoId);  供应商补货订单 只针对非gpo药品
		//purchaseOrderPlan.setGpoName(gpoName); 供应商补货订单 只针对非gpo药品
		String code = snService.getCode(projectCode, OrderType.orderPlan);
		purchaseOrderPlan.setCode(code);
		purchaseOrderPlan.setIsManyDelivery(0);
		purchaseOrderPlan.setIsAuto(0);
		purchaseOrderPlan.setIsPass(0);
		purchaseOrderPlan.setStatus(Status.uneffect);
		purchaseOrderPlan.setOrderDate(new Date());
		purchaseOrderPlan.setCreateMethod(CreateMethod.restock);
	
		purchaseOrderPlan = purchaseOrderPlanDao.save(purchaseOrderPlan);
		
		BigDecimal num = new BigDecimal(0);
		BigDecimal sum = new BigDecimal("0");
		for (int i = 0; i < goodspriceids.length; i++) {
			GoodsPrice goodsPrice = goodsPriceService.getById(projectCode, goodspriceids[i]);
			Product product = productService.getByCode(projectCode, goodsPrice.getProductCode());
			PurchaseOrderPlanDetail purchaseOrderPlanDetail = new PurchaseOrderPlanDetail();
			String code_detail = purchaseOrderPlan.getCode() + "-" + NumberUtil.addZeroForNum((i+1) + "", 4);
			purchaseOrderPlanDetail.setCode(code_detail);
			purchaseOrderPlanDetail.setOrderDate(purchaseOrderPlan.getOrderDate());
			purchaseOrderPlanDetail.setGoodsNum(nums[i]);
			purchaseOrderPlanDetail.setGoodsSum(goodsPrice.getFinalPrice().multiply(nums[i]));
			purchaseOrderPlanDetail.setIsPass(0);
			purchaseOrderPlanDetail.setPurchaseOrderPlan(purchaseOrderPlan);
			purchaseOrderPlanDetail.setProductCode(product.getCode());
			purchaseOrderPlanDetail.setProductName(product.getName());
			purchaseOrderPlanDetail.setProducerName(product.getProducerName());
			purchaseOrderPlanDetail.setDosageFormName(product.getDosageFormName());
			purchaseOrderPlanDetail.setModel(product.getModel());
			purchaseOrderPlanDetail.setPackDesc(product.getPackDesc());
			purchaseOrderPlanDetail.setUnit(product.getUnitName());
			purchaseOrderPlanDetail.setPrice(goodsPrice.getFinalPrice());
			purchaseOrderPlanDetail.setNotes("补货计划下单");
			
			purchaseOrderPlanDetailDao.save(purchaseOrderPlanDetail);
			
			num = num.add(purchaseOrderPlanDetail.getGoodsNum());
			sum = sum.add(purchaseOrderPlanDetail.getGoodsSum());
			i++;
		}
		purchaseOrderPlan.setNum(num);
		purchaseOrderPlan.setSum(sum);
		purchaseOrderPlanDao.update(purchaseOrderPlan);
		
		
		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", purchaseOrderPlan.getCode());
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(jo);
		
		return JSON.toJSONString(jsonArray);
	}


	@Override
	@Transactional
	public List<Map<String, Object>> queryGpoTimelyRankList(String projectCode, String year, String month) {
		return purchaseOrderPlanDao.queryGpoTimelyRankList(year, month);
	}

	@Override
	@Transactional
	public Long getDeliveryTimely(String projectCode, String year, String month, String vendorCode, Integer delayStart, Integer delayEnd) {
		return purchaseOrderPlanDao.getDeliveryTimely(year, month, vendorCode, delayStart, delayEnd);
	}

	@Override
	public List<PurchaseOrderPlan> listByPatientId(String projectCode, Long pId) {
		return purchaseOrderPlanDao.listByPatientId(pId);
	}

	@Override
	@Transactional
	public String savePurchaseOrderPlan(String projectCode, PurchasePlan purchasePlan) {
		Set<PurchasePlanDetail> details = purchasePlan.getPurchasePlanDetails();
		Map<String, Object> orderMap = new HashMap<String, Object>();
		Map<String, Object> goodsMap = new HashMap<String, Object>();
		Map<String, PurchaseOrderPlan> orderPlanMap = new HashMap<String, PurchaseOrderPlan>();
		JSONArray plan_arr = new JSONArray();
		int k=0;
		for(PurchasePlanDetail detail:details){
			k++;
			String vendorCode = detail.getVendorCode()+""; 
			String gpoCode = detail.getGpoCode()+"";
			BigDecimal goodsNum = detail.getGoodsNum();
			BigDecimal goodsSum = detail.getGoodsSum();
			//鎷嗗崟瑙勫垯  key = vendor+gpoCode
			String key = vendorCode+"_"+gpoCode;
			orderMap.put(key, "");
			if (goodsMap.get(key + "_goodsNum") != null)
				goodsNum = goodsNum.add(new BigDecimal(goodsMap.get(key + "_goodsNum").toString()));
			goodsMap.put(key + "_goodsNum", goodsNum);

			if (goodsMap.get(key + "_goodsSum") != null)
				goodsSum = ((BigDecimal)goodsMap.get(key + "_goodsSum")).add(goodsSum);
			goodsMap.put(key + "_goodsSum", goodsSum);
			JSONObject jo = new JSONObject();
			jo.put("sxh", k);
			jo.put("ddjhmxbh", detail.getCode());
			jo.put("cgjhdmxbh", detail.getInternalCode());
			plan_arr.add(jo);
		}
		JSONArray jsonArray = new JSONArray();
		List<PurchaseOrderPlan> purchaseOrderPlans = new ArrayList<>();
		for (String key : orderMap.keySet()) {
			String[] keyArr = key.split("_");
			String vendorCode = keyArr[0];
			String gpoCode = keyArr[1];
			Company vendor = null;
			Company gpo = null;
			if(!vendorCode.equals("null")){
				vendor = companyService.findByCode(projectCode,vendorCode, "isVendor=1");
			}
			if(!gpoCode.equals("null")){
				gpo = companyService.findByCode(projectCode,gpoCode, "isGPO=1");
			}
			PurchaseOrderPlan plan = new PurchaseOrderPlan();
			String code = snService.getCode(projectCode, OrderType.orderPlan);
			plan.setCode(code); // 订单编号
			plan.setInternalCode(purchasePlan.getInternalCode());
			plan.setOrderDate(purchasePlan.getOrderDate());
			if(vendor != null){
				plan.setVendorCode(vendor.getCode()); // 供应商id
				plan.setVendorName(vendor.getFullName()); // 供应商名称
			}
			if(gpo != null){
				plan.setGpoCode(gpo.getCode());//gpoId
				plan.setGpoName(gpo.getFullName());//gpoName
			}
			plan.setHospitalCode(purchasePlan.getHospitalCode()); // 医疗机构id
			plan.setHospitalName(purchasePlan.getHospitalName()); // 医疗机构名称
			plan.setWarehouseCode(purchasePlan.getWarehouseCode()); // 库房id
			plan.setWarehouseName(purchasePlan.getWarehouseName()); // 库房名称
			plan.setNum(new BigDecimal(goodsMap.get(key + "_goodsNum").toString()));//数量
			plan.setSum((BigDecimal) goodsMap.get(key + "_goodsSum")); // 金额
			plan.setIsPass(purchasePlan.getIsPass());
			plan.setRequireDate(purchasePlan.getRequireDate()); // 要求供货日期
			plan.setUrgencyLevel(purchasePlan.getUrgencyLevel());
			plan.setIsAuto(purchasePlan.getIsAuto());// 产生方式
			plan.setOrderType(purchasePlan.getOrderType()); // 订单类型
			plan.setStatus(Status.uneffect);// 状态
			plan.setIsManyDelivery(purchasePlan.getIsManyDelivery());//是否多次配送
			plan.setDatagramId(purchasePlan.getDatagramId());
			
			plan.setPurchasePlanCode(purchasePlan.getCode());
			
			plan = purchaseOrderPlanDao.saveJDBC(plan);
			purchaseOrderPlans.add(plan);
			
			JSONObject jo = new JSONObject();
			jo.put("ddjhbh", plan.getCode());
			jsonArray.add(jo);
			orderPlanMap.put(key, plan);
		}

		JSONObject jo = new JSONObject();
		jo.put("ddjhbh", purchasePlan.getCode());
		jo.put("jls", plan_arr.size());
		jo.put("arr", jsonArray);
		jo.put("mx", plan_arr);
		Map<String, Integer> numMap = new HashMap<String, Integer>();
		List<PurchaseOrderPlanDetail> purchasePlanDetails = new ArrayList<>();
		for(PurchasePlanDetail detail:details){
			String vendorCode = detail.getVendorCode()+"";
			String gpoCode = detail.getGpoCode()+"";
			String key = vendorCode+"_"+gpoCode;
			
			PurchaseOrderPlan plan = orderPlanMap.get(key);
			PurchaseOrderPlanDetail purchaseOrderPlanDetail = new PurchaseOrderPlanDetail();
			if (numMap.get(key) != null) {
				Integer i = numMap.get(key);
				numMap.put(key, i + 1);
			} else {
				numMap.put(key, 1);
			}
			String code_detail = plan.getCode() + "-" + NumberUtil.addZeroForNum(numMap.get(key) + "", 4);
			purchaseOrderPlanDetail.setCode(code_detail);
			purchaseOrderPlanDetail.setInternalCode(detail.getInternalCode());
			purchaseOrderPlanDetail.setOrderDate(detail.getOrderDate());
			purchaseOrderPlanDetail.setGoodsNum(detail.getGoodsNum());
			purchaseOrderPlanDetail.setGoodsSum(detail.getGoodsSum());
			purchaseOrderPlanDetail.setIsPass(detail.getIsPass());
			purchaseOrderPlanDetail.setPurchaseOrderPlan(plan);
			purchaseOrderPlanDetail.setNotes(detail.getNotes());
			purchaseOrderPlanDetail.setPurchasePlanDetailCode(detail.getCode());
			
			purchaseOrderPlanDetail.setProductCode(detail.getProductCode());
			purchaseOrderPlanDetail.setProductName(detail.getProductName());
			purchaseOrderPlanDetail.setProducerName(detail.getProducerName());
			purchaseOrderPlanDetail.setDosageFormName(detail.getDosageFormName());
			purchaseOrderPlanDetail.setModel(detail.getModel());
			purchaseOrderPlanDetail.setPackDesc(detail.getPackDesc());
			purchaseOrderPlanDetail.setUnit(detail.getUnit());
			purchaseOrderPlanDetail.setPrice(detail.getPrice());
			purchaseOrderPlanDetail.setContractDetailCode(detail.getContractDetailCode());
			//purchaseOrderPlanDetailDao.save(purchaseOrderPlanDetail);
			purchaseOrderPlanDetail.setStatus(PurchaseOrderPlanDetail.Status.normal);
			purchasePlanDetails.add(purchaseOrderPlanDetail);
		}
		purchaseOrderPlanDetailDao.saveBatch(purchasePlanDetails);
		return JSON.toJSONString(jo);
	}

	@Override
	@Transactional
	public void fedback(String projectCode, JSONObject jObject) {
		int zt = jObject.getIntValue("zt");
		String mx = jObject.getString("mx");//明细
		if(mx.startsWith("{")){
			mx = "[" + mx + "]";
		}
		String ddjhbh = jObject.getString("ddjhbh");	//订单计划编号
		PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanDao.findByCode(ddjhbh);
		List<JSONObject> list = JSON.parseArray(mx, JSONObject.class);
		if(zt == 1){
			purchaseOrderPlan.setStatus(Status.cancel);//取消
		}else if(zt == 0 || zt == 2){
			purchaseOrderPlan.setStatus(Status.effect);//已生效
		}
		purchaseOrderPlan.setAuditDate(new Date());
		purchaseOrderPlanDao.update(purchaseOrderPlan);
		
		for(JSONObject jo:list){
			String ddjhmxbh = jo.getString("ddjhmxbh");
			int cljg = jo.getIntValue("cljg");
			String bz = jo.getString("bz");
			PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailDao.findByCode(ddjhmxbh);
			OrderDetailStatus orderDetailStatus = null;
			if(cljg == 1){
				purchaseOrderPlanDetail.setStatus(PurchaseOrderPlanDetail.Status.cancel);
				purchaseOrderPlanDetail.setNotes(bz);
				purchaseOrderPlanDetailDao.update(purchaseOrderPlanDetail);
				orderDetailStatus = OrderDetailStatus.disagree;

				//更新合同计划量
				ContractDetail cd = contractDetailService.findByCode(projectCode, purchaseOrderPlanDetail.getContractDetailCode());
				if(cd != null){
					BigDecimal a = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
					BigDecimal b = cd.getPurchasePlanAmt()==null?new BigDecimal("0"):cd.getPurchasePlanAmt();
					cd.setPurchasePlanNum(a.subtract(purchaseOrderPlanDetail.getGoodsNum()));
					cd.setPurchasePlanAmt(b.subtract(purchaseOrderPlanDetail.getGoodsSum()));
					contractDetailService.update(projectCode, cd);
				}
				
			}else if(cljg == 0){
				purchaseOrderPlanDetail.setStatus(PurchaseOrderPlanDetail.Status.normal);
				purchaseOrderPlanDetailDao.update(purchaseOrderPlanDetail);
				orderDetailStatus = OrderDetailStatus.agree;
			}
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem.getField3().equals("1")){
				orderMsgService.saveOrderMsg(projectCode, purchaseOrderPlanDetail.getPurchasePlanDetailCode(), purchaseOrderPlanDetail.getCode(), orderDetailStatus);
			}
		}
	}

	@Override
	public JSONArray getToGPO(String projectCode, Company company, Boolean isGPO, Boolean isCode, JSONObject jObject) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		String cxkssj = jObject.getString("cxkssj");
		String cxjssj = jObject.getString("cxjssj");
		String ddjhbh = jObject.getString("ddjhbh");
		List<PurchaseOrderPlan> purchaseOrderPlans = new ArrayList<>();
		if(isCode){
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanDao.findByCode(ddjhbh);
			purchaseOrderPlans.add(purchaseOrderPlan);
		}else{
			purchaseOrderPlans = purchaseOrderPlanDao.listByDate(company.getCode(),cxkssj,cxjssj,isGPO);
		}
		JSONArray jsonArray = new JSONArray();
		for(PurchaseOrderPlan po: purchaseOrderPlans){
			
			JSONObject jo = new JSONObject();
			jo.put("yybm", po.getHospitalCode());			//医院编码
			jo.put("psdbm", po.getWarehouseCode());		//配送点编码
			if(po.getGpoCode() != null){
				jo.put("gpobm", po.getGpoCode());			//配送企业编码
			}else{
				jo.put("gpobm", "");					//配送企业编码
			}
			if(po.getVendorCode() != null){
				jo.put("gysbm", po.getVendorCode());
			}else{
				jo.put("gysbm", "");
			}
			jo.put("ddjhbh", po.getCode());				//订单编号
			jo.put("ddsj", DateUtil.dateToStr(po.getOrderDate()));//订单时间
			jo.put("yqphsj", DateUtil.dateToStr(po.getRequireDate()));//要求配货时间
			jo.put("jjcd", po.getUrgencyLevel().ordinal());//紧急程度
			jo.put("dcpsbs", po.getIsManyDelivery());	//多次配送标识

			if(po.getIsRead() == null || po.getIsRead()==0){
				po.setIsRead(1);
				super.updateWithInclude(projectCode, po, "isRead");
			}
			jo.put("dqzt", po.getIsRead());						//读取状态
			List<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = purchaseOrderPlanDetailDao.listByOrderPlanId(po.getId(), PurchaseOrderPlanDetail.Status.normal);

			jo.put("jls", purchaseOrderPlanDetails.size());    	//记录数
			JSONArray jods = new JSONArray();
			int sxh = 1;
			for(PurchaseOrderPlanDetail pod:purchaseOrderPlanDetails){
				JSONObject jod = new JSONObject();
				jod.put("sxh", sxh);					//顺序号
				jod.put("ddjhmxbh", pod.getCode());		//订单计划明细编号
				jod.put("ypbm", pod.getProductCode());	//药品编码
				jod.put("cgsl", pod.getGoodsNum());		//采购数量
				jod.put("cgdj", pod.getPrice());		//采购单价
				jod.put("bzsm", pod.getNotes());		//备注说明
				jod.put("htmxbh", pod.getContractDetailCode());//合同明细编号 
				jod.put("yyjhmxbh", pod.getInternalCode());		//医院计划明细 编号 
				jods.add(jod);
				sxh++;
			}
			jo.put("mx", jods);
			jsonArray.add(jo);
		}
		return jsonArray;
	}

	@Override
	public DataGrid<PurchaseOrderPlan> listBypurchaseOrderPlanAndDetail(String projectCode, PageRequest pageable) {
		return purchaseOrderPlanDao.listBypurchaseOrderPlanAndDetail(pageable);
	}

	@Override
	public List<PurchaseOrderPlan> listByIsPass(String projectCode, int isPass) {
		return purchaseOrderPlanDao.listByIsPass(isPass);
	}
	
	@Override
	public List<PurchaseOrderPlan> listByIsPassAudit(String projectCode, int isPassAudit) {
		return purchaseOrderPlanDao.listByIsPassAudit(isPassAudit);
	}

	@Override
	public List<Map<String, Object>> listByUnEffect(String projectCode, PageRequest page) {
		return purchaseOrderPlanDao.listByUnEffect(page);
	}
}
