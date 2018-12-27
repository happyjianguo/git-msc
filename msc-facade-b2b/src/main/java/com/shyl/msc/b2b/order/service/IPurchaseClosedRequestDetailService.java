package com.shyl.msc.b2b.order.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequestDetail;

public interface IPurchaseClosedRequestDetailService extends IBaseService<PurchaseClosedRequestDetail, Long> {

	public List<PurchaseClosedRequestDetail> listByRequestId(@ProjectCodeFlag String projectCode, Long id);

	
}
