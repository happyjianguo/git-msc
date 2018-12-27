package com.shyl.msc.b2b.order.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.Sn;
import com.shyl.msc.enmu.OrderType;
/**
 * 序列号Service接口
 * 
 * @author a_Q
 *
 */
public interface ISnService extends IBaseService<Sn, Long> {
	/**
	 * 生成序列号
	 * 
	 * @param orderType
	 *            类型
	 * @return 序列号
	 */
	String getCode(@ProjectCodeFlag String projectCode, OrderType orderType);
}
