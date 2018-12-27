package com.shyl.msc.b2b.order.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.order.entity.Cart;
/**
 * 购物车DAO接口
 * 
 * @author a_Q
 *
 */
public interface ICartDao extends IBaseDao<Cart, Long> {

	public Cart findByHopitalAndGoods(String hospitalCode, Long goodsId);
	List<Map<String, Object>> queryByHospital(String hospitalCode);
}
