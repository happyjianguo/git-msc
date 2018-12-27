package com.shyl.msc.b2b.stl.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.Payment;

/**
 * 付款
 * 
 * @author a_Q
 *
 */
public interface IPaymentDao extends IBaseDao<Payment, Long> {

	/**
	 * 根据结算时间取得结算和付款数据
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String, Object>> listByPeriod(String beginDate, String endDate);

	public Payment getByInternalCode(String internalCode);

	
}
