package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.ISicknessProductDao;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.SicknessProduct;

/**
 * 
 * 
 * @author a_Q
 *
 */
@Repository
public class SicknessProductDao extends BaseDao<SicknessProduct, Long> implements ISicknessProductDao {

	@Override
	public List<Product> listProductBySicknessCode(String sicknessCode) {
		String sql = "select p.* from T_DM_SICKNESSPRODUCT sp "
				+ " left join T_DM_PRODUCT p on sp.productCode=p.code "
				+ " where sp.SICKNESSCODE=?";
		return super.listBySql(sql, null, Product.class, sicknessCode);
	}

	@Override
	public SicknessProduct findByProductCode(String productCode) {
		String hql = "from SicknessProduct where productCode=? and rownum=1";
		return super.getByHql(hql, productCode);
	}

	@Override
	public DataGrid<Product> pageBySicknessCode(PageRequest pageable, String sicknessCode) {
		String sql = "select p.* from T_DM_SICKNESSPRODUCT sp "
				+ " left join T_DM_PRODUCT p on sp.productCode=p.code "
				+ " where sp.SICKNESSCODE=?";
		return super.findBySql(sql, pageable, Product.class, sicknessCode);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductWithSelected(PageRequest pageable, String sicknessCode) {
		String sql = "select p.*,sp.id as selected from "
				+ " t_dm_product p left join T_DM_SICKNESSPRODUCT sp on sp.productCode=p.code and sp.sicknessCode=? "
				+ " where p.isDisabled=0 ";
		return super.findBySql(sql, pageable, Map.class, sicknessCode);
	}

	@Override
	public SicknessProduct findByKey(String sicknessCode, String productCode) {
		String hql = "from SicknessProduct where sicknessCode=? and productCode=?";
		return super.getByHql(hql, sicknessCode, productCode);
	}
}
