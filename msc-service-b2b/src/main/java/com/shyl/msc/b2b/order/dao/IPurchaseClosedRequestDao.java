package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest.ClosedType;

public interface IPurchaseClosedRequestDao extends IBaseDao<PurchaseClosedRequest, Long> {

	public PurchaseClosedRequest findByPurchaseOrderCode(String code);

	public List<PurchaseClosedRequest> listByDate(String vendorCode, String startDate, String endDate, boolean isGPO);

	public PurchaseClosedRequest findByCode(String code);

	public PurchaseClosedRequest findByPurchaseOrderCode(String code, ClosedType closedType);

}
