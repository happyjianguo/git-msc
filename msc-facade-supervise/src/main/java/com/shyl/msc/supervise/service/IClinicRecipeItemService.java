package com.shyl.msc.supervise.service;


import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;


public interface IClinicRecipeItemService extends IBaseService<ClinicRecipeItem, Long> {
	
	public DataGrid<Map<String,Object>> queryByPage(@ProjectCodeFlag String projectCode,PageRequest page);
}
