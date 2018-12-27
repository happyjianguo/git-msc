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
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrder.Status;
/**
 * 订单DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchaseOrderDao extends BaseDao<PurchaseOrder, Long> implements IPurchaseOrderDao {

	@Override
	public List<PurchaseOrder> listByDate(String vendorCode, String startDate, String endDate) {
		String hql = "from PurchaseOrder po where po.vendorCode=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(po.orderDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		return listByHql(hql, null, vendorCode, startDate, endDate);
	}

	@Override
	public PurchaseOrder findByCode(String ddbh) {
		String hql = "from PurchaseOrder po where po.code=?";
		return super.getByHql(hql, ddbh);
	}

	@Override
	public DataGrid<Map<String, Object>> reportByYear(PageRequest pageable, String year) {
		String sql = "select to_char(op.createdate,'yyyy-mm') as month,sum(op.sum) as ordersum from t_order_purchaseorder op"
				+ " where to_char(op.createdate, 'yyyy')=? group by to_char(op.createdate,'yyyy-mm')";
		return super.findBySql(sql, pageable, Map.class, year);
	}

	@Override
	public Map<String, Object> totalTradeByYear(String year) {
		String sql = "select sum(po.sum) as totalsum from t_order_purchaseorder po where to_char(po.createdate,'yyyy') = ?";
		return super.getBySql(sql, Map.class, year);
	}

	@Override
	public Map<String, Object> totalTradeByMonth(String month) {
		String sql = "select sum(po.sum) as totalsum from t_order_purchaseorder po where to_char(po.createdate,'yyyymm') = ?";
		return super.getBySql(sql, Map.class, month);
	}

	@Override
	public List<Map<String, Object>> reportByYear(String year) {
		String sql = "select to_char(po.createdate,'yyyy-mm') as month,sum(po.sum) as ordersum"
				+ " from t_order_purchaseorder po"
				+ " where to_char(po.createdate, 'yyyy')=?"
				+ " group by to_char(po.createdate,'yyyy-mm')";
		return super.listBySql(sql, null, Map.class, year);
	}

	@Override
	public List<Map<String, Object>> reportVendorTrade(String vendorCode, String year) {
		String sql = "select to_char(po.createdate,'yyyy-mm') as month,sum(po.sum) as ordersum"
				+ " from t_order_purchaseorder po"
				+ " where to_char(po.createdate, 'yyyy')=? and po.vendorCode=?"
				+ " group by to_char(po.createdate,'yyyy-mm')";
		return super.listBySql(sql, null, Map.class, year, vendorCode);
	}

	@Override
	public List<PurchaseOrder> listByIsPass(Integer isPass) {
		String hql = "from PurchaseOrder po where po.isPass=?";
		return super.listByHql(hql, null, isPass);
	}
	
	@Override
	public List<PurchaseOrder> listByIsPassDelivery(Integer isPassDelivery) {
		String hql = "from PurchaseOrder po where po.isPassDelivery=? and firstDeliveryDate is not null";
		return super.listByHql(hql, null, isPassDelivery);
	}
	
	@Override
	public DataGrid<PurchaseOrder> queryUnClosed(String hospitalCode, PageRequest pageable) {
		String hql = "from PurchaseOrder po where po.hospitalCode=? and (po.status != ? and po.status !=  ? ) ";
		return super.query(hql, pageable, hospitalCode,Status.sent,Status.forceClosed);
	}

	@Override
	public PurchaseOrder getByInternalCode(String vendorCode, String internalCode) {
		String hql = "from PurchaseOrder po where po.vendorCode=? and po.internalCode=?";
		return super.getByHql(hql, vendorCode, internalCode);
	}


	@Override
	public List<Map<String, Object>> getGpoCentralizedByHospital(String year, String month, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(b.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(b.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select x.hospitalname as name,round(nvl(y.num,0)*100/x.num,2) as value  from ");
		sql.append(" (select b.hospitalcode,b.hospitalname,sum(b.sum)  as num ");
		sql.append(" from t_order_purchaseorder b ");
		sql.append(" where 1=1 ");
		sql.append(dateQuerySQL);
		sql.append(" group by b.hospitalcode,b.hospitalname) x,");
		sql.append(" (select b.hospitalname,b.hospitalcode,sum(a.goodssum) as num ");
		sql.append(" from t_order_purchaseorder_detail a,t_order_purchaseorder b,t_dm_product c ");
		sql.append(" where c.isgpopurchase=1 and a.productCode=c.code  ");
		sql.append(" and a.purchaseorderid=b.id ");
		sql.append(dateQuerySQL);
		sql.append(" group by b.hospitalcode,b.hospitalname) y ");
		sql.append(" where x.hospitalcode =y.hospitalcode(+) order by x.hospitalcode desc");
		
		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	} 
	

	@Override
	public List<Map<String, Object>> getGpoSumByHospital(String year, String month, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(b.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(b.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.hospitalname as name,round(sum(a.goodssum)/10000,2) as value, sum(a.goodssum) as sumje");
		sql.append(" from t_order_purchaseorder_detail a,t_order_purchaseorder b,t_dm_product c  ");
		sql.append(" where c.isgpopurchase=1 and a.productCode=c.code  ");
		sql.append(dateQuerySQL);
		sql.append(" and a.purchaseorderid=b.id group by b.hospitalname ");
		
		return super.limitBySql(sql.toString(), maxsize, new Sort(Direction.ASC,"sumje"), Map.class);
	}


	@Override
	public List<Map<String, Object>> getGpoCentralizedByRegion(String year, String month, String treepath, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(b.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(b.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select z.name,nvl(round(nvl(y.num,0)*100/x.num,2),0) as value from t_set_regioncode z, ");
		sql.append(" (select a.regioncode,sum(b.sum)  as num  ");
		sql.append(" from t_order_purchaseorder b,t_set_hospital a");
		sql.append(" where a.regioncode is not null and a.code=b.hospitalcode");
		sql.append(dateQuerySQL);
		sql.append("  group by a.regioncode) x, ");
		sql.append(" (select e.regioncode,sum(a.goodssum) as num ");
		sql.append(" from t_order_purchaseorder_detail a,t_order_purchaseorder b,t_dm_product c ,t_set_hospital e ");
		sql.append(" where  c.isgpopurchase=1 and  e.regioncode is not null and a.productCode=c.code  ");
		sql.append(" and a.purchaseorderid=b.id  and b.hospitalcode=e.code  ");
		sql.append(dateQuerySQL);
		sql.append(" group by e.regioncode) y ");
		sql.append(" where z.id = x.regioncode  and z.id = y.regioncode(+)  ");
		//如果区域书为空
		if (!StringUtils.isEmpty(treepath)) {
			sql.append(" and z.treepath like '").append(treepath).append("'||'%'");
		}
		sql.append(" order by z.id desc");
		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	} 
	

	@Override
	public List<Map<String, Object>> getGpoSumByRegion(String year, String month, String treepath, int maxsize) {
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select z.name,round(nvl(y.num,0)/10000, 2) as value,nvl(y.num,0),nvl(y.num,0) as sumje from t_set_regioncode z, ");
		sql.append(" (select e.regioncode,sum(a.goodssum) as num ");
		sql.append(" from t_order_purchaseorder_detail a,t_order_purchaseorder b, t_dm_product c ,t_set_hospital e ");
		sql.append(" where  c.isgpopurchase=1 and  e.regioncode is not null and a.productCode=c.code");
		sql.append(" and a.purchaseorderid=b.id  and b.hospitalcode=e.code ");
		
		//如果有日期的情況日期條件處理
		if (StringUtils.isEmpty(month)) {
			sql.append(" and to_char(b.orderDate,'yyyy') = '").append(year).append("'");
		} else {
			sql.append(" and to_char(b.orderDate,'yyyyMM') = '").append(year).append(month).append("'");
		}
		sql.append(" group by e.regioncode) y ");
		sql.append(" where z.id = y.regioncode ");
		//如果区域书为空
		if (!StringUtils.isEmpty(treepath)) {
			sql.append(" and z.treepath like '").append(treepath).append("'||'%'");
		}
		sql.append(" order by z.id asc");
		
		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	}

	@Override
	public List<Map<String, Object>> totalHospitalSum(String beginDate, String endDate) {
		String sql = "select hospitalCode,min(hospitalName) as name,sum(sum) as totalsum from t_order_purchaseorder "
				+ "where to_char(orderDate,'yyyy-mm-dd')>=? and to_char(orderDate,'yyyy-mm-dd')<=? "
				+ "group by hospitalCode";
		return super.listBySql(sql, null, Map.class, beginDate, endDate);
	}

	@Override
	public DataGrid<PurchaseOrder> queryByStatus(PageRequest pageable) {
		String hql = "from PurchaseOrder t where t.status in (0,1)";
		return super.query(hql, pageable);
	}

	@Override
	public PurchaseOrder getByPlanCode(String planCode) {
		String hql = "from PurchaseOrder t where t.purchaseOrderPlanCode=?";
		return super.getByHql(hql, planCode);
	}

	@Override
	public DataGrid<PurchaseOrder> listBypurchaseOrderAndDetail(PageRequest pageable) {
		String sql="SELECT distinct(t.code) as code,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,to_char(t.requireDate,'yyyy-MM-dd HH24:mi:ss') as requireDate,t.hospitalName,t.vendorName,t.purchaseOrderPlanCode,"
		+" t.num,t.deliveryNum,t.sum,t.status from t_order_purchaseorder t LEFT JOIN "
		+" t_order_purchaseorder_detail d ON t.id=d.purchaseOrderId  left join t_dm_product p on d.productCode=p.code ";
		DataGrid<PurchaseOrder> result = findBySql(sql, pageable, Map.class);
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
	public DataGrid<PurchaseOrder> listByPurchaseOrderAndDetailStatus(PageRequest pageable) {
		String sql="SELECT distinct(t.code) as code,to_char(t.createDate,'yyyy-MM-dd HH24:mi:ss') as createDate,t.id,to_char(t.orderDate,'yyyy-MM-dd HH24:mi:ss') as orderDate,to_char(t.requireDate,'yyyy-MM-dd HH24:mi:ss') as requireDate,t.hospitalName,t.vendorName,t.purchaseOrderPlanCode,"
		+" t.num,t.deliveryNum,t.sum,t.status,t.inoutBoundNum,t.returnsNum from t_order_purchaseorder t LEFT JOIN "
		+" t_order_purchaseorder_detail d ON t.id=d.purchaseOrderId  left join t_dm_product p on d.productCode=p.code  where 1=1 and t.status in (0,1) ";
		DataGrid<PurchaseOrder> result = findBySql(sql, pageable, Map.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql+=hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}

	@Override
	public Map<String, Object> orderSumByHospitalAndYear(String hospitalCode, String year) {
		String sql = "select t.hospitalCode,sum(t.sum) as ORDERSUM,sum(t.num) as ORDERNUM  from t_order_purchaseorder t where t.hospitalCode = ? and to_char(t.createDate,'yyyy')  = ? group by t.hospitalCode";
		return super.getBySql(sql, Map.class, hospitalCode,year);
	}

	@Override
	public Map<String, Object> orderSumByGpoAndYear(String vendorCode, String year) {
		String sql = "select t.vendorCode,sum(t.sum) as ORDERSUM,sum(t.num) as ORDERNUM  from t_order_purchaseorder t where t.vendorCode = ? and to_char(t.createDate,'yyyy')  = ? and t.status != ?  group by t.vendorCode";
		return super.getBySql(sql, Map.class, vendorCode,year,3);
	}

	@Override
	public List<Map<String, Object>> listByNODelivery(PageRequest page) {
		String sql = "select t.code as code,p.code as drugCode,p.name,p.model,p.dosageFormName,p.producerName,p.packDesc,d.goodsNum,d.price,d.goodsSum,d.contractDetailCode,d.internalCode, "
				+ " p.authorizeNo,t.vendorCode,t.vendorName,d.deliveryGoodsNum,d.inOutBoundGoodsNum,d.returnsGoodsNum from t_order_purchaseorder_detail d "
				+ " left join t_order_purchaseorder t ON t.id = d.purchaseOrderId left join t_dm_product p on d.productCode = p.code ";
		return super.listBySql2(sql, page, Map.class);
	}
}
