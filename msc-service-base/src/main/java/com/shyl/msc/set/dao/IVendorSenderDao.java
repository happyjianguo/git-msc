package com.shyl.msc.set.dao;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.VendorSender;
/**
 * 产品GPODAO接口
 * 
 * @author a_Q
 *
 */
public interface IVendorSenderDao extends IBaseDao<VendorSender, Long> {

	VendorSender findByKey(String vendorCode, String senderCode);


}
