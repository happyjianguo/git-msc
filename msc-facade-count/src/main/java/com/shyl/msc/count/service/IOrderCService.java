package com.shyl.msc.count.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.Order;
import com.shyl.msc.count.entity.OrderC;

public interface IOrderCService extends IBaseService<OrderC, Long> {
	/**
	 * 过账
	 * @param projectCode
	 */
	public void pass(@ProjectCodeFlag String projectCode);
	
	/**
	 * 过账订单
	 * @param projectCode
	 * @param order
	 * @param type
	 */
	public void passOrderC(@ProjectCodeFlag String projectCode, Order order, String type);
	
	/**
	 * 过账医院
	 * @param projectCode
	 * @param order
	 * @param type
	 */
	public void passHospitalC(@ProjectCodeFlag String projectCode, Order order, String type);
	
	/**
	 * 过账供应商
	 * @param projectCode
	 * @param order
	 * @param type
	 */
	public void passVendorC(@ProjectCodeFlag String projectCode, Order order, String type);
	
	/**
	 * 获得对象
	 * @param month
	 * @param hospitalCode
	 * @param hospitalName
	 * @param vendorCode
	 * @param vendorName
	 * @return
	 */
	public OrderC getOrderC(@ProjectCodeFlag String projectCode, String month, String hospitalCode, String hospitalName, String vendorCode, String vendorName);
	/**
	 * 批量修改
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);

	/**
	 * 根据供应商和月份 统计count
	 * @param pageable
	 * @return
	 */
	public List<Map<String, Object>> countByVendor(PageRequest pageable);
}
