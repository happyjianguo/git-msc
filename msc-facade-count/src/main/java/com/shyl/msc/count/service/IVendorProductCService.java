package com.shyl.msc.count.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.VendorProductC;
import com.shyl.msc.dm.entity.Product;

public interface IVendorProductCService extends IBaseService<VendorProductC, Long> {
	/**
	 * 根据key找对象
	 * @param month
	 * @param productId
	 * @param vendorCode
	 * @return
	 */
	public VendorProductC getByKey(@ProjectCodeFlag String projectCode, String month, Long productId, String hospitalCode);
	/**
	 * 获得对象
	 * @param month
	 * @param product
	 * @param vendorCode
	 * @param vendorName
	 * @return
	 */
	public VendorProductC getVendorProductC(@ProjectCodeFlag String projectCode, String month, Product product, String vendorCode, String vendorName);
	/**
	 * 批量修改
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);
	
	/**
	 * 过账
	 * @param projectCode
	 */
	public void pass(@ProjectCodeFlag String projectCode);
}
