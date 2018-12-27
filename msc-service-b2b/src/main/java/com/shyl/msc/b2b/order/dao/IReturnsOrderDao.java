package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.ReturnsOrder;
/**
 * 退货单DAO接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsOrderDao extends IBaseDao<ReturnsOrder, Long> {

	List<ReturnsOrder> listByIsPass(int isPass);

	/**
	 * 
	 * @param code
	 * @return
	 */
	public ReturnsOrder findByCode(String code);

	/**
	 * 
	 * @param companyCode
	 * @param internalCode
	 * @param isGPO 
	 * @return
	 */
	ReturnsOrder getByInternalCode(String companyCode, String internalCode, boolean isGPO);

	ReturnsOrder findByRequestCode(String requestCode);

	DataGrid<ReturnsOrder> listByReturnsOrderAndDetail(PageRequest pageable);

}
