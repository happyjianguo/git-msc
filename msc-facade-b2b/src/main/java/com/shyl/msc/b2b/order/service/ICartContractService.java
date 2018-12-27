package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.CartContract;
import com.shyl.sys.entity.User;
/**
 * 购物车Service接口
 * 
 * @author a_Q
 *
 */
public interface ICartContractService extends IBaseService<CartContract, Long> {

	CartContract findByHopitalAndGoods(@ProjectCodeFlag String projectCode, String hospitalCode, Long contractDId);

	void mkCartContract(@ProjectCodeFlag String projectCode, Integer goodsNum, Long contractDId, User user) throws Exception;

	List<CartContract> listByHospital(@ProjectCodeFlag String projectCode, String hospitalCode);

	List<Map<String, Object>> vendorList(@ProjectCodeFlag String projectCode, String orgCode);

	void delete(@ProjectCodeFlag String projectCode, Long[] cartids);

	String doExcelH(@ProjectCodeFlag String projectCode, String[][] upExcel, User user) throws Exception;

	/**
	 * 分页查询
	 * @param orgCode
	 * @param pageable
	 * @return
	 */
	DataGrid<Map<String, Object>> queryByHospital(@ProjectCodeFlag String projectCode, String orgCode, PageRequest pageable);

	/**
	 * 购物车统计
	 * @param orgCode
	 * @return
	 */
	Map<String, Object> getTotal(@ProjectCodeFlag String projectCode, String orgCode);

}
