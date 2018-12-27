package com.shyl.msc.count.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.count.entity.ProductC;

public interface IProductCDao extends IBaseDao<ProductC, Long> {
	/**
	 * 根据key找对象
	 * 
	 * @param month
	 * @param productId
	 * @return
	 */
	public ProductC getByKey(String month, Long productId);

	public Map<String, Object> countByMonth(String month);

	public List<Map<String, Object>> reportCTrade(String year);

	public DataGrid<Map<String, Object>> reportCGoodsTrade(String dateS, String dateE, PageRequest pageable);
}
