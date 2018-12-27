package com.shyl.msc.supervise.service;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.entity.BaseDrugProvide;


public interface IBaseDrugProgressService extends IBaseService<BaseDrugProgress,Long>{
	/**
	 * 查询唯一一笔数据
	 * @param month
	 * @param hospitalCode
	 * @return
	 */
	public BaseDrugProgress findUnique(@ProjectCodeFlag String projectCode, String month, String hospitalCode);
	
	public BaseDrugProgress findByType(@ProjectCodeFlag String projectCode,Integer healthStationType);
	
	public DataGrid<Map<String, Object>> queryProgressByPage(@ProjectCodeFlag String projectCode,PageRequest page,Integer queryType);
}
