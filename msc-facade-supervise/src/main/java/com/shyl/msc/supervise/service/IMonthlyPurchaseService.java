package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.MonthlyPurchase;

public interface IMonthlyPurchaseService extends IBaseService<MonthlyPurchase,Long>{
	
	/**
	 * 根据年月、医院查询采购记录
	 * @param startMonth
	 * @param toMonth
	 * @param hospitalCode
	 * @return
	 */
	public List<Map<String, Object>> query(@ProjectCodeFlag String projectCode,PageRequest page);
	
	/**
	 * 
	 * @param projectCode
	 * @param page
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryByPage(@ProjectCodeFlag String projectCode, PageRequest page);

}