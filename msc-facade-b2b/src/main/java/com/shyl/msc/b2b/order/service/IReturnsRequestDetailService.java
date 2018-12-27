package com.shyl.msc.b2b.order.service;

import java.util.List;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
/**
 * 退货申请单明细Service接口
 * 
 * @author a_Q
 *
 */
public interface IReturnsRequestDetailService extends IBaseService<ReturnsRequestDetail, Long> {

	public List<ReturnsRequestDetail> listByPid(@ProjectCodeFlag String projectCode, Long id);

	
	public ReturnsRequestDetail findByCode(@ProjectCodeFlag String projectCode, String code);

}
