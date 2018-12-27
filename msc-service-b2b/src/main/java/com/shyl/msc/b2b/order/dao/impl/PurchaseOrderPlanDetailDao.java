package com.shyl.msc.b2b.order.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderPlanDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlan;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail;
import com.shyl.msc.b2b.order.entity.PurchaseOrderPlanDetail.Status;
/**
 * 订单计划明细DAO实现
 * 
 * @author a_Q
 *
 */
@Repository
public class PurchaseOrderPlanDetailDao extends BaseDao<PurchaseOrderPlanDetail, Long> implements IPurchaseOrderPlanDetailDao {

	@Override
	public List<PurchaseOrderPlanDetail> listByOrderPlanId(Long id) {
		String hql = "from PurchaseOrderPlanDetail pod where pod.purchaseOrderPlan.id=? ";
		return listByHql(hql, null, id);
	}

	@Override
	public PurchaseOrderPlanDetail findByCode(String code) {
		String hql = "from PurchaseOrderPlanDetail pod where pod.code=?";
		return super.getByHql(hql, code);
	}
	@Override
	public DataGrid<PurchaseOrderPlanDetail> pageByOrderId(PageRequest pageable, Long orderId) {
		String hql = "from PurchaseOrderPlanDetail pod where pod.purchaseOrderPlan.id=?";
		return super.query(hql, pageable, orderId);
	}

	@Override
	public List<Map<String,Object>> listByDate(String beginDate,String endDate) {
		String sql = "select c.fullName as name,c.code as code,nvl(d.num,0) as num from t_set_company c left outer join "
				+ "(select k.vendorCode,count(*) as num from (select * from t_order_purchaseorderplan_d a "
				+ "left outer join t_order_purchaseorderplan b on a.purchaseorderplanid=b.id "
				+ "where to_char(b.createDate,'yyyy-mm-dd')>=? and to_char(a.createDate,'yyyy-mm-dd')<=? "
				+ "and a.status=1) k group by k.vendorCode order by num desc) d on c.code=d.vendorCode "
				+ "where c.isvendor=1 order by nvl(d.num,0) asc";
		return listBySql(sql, null,Map.class,beginDate,endDate);
	}

	@Override
	public List<PurchaseOrderPlanDetail> listByOrderPlanId(Long id, Status status) {
		String hql = "from PurchaseOrderPlanDetail pod where pod.purchaseOrderPlan.id=? and pod.status=?";
		return listByHql(hql, null, id, status);
	}

	@Override
	public PurchaseOrderPlanDetail findByPlanCode(String planCode) {
		String hql = "from PurchaseOrderPlanDetail pod where pod.purchasePlanDetailCode=? ";
		return getByHql(hql, planCode);
	}

	@Override
	public DataGrid<Map<String, Object>> listByHospitalDate(String projectCode, String vendorCode, String beginDate,
			String endDate, PageRequest pageable) {
		String sql = "select b.hospitalName,b.code,a.code as CODEDETAIL,b.orderDate,b.num,a.productCode,a.productName,a.dosageFormName,a.model,a.packDesc,a.producerName,a.goodsNum,a.notes "
				+ "from t_order_purchaseorderplan_d a "
				+ "left outer join t_order_purchaseorderplan b on a.purchaseorderplanid=b.id "
				+ "where b.vendorCode=? and to_char(b.createDate,'yyyy-mm-dd')>=? and to_char(a.createDate,'yyyy-mm-dd')<=? "
				+ "and a.status=1";
		
		return super.findBySql(sql, pageable, Map.class, vendorCode,beginDate,endDate);
	}
	
	@Override
	public List<PurchaseOrderPlanDetail> listByIsPass(int isPass) {
		String hql = "from PurchaseOrderPlanDetail popd where popd.isPass=? and (popd.purchaseOrderPlan.status=? or popd.purchaseOrderPlan.status=?)";
		return super.listByHql(hql, null, isPass, PurchaseOrderPlan.Status.effect, PurchaseOrderPlan.Status.cancel);
	}

	@Override
	public List<PurchaseOrderPlanDetail> listGetCancel(String companyCode, boolean isGPO) {
		String hql = "";
		if (isGPO) {
			hql = " purchaseOrderPlan.gpoCode=? ";
		} else {
			hql = " purchaseOrderPlan.vendorCode=? ";
		}
		return super.listByHql("from PurchaseOrderPlanDetail where " + hql
		       + " and (purchaseOrderPlan.status=? or purchaseOrderPlan.status=?) and status=? ", 
		       null, companyCode, PurchaseOrderPlan.Status.uneffect, PurchaseOrderPlan.Status.hcancel, PurchaseOrderPlanDetail.Status.hcancel);
	}

	@Override
	public DataGrid<PurchaseOrderPlanDetail> queryByCode(PageRequest pageable) {
		String sql = "select t.*,p.AUTHORIZENO from t_order_purchaseorderplan_d t left join t_order_purchaseorderplan op on "
		+" t.purchaseorderplanId=op.id left join t_dm_product p on t.productcode=p.code";
		return super.findBySql(sql,pageable,Map.class);
	}
}
