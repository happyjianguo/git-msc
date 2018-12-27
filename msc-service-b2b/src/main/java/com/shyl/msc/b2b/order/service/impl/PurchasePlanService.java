package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDao;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.CartContract;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.ICartContractService;
import com.shyl.msc.b2b.order.service.ICartService;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanService;
import com.shyl.msc.b2b.order.service.IPurchasePlanService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.dm.entity.GoodsPrice;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.service.IGoodsPriceService;
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
public class PurchasePlanService extends BaseService<PurchasePlan, Long> implements IPurchasePlanService {
	
	private IPurchasePlanDao purchasePlanDao;

	@Resource
	private ISnDao snDao;
	@Resource
	private ICompanyService companyService;
	@Resource
	private IPurchasePlanDetailDao purchasePlanDetailDao;
	@Resource
	private ICartService cartService;
	@Resource
	private ICartContractService cartContractService;
	@Resource
	private IGoodsPriceService goodsPriceService;
	@Resource
	private IProductService productService;
	@Resource
	private IHospitalService hospitalService;
	@Resource
	private IWarehouseService warehouseService;
	@Resource
	private IPurchaseOrderPlanService purchaseOrderPlanService;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IContractDetailDao contractDetailDao;
	@Resource
	private IAttributeItemService attributeItemService;
	
	
	@Resource
	public void setPurchasePlanDao(IPurchasePlanDao purchasePlanDao) {
		this.purchasePlanDao = purchasePlanDao;
		super.setBaseDao(purchasePlanDao);
	}

	@Override
	@Transactional
	public String mkOrder(String projectCode, PurchasePlan purchasePlan, Long[] cartids, Long[] goodspriceids, BigDecimal[] nums, User user) throws Exception {
		String code = snDao.getCode(OrderType.plan);
		purchasePlan.setCode(code);
		
		for (int i = 0; i < goodspriceids.length; i++) {
			GoodsPrice goodsPrice = goodsPriceService.getById(projectCode, goodspriceids[i]);
			//其他
			createPPDByGP(projectCode, purchasePlan, goodsPrice, nums[i],"","");	
		}

		purchasePlan.setOrderDate(new Date());
		purchasePlan.setIsManyDelivery(0);
		purchasePlan.setIsAuto(0);
		purchasePlan.setCreateMethod(CreateMethod.plan);
		
		Hospital hospital = hospitalService.getById(projectCode, user.getOrganization().getOrgId());
		if (hospital != null) {
			purchasePlan.setHospitalCode(hospital.getCode());
			purchasePlan.setHospitalName(hospital.getFullName());
		}
		
		if(purchasePlan.getWarehouseCode() != null){
			Warehouse wh = warehouseService.findByCode(projectCode, purchasePlan.getWarehouseCode());
			purchasePlan.setWarehouseName(wh.getName());
		}
		
		
		String ddbh = this.savePurchasePlan(projectCode, purchasePlan);
		// 删除购物车
		for (Long cartid : cartids) {
			cartService.delete(projectCode, cartid);
		}
		return ddbh;
	}

	@Override
	public PurchasePlan addGPOPurchase(String projectCode, Map<String, List<ContractDetail>> contractDetailmap, PurchasePlan purchasePlan, Product product, BigDecimal nums, String notes,String internalCode) throws Exception {
		//nums的量 可能需要多个合同明细才够量，按照从先到后顺序使用合同明细,自动拆成多个明细
		BigDecimal lastnums = nums;
		String key = purchasePlan.getHospitalCode()+"_"+purchasePlan.getVendorCode()+"_"+product.getCode();
		List<ContractDetail> cdlist = contractDetailmap.get(key);
		//System.out.println("cdlist=="+cdlist.size());
		if(cdlist != null){
			for (int i = 0; i < cdlist.size(); i++) {
				ContractDetail cd = cdlist.get(i);
				BigDecimal l = cd.getContractNum().subtract(cd.getPurchasePlanNum());
				if(l.compareTo(lastnums)>=0){//足够
					createPPD(purchasePlan,cd,lastnums, notes, internalCode, product);
					return purchasePlan;
				}else{//不足
					lastnums = lastnums.subtract(l);
					createPPD(purchasePlan,cd,l, notes, internalCode, product);
				}
			}
		}
		GoodsPrice gp = goodsPriceService.findByKey(projectCode, product.getCode(), purchasePlan.getVendorCode(), purchasePlan.getHospitalCode(), 0, 0, 0);	
		if(gp == null)
			throw new MyException(product.getName()+"的合同余量不足");
		else{
			createPPDByGP(projectCode, purchasePlan,gp,nums,notes,internalCode);
			return purchasePlan;
		}
	}

