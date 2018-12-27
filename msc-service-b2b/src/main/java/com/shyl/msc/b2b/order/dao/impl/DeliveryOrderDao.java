package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.ReturnsRequest;

/**
 * 配送单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class DeliveryOrderDao extends BaseDao<DeliveryOrder, Long> implements IDeliveryOrderDao {

	@Override
	public DeliveryOrder findByCode(String code) {
		String hql = "from DeliveryOrder d where d.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<DeliveryOrder> listByIsPass(int isPass) {
		String hql = "from DeliveryOrder o where o.isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public List<DeliveryOrder> listByPurchaseOrder(String code) {
		String hql = "from DeliveryOrder d where d.purchaseOrderCode=?";
		return super.listByHql(hql, null, code);
	}

	@Override
	public Map<String, Object> getNumByPurchaseCode(String code) {
		String sql = "select sum(d.num) as num from t_order_deliveryorder d where d.purchaseOrderCode=?";
		return super.getBySql(sql, Map.class, code);
	}

	@Override
	public DeliveryOrder getByInternalCode(String companyCode, String internalCode, boolean isGPO) {
		String hql = "";
		if (isGPO) {
			hql = "from DeliveryOrder d where d.gpoCode=? and d.internalCode=?";
		} else {
			hql = "from DeliveryOrder d where d.vendorCode=? and d.internalCode=?";
		}
		return super.getByHql(hql, companyCode, internalCode);
	}

	@Override
	public DeliveryOrder listByHospitalAndBarcode(String hospitalCode, String barcode) {
		String hql = "from DeliveryOrder d where d.hospitalCode=? and d.barcode=?";
		return super.getByHql(hql, hospitalCode, barcode);
	}

	@Override
	public DeliveryOrder getByBarcode(String barcode) {
		String hql = "from DeliveryOrder d where d.barcode=?";
		return super.getByHql(hql, barcode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryUninvoice(PageRequest pageable, String code) {
		String sql = "select * from (select distinct t.purchaseOrderCode,t.hospitalName,p.sum as PSUM,p.orderDate as PORDERDATE "
				+ "FROM t_order_deliveryorder t left join t_order_purchaseorder p on t.purchaseOrderCode = p.code "
				+ "where t.isInvoiced=0 and t.vendorCode =? ) order by PORDERDATE desc";
		return super.findBySql(sql, pageable, Map.class, code);
	}

	@Override
	public DataGrid<DeliveryOrder> listByDeliveryOrderAndDetail(PageRequest pageable) {

		String sql = "select distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,t.vendorName,t.hospitalName,t.senderName,t.barcode,"
				+ "t.num,t.sum,t.status,t.purchaseOrderCode from t_order_deliveryorder t "
				+ "left join t_order_deliveryorder_detail d on t.id=d.deliveryorderid  left join t_dm_product p on d.productCode=p.code ";
		DataGrid<DeliveryOrder> result = findBySql(sql, pageable, Map.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql = sql + " where 1=1 " + hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}

	@Override
	public List<DeliveryOrder> listByDate(String hospitalCode, String startDate, String endDate) {
		String hql = "from DeliveryOrder d where hospitalCode = ? and to_char(d.createDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(d.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		return super.listByHql(hql,null,hospitalCode,startDate,endDate);
	}
}
