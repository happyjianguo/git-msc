package com.shyl.msc.supervise.dao;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.BaseDrugProvide;

public interface IBaseDrugProvideDao extends IBaseDao<BaseDrugProvide, Long>{
	/**
	 * 查询唯一一笔数据
	 * @param month
	 * @param hospitalCode
	 * @param productId
	 * @return
	 */
	public BaseDrugProvide findUnique(String month, String hospitalCode);
	/**
	 * 根据 区 ,医院 和月份逐步钻取
	 */
	public DataGrid<Map<String, Object>> groupByCounty(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByHospital(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByMonth(PageRequest page);
}
