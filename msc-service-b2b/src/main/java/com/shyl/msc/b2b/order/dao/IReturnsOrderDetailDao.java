package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
/**
 * 退货单明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsOrderDetailDao extends IBaseDao<ReturnsOrderDetail, Long> {

	/**
	 * 
	 * @param isPass
	 * @return
	 */
	public List<ReturnsOrderDetail> listByIsPass(int isPass);

	/**
	 * 
	 * @param code
	 * @return
	 */
	public ReturnsOrderDetail findByCode(String code);

	public List<ReturnsOrderDetail> listByReturnId(Long returnId);

	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(PageRequest pageable);

}
