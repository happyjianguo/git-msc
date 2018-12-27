package com.shyl.msc.count.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.count.dao.IHospitalCDao;
import com.shyl.msc.count.entity.HospitalC;

@Repository
public class HospitalCDao extends BaseDao<HospitalC, Long> implements IHospitalCDao {


	@Override
	public HospitalC getByKey(String month, String hospitalCode){
		return super.getByHql("from HospitalC where month=?  "
				+ "and hospitalCode=?", month, hospitalCode);
	}
	
	@Override
	public List<HospitalC> listByStockFlag(int stockFlag, String month) {
		String hql = "from HospitalC where stockFlag=? and month<?";
		return super.listByHql(hql, null, stockFlag, month);
	}
}
