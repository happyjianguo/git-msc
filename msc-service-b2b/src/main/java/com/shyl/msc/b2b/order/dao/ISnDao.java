/*
 * 
 * 
 * 
 */
package com.shyl.msc.b2b.order.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.Sn;
import com.shyl.msc.enmu.OrderType;


/**
 * Dao - 序列号
 * 
 * 
 * 
 */
public interface ISnDao extends IBaseDao<Sn,Long>{

	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	String getCode(OrderType type);

}