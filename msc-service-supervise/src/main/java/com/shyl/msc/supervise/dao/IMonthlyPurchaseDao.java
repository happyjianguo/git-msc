package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.MonthlyPurchase;

public interface IMonthlyPurchaseDao extends IBaseDao<MonthlyPurchase,Long>{
	/**
	 * 根据年月、医院查询采购记录
	 * @param startMonth
	 * @param toMonth
	 * @param hospitalCode
	 * @return
	 */
	public List<Map<String, Object>> query(PageRequest page);
	
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page);
}
