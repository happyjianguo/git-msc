package com.shyl.msc.dm.service;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.dm.entity.ProductDetail;
import com.shyl.sys.entity.User;

public interface IProductDetailService extends IBaseService<ProductDetail, Long> {

	public ProductDetail getByKey(@ProjectCodeFlag String projectCode, Long productId, String vendorCode, String hospitalCode);

	/**
	 * 每家医院某个药品，只有一个供应商，只有一个价格
	 * @param productId
	 * @param hospitalCode
	 * @return
	 */
	public ProductDetail getByKey2(@ProjectCodeFlag String projectCode, Long productId, String hospitalCode);

	public ProductDetail getByKey3(@ProjectCodeFlag String projectCode, String productCode, String vendorCode, String hospitalCode);
	
	public DataGrid<ProductDetail> page(@ProjectCodeFlag String projectCode, PageRequest pageRequest);

	public String importExcel(@ProjectCodeFlag String projectCode, String[][] upExcel, User user);

	public List<ProductDetail> listByGPO(@ProjectCodeFlag String projectCode, String gpoCode);
	
	public List<ProductDetail> listByVendor(@ProjectCodeFlag String projectCode, String vendorCode);

	public List<ProductDetail> listByProductId(Sort sort, Long productId);
}
