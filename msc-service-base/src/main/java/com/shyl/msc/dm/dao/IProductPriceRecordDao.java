package com.shyl.msc.dm.dao;

import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductPriceRecord;

public interface IProductPriceRecordDao extends IBaseDao<ProductPriceRecord,Long>{

	DataGrid<Map<String, Object>> getIdsForProductPriceMdf(PageRequest pageable);

	DataGrid<Map<String, Object>> getReportForProductPriceMdf(String ids, PageRequest pageable);

}
