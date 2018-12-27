package com.shyl.msc.b2b.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDao;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderPlanDetailService;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.set.entity.Company;

/**
 * 订单计划明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class PurchaseOrderPlanDetailService extends BaseService<PurchaseOrderPlanDetail, Long> implements IPurchaseOrderPlanDetailService {
	@Resource
	private IContractDetailDao contractDetailDao;
	
	@Resource
	private IPurchaseOrderPlanDao purchaseOrderPlanDao;
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	
	@Resource
	private IPurchasePlanDao purchasePlanDao;
	@Resource
	private IPurchasePlanDetailDao purchasePlanDetailDao;
	
	private IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao;

	public IPurchaseOrderPlanDetailDao getPurchaseOrderPlanDetailDao() {
		return purchaseOrderPlanDetailDao;
	}

	@Resource
	public void setPurchaseOrderPlanDetailDao(IPurchaseOrderPlanDetailDao purchaseOrderPlanDetailDao) {
		this.purchaseOrderPlanDetailDao = purchaseOrderPlanDetailDao;
		super.setBaseDao(purchaseOrderPlanDetailDao);
	}

	@Override
	public List<PurchaseOrderPlanDetail> listByOrderPlanId(String projectCode, Long id) {
		return purchaseOrderPlanDetailDao.listByOrderPlanId(id);
	}

	@Override
	public PurchaseOrderPlanDetail findByCode(String projectCode, String code) {
		return purchaseOrderPlanDetailDao.findByCode(code);
	}

	@Override
	public DataGrid<PurchaseOrderPlanDetail> pageByOrderId(String projectCode, PageRequest pageable, Long orderId) {
		return purchaseOrderPlanDetailDao.pageByOrderId(pageable, orderId);
	}
	
	@Override
	public List<Map<String,Object>> listByDate(String projectCode, String beginDate,String endDate){
		return purchaseOrderPlanDetailDao.listByDate(beginDate, endDate);
	}

	@Override
	@Transactional
	public void checkDetailByH(String projectCode, Long id) {
		PurchaseOrderPlanDetail ppd = purchaseOrderPlanDetailDao.getById(id);
		PurchaseOrderPlan pp = ppd.getPurchaseOrderPlan();
		if(pp.getStatus().equals(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.uneffect)){
			ppd.setStatus(Status.hcancel);
			purchaseOrderPlanDetailDao.update(ppd);
			//修改订单计划主档 数量和总金额
			pp.setNum(pp.getNum().subtract(ppd.getGoodsNum()));
			pp.setSum(pp.getSum().subtract(ppd.getGoodsSum()));
			
			//更新合同计划量
			ContractDetail cd = contractDetailDao.findByCode(ppd.getContractDetailCode());
			if(cd != null){
				BigDecimal a = cd.getPurchasePlanNum()==null?new BigDecimal("0"):cd.getPurchasePlanNum();
				cd.setPurchasePlanNum(a.subtract(ppd.getGoodsNum()));
				contractDetailDao.update(cd);
			}
			//所有明细都取消了，则主档取消
			List<PurchaseOrderPlanDetail> ppdlist =	purchaseOrderPlanDetailDao.listByOrderPlanId(pp.getId());
			int flagd = 0;
			for (PurchaseOrderPlanDetail purchaseOrderPlanDetail : ppdlist) {
				if(!purchaseOrderPlanDetail.getStatus().equals(Status.hcancel)){
					flagd = 1;
					break;
				}
			}
			if(flagd == 0){
				pp.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.hcancel);
				//修改主档修改日期
				pp.setModifyDate(new Date());
				purchaseOrderPlanDao.update(pp);
			}
			
			
			//修改采购计划主档 数量和总金额
			PurchasePlan pplan = purchasePlanDao.findByCode(pp.getPurchasePlanCode());
			pplan.setNum(pplan.getNum().subtract(ppd.getGoodsNum()));
			pplan.setSum(pplan.getSum().subtract(ppd.getGoodsSum()));
			pplan.setModifyDate(new Date());
			purchasePlanDao.update(pplan);
			
			//删除对应的采购计划明细
			PurchasePlanDetail pplanD = purchasePlanDetailDao.findByCode(ppd.getPurchasePlanDetailCode());
			pplanD.setStatus(com.shyl.msc.b2b.order.entity.PurchasePlanDetail.Status.hcancel);
			pplanD.setModifyDate(new Date());
			purchasePlanDetailDao.update(pplanD);
			
			/* 订单审核之后不能取消了，屏蔽
			//已生效的，若订单也是已生效状态，则删除订单、订单明细，订单计划修改状态为未生效
			if(pp.getStatus().equals(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.effect)){
				PurchaseOrder po = purchaseOrderDao.getByPlanCode(pp.getCode());
				if(po !=null && po.getStatus().equals(com.shyl.msc.b2b.order.entity.PurchaseOrder.Status.effect)){
					List<PurchaseOrderDetail> podlist = purchaseOrderDetailDao.listByOrderId(po.getId());
					for (PurchaseOrderDetail purchaseOrderDetail : podlist) {
						purchaseOrderDetailDao.delete(purchaseOrderDetail);
					}
					purchaseOrderDao.delete(po);
					pp.setStatus(com.shyl.msc.b2b.order.entity.PurchaseOrderPlan.Status.uneffect);
					purchaseOrderPlanDao.update(pp);
				}
			}
			*/
		}else{
			throw new MyException("只有【未生效】的订单计划才能取消明细");
		}
		
		
	}
	
	@Override
	@Transactional
	public void cancelDetail(String projectCode, List<JSONObject> list) {
		for(JSONObject jo:list){
			String ddjhmxbh = jo.getString("ddjhmxbh");		//订单计划明细 编号
			PurchaseOrderPlanDetail orderPlanDetail = purchaseOrderPlanDetailDao.findByPlanCode(ddjhmxbh);
			this.checkDetailByH(projectCode, orderPlanDetail.getId());
		}
	}

	@Override
	public PurchaseOrderPlanDetail findByPlanCode(String projectCode, String planCode) {
		return purchaseOrderPlanDetailDao.findByPlanCode(planCode);
	}

	@Override
	public DataGrid<Map<String, Object>> listByHospitalDate(String projectCode, String vendorCode, String beginDate,
			String endDate, PageRequest pageable) {
		return purchaseOrderPlanDetailDao.listByHospitalDate( projectCode,  vendorCode,  beginDate,
				 endDate,  pageable);
	}

	@Override
	public List<PurchaseOrderPlanDetail> listByIsPass(String projectCode, int isPass) {
		return purchaseOrderPlanDetailDao.listByIsPass(isPass);
	}
	
	@Override
	public JSONArray getCancel(String projectCode, Company company, Boolean isGPO)  {		
		List<PurchaseOrderPlanDetail> purchaseOrderPlanDetails = purchaseOrderPlanDetailDao.listGetCancel(company.getCode(), isGPO);
		JSONArray jsonArray = new JSONArray();
		for(PurchaseOrderPlanDetail pod: purchaseOrderPlanDetails){		
			JSONObject jo = new JSONObject();
			jo.put("ddjhmxbh", pod.getCode());			//订单计划明细编号
			
			jsonArray.add(jo);
		}
		return jsonArray;
	}

	@Override
	public DataGrid<PurchaseOrderPlanDetail> queryByCode(String projectCode, PageRequest pageable) {
		return purchaseOrderPlanDetailDao.queryByCode(pageable);
	}

}
