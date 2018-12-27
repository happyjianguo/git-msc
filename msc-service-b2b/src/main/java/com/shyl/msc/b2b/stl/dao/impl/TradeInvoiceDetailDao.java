package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;

import com.shyl.msc.b2b.stl.entity.TradeInvoice;
import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.ITradeInvoiceDetailDao;
import com.shyl.msc.b2b.stl.entity.TradeInvoiceDetail;

/**
 * 交易发票明细
 * 
 * @author a_Q
 *
 */
@Repository
public class TradeInvoiceDetailDao extends BaseDao<TradeInvoiceDetail, Long> implements ITradeInvoiceDetailDao {
	
	@Override
	public List<TradeInvoiceDetail> listByInvoiceId(Long tradeInvoiceId) {
		String hql = "from TradeInvoiceDetail d where d.tradeInvoice.id=?";
		return super.listByHql(hql, null, tradeInvoiceId);
	}

	@Override
	public List<TradeInvoiceDetail> getByCode(String str, TradeInvoice.Type type) {
		String sql = "select t.* from t_stl_tradeInvoice_detail t inner join t_stl_tradeInvoice s"
				+" on t.tradeInvoiceId = s.id where t.productCode||t.batchCode||s.customerCode in "
				+"("+ str + ") and s.type = ?";
		return super.listBySql(sql,null,TradeInvoiceDetail.class,type.ordinal());
	}
}
