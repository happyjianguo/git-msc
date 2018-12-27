package com.shyl.msc.b2b.stl.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.stl.entity.AccountOrder;
/**
 * 账务DAO接口
 * 
 * @author a_Q
 *
 */
public interface IAccountOrderDao extends IBaseDao<AccountOrder, Long> {

	/**
	 * 取得一笔账务数据
	 * @param month
	 * @param vendorCode
	 * @param hospitalCode
	 * @return
	 */
	public AccountOrder getOne(String month,String vendorCode,String hospitalCode);

	public List<Map<String, Object>> listByHospitalPerMonth(String hospitalCode, String year);

	public Map<String, Object> orderSumByHospitalAndYear(String hospitalCode, String year);

	public Map<String, Object> orderSumByGpoAndYear(String vendorCode, String year);

	public List<Map<String, Object>> listByGpoPerMonth(String vendorCode, String year);

}
