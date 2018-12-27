package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IPrescriptProductDao;
import com.shyl.msc.dm.entity.PrescriptProduct;


/**
 * 
 * 
 * @author a_Q
 *
 */
@Repository
public class PrescriptProductDao extends BaseDao<PrescriptProduct, Long> implements IPrescriptProductDao {

	@Override
	public List<PrescriptProduct> listProductByModifyDate(String modifyDate) {
		String hql = "from PrescriptProduct p  where to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=? and "
				+ "to_char(p.product.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=? and p.product.isDisabled=0";
		return super.listByHql(hql, null, modifyDate, modifyDate);
	}

	@Override
	public PrescriptProduct findByProductId(Long productId) {
		String hql = "from PrescriptProduct where product.id=? ";
		return super.getByHql(hql, productId);
	}
	
	@Override
	public DataGrid<Map<String, Object>> pageByProductWithSelected(PageRequest pageable) {
		String sql = "select p.*,pp.id as selected from "
				+ " t_dm_product p left join T_DM_PRESCRIPTPRODUCT pp on pp.productId=p.id  "
				+ " where p.isDisabled=0 ";
		return super.findBySql(sql, pageable, Map.class);
	}

}
