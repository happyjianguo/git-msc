package com.shyl.msc.b2b.stl.dao.impl;


import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.ITradeInvoiceDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoice;

/**
 * 交易发票
 * 
 * @author a_Q
 *
 */
@Repository
public class TradeInvoiceDao extends BaseDao<TradeInvoice, Long> implements ITradeInvoiceDao {

	@Override
	public TradeInvoice getByInternalCode(String fph,TradeInvoice.Type type) {
		String hql = "from TradeInvoice i where i.internalCode=?";
		if(type != null){
			hql += " and i.type = ?";
			return this.getByHql(hql,fph,type);
		}
		return super.getByHql(hql,fph);
	}

	@Override
	public TradeInvoice getByCode(String companyCode, String code, boolean isGPO) {
		String hql = "";
		if(isGPO){
			hql = "from TradeInvoice i where i.gpoCode=? and i.code=?";
		}else{
			hql = "from TradeInvoice i where i.vendorCode=? and i.code=?";
		}
		return super.getByHql(hql, companyCode, code);
	}
}
