package com.shyl.msc.count.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IHospitalProductCDao;
import com.shyl.msc.count.entity.HospitalProductC;

@Repository
public class HospitalProductCDao extends BaseDao<HospitalProductC, Long> implements IHospitalProductCDao {

	@Override
	public HospitalProductC getByKey(String month, Long productId, String hospitalCode){
		return super.getByHql("from HospitalProductC where month=? and product.id=? "
				+ "and hospitalCode=? ", month, productId, hospitalCode);
	}
	
	@Override
	public List<HospitalProductC> listByStockFlag(int stockFlag, String month) {
		String hql = "from HospitalProductC where stockFlag=? and month<?";
		return super.listByHql(hql, null, stockFlag, month);
	}
}
