package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
/**
 * 配送单DAO接口
 * 
 * @author a_Q
 *
 */
public interface IDeliveryOrderDao extends IBaseDao<DeliveryOrder, Long> {

	/**
	 * 
	 * @param code
	 * @return
	 */
	public DeliveryOrder findByCode(String code);

	/**
	 * 
	 * @param i
	 * @return
	 */
	public List<DeliveryOrder> listByIsPass(int isPass);

	public List<DeliveryOrder> listByPurchaseOrder(String code);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getNumByPurchaseCode(String code);

	/**
	 * 
	 * @param companyCode
	 * @param internalCode
	 * @param isGPO 
	 * @return
	 */
	public DeliveryOrder getByInternalCode(String companyCode, String internalCode, boolean isGPO);
	
	/**
	 * 根据医院ID和条形码查配送单
	 * @param hospitalCode
	 * @param psdtxm
	 * @return
	 */
	public DeliveryOrder listByHospitalAndBarcode(String hospitalCode, String barcode);
	
	/**
	 * 根据条形码取得配送单
	 * @param barcode
	 * @return
	 */
	public DeliveryOrder getByBarcode(String barcode);

	public DataGrid<Map<String, Object>> queryUninvoice(PageRequest pageable, String code);

	public DataGrid<DeliveryOrder> listByDeliveryOrderAndDetail(PageRequest pageable);

	public List<DeliveryOrder> listByDate (String hospitalCode, String startDate, String endDate);

}
