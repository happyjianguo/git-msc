package com.shyl.msc.b2b.order.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.entity.InOutBound;
import com.shyl.sys.entity.User;
/**
 * 出入库单Service接口
 * 
 * @author a_Q
 *
 */
public interface IInOutBoundService extends IBaseService<InOutBound, Long> {
	/**
	 * 保存出入库单
	 * @param inOutBound
	 */
	public void saveInOutBound(@ProjectCodeFlag String projectCode, InOutBound inOutBound);

	/**
	 * 需求管理中确认收货
	 * @param fastjson
	 * @param user
	 * @return 
	 */
	public List<InOutBound> takeInOutBound(@ProjectCodeFlag String projectCode, List<DeliveryOrderDetail> fastjson, User user);

	/**
	 * 
	 * @param hospitalCode
	 * @param cxksrq
	 * @param cxjsrq
	 * @param isGPO 
	 * @return
	 */
	public List<InOutBound> listByDate(@ProjectCodeFlag String projectCode, String companyCode, String cxksrq, String cxjsrq, boolean isGPO);

	/**
	 * 
	 * @param hospitalCode
	 * @param internalCode
	 * @return
	 */
	public InOutBound getByInternalCode(@ProjectCodeFlag String projectCode, String hospitalCode, String internalCode);

	public void saveInOutBound(@ProjectCodeFlag String projectCode, List<JSONObject> list, InOutBound inOutBound);

	public InOutBound findByCode(@ProjectCodeFlag String projectCode, String code);

	public DataGrid<InOutBound> listByInOutBoundAndDetail(PageRequest pageable);
	
	/**
	 * 过账列表
	 * @param isPass
	 * @return
	 */
	public List<InOutBound> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
}
