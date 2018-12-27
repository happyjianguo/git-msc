package com.shyl.msc.supervise.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.ClinicRecipe;

public interface IClinicRecipeService extends IBaseService<ClinicRecipe, Long> {


	/**
	 * 查询住院及门诊处方信息
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryRecipeAndReg(@ProjectCodeFlag String projectCode,PageRequest pageable, Integer sumType);
	public List<Map<String, Object>> queryRecipeAndRegAll(@ProjectCodeFlag String projectCode,PageRequest page, Integer type);
}
