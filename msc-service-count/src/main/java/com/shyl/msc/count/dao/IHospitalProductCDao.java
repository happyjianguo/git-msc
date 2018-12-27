package com.shyl.msc.count.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.HospitalProductC;

public interface IHospitalProductCDao extends IBaseDao<HospitalProductC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param productId
	 * @param hospitalCode
	 * @return
	 */
	public HospitalProductC getByKey(String month, Long productId, String hospitalCode);
	/**
	 * 根据库存标志查找对象列表
	 * @param stockFlag
	 * @param month
	 * @return
	 */
	public List<HospitalProductC> listByStockFlag(int stockFlag, String month);
}
