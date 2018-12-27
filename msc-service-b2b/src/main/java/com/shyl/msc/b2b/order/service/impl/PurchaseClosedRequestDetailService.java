package com.shyl.msc.b2b.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IPurchaseClosedRequestDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;
import com.shyl.msc.b2b.order.service.IPurchaseClosedRequestDetailService;

@Service
@Transactional(readOnly = true)
public class PurchaseClosedRequestDetailService extends BaseService<PurchaseClosedRequestDetail, Long>
		implements IPurchaseClosedRequestDetailService {

	private IPurchaseClosedRequestDetailDao purchaseClosedRequestDetailDao;

	public IPurchaseClosedRequestDetailDao getPurchaseClosedRequestDetailDao() {
		return purchaseClosedRequestDetailDao;
	}

	@Resource
	public void setPurchaseClosedRequestDetailDao(IPurchaseClosedRequestDetailDao purchaseClosedRequestDetailDao) {
		this.purchaseClosedRequestDetailDao = purchaseClosedRequestDetailDao;
		super.setBaseDao(purchaseClosedRequestDetailDao);
	}

	@Override
	public List<PurchaseClosedRequestDetail> listByRequestId(String projectCode, Long id) {
		return purchaseClosedRequestDetailDao.listByRequestId(id);
	}
	
}
