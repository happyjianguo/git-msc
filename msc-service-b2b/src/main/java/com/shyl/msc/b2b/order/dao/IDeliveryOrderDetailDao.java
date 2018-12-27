package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
/**
 * 配送单明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IDeliveryOrderDetailDao extends IBaseDao<DeliveryOrderDetail, Long> {

	/**
	 * 
	 * @param code
	 * @return
	 */
	public DeliveryOrderDetail findByCode(String code);

	public DataGrid<DeliveryOrderDetail> pageNotCloseByHospital(PageRequest pageable,String hospitalCode);

	/**
	 * 根据配送单查明细列表
	 * @param id
	 * @return
	 */
	public List<DeliveryOrderDetail> listByDeliveryOrder(Long id);

	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<DeliveryOrderDetail> listByIsPass(int isPass);

	/**
	 * 分页查询
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<DeliveryOrderDetail> pageByHospital(PageRequest pageable, String hospitalCode);
	
	/**
	 * 分页查询(收货)
	 * @param pageable
	 * @param hospitalCode
	 * @return
	 */
	public DataGrid<DeliveryOrderDetail> pageByHospitalNotIn(PageRequest pageable, String hospitalCode);

	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(PageRequest pageable);

	public DataGrid<DeliveryOrderDetail> queryByCode(PageRequest pageable);

	public List<DeliveryOrderDetail> listByDeliveryOrderId(Long deliveryOrderId);
}
