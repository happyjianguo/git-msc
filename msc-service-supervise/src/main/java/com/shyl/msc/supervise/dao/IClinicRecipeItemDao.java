package com.shyl.msc.supervise.dao;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;

public interface IClinicRecipeItemDao extends IBaseDao<ClinicRecipeItem, Long> {
	
	public DataGrid<Map<String,Object>> queryByPage(PageRequest page);
}
