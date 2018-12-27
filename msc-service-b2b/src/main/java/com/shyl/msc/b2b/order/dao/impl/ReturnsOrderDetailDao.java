package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDetailDao;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
/**
 * 退货单明细DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ReturnsOrderDetailDao extends BaseDao<ReturnsOrderDetail, Long> implements IReturnsOrderDetailDao{

	@Override
	public List<ReturnsOrderDetail> listByIsPass(int isPass) {
		String hql = "from ReturnsOrderDetail d where d.isPass=?";
		return super.listByHql(hql, null, isPass);
	}

	@Override
	public ReturnsOrderDetail findByCode(String code) {
		String hql = "from ReturnsOrderDetail where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<ReturnsOrderDetail> listByReturnId(Long returnId) {
		String hql = "from ReturnsOrderDetail where returnsOrder.id=?";
		return super.listByHql(hql, null, returnId);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_returnsorder_detail p"
				+ " left join t_order_returnsorder po on p.RETURNSORDERID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
		String sql = "select p.PRODUCTCODE,p.PRODUCTNAME,p.DOSAGEFORMNAME,p.unit as UNITNAME,"
				+ " p.MODEL,p.PACKDESC,p.PRODUCERNAME,p.goodsNum as NUM,p.goodsSum as AMT,"
				+ " p.CODE,p.BATCHCODE,p.BATCHDATE,p.EXPIRYDATE,po.VENDORNAME,po.HOSPITALNAME,po.code as RETURNSORDERCODE"
				+ " from t_order_returnsorder_detail p"
				+ " left join t_order_returnsorder po on p.RETURNSORDERID=po.ID";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
		String sql = "select b.code as PRODUCTCODE,b.name as PRODUCTNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,b.AUTHORIZENO,"
				+ " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(p.goodsNum) as NUM, SUM(p.goodsSum) as AMT,max(p.price) as price "
				+ " from t_order_returnsorder_detail p"
				+ " left join t_order_returnsorder po on p.RETURNSORDERID=po.ID "
				+"  left join t_dm_product b on p.productCode=b.code"
				+ " group by b.code, b.name, b.DOSAGEFORMNAME, b.UNITNAME, b.MODEL,b.PACKDESC, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
		return super.listBySql2(sql, pageable, Map.class);
	}

}