	public PurchasePlan addPurchase(String projectCode, Map<String, List<ContractDetail>> contractDetailmap, PurchasePlan purchasePlan, Product product, BigDecimal nums,BigDecimal price, String notes,String internalCode) throws Exception {
		//nums的量 可能需要多个合同明细才够量，按照从先到后顺序使用合同明细,自动拆成多个明细
		BigDecimal lastnums = nums;
		String key = purchasePlan.getHospitalCode()+"_"+purchasePlan.getVendorCode()+"_"+product.getCode();
		List<ContractDetail> cdlist = contractDetailmap.get(key);
		//System.out.println("cdlist=="+cdlist.size());
		if(cdlist != null){
			for (int i = 0; i < cdlist.size(); i++) {
				ContractDetail cd = cdlist.get(i);
				BigDecimal l = cd.getContractNum().subtract(cd.getPurchasePlanNum());
				if(l.compareTo(lastnums)>=0){//足够
					createPPD(purchasePlan,cd,lastnums, notes, internalCode, product);
					return purchasePlan;
				}else{//不足
					lastnums = lastnums.subtract(l);
					createPPD(purchasePlan,cd,l, notes, internalCode, product);
				}
			}
		}
		/*GoodsPrice gp = goodsPriceService.findByKey(projectCode, product.getCode(), purchasePlan.getVendorCode(), purchasePlan.getHospitalCode(), 0, 0, 0);
		if(gp == null)
			throw new MyException(product.getName()+"的合同余量不足");
		else{
			createPPDByGP(projectCode, purchasePlan,gp,nums,notes,internalCode);
			return purchasePlan;
		}*/
		createPPDetail(projectCode, purchasePlan,product,nums,price,notes,internalCode);
		return purchasePlan;
	}


	private void createPPDByGP(String projectCode, PurchasePlan purchasePlan,  GoodsPrice gp, BigDecimal nums, String notes, String internalCode) {
		int dcount = purchasePlan.getPurchasePlanDetails().size()+1;
		Product product = productService.getByCode(projectCode, gp.getProductCode());
		String code_detail = purchasePlan.getCode() + "-" + NumberUtil.addZeroForNum(dcount + "", 4);
		PurchasePlanDetail purchasePlanDetail = new PurchasePlanDetail();
		purchasePlanDetail.setCode(code_detail);
		purchasePlanDetail.setInternalCode(internalCode);
		purchasePlanDetail.setOrderDate(new Date());
		purchasePlanDetail.setGoodsNum(nums);
		purchasePlanDetail.setGoodsSum(gp.getFinalPrice().multiply(nums));
		purchasePlanDetail.setIsPass(0);
		purchasePlanDetail.setPurchasePlan(purchasePlan);
		purchasePlanDetail.setProductCode(product.getCode());
		purchasePlanDetail.setProductName(product.getName());
		purchasePlanDetail.setProducerName(product.getProducerName());
		purchasePlanDetail.setDosageFormName(product.getDosageFormName());
		purchasePlanDetail.setModel(product.getModel());
		purchasePlanDetail.setPackDesc(product.getPackDesc());
		purchasePlanDetail.setUnit(product.getUnitName());
		purchasePlanDetail.setPrice(gp.getFinalPrice());
		purchasePlanDetail.setNotes(notes);
		purchasePlanDetail.setVendorCode(gp.getVendorCode());
		purchasePlanDetail.setVendorName(gp.getVendorName());
		
		purchasePlan.getPurchasePlanDetails().add(purchasePlanDetail);
		BigDecimal num = purchasePlan.getNum()==null?new BigDecimal("0"):purchasePlan.getNum();
		BigDecimal sum = purchasePlan.getSum()==null?new BigDecimal("0"):purchasePlan.getSum();
		purchasePlan.setNum(num.add(purchasePlanDetail.getGoodsNum()));
		purchasePlan.setSum(sum.add(purchasePlanDetail.getGoodsSum()));
	}

