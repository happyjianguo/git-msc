package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrder;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
/**
 * 订单明细DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchaseOrderDetailDao extends BaseDao<PurchaseOrderDetail, Long> implements IPurchaseOrderDetailDao {

	@Override
	public DataGrid<PurchaseOrderDetail> pageByOrderId(PageRequest pageable,Long orderId) {
		String hql = "from PurchaseOrderDetail pod where pod.purchaseOrder.id=?";
		return super.query(hql, pageable, orderId);
	}

	@Override
	public PurchaseOrderDetail findByCode(String code) {
		String hql = "from PurchaseOrderDetail pod where pod.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<PurchaseOrderDetail> listByOrderId(Long orderId) {
		String hql = "from PurchaseOrderDetail pod where pod.purchaseOrder.id=? ";
		return listByHql(hql, null, orderId);
	}
	
	@Override
	public DataGrid<Map<String, String>> report2(String name, String dataS, String dataE, PageRequest pageable) {
		System.out.println("name= "+name);
		System.out.println("dataS= "+dataS);
		System.out.println("dataE= "+dataE);
		String sql ="select o1.hospitalCode,o1.hospitalName,sum(o.goodsSum) as SUM,"
				+ "sum(case when d.INSURANCEDRUGTYPE is not null then o.goodsSum  else 0 end) as SBSUM"
				+ " from t_order_purchaseorder_detail o  left JOIN t_dm_product d on o.productCode = d.Code"
				+ " JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id ";
		sql += " where o1.hospitalName like ? ";
		sql += " and to_char(o.createdate,'YYYYMM')>=? and to_char(o.createdate,'YYYYMM')<=?  group by o1.hospitalCode,o1.hospitalName";
		return super.findBySql(sql, pageable, Map.class, "%"+name+"%",dataS,dataE);
		
	}

	@Override
	public DataGrid<Map<String, String>> report1(String year, PageRequest pageable) {
		String sql ="select to_char(o.createdate,'YYYY-MM') as YM,"
				+ "sum(case when d.INSURANCEDRUGTYPE is not null then o.goodsSum  else 0 end) as SBSUM,"
				+ "sum(case when d.INSURANCEDRUGTYPE is null then o.goodsSum  else 0 end) as FSBSUM"
				+ " from t_order_purchaseorder_detail o  left JOIN t_dm_product d on o.productCode = d.Code  ";
		sql += " where to_char(o.createdate,'YYYY')=?   group by to_char(o.createdate,'YYYY-MM') ";
		return super.findBySql(sql, pageable, Map.class,year);
		
	}
	@Override
	public List<Map<String, Object>> chart1(String year) {
		String sql ="select to_char(o.createdate,'YYYY-MM') as YM,"
				+ "sum(case when d.INSURANCEDRUGTYPE is not null then o.goodsSum  else 0 end) as SBSUM,"
				+ "sum(case when d.INSURANCEDRUGTYPE is null then o.goodsSum  else 0 end) as FSBSUM"
				+ " from t_order_purchaseorder_detail o  left JOIN t_dm_product d on o.productCode = d.code"
				+ " where to_char(o.createdate,'YYYY')=?   group by to_char(o.createdate,'YYYY-MM') ";
		return super.listBySql(sql, null, Map.class,year);
	}
	
	@Override
	public DataGrid<Map<String, Object>> report4(String name, PageRequest pageable) {
		String sql ="select o1.hospitalCode,o1.hospitalName,sum(o.goodsSum) as SUM,"
				+ "sum(case when d.baseDrugType is not null then o.goodsSum  else 0 end) as JBSUM,"
				+ "sum(case when d.baseDrugType is  null then o.goodsSum  else 0 end) as FJBSUM, "
				+ "sum(case when d.INSURANCEDRUGTYPE is not null then o.goodsSum  else 0 end) as SBSUM "
				+ "From t_order_purchaseorder_detail o  left JOIN t_dm_product d on o.productCode = d.code "
				+ "JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id where o1.status != 3 ";
		pageable.setMySort(new Sort(Direction.DESC,"sum(o.goodsSum)"));
		Object[] args = null;
		if(name != null && !name.equals("")){
			sql += " and o1.hospitalName like ? ";
			args = new Object[1];
			args[0] = "%"+name+"%";
		}
		sql += " group by o1.hospitalCode,o1.hospitalName";
		return super.findBySql(sql, pageable, Map.class, args);
	}
	
	@Override
	public List<Map<String, Object>> chart4(String name, String dateS, String dateE) {
		Integer date0 = Integer.valueOf(dateS.replaceAll("-", ""));
		Integer date1 = Integer.valueOf(dateE.replaceAll("-", ""));
		String format = "YYYY-MM";
		if (date1-date0 < 32) {
			format = "YYYY-MM-YY";
		}
		String sql ="select to_char(o.createdate,'"+format+"') as year,sum(o.goodsSum) as SUM,"
				+ "sum(case when d.baseDrugType is not null then o.goodsSum  else 0 end) as JBSUM,"
				+ "sum(case when d.baseDrugType is  null then o.goodsSum  else 0 end) as FJBSUM"
				+ " from t_order_purchaseorder_detail o  left JOIN t_dm_product d on o.productCode = d.Code"
				+ " JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id "
				+ " where  o1.hospitalCode = ? "
			    + " and to_char(o.createdate,'YYYY-MM-DD')>=? and to_char(o.createdate,'YYYY-MM-DD')<=?   group by to_char(o.createdate,'"+format+"')"
			    + " order by to_char(o.createdate,'"+format+"') asc";
		return super.listBySql(sql,null, Map.class, name, dateS, dateE);
		
	}

	@Override
	public DataGrid<Map<String, Object>> report5(String name, String month, PageRequest pageable) {
		String sql ="select * from (select o1.hospitalCode,o1.hospitalName,count(distinct o.productCode) as SL,sum(o.goodssum) as JE FROM t_order_purchaseorder_detail o"
				+ " JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id "
				+ " where 1=1 and o1.status!=3 ";
		if(name != null && !name.equals("")){
			sql += " and o1.hospitalName like ? ";
			sql += " and to_char(o.createdate,'YYYYMM')=? group by o1.hospitalCode,o1.hospitalName";
			sql += ") order by hospitalCode asc ";
			return super.findBySql(sql, pageable, Map.class, "%"+name+"%",month);
		}else{
			sql += " and to_char(o.createdate,'YYYYMM')=? group by o1.hospitalCode,o1.hospitalName";
			sql += ") order by hospitalCode asc ";
			return super.findBySql(sql, pageable, Map.class,month);
		}
	}

	@Override
	public DataGrid<Map<String, Object>> reportProducerTrade(
			PageRequest pageable, String name, String year) {
		String sql = "select to_char(pod.createdate,'yyyy') as year,g.producerId as ID,g.producername as name, "
				+ "sum(pod.goodssum) as ordersum, count(distinct pod.productId) as goodsNum "
		        + " from t_order_purchaseorder_detail pod left join t_dm_product g on pod.productCode = g.Code "
		        + " where to_char(pod.createdate,'yyyy') = ?";
		if(!StringUtils.isEmpty(name)){
			sql += " and g.producername like '%"+name+"%' and ";
		}
		sql += " group by to_char(pod.createdate,'yyyy'),g.producerId,g.producername";
		return super.findBySql(sql, pageable, Map.class,year);
	}

	@Override
	public DataGrid<Map<String, Object>> report5mx(String hospitalcode, String month, PageRequest pageable) {
		String sql ="select d.id,d.name,d.dosageFormName,d.model,d.producerName,o1.vendorname,sum(goodsNum) as SL,sum(goodsSum) as JE "
		        + " from t_order_purchaseorder_detail o  "
		        + " left JOIN t_dm_product d on o.productCode = d.code "
				+ " left JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id "
		        + " where  o1.hospitalcode=? and to_char(o.createdate,'YYYYMM')=? "
		        + "  group by d.id,d.name,d.dosageFormName,d.model,d.producerName,o1.vendorname ";
		return super.findBySql(sql, pageable, Map.class,hospitalcode,month);
	}

	@Override
	public DataGrid<Map<String, Object>> reportVendorTrade(PageRequest pageable, String name, String year) {
		String sql = "select to_char(po.createdate,'yyyy') as year, po.VENDORCODE as CODE, po.VENDORNAME as name, sum(pod.goodssum) as ordersum, "
				+ "count(distinct pod.productCode) as goodsNum from t_order_purchaseorder_detail pod "
		        + "left join t_order_purchaseorder po on pod.PURCHASEORDERID = po.ID"
		        + " where  to_char(po.createdate,'yyyy') = ?";
		if(!StringUtils.isEmpty(name)){
			sql += " and po.VENDORNAME like '%"+name+"%'";
		}
		sql += " group by to_char(po.createdate,'yyyy'),po.VENDORCODE, po.VENDORNAME";

		return super.findBySql(sql, pageable, Map.class,year);
	}

	@Override
	public DataGrid<Map<String, Object>> report7(String name, String model, String producerName, String vendorName,
			PageRequest pageable) {
		String sql ="select d.id,d.name,d.dosageFormName,d.model,d.producerName,d.vendorName,sum(goodsNum) as SL,sum(goodsSum) as JE "
				+ " from t_order_purchaseorder_detail o  "
				+ "left JOIN t_dm_product d on o.productCode = d.code   "
				+ " where  d.name like ? and d.model like ? and d.producerName like ? and d.vendorName like ? "
				+ "  group by d.id,d.name,d.dosageFormName,d.model,d.producerName,d.vendorName ";
		return super.findBySql(sql, pageable, Map.class,name,model,producerName,vendorName);
	}
	@Override
	public DataGrid<Map<String, Object>> reportGoodsTradeMX(Long id, String dateS, String dateE, PageRequest pageable) {
		System.out.println("7id = "+id);
		String sql ="select o1.hospitalName,o1.vendorName,o.createdate as CGSJ,o.goodsNum as SL,o.goodsSum as JE "
				+ " from t_order_purchaseorder_detail o "
				+ " JOIN t_dm_product d on o.productCode = d.code "
				+ " left JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id "
				+ " where   d.id = ? and to_char(o.createdate,'YYYYMM')>=? and to_char(o.createdate,'YYYYMM')<=? and o.ispass=1 order by o.createdate desc,o1.hospitalName,o1.vendorName";
		return super.findBySql(sql, pageable, Map.class,id, dateS, dateE);
	}

	@Override
	public List<Map<String, Object>> reportProducerTrade(Long producerId, String year) {
		String sql = "select to_char(pod.createdate,'yyyy-mm') as month, sum(pod.goodssum) as ordersum "
				+ " from t_order_purchaseorder_detail pod "
				+ "left join t_dm_product g on pod.productCode = g.Code "
				+ " where  to_char(pod.createdate,'yyyy') = ? and g.producerId=?"
				+ " group by to_char(pod.createdate,'yyyy-mm')";
		return listBySql(sql, null, Map.class, year, producerId);
	}

	@Override
	public List<PurchaseOrderDetail> listByIsPass(int isPass) {
		String hql = "from PurchaseOrderDetail pod  left join fetch pod.purchaseOrder p where pod.isPass=? and " +
				"p.status!=3";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO, "
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_purchaseorder_detail p"
				+ " left join t_order_purchaseorder po on p.PURCHASEORDERID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " where po.status!=?"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.unitName, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.findBySql(sql, pageable, Map.class, PurchaseOrder.Status.forceClosed.ordinal());
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
		String sql = "select p.PRODUCTCODE,p.PRODUCTNAME,p.DOSAGEFORMNAME,p.unit as UNITNAME,"
				+ " p.MODEL,p.PACKDESC,p.PRODUCERNAME,p.goodsNum as NUM,p.goodsSum as AMT,"
				+ " p.CODE,po.VENDORNAME,po.HOSPITALNAME,po.code as PURCHASEORDERCODE"
				+ " from t_order_purchaseorder_detail p"
				+ " left join t_order_purchaseorder po on p.PURCHASEORDERID=po.ID"
				+ " where po.status!=?";
		return super.findBySql(sql, pageable, Map.class, PurchaseOrder.Status.forceClosed.ordinal());
	}

	@Override
	public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO, "
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_purchaseorder_detail p"
				+ " left join t_order_purchaseorder po on p.PURCHASEORDERID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " where po.status!=?"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.unitName, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.listBySql2(sql, pageable, Map.class, PurchaseOrder.Status.forceClosed.ordinal());
	}

	@Override
	public DataGrid<Map<String, Object>> reportForProductOrder(String projectCode, String startDate, String endDate,PageRequest pageable) {
		String sql = "select pd.productCode,pd.productName,pd.dosageFormName,pd.model,pd.packDesc,pd.producerName,sum(pd.goodsNum) as num "
				+ " from t_order_purchaseorder_detail pd "
				+ " left join t_order_purchaseorder p on pd.purchaseOrderId=p.ID   "
				+ " left join t_set_hospital h on p.hospitalCode=h.code "
				+ " left join t_set_regioncode rc on h.regionCode = rc.id  "
				+ " where p.Status != 3   ";
		
		if(!StringUtils.isEmpty(startDate)){
			sql += " and to_char(p.orderDate,'yyyy-mm-dd') >='"+startDate+"'";
		}
		if(!StringUtils.isEmpty(endDate)){
			sql += " and to_char(p.orderDate,'yyyy-mm-dd') <='"+endDate+"'";
		}
		sql += " group by pd.productCode,pd.productName,pd.dosageFormName,pd.model,pd.packDesc,pd.producerName ";
		
		System.out.println("sql ==dao== "+sql);
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> reportDetailForProductOrder(String projectCode, String startDate,
			String endDate, PageRequest pageable) {
		String sql = "select p.hospitalName,p.code,pd.code as codedetail,p.orderdate,pd.goodsNum "
				+ " from t_order_purchaseorder_detail pd "
				+ " left join t_order_purchaseorder p on pd.purchaseOrderId=p.ID   "
				+ " left join t_set_hospital h on p.hospitalCode=h.code "
				+ " left join t_set_regioncode rc on h.regionCode = rc.id  "
				+ " where (p.Status = 2 or p.Status=3)   ";
		
		if(!StringUtils.isEmpty(startDate)){
			sql += " and to_char(p.orderDate,'yyyy-mm-dd') >='"+startDate+"'";
		}
		if(!StringUtils.isEmpty(endDate)){
			sql += " and to_char(p.orderDate,'yyyy-mm-dd') <='"+endDate+"'";
		}
		
		System.out.println("sql ==dao== "+sql);
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<PurchaseOrderDetail> queryBycode(PageRequest pageable) {
		String sql = "select t.*,p.authorizeNo from t_order_purchaseorder_detail t left join t_order_purchaseorder op "
		+" on purchaseorderId = op.id left join t_dm_product p on t.productcode=p.code";
		return super.findBySql(sql,pageable,Map.class);
	}

	@Override
	public List<Map<String, Object>> reportAll(String name, String month, PageRequest pageable) {
		String sql ="select * from (select o1.hospitalCode,o1.hospitalName,count(distinct o.productCode) as SL,sum(o.goodssum) as JE FROM t_order_purchaseorder_detail o"
				+ " JOIN t_order_purchaseorder o1 ON o.PURCHASEORDERID = o1.id "
				+ " where 1=1 ";
		if(name != null && !name.equals("")){
			sql += " and o1.hospitalName like ? ";
			sql += " and to_char(o.createdate,'YYYYMM')=? group by o1.hospitalCode,o1.hospitalName";
			sql += ") order by hospitalCode asc ";
			return this.listBySql2(sql, pageable, Map.class, "%"+name+"%",month);
		}else{
			sql += " and to_char(o.createdate,'YYYYMM')=? group by o1.hospitalCode,o1.hospitalName";
			sql += ") order by hospitalCode asc ";
		return this.listBySql2(sql, pageable, Map.class, month);
		}

	}

}
