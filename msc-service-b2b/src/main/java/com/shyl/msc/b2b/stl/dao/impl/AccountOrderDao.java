package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.IAccountOrderDao;
import com.shyl.msc.b2b.stl.entity.AccountOrder;

/**
 * 账务
 * 
 * @author a_Q
 *
 */
@Repository
public class AccountOrderDao extends BaseDao<AccountOrder, Long> implements IAccountOrderDao {
	@Override
	public AccountOrder getOne(String month,String vendorCode,String hospitalCode) {
		String hql = "select a from AccountOrder  a where a.month=? and a.vendorCode=? and a.hospitalCode=?";
		return super.getByHql(hql,month,vendorCode,hospitalCode);
	}

	@Override
	public List<Map<String, Object>> listByHospitalPerMonth(String hospitalCode, String year) {
		String sql = "select a.hospitalCode as CODE,month as MONTH,sum(a.orderSum) as ORDERSUM,sum(a.orderNum) as ORDERNUM "
				+ " from t_stl_account_order a where month like ? and a.hospitalCode = ? "
				+ "group by a.hospitalCode,month ";
		return super.listBySql(sql,null, Map.class, year+"%",hospitalCode);
	}

	@Override
	public Map<String, Object> orderSumByHospitalAndYear(String hospitalCode, String year) {
		String sql = "select sum(a.orderSum) as ORDERSUM,sum(a.orderNum) as ORDERNUM "
				+ " from t_stl_account_order a where to_char(a.createDate,'YYYY')=? and a.hospitalCode = ? "
				+ "group by to_char(a.createDate,'YYYY') ";
		return super.getBySql(sql, Map.class, year+"",hospitalCode);
	}

	@Override
	public Map<String, Object> orderSumByGpoAndYear(String vendorCode, String year) {
		String sql = "select sum(a.orderSum) as ORDERSUM,sum(a.orderNum) as ORDERNUM "
				+ " from t_stl_account_order a where to_char(a.createDate,'YYYY')=? and a.vendorCode = ? "
				+ "group by to_char(a.createDate,'YYYY') ";
		return super.getBySql(sql, Map.class, year+"",vendorCode);
	}

	@Override
	public List<Map<String, Object>> listByGpoPerMonth(String vendorCode, String year) {
		String sql = "select a.hospitalCode as code,month as MONTH,sum(a.orderSum) as ORDERSUM,sum(a.orderNum) as ORDERNUM "
				+ " from t_stl_account_order a where month like ? and a.vendorCode = ? "
				+ "group by a.hospitalCode,month ";
		
		return super.listBySql(sql,null, Map.class, year+"%",vendorCode);
	}
}
