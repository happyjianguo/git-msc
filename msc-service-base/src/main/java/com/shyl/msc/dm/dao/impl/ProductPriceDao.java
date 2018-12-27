package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IProductPriceDao;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.enmu.TradeType;
/**
 * 产品价格DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
@Transactional(readOnly = true)
public class ProductPriceDao extends BaseDao<ProductPrice, Long> implements IProductPriceDao {

	@Override
	public DataGrid<ProductPrice> pageByGPO(PageRequest pageable,String vendorCode) {
		String flag = "";
		if(pageable.getQuery()!=null){
			String name = (String)pageable.getQuery().get("G#NAME_M_LK");
			if(!name.equals("")){
				flag = "and (g.productname like '%"+name+"%')";
			}
		}
		String sql = "select g.id,g.productname,g.biddingPrice from dm_productPrice g where g.vendorCode=?" + flag;
		return super.findBySql(sql, pageable, Map.class, vendorCode);
	}

	@Override
	public DataGrid<ProductPrice> pageByEnabled(PageRequest pageable) {
		String flag = "";
		if(pageable.getQuery()!=null){
			String name = (String)pageable.getQuery().get("G#NAME_M_LK");
			if(!StringUtils.isEmpty(name)){
				flag = " where  (g.productname like '%"+name+"%')";
			}
		}
		String sql = "select g.id,g.productname,g.biddingPrice from dm_productPrice g " + flag;
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public ProductPrice getByCode(String code) {
		String hql = "from ProductPrice g where g.productcode=? and g.hospitalCode is null and g.tradeType=? and  g.isDisabled=0";
		return super.getByHql(hql, code, TradeType.hospital);
	}

	@Override
	public ProductPrice getByProduct(String productCode,TradeType type) {
		String hql = "from ProductPrice where productCode=? and isDisabled=0 and hospitalCode is null and tradeType=?";
		return super.getByHql(hql, productCode,type);
	}

	@Override
	public ProductPrice getByProductAndHospital(String hospitalCode, String productCode) {
		String hql = "from ProductPrice where hospitalCode=? and productCode=? and isDisabled=0 and tradeType=? ";
		return super.getByHql(hql, hospitalCode,productCode,TradeType.hospital);
	}

	@Override
	public DataGrid<ProductPrice> queryByProductAndGpo(String productCode, String vendorCode, PageRequest pageable) {
		String hql = "from ProductPrice where productCode=? and vendorCode=? order by modifyDate desc ";
		return super.query(hql, pageable, productCode, vendorCode);
		
	}

	@Override
	public ProductPrice getPatientPrice(String productCode, String vendorCode) {
		String hql = "from ProductPrice where productCode=? and vendorCode=? and tradeType=?  ";
		return super.getByHql(hql, null, productCode,vendorCode,TradeType.patient);
	}

	@Override
	public List<ProductPrice> effectList(String today) {
		String hql = "from ProductPrice where beginDate<=?  and isEffected=0 order by createDate desc ";
		return super.listByHql(hql, null, today);
	}

	@Override
	public ProductPrice findByKey(String productCode, String vendorCode, String hospitalCode, TradeType tradeType) {
		String hql = "from ProductPrice where productCode=? and vendorCode=? and tradeType=? and isEffected=1 ";
		if(hospitalCode != null){
			hql += "and hospitalCode = '"+hospitalCode+"'";
		}else{
			hql += "and hospitalCode is null ";
		}
		return super.getByHql(hql, productCode,vendorCode,tradeType);
	}

	@Override
	public List<ProductPrice> listByProduct(String productCode) {
		String hql = "from ProductPrice where productCode=? and tradeType=? and hospitalCode is null and isEffected=1 ";
		return super.listByHql(hql, null, productCode,TradeType.hospital);
	}

	@Override
	public List<ProductPrice> listByProductAndHospital(String productCode, String hospitalCode) {
		String hql = "from ProductPrice where productCode=? and hospitalCode=? and tradeType=? and isEffected=1 ";
		return super.listByHql(hql, null, productCode,hospitalCode,TradeType.hospital);
	}

	@Override
	public List<ProductPrice> effectList(String productCode, String vendorCode) {
		String hql = "from ProductPrice where productCode=? and vendorCode=? and isEffected=1 ";
		return super.listByHql(hql, null, productCode,vendorCode);
	}
}
