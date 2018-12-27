package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.ICartDao;
import com.shyl.msc.b2b.order.entity.Cart;
/**
 * 购物车DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class CartDao extends BaseDao<Cart, Long> implements ICartDao {

	@Override
	public Cart findByHopitalAndGoods(String hospitalCode, Long goodsPriceId) {
		String hql = "from Cart c where c.hospitalCode=? and goodsPriceId=?";
		return super.getByHql(hql, hospitalCode, goodsPriceId);
	}
	
	@Override
	public List<Map<String, Object>> queryByHospital(String hospitalCode) {
		String sql = "select c.id,p.productCode as PRODUCTCode,c.num,g.pinyin,g.code,g.name,g.dosageFormName,y.fullname as vendorname,"
				+ "p.finalPrice,p.id as GOODSPRICEID,g.model, g.producerName,g.packDesc,g.unitName from t_order_cart c "
		        + " left join t_dm_goods_price p on c.goodsPriceId=p.id and p.isdisabled =0 and p.isdisabledByH=0 "
		        + " left join  t_dm_product g on p.productCode = g.code  "
		        + " left join  t_set_company y on p.vendorCode = y.code  "
		        + " where c.hospitalCode = ? ";
		return super.listBySql(sql, null, Map.class, hospitalCode);
	}

}
