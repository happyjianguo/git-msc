package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IPurchasePlanDetailDao;
import com.shyl.msc.b2b.order.entity.PurchasePlanDetail;
import com.shyl.common.framework.util.HqlUtil;

/**
 * 订单计划DAO实现
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchasePlanDetailDao extends BaseDao<PurchasePlanDetail, Long> implements IPurchasePlanDetailDao {

	@Override
	public PurchasePlanDetail findByCode(String code) {
		String hql = "from PurchasePlanDetail where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<PurchasePlanDetail> listByPId(Long pid) {
		String hql = "from PurchasePlanDetail t where t.purchasePlan.id=?";
		return super.listByHql(hql, null, pid);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ "b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price"
				+ " from t_order_purchaseorderplan_d p"
				+ " left join t_order_purchaseorderplan po on p.purchasePlanDetailCode=po.purchasePlanCode "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
		String sql = "select  p.status,p.deliveryGoodsNum,p.deliveryGoodsSum,p.inOutBoundGoodsNum,p.inOutBoundGoodsSum,"
				+ "p.returnsGoodsNum,p.returnsGoodsSum,p.notes,"
				+ "p.isUsed,p.purchasePlanDetailCode,p.PRODUCTCODE,p.PRODUCTNAME,"
				+ "p.DOSAGEFORMNAME,p.unit as UNITNAME,p.MODEL,p.PACKDESC,p.PRODUCERNAME,p.goodsNum as NUM,p.goodsSum as AMT,"
				+ "p.CODE,po.HOSPITALNAME,po.code as ORDERPLANCODE from t_order_purchaseorderplan_d p"
				+ " left join t_order_purchaseorderplan po on p.purchasePlanDetailCode=po.purchasePlanCode";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ "b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_purchaseorderplan_d p"
				+ " left join t_order_purchaseorderplan po on p.purchasePlanDetailCode=po.purchasePlanCode "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<PurchasePlanDetail> queryByPurchasePlanAndCode(PageRequest pageable) {
		String sql = "select t.PRODUCTCODE,t.PRODUCTNAME,t.DOSAGEFORMNAME,t.MODEL,t.PRODUCERNAME,t.VENDORNAME,t.PACKDESC,t.PRICE,t.GOODSNUM,t.GOODSSUM,p.AUTHORIZENO from t_order_purchaseplan_detail t "
				+" left join t_dm_product p on t.productcode=p.code";
		return findBySql(sql, pageable, Map.class);
	}

}
