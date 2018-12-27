package com.shyl.msc.supervise.service;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.supervise.entity.HisRegItem;

import java.util.Map;


public interface IHisRegItemService extends IBaseService<HisRegItem, Long> {
    DataGrid<Map<String,Object>> queryByPage(@ProjectCodeFlag String projectCode, PageRequest page);
}
