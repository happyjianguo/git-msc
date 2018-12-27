package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.CartContract;
/**
 * 购物车DAO接口
 * 
 * @author a_Q
 *
 */
public interface ICartContractDao extends IBaseDao<CartContract, Long> {

	CartContract findByHopitalAndGoods(String hospitalCode, Long contractDId);

	List<CartContract> listByHospital(String hospitalCode);

	List<Map<String, Object>> vendorList(String hospitalCode);

	DataGrid<Map<String, Object>> queryByHospital(String hospitalCode, PageRequest pageable);

	Map<String, Object> getTotal(String orgCode);

}
