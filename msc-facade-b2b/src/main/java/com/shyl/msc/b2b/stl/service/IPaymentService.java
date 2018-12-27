package com.shyl.msc.b2b.stl.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.stl.entity.Payment;
import com.shyl.sys.entity.User;
/**
 * 付款单
 * 
 * @author a_Q
 *
 */
public interface IPaymentService extends IBaseService<Payment, Long> {


	/**
	 * 生成付款单
	 * @param payment
	 * @param settleId
	 * @param user
	 * @return
	 */
	String mkpayment(@ProjectCodeFlag String projectCode, Payment payment, Long settleId, User user);
	
	/**
	 * 根据结算时间取得结算和付款数据
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> listByPeriod(@ProjectCodeFlag String projectCode, String beginDate, String endDate);

	public void savePayment(@ProjectCodeFlag String projectCode, Payment payment);

	public Payment getByInternalCode(@ProjectCodeFlag String projectCode, String internalCode);
	
}
