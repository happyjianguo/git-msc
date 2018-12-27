package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;

public interface IPurchaseClosedRequestDetailDao extends IBaseDao<PurchaseClosedRequestDetail, Long> {

	public List<PurchaseClosedRequestDetail> listByRequestId(Long id);

}
