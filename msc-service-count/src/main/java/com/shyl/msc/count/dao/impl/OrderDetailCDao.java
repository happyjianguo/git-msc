package com.shyl.msc.count.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IOrderDetailCDao;
import com.shyl.msc.count.entity.OrderDetailC;

@Repository
public class OrderDetailCDao extends BaseDao<OrderDetailC, Long> implements IOrderDetailCDao {

	@Override
	public OrderDetailC getByKey(String month, Long productId, String hospitalCode, String vendorCode){
		return super.getByHql("from OrderDetailC where month=? and product.id=? "
				+ "and hospitalCode=? and vendorCode=?", month, productId, hospitalCode, vendorCode);
	}
	
	@Override
	  public DataGrid<Map<String, Object>> reportCGoodsTradeMX(Long id, String dateS, String dateE, PageRequest pageable) {
	    System.out.println("7id = "+id);
		String sql = "select o.hospitalName,o.vendorName,o.createdate as CGSJ,o.purchaseNum as SL,o.purchaseSum as JE "
        +" from t_count_orderdetail_c o  JOIN t_dm_product d on o.productId = d.id" 
       +" where   d.id =? and to_char(o.createdate,'YYYYMM')>=? and to_char(o.createdate,'YYYYMM')<=? order by o.createdate desc,o.hospitalName,o.vendorName";
        
	    return super.findBySql(sql, pageable, Map.class,id, dateS, dateE);
	  }
}
