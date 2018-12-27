package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
/**
 * 退货单明细Service接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsOrderDetailService extends IBaseService<ReturnsOrderDetail, Long> {

	/**
	 * 
	 * @param code
	 * @return
	 */
	public ReturnsOrderDetail findByCode(@ProjectCodeFlag String projectCode, String code);

	public List<ReturnsOrderDetail> listByReturnId(@ProjectCodeFlag String projectCode, Long returnId);

	public DataGrid<Map<String, Object>> pageByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<ReturnsOrderDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
}
