package com.shyl.msc.count.dao;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.OrderDetailC;

public interface IOrderDetailCDao extends IBaseDao<OrderDetailC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param productId
	 * @param hospitalCode
	 * @param vendorCode
	 * @return
	 */
	public OrderDetailC getByKey(String month, Long productId, String hospitalCode, String vendorCode);
/**
 * 查询交易药品详情
 * @param id
 * @param dateS
 * @param dateE
 * @param pageable
 * @return
 */
	DataGrid<Map<String, Object>> reportCGoodsTradeMX(Long id, String dateS, String dateE, PageRequest pageable);
}
