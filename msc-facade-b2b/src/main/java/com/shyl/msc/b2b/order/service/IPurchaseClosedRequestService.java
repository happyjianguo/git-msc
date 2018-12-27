package com.shyl.msc.b2b.order.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest;
import com.shyl.msc.b2b.order.entity.PurchaseClosedRequest.ClosedType;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.dto.Message;
import com.shyl.sys.entity.User;

public interface IPurchaseClosedRequestService extends IBaseService<PurchaseClosedRequest, Long> {

	public PurchaseClosedRequest findByPurchaseOrderCode(@ProjectCodeFlag String projectCode, String code);

	public List<PurchaseClosedRequest> listByDate(@ProjectCodeFlag String projectCode, String vendorCode, String startDate, String endDate, boolean isGPO);

	public PurchaseClosedRequest findByCode(@ProjectCodeFlag String projectCode, String ddjasqdbh);

	public void saveRequest(@ProjectCodeFlag String projectCode,PurchaseClosedRequest purchaseClosedRequest);

	public JSONArray getToGPO(@ProjectCodeFlag String projectCode, Company company, boolean isGPO, Boolean isCode, JSONObject jObject) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException;

	public void closeCommit(@ProjectCodeFlag String projectCode, Long id, String status, String reply);

	public PurchaseClosedRequest findByPurchaseOrderCode(@ProjectCodeFlag String projectCode, String code, ClosedType closedType);

	public Message savePurchaseRequest(@ProjectCodeFlag String projectCode, Long id, String reason, String data, User user);

}
