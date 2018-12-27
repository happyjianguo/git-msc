package com.shyl.msc.dm.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;

public interface IProductPriceRecordHisService extends IBaseService<ProductPriceRecordHis,Long>{

	Map<String, Object> getReportForProductPriceMdf(@ProjectCodeFlag String projectCode, String productCode);
	List<ProductPriceRecordHis> listByVendorAndDate(@ProjectCodeFlag String projectCode, String vendorCode, String productCode, String startDate, String endDate);
}
