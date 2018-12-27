package com.shyl.msc.b2b.stl.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.b2b.stl.entity.AccountOrder;

/**
 * 账务
 * 
 * @author a_Q
 *
 */
public interface IAccountOrderService extends IBaseService<AccountOrder, Long> {
	
	/**
	 * 过账
	 * @param order
	 */
	public void passAccount(@ProjectCodeFlag String projectCode, Order order);

	/**
	 * 医院今年每月的采购量
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> listByHospitalPerMonth(@ProjectCodeFlag String projectCode, String hospitalCode, String year);

	/**
	 * 医院年采购金额
	 * @param year
	 * @return
	 */
	public Map<String, Object> orderSumByHospitalAndYear(@ProjectCodeFlag String projectCode, String hospitalCode, String year);

	/**
	 * 过订单账务
	 */
	void passAccount(@ProjectCodeFlag String projectCode);
	
	/**
	 * 供应商年采购金额
	 * @param year
	 * @return
	 */
	public Map<String, Object> orderSumByGpoAndYear(@ProjectCodeFlag String projectCode, String vendorCode, String year);
	/**
	 * 供应商今年每月的采购量
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> listByGpoPerMonth(@ProjectCodeFlag String projectCode, String vendorCode, String year);
}
