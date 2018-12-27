package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.IPaymentDao;
import com.shyl.msc.b2b.stl.entity.Payment;

/**
 * 付款
 * 
 * @author a_Q
 *
 */
@Repository
public class PaymentDao extends BaseDao<Payment, Long> implements IPaymentDao {
	@Override
	public List<Map<String, Object>> listByPeriod(String beginDate, String endDate) {
		String sql = "select s.hospitalCode,s.hospitalName,s.accBeginDate,s.accEndDate,s.sum,s.paidAmt,"
				+ "p.sum as paysum,p.payDate,p.settlementcode from t_stl_payment p "
				+ "left outer join t_stl_settlement s on p.settlementCode=s.code "
				+ "where to_char(s.orderDate,'yyyy-mm-dd')>=? and to_char(s.orderDate,'yyyy-mm-dd')<=?";	
		return super.listBySql(sql,null, Map.class, beginDate, endDate);
	}

	@Override
	public Payment getByInternalCode(String internalCode) {
		String hql = "from Payment p where p.internalCode=?";
		return super.getByHql(hql, internalCode);
	}
}
