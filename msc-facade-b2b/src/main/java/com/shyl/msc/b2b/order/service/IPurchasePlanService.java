package com.shyl.msc.b2b.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchasePlan;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.msc.b2b.plan.entity.ContractDetail;
import com.shyl.msc.dm.entity.Product;
import com.shyl.sys.entity.User;

/**
 * 订单计划Service接口
 * 
 * @author a_Q
 *
 */
public interface IPurchasePlanService extends IBaseService<PurchasePlan, Long> {

	/**
	 * 通过购物车下单
	 * @param purchasePlan
	 * @param cartids
	 * @param goodsids
	 * @param nums
	 * @param user
	 * @return
	 */
	String mkOrder(@ProjectCodeFlag String projectCode, PurchasePlan purchasePlan, Long[] cartids, Long[] goodspriceids, BigDecimal[] nums, User user) throws Exception  ;


	public String savePurchasePlan(@ProjectCodeFlag String projectCode, PurchasePlan purchasePlan);


	public String mkOrderContract(@ProjectCodeFlag String projectCode, PurchasePlan purchasePlan, User user) throws Exception;


	public PurchasePlan addGPOPurchase(@ProjectCodeFlag String projectCode, Map<String, List<ContractDetail>> contractDetailmap, PurchasePlan purchasePlan, Product product, BigDecimal num, String notes, String internalCode) throws Exception;


	public String savePurchasePlan(@ProjectCodeFlag String projectCode, List<JSONObject> list, PurchasePlan plan,String field3) throws Exception ;


	public DataGrid<PurchasePlan> listByPurchasePlanAndDetail(String projectCode, PageRequest pageable);

	List<PurchasePlan> queryByPlanCode (@ProjectCodeFlag String projectCode);

	List<PurchasePlanDetail> getDetailById(@ProjectCodeFlag String projectCode,Long purchasePlanId);

	
}
