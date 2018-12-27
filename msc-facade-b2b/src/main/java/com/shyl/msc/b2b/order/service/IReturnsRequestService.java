package com.shyl.msc.b2b.order.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
import com.shyl.sys.entity.User;
/**
 * 退货申请单Service接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsRequestService extends IBaseService<ReturnsRequest, Long> {

	/**
	 * 医院退货申请
	 * @param deliveryId
	 * @param detailIds
	 * @param nums
	 * @param reasons
	 * @param user
	 * @return
	 */
	public String mkreturn(@ProjectCodeFlag String projectCode, String data, User user);

	/**
	 * 
	 * @param vendorCode
	 * @param startDate
	 * @param endDate
	 * @param isGPO 
	 * @return
	 */
	public List<ReturnsRequest> listByDate(@ProjectCodeFlag String projectCode, String vendorCode, String startDate, String endDate, boolean isGPO);

	/**
	 * 
	 * @param thsqdbh
	 * @return
	 */
	public ReturnsRequest findByCode(@ProjectCodeFlag String projectCode, String thsqdbh);

	public void saveReturnsRequest(@ProjectCodeFlag String projectCode, ReturnsRequest returnsRequest);

	public void udateReturnsRequest(@ProjectCodeFlag String projectCode, ReturnsRequest returnsRequest);

	public void saveReturnsRequest(@ProjectCodeFlag String projectCode, ReturnsRequest returnsRequest, List<JSONObject> list);
	public DataGrid<ReturnsRequest> listByReturnsRequestAndDetail(PageRequest pageable);
}
