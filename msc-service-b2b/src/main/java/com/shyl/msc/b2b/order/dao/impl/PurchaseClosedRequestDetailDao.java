package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IPurchaseClosedRequestDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;

@Repository
public class PurchaseClosedRequestDetailDao extends BaseDao<PurchaseClosedRequestDetail, Long> implements IPurchaseClosedRequestDetailDao {

	@Override
	public List<PurchaseClosedRequestDetail> listByRequestId(Long id) {
		String hql = "from PurchaseClosedRequestDetail crd where crd.purchaseClosedRequest.id=? ";
		return listByHql(hql, null, id);
	}


}
