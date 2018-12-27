package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.ISettlementDao;
import com.shyl.msc.b2b.stl.entity.Settlement;
import com.shyl.msc.b2b.stl.entity.SettlementDetail;

/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Repository
public class SettlementDao extends BaseDao<Settlement, Long> implements ISettlementDao {

	@Override
	public Settlement findByCode(String code) {
		String hql = "from Settlement where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public Settlement getByInternalCode(String code, String internalCode, Boolean isGPO) {
		String hql = "";
		if(isGPO){
			hql = "from Settlement ro where ro.gpoCode=? and ro.internalCode=?";
		}else{
			hql = "from Settlement ro where ro.vendorCode=? and ro.internalCode=?";
		}
		return super.getByHql(hql, code, internalCode);
	}

	@Override
	public List<SettlementDetail> listBySettlementId(Long id) {
		String hql = "from SettlementDetail where settlement.id=?";
		return super.listByHql(hql, null, id);
	}

}
