package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrder.Status;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
/**
 * 配送单明细DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class DeliveryOrderDetailDao extends BaseDao<DeliveryOrderDetail, Long> implements IDeliveryOrderDetailDao {

	@Override
	public DeliveryOrderDetail findByCode(String code) {
		String hql = "from DeliveryOrderDetail d where d.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public DataGrid<DeliveryOrderDetail> pageNotCloseByHospital(PageRequest pageable, String hospitalCode) {
		String hql = "from DeliveryOrderDetail do left join fetch do.deliveryOrder d where d.hospitalCode=? and d.status<>?";
		return super.query(hql, pageable, hospitalCode, Status.closed);
	}

	@Override
	public List<DeliveryOrderDetail> listByDeliveryOrder(Long id) {
		String hql = "from DeliveryOrderDetail d where d.deliveryOrder.id=?";
		return super.listByHql(hql, null, id);
	}

	@Override
	public List<DeliveryOrderDetail> listByIsPass(int isPass) {
		String hql = "from DeliveryOrderDetail dod where dod.isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public DataGrid<DeliveryOrderDetail> pageByHospital(PageRequest pageable, String hospitalCode) {
		String hql = "from DeliveryOrderDetail t left join fetch t.deliveryOrder d "
				+ "where d.hospitalCode=?";
		return super.query(hql, pageable, hospitalCode);
	}
	
	@Override
	public DataGrid<DeliveryOrderDetail> pageByHospitalNotIn(PageRequest pageable, String hospitalCode) {
		String hql = "from DeliveryOrderDetail t left join fetch t.deliveryOrder d "
				+ " where d.hospitalCode=? and (inOutBoundGoodsNum<>goodsNum or inOutBoundGoodsNum is null)";
		return super.query(hql, pageable, hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO, "
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_deliveryorder_detail p"
				+ " left join t_order_deliveryorder po on p.DELIVERYORDERID=po.ID "
				+ " left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
		String sql = "select p.PRODUCTCODE,p.PRODUCTNAME,p.DOSAGEFORMNAME,p.unit as UNITNAME,"
				+ " p.MODEL,p.PACKDESC,p.PRODUCERNAME,p.goodsNum as NUM, p.goodsSum as AMT,"
				+ " p.CODE,p.BATCHCODE,p.BATCHDATE,p.EXPIRYDATE,po.VENDORNAME,po.HOSPITALNAME,po.code as  DELIVERYORDERCODE,po.BARCODE"
				+ " from t_order_deliveryorder_detail p"
				+ " left join t_order_deliveryorder po on p.DELIVERYORDERID=po.ID";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price"
				+ " from t_order_deliveryorder_detail p"
				+ " left join t_order_deliveryorder po on p.DELIVERYORDERID=po.ID "
				+ " left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO ";
		return super.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<DeliveryOrderDetail> queryByCode(PageRequest pageable) {
		String sql = "select t.* from t_order_deliveryorder_detail t left join t_order_deliveryorder op on t.deliveryOrderId "
				+" =op.id left join t_dm_product p on t.productcode=p.code";
		return super.findBySql(sql,pageable,Map.class);
	}

	@Override
	public List<DeliveryOrderDetail> listByDeliveryOrderId(Long deliveryOrderId) {
		String hql = "from DeliveryOrderDetail d where d.deliveryOrder.id = ?";
		return super.listByHql(hql,null,deliveryOrderId);
	}
}
