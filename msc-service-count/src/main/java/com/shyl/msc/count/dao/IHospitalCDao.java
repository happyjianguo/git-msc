package com.shyl.msc.count.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.HospitalC;

public interface IHospitalCDao extends IBaseDao<HospitalC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param hospitalCode
	 * @return
	 */
	public HospitalC getByKey(String month, String hospitalCode);
	
	/**
	 * 根据库存标志查找对象列表
	 * @param stockFlag
	 * @param month
	 * @return
	 */
	public List<HospitalC> listByStockFlag(int stockFlag, String month);
}
