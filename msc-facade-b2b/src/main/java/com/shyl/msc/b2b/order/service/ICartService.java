package com.shyl.msc.b2b.order.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.order.entity.Cart;
import com.shyl.sys.entity.User;
/**
 * 购物车Service接口
 * 
 * @author a_Q
 *
 */
public interface ICartService extends IBaseService<Cart, Long> {

	public List<Map<String, Object>> queryByHospital(@ProjectCodeFlag String projectCode, String hospitalCode);
	
	/**
	 * 根据医院和药品取Cart
	 * @param hospitalCode
	 * @param goodsId
	 * @return
	 */
	public Cart findByHopitalAndGoods(@ProjectCodeFlag String projectCode, String hospitalCode, Long goodsId);
	
	/**
	 * 导入excel资料
	 * @param upExcel
	 * @param user 
	 * @return 
	 * @throws Exception 
	 */
	public String doExcelH(@ProjectCodeFlag String projectCode, String[][] upExcel, User user) throws Exception;
	
}