	private void createPPDetail(String projectCode, PurchasePlan purchasePlan,Product product, BigDecimal nums,BigDecimal price, String notes, String internalCode) {
		int dcount = purchasePlan.getPurchasePlanDetails().size()+1;
		String code_detail = purchasePlan.getCode() + "-" + NumberUtil.addZeroForNum(dcount + "", 4);
		PurchasePlanDetail purchasePlanDetail = new PurchasePlanDetail();
		purchasePlanDetail.setCode(code_detail);
		purchasePlanDetail.setInternalCode(internalCode);
		purchasePlanDetail.setOrderDate(new Date());
		purchasePlanDetail.setGoodsNum(nums);
		purchasePlanDetail.setGoodsSum(price.multiply(nums));
		purchasePlanDetail.setIsPass(0);
		purchasePlanDetail.setPurchasePlan(purchasePlan);
		purchasePlanDetail.setProductCode(product.getCode());
		purchasePlanDetail.setProductName(product.getName());
		purchasePlanDetail.setProducerName(product.getProducerName());
		purchasePlanDetail.setDosageFormName(product.getDosageFormName());
		purchasePlanDetail.setModel(product.getModel());
		purchasePlanDetail.setPackDesc(product.getPackDesc());
		purchasePlanDetail.setUnit(product.getUnitName());
		purchasePlanDetail.setPrice(price);
		purchasePlanDetail.setNotes(notes);
		purchasePlanDetail.setVendorCode(purchasePlan.getVendorCode());
		purchasePlanDetail.setVendorName(purchasePlan.getVendorName());

		purchasePlan.getPurchasePlanDetails().add(purchasePlanDetail);
		BigDecimal num = purchasePlan.getNum()==null?new BigDecimal("0"):purchasePlan.getNum();
		BigDecimal sum = purchasePlan.getSum()==null?new BigDecimal("0"):purchasePlan.getSum();
		purchasePlan.setNum(num.add(purchasePlanDetail.getGoodsNum()));
		purchasePlan.setSum(sum.add(purchasePlanDetail.getGoodsSum()));
	}
	
	private void createPPD(PurchasePlan purchasePlan,  ContractDetail cd, BigDecimal nums, String notes, String internalCode, Product product) {
		int dcount = purchasePlan.getPurchasePlanDetails().size()+1;
		Contract c = cd.getContract();
		String code_detail = purchasePlan.getCode() + "-" + NumberUtil.addZeroForNum(dcount + "", 4);
		PurchasePlanDetail purchasePlanDetail = new PurchasePlanDetail();
		purchasePlanDetail.setCode(code_detail);
		purchasePlanDetail.setInternalCode(internalCode);
		purchasePlanDetail.setOrderDate(new Date());
		purchasePlanDetail.setGoodsNum(nums);
		purchasePlanDetail.setGoodsSum(cd.getPrice().multiply(nums));
		purchasePlanDetail.setIsPass(0);
		purchasePlanDetail.setPurchasePlan(purchasePlan);
		purchasePlanDetail.setProductCode(product.getCode());
		purchasePlanDetail.setProductName(product.getName());
		purchasePlanDetail.setProducerName(product.getProducerName());
		purchasePlanDetail.setDosageFormName(product.getDosageFormName());
		purchasePlanDetail.setModel(product.getModel());
		purchasePlanDetail.setPackDesc(product.getPackDesc());
		purchasePlanDetail.setUnit(product.getUnitName());
		purchasePlanDetail.setPrice(cd.getPrice());
		purchasePlanDetail.setNotes(notes);
		purchasePlanDetail.setVendorCode(c.getVendorCode());
		purchasePlanDetail.setVendorName(c.getVendorName());
		purchasePlanDetail.setGpoCode(c.getGpoCode());
		purchasePlanDetail.setGpoName(c.getGpoName());
		purchasePlanDetail.setContractDetailCode(cd.getCode());
		
		purchasePlan.getPurchasePlanDetails().add(purchasePlanDetail);
		BigDecimal num = purchasePlan.getNum()==null?new BigDecimal("0"):purchasePlan.getNum();
		BigDecimal sum = purchasePlan.getSum()==null?new BigDecimal("0"):purchasePlan.getSum();
		purchasePlan.setNum(num.add(purchasePlanDetail.getGoodsNum()));
		purchasePlan.setSum(sum.add(purchasePlanDetail.getGoodsSum()));
	}


	private ContractDetail checkOutOfNum(ContractDetail cd, BigDecimal nums) throws Exception {
		
		//超量检查
		System.out.println("nums==="+nums);
		BigDecimal a = cd.getContractNum()==null?new BigDecimal("0"):cd.getContractNum();
		BigDecimal b = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
		BigDecimal c = a.subtract(b);
		System.out.println("c==="+c);
		if(c.subtract(nums).compareTo(new BigDecimal("0"))<0){
			throw new MyException(cd.getProduct().getCode()+"已超量，最多还可采购数量为"+c);
		}
		return cd;
	}


