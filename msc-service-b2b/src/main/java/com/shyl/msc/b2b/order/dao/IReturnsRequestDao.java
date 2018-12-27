package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;
/**
 * 退货申请单DAO接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsRequestDao extends IBaseDao<ReturnsRequest, Long> {

	public List<ReturnsRequest> listByDate(String vendorCode, String startDate, String endDate, boolean isGPO);

	public ReturnsRequest findByCode(String code);

	public DataGrid<ReturnsRequest> listByReturnsRequestAndDetail(PageRequest pageable);

}
