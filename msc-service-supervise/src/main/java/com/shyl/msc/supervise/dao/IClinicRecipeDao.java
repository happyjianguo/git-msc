package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.ClinicRecipe;

public interface IClinicRecipeDao extends IBaseDao<ClinicRecipe, Long>{
	
	/**
	 * 查询住院及门诊处方信息
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryRecipeAndReg(PageRequest pageable, Integer sumType);

	public List<Map<String, Object>> queryRecipeAndRegAll(PageRequest page, Integer sumType);
}
