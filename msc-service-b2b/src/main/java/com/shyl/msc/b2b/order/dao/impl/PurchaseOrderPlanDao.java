package com.shyl.msc.b2b.order.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;

/**
 * 订单计划DAO实现
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchaseOrderPlanDao extends BaseDao<PurchaseOrderPlan, Long> implements IPurchaseOrderPlanDao {

	@Override
	public List<PurchaseOrderPlan> listByDate(String companyCode, String startDate, String endDate, boolean isGPO) {
		String hql = "";
		if (isGPO) {
			hql = "from PurchaseOrderPlan po where po.gpoCode=? and po.status=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		} else {
			hql = "from PurchaseOrderPlan po where po.vendorCode=? and po.status=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		}
		return listByHql(hql, null, companyCode, PurchaseOrderPlan.Status.uneffect, startDate, endDate);
	}

	@Override
	public PurchaseOrderPlan findByCode(String code) {
		String hql = "from PurchaseOrderPlan po where po.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public PurchaseOrderPlan getByInternalCode(String hospitalCode, String internalCode) {
		String hql = "from PurchaseOrderPlan pop where pop.hospitalCode=? and pop.internalCode=?";
		return super.getByHql(hql, hospitalCode, internalCode);
	}

	@Override
	public Long getCountByInternalCode(String hospitalCode, String internalCode) {
		String hql = "select count(1) from PurchaseOrderPlan pop where pop.hospitalCode=? and pop.internalCode=?";
		return super.count(hql, hospitalCode, internalCode);
	}

	/**
	 * 获取GPO及时率排名
	 * 
	 * @param year
	 * @param month
	 * @param vendorId
	 * @return
	 */
	public List<Map<String, Object>> queryGpoTimelyRankList(String year, String month) {
		// 判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		// 默認日期查詢條件
		String dateQuerySQL = " and to_char(orderDate,'yyyy') = '" + year + "'";
		// 如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(orderDate,'yyyyMM') = '" + year + month + "'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(
				"select round(avg((firstdeliverydate-orderdate)*24),2) as value,vendorname,vendorcode from t_order_purchaseorderplan a ");
		sql.append(" where firstdeliverydate is not null and vendorname is not null ");
		sql.append(dateQuerySQL);
		sql.append(" group by vendorname, vendorCode order by round(avg((firstdeliverydate-orderdate)*24),2) desc");
		return super.listBySql(sql.toString(), null, Map.class);
	}

	/**
	 * 获取GPO配送时间
	 * 
	 * @param year
	 * @param month
	 * @param vendorCode
	 * @param delay配送时长(小时)0-6小时
	 *            6-12小时 12-24小时 24-48小时 48 -72小时 72小时~（3天以上）
	 * @return
	 */
	public Long getDeliveryTimely(String year, String month, String vendorCode, Integer delayStart, Integer delayEnd) {
		// 判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		// 默認日期查詢條件
		String dateQuerySQL = " and to_char(orderDate,'yyyy') = '" + year + "'";
		// 如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(orderDate,'yyyyMM') = '" + year + month + "'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from t_order_purchaseorderplan ");
		sql.append(" where firstdeliverydate is not null and vendorname is not null and vendorcode =?");
		sql.append(dateQuerySQL);
		// 配送时间开始
		if (delayStart != null) {
			sql.append("and (firstdeliverydate-orderdate)*24 >").append(delayStart);
		}
		// 配送时间结束
		if (delayEnd != null) {
			sql.append(" and (firstdeliverydate-orderdate)*24 <=").append(delayEnd);
		}
		return super.countBySql(sql.toString(), vendorCode);

	}

	@Override
	public List<PurchaseOrderPlan> listByPatientId(Long pId) {
		String hql = "from PurchaseOrderPlan op where op.patientId=?";
		Sort sort = new Sort(new Order(Direction.DESC, "orderDate"));
		return super.listByHql(hql, sort, pId);
	}

	@Override
	public DataGrid<PurchaseOrderPlan> listBypurchaseOrderPlanAndDetail(PageRequest pageable) {
		String sql = "SELECT distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,to_char(t.requireDate,'yyyy-MM-dd HH24:mi:ss') as requireDate,t.hospitalName,t.vendorName,"
				+ "t.num,t.deliveryNum,t.sum,t.status from t_order_purchaseorderplan t LEFT JOIN "
				+ "t_order_purchaseorderplan_d d ON t.id=d.purchaseOrderPlanId left join t_dm_product p on d.productCode=p.code ";
		DataGrid<PurchaseOrderPlan> result = findBySql(sql, pageable, Map.class);
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
	public List<PurchaseOrderPlan> listByIsPass(Integer isPass) {
		String hql = "from PurchaseOrderPlan po where po.isPass=?";
		return super.listByHql(hql, null, isPass);
	}
	
	@Override
	public List<PurchaseOrderPlan> listByIsPassAudit(Integer isPassAudit) {
		String hql = "from PurchaseOrderPlan po where po.isPassAudit=? and po.status<>? and auditDate is not null";
		return super.listByHql(hql, null, isPassAudit, PurchaseOrderPlan.Status.uneffect);
	}

	/**
	 * 过滤未生效的订单
	 * @param page
	 * @return
	 */
	@Override
	public List<Map<String, Object>> listByUnEffect(PageRequest page) {
		String sql = "select t.code as code,p.code as drugCode,p.name,p.model,p.dosageFormName,p.producerName,p.packDesc,d.goodsNum,d.price,d.goodsSum,d.contractDetailCode,d.internalCode, "
				+ " p.authorizeNo,t.vendorCode,t.vendorName,d.deliveryGoodsNum,d.inOutBoundGoodsNum,d.returnsGoodsNum from t_order_purchaseorderplan_d d "
				+ " left join t_order_purchaseorderplan t ON t.id = d.purchaseOrderPlanId left join t_dm_product p on d.productCode = p.code ";
		return super.listBySql2(sql, page, Map.class);
	}
}
