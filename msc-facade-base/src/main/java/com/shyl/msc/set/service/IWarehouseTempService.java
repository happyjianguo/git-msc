package com.shyl.msc.set.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.WarehouseTemp;

public interface IWarehouseTempService extends IBaseService<WarehouseTemp, Long> {

	JSONArray saveWarehouse(@ProjectCodeFlag String projectCode, JSONObject jObject);

}
