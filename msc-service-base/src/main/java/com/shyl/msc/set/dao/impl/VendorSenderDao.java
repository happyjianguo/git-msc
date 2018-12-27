package com.shyl.msc.set.dao.impl;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IVendorSenderDao;
import com.shyl.msc.set.entity.VendorSender;
/**
 * 产品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class VendorSenderDao extends BaseDao<VendorSender, Long> implements IVendorSenderDao {

	@Override
	public VendorSender findByKey(String vendorCode, String senderCode) {
		String hql = "from VendorSender where vendorCode=? and senderCode=? and isDisabled=0";
		return super.getByHql(hql, vendorCode,senderCode);
	}


}
