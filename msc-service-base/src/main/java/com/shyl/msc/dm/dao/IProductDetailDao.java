package com.shyl.msc.dm.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.dm.entity.ProductDetail;

public interface IProductDetailDao extends IBaseDao<ProductDetail, Long> {

	public ProductDetail getByKey(Long productId, String vendorCode, String hospitalCode);

	public ProductDetail getByKey3(String productCode, String vendorCode, String hospitalCode);

	public DataGrid<ProductDetail> page(PageRequest pageRequest);

	public ProductDetail getByKey2(Long productId, String hospitalCode);

	public List<ProductDetail> listByGPO(String gpoCode);
	
	public List<ProductDetail> listByVendor(String vendorCode);

	public List<ProductDetail> listByProductId(Sort sort, Long productId);
}
