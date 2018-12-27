package com.shyl.msc.supervise.dao;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.supervise.entity.HisRegItem;

import java.util.Map;

public interface IHisRegItemDao extends IBaseDao<HisRegItem, Long> {
    DataGrid<Map<String,Object>> queryByPage(PageRequest page);
}
