package com.shyl.msc.set.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IVendorSenderDao;
import com.shyl.msc.set.entity.VendorSender;
import com.shyl.msc.set.service.IVendorSenderService;
/**
 * 产品GPOService实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class VendorSenderService extends BaseService<VendorSender, Long> implements IVendorSenderService {
	private IVendorSenderDao vendorSenderDao;

	public IVendorSenderDao getVendorSenderDao() {
		return vendorSenderDao;
	}

	@Resource
	public void setVendorSenderDao(IVendorSenderDao vendorSenderDao) {
		this.vendorSenderDao = vendorSenderDao;
		super.setBaseDao(vendorSenderDao);
	}

	@Override
	public VendorSender findByKey(String projectCode, String vendorCode, String senderCode) {
		return vendorSenderDao.findByKey(vendorCode, senderCode);
	}
	

}
