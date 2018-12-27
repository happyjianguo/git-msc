package com.shyl.msc.count.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.VendorC;

public interface IVendorCService extends IBaseService<VendorC, Long> {
	/**
	 * 获得对象
	 * @param month
	 * @param vendorCode
	 * @param vendorName
	 * @return
	 */
	public VendorC getVendorC(@ProjectCodeFlag String projectCode, String month, String vendorCode, String vendorName);
	/**
	 * 批量修改
	 * @param projectCode
	 */
	public void updateBatch(String projectCode);
}
