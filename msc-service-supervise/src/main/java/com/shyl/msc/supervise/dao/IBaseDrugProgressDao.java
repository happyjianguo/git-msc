package com.shyl.msc.supervise.dao;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.entity.BaseDrugProvide;

public interface IBaseDrugProgressDao extends IBaseDao<BaseDrugProgress,Long>{

	/**
	 * 查询唯一一笔数据
	 * @param month
	 * @param hospitalCode
	 * @return
	 */
	public BaseDrugProgress findUnique(String month, String hospitalCode);
	/**
	 * 医院选择了一个机构类型之后就不能再选择其它的类型
	 * @param month
	 * @param hospitalCode
	 * @return
	 */
	public BaseDrugProgress findByType(Integer healthStationType);
	/**
	 * 根据 区 ,医院 和月份逐步钻取
	 */
	public DataGrid<Map<String, Object>> groupByCity(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByCounty(PageRequest page);
	
	public DataGrid<Map<String, Object>> groupByHealth(PageRequest page);
}
