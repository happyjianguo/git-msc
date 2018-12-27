package com.shyl.msc.b2b.stl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.stl.dao.IInvoiceDao;
import com.shyl.msc.b2b.stl.entity.Invoice;

/**
 * 发票
 * 
 * @author a_Q
 *
 */
@Repository
public class InvoiceDao extends BaseDao<Invoice, Long> implements IInvoiceDao {

	@Override
	public Invoice getByInternalCode(String companyCode, String fph, boolean isGPO) {
		String hql = "";
		if(isGPO){
			hql = "from Invoice i where i.gpoCode=? and i.internalCode=?";
		}else{
			hql = "from Invoice i where i.vendorCode=? and i.internalCode=?";
		}
		return super.getByHql(hql, companyCode, fph);
	}

	@Override
	public DataGrid<Map<String, Object>> hospitalListForSettle(String vendorCode, PageRequest pageable) {
		String sql = "select i.hospitalCode,i.hospitalName,count(i.id) as NUM from t_stl_invoice i "
				+ "where i.vendorCode=? and i.status=0 group by i.hospitalCode,i.hospitalName";
		return super.findBySql(sql, pageable, Map.class, vendorCode);
	}

	@Override
	public List<Map<String, Object>> listForSettle(String hospitalCode, String vendorCode) {
		String sql = "select i.* from t_stl_invoice i "
				+ "where i.hospitalCode=? and i.vendorCode=? and i.status=0 ";
		return super.listBySql(sql, null, Map.class, hospitalCode,vendorCode);
	}

	@Override
	public Invoice findByCode(String code) {
		String hql = "from Invoice where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<Invoice> listByDeliveryOrReturnsCode(String stdbh) {
		String hql = "from Invoice where deliveryOrReturnsCode=?";
		return super.listByHql(hql, null, stdbh);
	}
	
	@Override
	public List<Invoice> listByDate(String hospitalCode, String startDate, String endDate) {
		String hql = "from Invoice i where i.hospitalCode=? "
				+ "and to_char(i.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? "
				+ "and to_char(i.orderDate, 'yyyy-mm-dd hh24:mi:ss')<=? ";
		return listByHql(hql, null, hospitalCode, startDate, endDate);
	}


	@Override
	public Invoice getByInternalCode(String hospitalCode, String code) {
		String hql = "from Invoice where hospitalCode=? and (code=? or internalCode=?)";
		return super.getByHql(hql, hospitalCode, code,code);
	}
}
