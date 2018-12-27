package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.common.util.NumberUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.msc.b2b.order.entity.InOutBound.IOType;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.service.IInOutBoundService;
import com.shyl.msc.b2b.order.service.IOrderMsgService;
import com.shyl.msc.enmu.OrderDetailStatus;
import com.shyl.msc.enmu.OrderType;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.sys.entity.User;
/**
 * 出入库单Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class InOutBoundService extends BaseService<InOutBound, Long> implements IInOutBoundService {
	@Resource
	private IInOutBoundDetailDao inOutBoundDetailDao;
	@Resource
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	private IDeliveryOrderDao deliveryOrderDao;
	@Resource 
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IPurchaseOrderPlanDao purchaseOrderPlanDao;
	@Resource
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;
	@Resource
	private ISnDao snDao;
	@Resource
	private IOrderMsgService orderMsgService;
	@Resource
	private IAttributeItemService attributeItemService;
	private IInOutBoundDao inOutBoundDao;
	public IInOutBoundDao getInOutBoundDao() {
		return inOutBoundDao;
	}
	@Resource
	public void setInOutBoundDao(IInOutBoundDao inOutBoundDao) {
		this.inOutBoundDao = inOutBoundDao;
		super.setBaseDao(inOutBoundDao);
	}
	
	
	@Override
	@Transactional
	public void saveInOutBound(String projectCode,InOutBound inOutBound){
		inOutBound = inOutBoundDao.saveJDBC(inOutBound);
		List<InOutBoundDetail> inOutBoundDetails = new ArrayList<>();
		List<DeliveryOrderDetail> deliveryOrderDetails = new ArrayList<>();
		List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
		List<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = new ArrayList<>();
		for (InOutBoundDetail inOutBoundDetail : inOutBound.getInOutBoundDetails()) {
			inOutBoundDetail.setInOutBound(inOutBound);
			inOutBoundDetails.add(inOutBoundDetail);
			//inOutBoundDetailDao.save(inOutBoundDetail);
			
			//处方外配情况
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo(projectCode,"publicUser", "IS_OPEN_PE");
			if(attributeItem.getField3().equals("1")){
				orderMsgService.saveOrderMsg(inOutBound.getProjectCode(), inOutBoundDetail.getDeliveryOrderDetailCode(), inOutBoundDetail.getCode(), OrderDetailStatus.receive);
			}
			if (!StringUtils.isEmpty(inOutBoundDetail.getDeliveryOrderDetailCode())) {
				//更新配送单明细
				DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.findByCode(inOutBoundDetail.getDeliveryOrderDetailCode());
				BigDecimal oldNum = deliveryOrderDetail.getInOutBoundGoodsNum()==null?new BigDecimal(0):deliveryOrderDetail.getInOutBoundGoodsNum();
				BigDecimal oldSum = deliveryOrderDetail.getInOutBoundGoodsSum()==null?new BigDecimal(0):deliveryOrderDetail.getInOutBoundGoodsSum();
				deliveryOrderDetail.setInOutBoundGoodsNum(oldNum.add(inOutBoundDetail.getGoodsNum()));
				deliveryOrderDetail.setInOutBoundGoodsSum(oldSum.add(inOutBoundDetail.getGoodsSum()));
				deliveryOrderDetails.add(deliveryOrderDetail);
				//更新订单明细
				PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.findByCode(deliveryOrderDetail.getPurchaseOrderDetailCode());
				oldNum = purchaseOrderDetail.getInOutBoundGoodsNum()==null?new BigDecimal(0):purchaseOrderDetail.getInOutBoundGoodsNum();
				oldSum = purchaseOrderDetail.getInOutBoundGoodsSum()==null?new BigDecimal(0):purchaseOrderDetail.getInOutBoundGoodsSum();
				purchaseOrderDetail.setInOutBoundGoodsNum(oldNum.add(inOutBoundDetail.getGoodsNum()));
				purchaseOrderDetail.setInOutBoundGoodsSum(oldSum.add(inOutBoundDetail.getGoodsSum()));
				purchaseOrderDetails.add(purchaseOrderDetail);
				//purchaseOrderDetailDao.update(purchaseOrderDetail);
				
				//更新订单计划明细
				PurchaseOrderPlanDetail purchaseOrderPlanDetail = purchaseOrderPlanDetailDao.findByCode(purchaseOrderDetail.getPurchaseOrderPlanDetailCode());
				oldNum = purchaseOrderPlanDetail.getInOutBoundGoodsNum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getInOutBoundGoodsNum();
				oldSum = purchaseOrderPlanDetail.getInOutBoundGoodsSum()==null?new BigDecimal(0):purchaseOrderPlanDetail.getInOutBoundGoodsSum();
				purchaseOrderPlanDetail.setInOutBoundGoodsNum(oldNum.add(inOutBoundDetail.getGoodsNum()));
				purchaseOrderPlanDetail.setInOutBoundGoodsSum(oldSum.add(inOutBoundDetail.getGoodsSum()));
				purchaseOrderPlanDetails.add(purchaseOrderPlanDetail);
			}
			//deliveryOrderDetailDao.update(deliveryOrderDetail);
			
			//purchaseOrderPlanDetailDao.update(purchaseOrderPlanDetail);
			
		}
		if (inOutBoundDetails.size() > 0) {
			//批量新增inOutBoundDetail
			inOutBoundDetailDao.saveBatch(inOutBoundDetails);
		}
		if (deliveryOrderDetails.size() > 0) {
			//批量修改deliveryOrderDetail
			deliveryOrderDetailDao.updateBatch(deliveryOrderDetails);
		}
		if (purchaseOrderDetails.size() > 0) {
			//批量修改purchaseOrderDetail
			purchaseOrderDetailDao.updateBatch(purchaseOrderDetails);
		}
		if (purchaseOrderPlanDetails.size() > 0) {
			//批量修改purchaseOrderPlanDetail
			purchaseOrderPlanDetailDao.updateBatch(purchaseOrderPlanDetails);
		}
		if (!StringUtils.isEmpty(inOutBound.getDeliveryOrderCode())) {
			//更新配送单主档
			DeliveryOrder deliveryOrder = deliveryOrderDao.findByCode(inOutBound.getDeliveryOrderCode());
			BigDecimal oldNum = deliveryOrder.getInOutBoundNum()==null?new BigDecimal(0):deliveryOrder.getInOutBoundNum();
			BigDecimal oldSum = deliveryOrder.getInOutBoundSum()==null?new BigDecimal(0):deliveryOrder.getInOutBoundSum();
			deliveryOrder.setInOutBoundNum(oldNum.add(inOutBound.getNum()));
			deliveryOrder.setInOutBoundSum(oldSum.add(inOutBound.getSum()));
			
			Map<String, Object> map = inOutBoundDao.getNumByDeliveryCode(deliveryOrder.getCode());//取该配送单下所有入库总量
			BigDecimal inOutBoundNum = inOutBound.getNum();
			if(map.get("NUM") != null){
				inOutBoundNum = inOutBoundNum.add(new BigDecimal(map.get("NUM").toString()));
			}
			if(inOutBoundNum.compareTo(deliveryOrder.getNum())>=0){//入库总量大于配送数量时
				deliveryOrder.setStatus(DeliveryOrder.Status.closed);
			}else{
				deliveryOrder.setStatus(DeliveryOrder.Status.receiving);
			}
			deliveryOrderDao.update(deliveryOrder);

			//更新订单主档
			PurchaseOrder purchaseOrder = purchaseOrderDao.findByCode(deliveryOrder.getPurchaseOrderCode());
			BigDecimal oldPNum = purchaseOrder.getInOutBoundNum()==null?new BigDecimal(0):purchaseOrder.getInOutBoundNum();
			BigDecimal oldPSum = purchaseOrder.getInOutBoundSum()==null?new BigDecimal(0):purchaseOrder.getInOutBoundSum();
			purchaseOrder.setInOutBoundNum(oldPNum.add(inOutBound.getNum()));
			purchaseOrder.setInOutBoundSum(oldPSum.add(inOutBound.getSum()));
			purchaseOrderDao.update(purchaseOrder);
			//更新订单计划主档
			PurchaseOrderPlan purchaseOrderPlan = purchaseOrderPlanDao.findByCode(purchaseOrder.getPurchaseOrderPlanCode());
			oldPNum = purchaseOrderPlan.getInOutBoundNum()==null?new BigDecimal(0):purchaseOrderPlan.getInOutBoundNum();
			oldPSum = purchaseOrderPlan.getInOutBoundSum()==null?new BigDecimal(0):purchaseOrderPlan.getInOutBoundSum();
			purchaseOrderPlan.setInOutBoundNum(oldPNum.add(inOutBound.getNum()));
			purchaseOrderPlan.setInOutBoundSum(oldPSum.add(inOutBound.getSum()));
			purchaseOrderPlanDao.update(purchaseOrderPlan);
		}
		
		
	}
	
	@Override
	@Transactional
	public List<InOutBound> takeInOutBound(String projectCode, List<DeliveryOrderDetail> fastjson, User user) {
		List<InOutBound> rtnList = new ArrayList<InOutBound>();
		
	
		Map<Long, List<Map<String, Object>>> deliveryMap = new HashMap<Long, List<Map<String, Object>>>();
		List<Map<String, Object>> deliveryList = new ArrayList<Map<String,Object>>();
		for(DeliveryOrderDetail jo:fastjson){
			if(jo == null){
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			
			
			DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.getById(jo.getId());
			DeliveryOrder deliveryOrder = deliveryOrderDetail.getDeliveryOrder();
			if(deliveryMap.get(deliveryOrder.getId()) != null){
				deliveryList = deliveryMap.get(deliveryOrder.getId());
			}else{
				deliveryList = new ArrayList<Map<String,Object>>();
			}
			map.put("num", jo.getGoodsNum());
			map.put("deliveryOrderDetail", deliveryOrderDetail);
			deliveryList.add(map);
			deliveryMap.put(deliveryOrder.getId(), deliveryList);
		}
		
		for (Long key : deliveryMap.keySet()) {
			DeliveryOrder deliveryOrder = deliveryOrderDao.getById(key);
			List<Map<String, Object>> list2 = deliveryMap.get(key);
			BigDecimal inOutBoundNum = new BigDecimal(0);
			BigDecimal inOutBoundSum = new BigDecimal(0);
			
			InOutBound inOutBound = new InOutBound();
			inOutBound.setCode(snDao.getCode(OrderType.inoutbound));
			inOutBound.setOrderDate(new Date());
			inOutBound.setGpoCode(deliveryOrder.getGpoCode());
			inOutBound.setGpoName(deliveryOrder.getGpoName());
			inOutBound.setVendorCode(deliveryOrder.getVendorCode());
			inOutBound.setVendorName(deliveryOrder.getVendorName());
			inOutBound.setHospitalCode(deliveryOrder.getHospitalCode());
			inOutBound.setHospitalName(deliveryOrder.getHospitalName());
			inOutBound.setWarehouseCode(deliveryOrder.getWarehouseCode());
			inOutBound.setWarehouseName(deliveryOrder.getWarehouseName());
			inOutBound.setIoType(IOType.in);
			inOutBound.setOperator(user.getName());
			inOutBound.setDeliveryOrderCode(deliveryOrder.getCode());
			
			int i = 0;
			for (Map<String, Object> map:list2) {
				i++;
				BigDecimal goodsNum = new BigDecimal(map.get("num").toString());
				DeliveryOrderDetail deliveryOrderDetail = (DeliveryOrderDetail) map.get("deliveryOrderDetail");
				BigDecimal goodsSum = deliveryOrderDetail.getPrice().multiply(goodsNum);
				InOutBoundDetail inOutBoundDetail = new InOutBoundDetail();
				inOutBoundDetail.setCode(inOutBound.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4));
				inOutBoundDetail.setInOutBound(inOutBound);
				inOutBoundDetail.setOrderDate(inOutBound.getOrderDate());
				inOutBoundDetail.setBatchCode(deliveryOrderDetail.getBatchCode());
				inOutBoundDetail.setBatchDate(deliveryOrderDetail.getBatchDate());
				inOutBoundDetail.setProductCode(deliveryOrderDetail.getProductCode());
				inOutBoundDetail.setProductName(deliveryOrderDetail.getProductName());
				inOutBoundDetail.setProducerName(deliveryOrderDetail.getProducerName());
				inOutBoundDetail.setDosageFormName(deliveryOrderDetail.getDosageFormName());
				inOutBoundDetail.setModel(deliveryOrderDetail.getModel());
				inOutBoundDetail.setPackDesc(deliveryOrderDetail.getPackDesc());
				inOutBoundDetail.setUnit(deliveryOrderDetail.getUnit());
				inOutBoundDetail.setPrice(deliveryOrderDetail.getPrice());
				inOutBoundDetail.setGoodsNum(goodsNum);
				inOutBoundDetail.setGoodsSum(goodsSum);
				inOutBoundDetail.setExpiryDate(deliveryOrderDetail.getExpiryDate());
				inOutBoundDetail.setDeliveryOrderDetailCode(deliveryOrderDetail.getCode());

				inOutBound.getInOutBoundDetails().add(inOutBoundDetail);
				inOutBoundNum = inOutBoundNum.add(inOutBoundDetail.getGoodsNum());
				inOutBoundSum = inOutBoundSum.add(inOutBoundDetail.getGoodsSum());
			}
			
			inOutBound.setNum(inOutBoundNum);
			inOutBound.setSum(inOutBoundSum);
			this.saveInOutBound(projectCode,inOutBound);
			rtnList.add(inOutBound);
		}
		return rtnList;
	}
	@Override
	public List<InOutBound> listByDate(String projectCode, String companyCode, String startDate, String endDate, boolean isGPO) {
		return inOutBoundDao.listByDate(companyCode, startDate, endDate, isGPO);
	}
	
	@Override
	public InOutBound getByInternalCode(String projectCode, String hospitalCode, String internalCode) {
		return inOutBoundDao.getByInternalCode(hospitalCode, internalCode);
	}
	@Override
	public void saveInOutBound(String projectCode, List<JSONObject> list, InOutBound inOutBound) {
		BigDecimal inOutBoundNum = new BigDecimal(0);
		BigDecimal inOutBoundSum = new BigDecimal(0);
		int i = 0;
		for(JSONObject jod:list){
			i++;
			BigDecimal crksl = jod.getBigDecimal("crksl");//出入库数量
			String psdmxbh = jod.getString("psdmxbh");//配送单明细编号
			String rkdmxbh = jod.getString("rkdmxbh");//入库单明细编号
			String scph = jod.getString("scph");//入库单明细编号
			String scrq = jod.getString("scrq");//入库单明细编号
			String yxrq = jod.getString("yxrq");//入库单明细编号
			
			DeliveryOrderDetail deliveryOrderDetail = deliveryOrderDetailDao.findByCode(psdmxbh);
			InOutBoundDetail inOutBoundDetail = new InOutBoundDetail();
			String code_detail = inOutBound.getCode()+"-"+NumberUtil.addZeroForNum(i+"", 4);
			inOutBoundDetail.setCode(code_detail);
			inOutBoundDetail.setInternalCode(rkdmxbh);
			inOutBoundDetail.setIsPass(0);
			inOutBoundDetail.setOrderDate(inOutBound.getOrderDate());
			inOutBoundDetail.setInOutBound(inOutBound);
			inOutBoundDetail.setBatchCode(scph);
			inOutBoundDetail.setBatchDate(scrq);
			inOutBoundDetail.setExpiryDate(yxrq);				
			inOutBoundDetail.setProductCode(deliveryOrderDetail.getProductCode());
			inOutBoundDetail.setProductName(deliveryOrderDetail.getProductName());
			inOutBoundDetail.setProducerName(deliveryOrderDetail.getProducerName());
			inOutBoundDetail.setDosageFormName(deliveryOrderDetail.getDosageFormName());
			inOutBoundDetail.setModel(deliveryOrderDetail.getModel());
			inOutBoundDetail.setPackDesc(deliveryOrderDetail.getPackDesc());
			inOutBoundDetail.setUnit(deliveryOrderDetail.getUnit());
			inOutBoundDetail.setPrice(deliveryOrderDetail.getPrice());
			inOutBoundDetail.setGoodsNum(crksl);
			inOutBoundDetail.setGoodsSum(inOutBoundDetail.getPrice().add(crksl));
			inOutBoundDetail.setDeliveryOrderDetailCode(psdmxbh);
			if(inOutBound.getCreateUser() != null){
				inOutBoundDetail.setCreateUser(inOutBound.getCreateUser());;
			}
			inOutBound.getInOutBoundDetails().add(inOutBoundDetail);
			inOutBoundNum = inOutBoundNum.add(inOutBoundDetail.getGoodsNum());
			inOutBoundSum = inOutBoundSum.add(inOutBoundDetail.getGoodsSum());
		}
		inOutBound.setNum(inOutBoundNum);
		inOutBound.setSum(inOutBoundSum);
		
		this.saveInOutBound(projectCode,inOutBound);
	}
	
	@Override
	public InOutBound findByCode(String projectCode, String code) {
		return inOutBoundDao.findByCode(code);
	}
	@Override
	public DataGrid<InOutBound> listByInOutBoundAndDetail(PageRequest pageable) {
		return inOutBoundDao.listByInOutBoundAndDetail(pageable);
	}
	
	@Override
	public List<InOutBound> listByIsPass(String projectCode, int isPass){
		return inOutBoundDao.listByIsPass(isPass);
	}
}
