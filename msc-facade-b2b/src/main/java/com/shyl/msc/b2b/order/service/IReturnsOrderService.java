package com.shyl.msc.b2b.order.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
import com.shyl.sys.entity.User;
/**
 * 退货单Service接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsOrderService extends IBaseService<ReturnsOrder, Long> {
	/**
	 * 保存退货单
	 * @param returnsOrder
	 */
	public void saveReturnsOrder(@ProjectCodeFlag String projectCode, ReturnsOrder returnsOrder);

	/**
	 * 
	 * @param stdbh
	 * @return
	 */
	public ReturnsOrder findByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 
	 * @param vendorCode
	 * @param internalCode
	 * @param isGPO 
	 * @return
	 */
	public ReturnsOrder getByInternalCode(@ProjectCodeFlag String projectCode, String vendorCode, String internalCode, boolean isGPO);

	/**
	 * 供应商退货
	 * @param orderId
	 * @param detailIds
	 * @param nums
	 * @param reasons 
	 * @param user 
	 * @return
	 */
	public String mkreturn(@ProjectCodeFlag String projectCode, Long orderId, Long[] detailIds, BigDecimal[] nums, String[] reasons, User user);

	/**
	 * 退货审核 同意
	 * @param returnrequestId
	 * @param explain 
	 * @return
	 */
	public String mkreturn(@ProjectCodeFlag String projectCode, Long returnrequestId, String explain, String data);

	public ReturnsOrder findByRequestCode(@ProjectCodeFlag String projectCode, String requestCode);

	public JSONArray saveReturnsOrder(@ProjectCodeFlag String projectCode, ReturnsOrder returnsOrder, List<JSONObject> arr);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<ReturnsOrder> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	
	
	public DataGrid<ReturnsOrder> listByReturnsOrderAndDetail(@ProjectCodeFlag String projectCode, PageRequest pageable);

}
