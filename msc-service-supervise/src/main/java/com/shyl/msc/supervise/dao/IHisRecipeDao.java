package com.shyl.msc.supervise.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HisReg;

public interface IHisRecipeDao extends IBaseDao<HisReg, Long>{
	
	/**
	 * 查询住院处方信息
	 * @param pageable
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryRecipe(PageRequest pageable);

	public List<Map<String, Object>> queryRecipeExport(PageRequest page);

}
