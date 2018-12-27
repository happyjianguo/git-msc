package com.shyl.msc.b2b.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;

/**
 * 配送单Service接口
 * 
 * @author a_Q
 *
 */
public interface IDeliveryOrderService extends IBaseService<DeliveryOrder, Long> {

	
	/**
	 * 根据编号查配送单
	 * @param code
	 * @return
	 */
	public DeliveryOrder findByCode(@ProjectCodeFlag String projectCode, String code);
	
	/**
	 * 保存配送单
	 * @param deliverOrder
	 */
	public void saveDeliveryOrder(@ProjectCodeFlag String projectCode, DeliveryOrder deliverOrder);

	/**
	 * 
	 * @param vendorCode
	 * @param internalCode
	 * @param isGPO 
	 * @return
	 */
	public DeliveryOrder getByInternalCode(@ProjectCodeFlag String projectCode, String vendorCode, String internalCode, boolean isGPO);
	
	/**
	 * 根据条形码取得配送单
	 * @param barcode
	 * @return
	 */
	public DeliveryOrder getByBarcode(@ProjectCodeFlag String projectCode, String barcode);

	/**
	 * 生成配送单
	 * @param orderId
	 * @param senderCode 
	 * @param internalCode 
	 * @param detailIds
	 * @param nums
	 * @param expiryDates 
	 * @param batchDates 
	 * @param batchCodes 
	 * @param inspectionReportUrls 
	 * @param qualityRecords 
	 * @return
	 */
	public String mkdelivery(@ProjectCodeFlag String projectCode, Long orderId, String senderCode, String barcode, Long[] detailIds, BigDecimal[] nums, String[] batchCodes, String[] batchDates, String[] expiryDates, String[] qualityRecords, String[] inspectionReportUrls);

	/**
	 * 根据医院和条形码查配送单
	 * @param hospitalCode
	 * @param psdtxm
	 * @return
	 */
	public DeliveryOrder listByHospitalAndBarcode(@ProjectCodeFlag String projectCode, String hospitalCode, String barcode);

	/**
	 * 未开票的订单
	 * @param pageable
	 * @param code
	 * @return
	 */
	public DataGrid<Map<String, Object>> queryUninvoice(@ProjectCodeFlag String projectCode, PageRequest pageable, String code);

	public DataGrid<DeliveryOrder> listByDeliveryOrderAndDetail(PageRequest pageable);

	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<DeliveryOrder> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);

	/**
	 * 配送单清单
	 * @param projectCode
	 * @param hospitalCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<DeliveryOrder> listByDate(@ProjectCodeFlag String projectCode,String hospitalCode,String startDate,String endDate);

	void returnDeliveryBack (@ProjectCodeFlag String projectCode,DeliveryOrder deliveryOrder,List<DeliveryOrderDetail> deliveryOrderDetails);
}
