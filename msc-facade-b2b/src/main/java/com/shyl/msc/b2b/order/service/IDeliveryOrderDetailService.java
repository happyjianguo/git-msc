package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
/**
 * 配送单明细Service接口
 * 
 * @author a_Q
 *
 */
public interface IDeliveryOrderDetailService extends IBaseService<DeliveryOrderDetail, Long> {

	/**
	 * 
	 * @param psdmxbh
	 * @return
	 */
	public DeliveryOrderDetail findByCode(@ProjectCodeFlag String projectCode, String psdmxbh);

	/**
	 * 根据医院查配送单明细
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<DeliveryOrderDetail> pageNotCloseByHospital(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode);

	/**
	 * 根据配送单查明细列表
	 * @param id
	 * @return
	 */
	public List<DeliveryOrderDetail> listByDeliveryOrder(@ProjectCodeFlag String projectCode, Long id);

	/**
	 * 
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<DeliveryOrderDetail> pageByHospital(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode);
	
	/**
	 * 分页查询(收货)
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<DeliveryOrderDetail> pageByHospitalNotIn(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode);

	public DataGrid<Map<String, Object>> pageByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<DeliveryOrderDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);

	public DataGrid<DeliveryOrderDetail> queryByCode(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<DeliveryOrderDetail> listByDeliveryOrderId(@ProjectCodeFlag String projectCode,Long id);

}
