package com.shyl.msc.dm.dao.impl;

import java.util.List;

import com.shyl.common.entity.Sort;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IProductDetailDao;
import com.shyl.msc.dm.entity.ProductDetail;

@Repository
public class ProductDetailDao extends BaseDao<ProductDetail, Long> implements IProductDetailDao {

	@Override
	public ProductDetail getByKey(Long productId, String vendorCode, String hospitalCode) {
		String hql = "from ProductDetail dv where dv.product.id=? and dv.vendorCode=? and dv.hospitalCode=?";
		return super.getByHql(hql, productId, vendorCode, hospitalCode);
	}
	@Override
	public ProductDetail getByKey3(String productCode, String vendorCode, String hospitalCode) {
		String hql = "from ProductDetail dv where dv.product.code=? and dv.vendorCode=? and dv.hospitalCode=?";
		return super.getByHql(hql, productCode, vendorCode, hospitalCode);
	}

	@Override
	public DataGrid<ProductDetail> page(PageRequest pageRequest) {
		String hql = "from ProductDetail ";
		return super.query(hql, pageRequest);
	}

	@Override
	public ProductDetail getByKey2(Long productId, String hospitalCode) {
		String hql = "from ProductDetail dv where dv.product.id=? and dv.hospitalCode=? and rownum=1";
		return super.getByHql(hql, productId, hospitalCode);
	}

	@Override
	public List<ProductDetail> listByGPO(String gpoCode){
		return super.listByHql("from ProductDetail where product.gpoCode=?", null, gpoCode);
	}

	@Override
	public List<ProductDetail> listByProductId(Sort sort, Long productId){
		return super.listByHql("from ProductDetail where product.id=? and rownum < 10", sort, productId);
	}

	@Override
	public List<ProductDetail> listByVendor(String vendorCode){
		return super.listByHql("from ProductDetail where vendorCode=?", null, vendorCode);
	}
}
