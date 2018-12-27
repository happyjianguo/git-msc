package com.shyl.msc.count.service;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.OrderDetailC;
import com.shyl.msc.dm.entity.Product;

public interface IOrderDetailCService extends IBaseService<OrderDetailC, Long> {
	/**
	 * 过账
	 * @param projectCode
	 */
	public void pass(@ProjectCodeFlag String projectCode);
	
	/**
	 * 获得对象
	 * @param month
	 * @param product
	 * @param hospitalCode
	 * @param hospitalName
	 * @param vendorCode
	 * @param vendorName
	 * @return
	 */
	public OrderDetailC getOrderDetailC(@ProjectCodeFlag String projectCode, String month, Product product, String hospitalCode, String hospitalName, String vendorCode, String vendorName);
	/**
	 * 批量修改
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);
	/**
	 * 交易药品汇总详情
	 * @param projectCode 
	 * @param id
	 * @param dateS
	 * @param dateE
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportCGoodsTradeMX(String projectCode, Long id, String dateS, String dateE, PageRequest pageable);
}
