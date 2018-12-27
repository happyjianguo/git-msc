package com.shyl.msc.count.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.ProductC;
import com.shyl.msc.dm.entity.Product;

public interface IProductCService extends IBaseService<ProductC, Long> {
	/**
	 * 获得对象
	 * 
	 * @param month
	 * @param product
	 * @return
	 */
	public ProductC getProductC(@ProjectCodeFlag String projectCode, String month, Product product);

	/**
	 * 批量修改
	 * 
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);

	/**
	 * 某月的总品规数
	 * 
	 * @param month
	 * @return
	 */
	public Map<String, Object> countByMonth(String month);

	/**
	 * 根据productC表查询交易金额汇总
	 * 
	 * @param year
	 * @return
	 */
	public List<Map<String, Object>> reportCTrade(String projectcode,String year);

	/**
	 * 根据productC表查询交易药品汇总
	 * 
	 * @param dateS
	 * @param dateE
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> reportCGoodsTrade(String projectCode, String dateS, String dateE,
			PageRequest pageable);
}
