package com.shyl.msc.b2b.order.dao;

import java.util.List;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
/**
 * 退货申请单明细DAO接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsRequestDetailDao extends IBaseDao<ReturnsRequestDetail, Long> {

	public List<ReturnsRequestDetail> listById(Long id);

	public List<ReturnsRequestDetail> listByPid(Long id);

	public ReturnsRequestDetail findByCode(String code);

}
