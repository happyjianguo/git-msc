package com.shyl.msc.count.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.OrderC;

public interface IOrderCDao extends IBaseDao<OrderC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param hospitalCode
	 * @param vendorCode
	 * @return
	 */
	public OrderC getByKey(String month, String hospitalCode, String vendorCode);

	public List<Map<String, Object>> countByVendor(PageRequest pageable);
}