	@Override
	@Transactional(timeout = 90,rollbackFor = Exception.class)
	public String savePurchasePlan(String projectCode, PurchasePlan purchasePlan) {
		//新增purchasePlan
		purchasePlan = purchasePlanDao.saveJDBC(purchasePlan);
		
		List<ContractDetail> cds = contractDetailDao.listBySigned(purchasePlan.getHospitalCode());
		Map<String, ContractDetail> cdMap = new HashMap<>();
		for(ContractDetail cd:cds){
			cdMap.put(cd.getCode(), cd);
		}
		List<PurchasePlanDetail> purchasePlanDetails = new ArrayList<>();
		List<Object[]> contractDetails = new ArrayList<>();

		for (PurchasePlanDetail purchasePlanDetail : purchasePlan.getPurchasePlanDetails()) {
			purchasePlanDetail.setPurchasePlan(purchasePlan);
			purchasePlanDetails.add(purchasePlanDetail);
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem!=null&&"1".equals(attributeItem.getField3())){
				orderMsgService.saveOrderMsg(projectCode, purchasePlanDetail.getCode(),purchasePlanDetail.getCode(), OrderDetailStatus.create);
			}
			//更新合同计划量 购物车
			ContractDetail cd = cdMap.get(purchasePlanDetail.getContractDetailCode());
			if(cd != null){
				//批量更新ContractDetail，优先更新数量
				int i = contractDetailDao.updatePurchasePlanNum(cd.getId(), purchasePlanDetail.getGoodsNum());
				if (i == 0) {
					throw new MyException("合同明细编号："+cd.getCode()+",采购数量为："+purchasePlanDetail.getGoodsNum()+",药品"+cd.getProduct().getCode()+"合同数量不足");
				}
			}
		}
		//批量新增PurchasePlanDetail
		purchasePlanDetailDao.saveBatch(purchasePlanDetails);
		return purchaseOrderPlanService.savePurchaseOrderPlan(projectCode, purchasePlan);
	}


	@Override
	@Transactional
	public String mkOrderContract(String projectCode, PurchasePlan purchasePlan,  User user) throws Exception {
		String code = snDao.getCode(OrderType.plan);
		
		BigDecimal num = new BigDecimal("0");
		BigDecimal sum = new BigDecimal("0");
		List<CartContract> cclist = cartContractService.listByHospital(projectCode, purchasePlan.getHospitalCode());
		
		for (int i = 0; i < cclist.size(); i++) {
			CartContract cc =  cclist.get(i);
			ContractDetail cd = contractDetailDao.getById(cc.getContractDetailId());
			if(cd == null){
				throw new MyException("第"+(i+1)+"笔，没有签订合同，无法下单");
			}
			Contract c = cd.getContract();
			
			
			//检查是否超量
			BigDecimal ccnum = new BigDecimal(cc.getNum());
			checkOutOfNum(cd,ccnum);
			
			Product product = cd.getProduct();
			Company vendor = companyService.findByCode(projectCode,c.getVendorCode(),"isVendor=1");
			Company gpo = null;
			if(product.getIsGPOPurchase()!=null&&product.getIsGPOPurchase() == 1){
				gpo = companyService.getById(projectCode, product.getGpoId());
			}
			
			
			String code_detail = code + "-" + NumberUtil.addZeroForNum(i + "", 4);
			PurchasePlanDetail purchasePlanDetail = new PurchasePlanDetail();
			purchasePlanDetail.setCode(code_detail);
			purchasePlanDetail.setOrderDate(new Date());
			purchasePlanDetail.setGoodsNum(ccnum);
			purchasePlanDetail.setGoodsSum(cd.getPrice().multiply(ccnum));
			purchasePlanDetail.setIsPass(0);
			purchasePlanDetail.setPurchasePlan(purchasePlan);
			purchasePlanDetail.setProductCode(product.getCode());
			purchasePlanDetail.setProductName(product.getName());
			purchasePlanDetail.setProducerName(product.getProducerName());
			purchasePlanDetail.setDosageFormName(product.getDosageFormName());
			purchasePlanDetail.setModel(product.getModel());
			purchasePlanDetail.setPackDesc(product.getPackDesc());
			purchasePlanDetail.setUnit(product.getUnitName());
			purchasePlanDetail.setPrice(cd.getPrice());
			purchasePlanDetail.setNotes("通过合同购物车下单");
			purchasePlanDetail.setVendorCode(vendor.getCode());
			purchasePlanDetail.setVendorName(vendor.getFullName());
			purchasePlanDetail.setContractDetailCode(cd.getCode());
			if(product.getIsGPOPurchase()!=null&&product.getIsGPOPurchase() == 1){
				purchasePlanDetail.setGpoCode(gpo.getCode());
				purchasePlanDetail.setGpoName(gpo.getFullName());
			}
			purchasePlan.getPurchasePlanDetails().add(purchasePlanDetail);
			
			num = num.add(purchasePlanDetail.getGoodsNum());
			sum = sum.add(purchasePlanDetail.getGoodsSum());
			
		}

		purchasePlan.setCode(code);
		purchasePlan.setNum(num);
		purchasePlan.setSum(sum);
		purchasePlan.setOrderDate(new Date());
		purchasePlan.setIsManyDelivery(0);
		purchasePlan.setIsAuto(0);
		purchasePlan.setCreateMethod(CreateMethod.plan);
		
		Hospital hospital = hospitalService.getById(projectCode, user.getOrganization().getOrgId());
		if (hospital != null) {
			purchasePlan.setHospitalCode(hospital.getCode());
			purchasePlan.setHospitalName(hospital.getFullName());
		}
		
		if(purchasePlan.getWarehouseCode() != null){
			Warehouse wh = warehouseService.findByCode(projectCode, purchasePlan.getWarehouseCode());
			purchasePlan.setWarehouseName(wh.getName());
		}
		
		
		String ddbh = this.savePurchasePlan(projectCode, purchasePlan);
		// 删除购物车
		for (CartContract cc : cclist) {
			cartContractService.delete(projectCode, cc);
		}
		return ddbh;
	}

	@Override
	public String savePurchasePlan(String projectCode, List<JSONObject> list, PurchasePlan plan,String field3) throws Exception {

		Map<String, List<ContractDetail>> contractDetailmap = getDetailMap(plan.getHospitalCode());
		for(JSONObject jo:list){
			String ypbm = jo.getString("ypbm");//药品编码
			BigDecimal cgsl = jo.getBigDecimal("cgsl");//采购数量
			BigDecimal cgdj = jo.getBigDecimal("cgdj");//采购单价
			String bzsm = jo.getString("bzsm");//备注说明
			String cgjhdmxbh = jo.getString("cgjhdmxbh");//采购计划单明细编号
			String gysbm = jo.getString("gysbm");	//供应商编码
			
			Product product = productService.getByCode(projectCode, ypbm);
			Company vendor = companyService.findByCode(projectCode,gysbm,"isVendor=1");
			plan.setVendorCode(vendor.getCode());
			plan.setVendorName(vendor.getFullName());
			if("1".equals(field3)){
				plan = this.addGPOPurchase(projectCode, contractDetailmap, plan, product, cgsl, bzsm, cgjhdmxbh);
			}else{
				plan = this.addPurchase(projectCode, contractDetailmap, plan, product, cgsl,cgdj, bzsm, cgjhdmxbh);
			}
		}
		return this.savePurchasePlan(projectCode, plan);
	}

	@Override
	public DataGrid<PurchasePlan> listByPurchasePlanAndDetail(String projectCode, PageRequest pageable) {
		return purchasePlanDao.listByPurchasePlanAndDetail(pageable);
	}
	

	
	private Map<String, List<ContractDetail>> getDetailMap(String hospitalCode) {
		List<ContractDetail> contractDetails = contractDetailDao.listBySigned(hospitalCode);
		Map<String, List<ContractDetail>> map = new HashMap<>();
		for(ContractDetail contractDetail:contractDetails){
			String key = contractDetail.getContract().getHospitalCode()
					+"_"+contractDetail.getContract().getVendorCode()
					+"_"+contractDetail.getProduct().getCode();

			List<ContractDetail> cds =  map.get(key);
			if(map.get(key) == null){
				cds = new ArrayList<>();
			}
			cds.add(contractDetail);
			map.put(key, cds);
		}
		return map;
	}

	@Override
	public List<PurchasePlan> queryByPlanCode(String projectCode) {
		return purchasePlanDao.queryByPlanCode();
	}

	@Override
	public List<PurchasePlanDetail> getDetailById(String projectCode, Long purchasePlanId) {
		return purchasePlanDao.getDetailById(purchasePlanId);
	}
}
