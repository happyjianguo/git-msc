package com.shyl.msc.dm.service;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductPriceRecord;

public interface IProductPriceRecordService extends IBaseService<ProductPriceRecord,Long>{

	DataGrid<Map<String, Object>> getIdsForProductPriceMdf(@ProjectCodeFlag String projectCode, PageRequest pageable);

	DataGrid<Map<String, Object>> getReportForProductPriceMdf(@ProjectCodeFlag String projectCode, String ids, PageRequest pageable);

}
