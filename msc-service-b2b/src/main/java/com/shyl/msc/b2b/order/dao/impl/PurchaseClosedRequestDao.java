package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IPurchaseClosedRequestDao;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest.ClosedType;

@Repository
public class PurchaseClosedRequestDao extends BaseDao<PurchaseClosedRequest, Long> implements IPurchaseClosedRequestDao {

	@Override
	public PurchaseClosedRequest findByPurchaseOrderCode(String code) {
		String hql = "from PurchaseClosedRequest where purchaseOrderCode=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<PurchaseClosedRequest> listByDate(String vendorCode, String startDate, String endDate, boolean isGPO) {
		String hql = "";
		if(isGPO)
			hql = "from PurchaseClosedRequest po where po.gpoCode=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		else
			hql = "from PurchaseClosedRequest po where po.vendorCode=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		return listByHql(hql, null, vendorCode, startDate, endDate);
	}

	@Override
	public PurchaseClosedRequest findByCode(String code) {
		String hql = "from PurchaseClosedRequest where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public PurchaseClosedRequest findByPurchaseOrderCode(String code, ClosedType closedType) {
		String hql = "from PurchaseClosedRequest where purchaseOrderCode=? and closedType=?";
		return super.getByHql(hql, code, closedType);
	}

}
