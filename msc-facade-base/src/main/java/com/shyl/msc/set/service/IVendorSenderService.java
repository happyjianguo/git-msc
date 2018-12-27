package com.shyl.msc.set.service;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.VendorSender;
/**
 * GPO配送商Service接口
 * 
 * @author a_Q
 *
 */
public interface IVendorSenderService extends IBaseService<VendorSender, Long> {

	/**
	 * 根据键值获取
	 * @param vendorCode
	 * @param senderCode
	 * @return
	 */
	VendorSender findByKey(@ProjectCodeFlag String projectCode, String vendorCode, String senderCode);

	
}
