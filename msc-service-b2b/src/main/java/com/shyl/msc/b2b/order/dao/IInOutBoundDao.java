package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.InOutBound;
/**
 * 出入库单DAO接口
 * 
 * @author a_Q
 *
 */
public interface IInOutBoundDao extends IBaseDao<InOutBound, Long> {

	public List<InOutBound> listByIsPass(int isPass);

	public List<InOutBound> listByDate(String companyCode, String startDate, String endDate, boolean isGPO);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getNumByDeliveryCode(String id);

	/**
	 * 
	 * @param hospitalId
	 * @param internalCode
	 * @return
	 */
	public InOutBound getByInternalCode(String hospitalCode, String internalCode);

	public InOutBound findByCode(String code);

	public DataGrid<InOutBound> listByInOutBoundAndDetail(PageRequest pageable);

}
