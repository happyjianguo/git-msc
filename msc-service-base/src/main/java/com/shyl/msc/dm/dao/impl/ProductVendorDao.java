package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IProductVendorDao;
import com.shyl.msc.dm.entity.ProductVendor;

/**
 * 产品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ProductVendorDao extends BaseDao<ProductVendor, Long> implements IProductVendorDao {

	@Override
	public ProductVendor findByKey(String productCode,String vendorCode) {
		String hql = "from ProductVendor where productCode = ? and vendorCode = ? ";
		return super.getByHql(hql, productCode, vendorCode);
	}

	@Override
	public DataGrid<ProductVendor> queryByVendor(PageRequest pageable, String vendorCode) {
		String hql = "from ProductVendor t where  t.vendorCode = ? ";
		return super.query(hql, pageable, vendorCode);
	}

	@Override
	public DataGrid<Map<String, Object>> mapByVendor(PageRequest pageable, String vendorCode) {
		String sql = "select t.*,p.name as productname,p.PRODUCERNAME,p.MODEL,p.PACKDESC,c.fullname as vendorname from t_dm_product_vendor t "
				+ "left join t_dm_product p on t.productCode = p.code "
				+ "left join t_set_company c on t.vendorCode = c.code "
				+ "where t.isDisabled = 0 ";
		if(vendorCode != null){
			sql += " and t.vendorCode = '"+vendorCode+"'";
		}
		pageable.setSort(new Sort(new Order(Direction.ASC, "p.code")));
		return super.findBySql(sql, pageable, Map.class);
	}
	
	@Override
	public ProductVendor findByProduct(String productCode) {
		String hql = "from ProductVendor where productCode = ? and isDisabled = 0";
		return super.getByHql(hql, productCode);
	}
	
	@Override
	public DataGrid<Map<String, Object>> findByStatus(String vendorCode, Integer isDisabled,PageRequest pageable) {
		String sql = "select p.*,pg.id as selected from t_dm_product p "
				+ "left join t_dm_product_vendor pg on p.code = pg.productCode and pg.isDisabled=0 and pg.vendorCode=? "
				+ "where p.isDisabled = ?";
		pageable.setSort(new Sort(new Order(Direction.ASC, "p.code")));
		return super.findBySql(sql, pageable, Map.class, vendorCode,isDisabled);
	}

	@Override
	public List<Map<String, Object>> listByVendor(String vendorCode, String scgxsj) {
		
		String sql = "select p.CODE,p.GENERICNAME,p.NAME,p.ENGLISHNAME,p.TRADENAME,p.DOSAGEFORMNAME,p.drugType,"
				+ " p.MODEL,p.PRODUCERNAME,p.AUTHORIZENO,p.PACKAGEMATERIAL,p.UNITNAME,p.CONVERTRATIO,"
				+ " p.PACKDESC,p.NOTES,p.ISGPOPURCHASE,p.GPOID,p.isdisabled as isdisabled,pg.isdisabled as pg_isdisabled,p.importFileNo "
				+ " from t_dm_product_vendor pg"
				+ " left join t_dm_product p on pg.productCode=p.code "
				+ " where pg.vendorCode=? and pg.ISDISABLED=0 and (to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?"
				+ " or to_char(pg.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?)";
		
		return super.listBySql(sql, null, Map.class, vendorCode, scgxsj, scgxsj);
	}

	@Override
	public List<Map<String, Object>> listByGPO(Long gpoId, String scgxsj) {
		
		String sql = "select p.CODE,p.GENERICNAME,p.NAME,p.ENGLISHNAME,p.TRADENAME,p.DOSAGEFORMNAME,p.drugType,"
				+ " p.MODEL,p.PRODUCERNAME,p.AUTHORIZENO,p.PACKAGEMATERIAL,p.UNITNAME,p.CONVERTRATIO,"
				+ " p.PACKDESC,p.NOTES,p.isdisabled as isdisabled,pg.isdisabled as pg_isdisabled,p.importFileNo "
				+ " from t_dm_product_vendor pg"
				+ " left join t_dm_product p on pg.productCode=p.code "
				+ " where p.gpoId=? and pg.ISDISABLED=0 and (to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?"
				+ " or to_char(pg.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?)";
		
		return super.listBySql(sql, null, Map.class, gpoId, scgxsj, scgxsj);
	}

	@Override
	public List<Map<String, Object>> listByVendorAndCode(String vendorCode, String productCode) {
		String sql = "select p.CODE,p.GENERICNAME,p.NAME,p.ENGLISHNAME,p.TRADENAME,p.DOSAGEFORMNAME,p.drugType,"
				+ " p.MODEL,p.PRODUCERNAME,p.AUTHORIZENO,p.PACKAGEMATERIAL,p.UNITNAME,p.CONVERTRATIO,"
				+ " p.PACKDESC,p.NOTES,p.isdisabled as isdisabled,pg.isdisabled as pg_isdisabled,p.importFileNo "
				+ " from t_dm_product_vendor pg"
				+ " left join t_dm_product p on pg.productCode=p.code "
				+ " where pg.vendorCode=? and pg.ISDISABLED=0 and p.code=?";
		return super.listBySql(sql, null, Map.class, vendorCode, productCode);
	}
}
