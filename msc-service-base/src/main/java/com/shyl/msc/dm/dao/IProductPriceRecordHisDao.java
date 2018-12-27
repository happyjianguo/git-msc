package com.shyl.msc.dm.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;

public interface IProductPriceRecordHisDao extends IBaseDao<ProductPriceRecordHis,Long>{

	Map<String, Object> getReportForProductPriceMdf(String productCode);
	List<ProductPriceRecordHis> listByVendorAndDate(String vendorCode, String productCode, String startDate, String endDate);
}
