package com.shyl.msc.supervise.service;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.BaseDrugProvide;

public interface IBaseDrugProvideService extends IBaseService<BaseDrugProvide, Long> {
	/**
	 * 查询唯一一笔数据
	 * @param month
	 * @param hospitalCode
	 * @return
	 */
	public BaseDrugProvide findUnique(@ProjectCodeFlag String projectCode, String month, String hospitalCode);
	/**
	 * 查询某一个月分的数据
	 * @param month
	 * @param hospitalName
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryProvideByPage(@ProjectCodeFlag String projectCode,PageRequest page,Integer queryType);
	
}
