package com.shyl.msc.dm.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductPriceRecordHisDao;
import com.shyl.msc.dm.entity.ProductPriceRecordHis;
import com.shyl.msc.dm.service.IProductPriceRecordHisService;

@Service
@Transactional(readOnly=true)
public class ProductPriceRecordHisService extends BaseService<ProductPriceRecordHis,Long> implements IProductPriceRecordHisService{
	private IProductPriceRecordHisDao productPriceRecordHisDao;

	public IProductPriceRecordHisDao getProductPriceRecordHisDao() {
		return productPriceRecordHisDao;
	}

	@Resource
	public void setProductPriceRecordHisDao(IProductPriceRecordHisDao productPriceRecordHisDao) {
		this.productPriceRecordHisDao = productPriceRecordHisDao;
		super.setBaseDao(productPriceRecordHisDao);
	}

	@Override
	public Map<String, Object> getReportForProductPriceMdf(String projectCode, String productCode) {
		return productPriceRecordHisDao.getReportForProductPriceMdf(productCode);
	}

	@Override
	public List<ProductPriceRecordHis> listByVendorAndDate(String projectCode, String vendorCode, String productCode, String startDate, String endDate) {
		return productPriceRecordHisDao.listByVendorAndDate(vendorCode, productCode, startDate, endDate);
	}
}
