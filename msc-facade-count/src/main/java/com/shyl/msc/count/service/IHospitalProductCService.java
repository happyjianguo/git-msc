package com.shyl.msc.count.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.count.entity.HospitalProductC;
import com.shyl.msc.dm.entity.Product;

public interface IHospitalProductCService extends IBaseService<HospitalProductC, Long> {	
	/**
	 * 根据key找对象
	 * @param month
	 * @param productId
	 * @param hospitalCode
	 * @return
	 */
	public HospitalProductC getByKey(@ProjectCodeFlag String projectCode, String month, Long productId, String hospitalCode);
	/**
	 * 获得对象
	 * @param month
	 * @param product
	 * @param hospitalCode
	 * @param hospitalName
	 * @return
	 */
	public HospitalProductC getHospitalProductC(@ProjectCodeFlag String projectCode, String month, Product product, String hospitalCode, String hospitalName);
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
