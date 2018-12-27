package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IMonthlyPurchaseDao;
import com.shyl.msc.supervise.entity.MonthlyPurchase;
@Repository
public class MonthlyPurchaseDao extends BaseDao<MonthlyPurchase,Long> implements IMonthlyPurchaseDao{

	@Override
	public List<Map<String, Object>> query(PageRequest page) {
		String sql = "select l.month,l.hospitalCode,l.hospitalName,l.code,l.name,l.dosageFormname,l.model,l.packDesc,l.producerName,l.gpoNum,p.isGPOPurchase, "
				+ "l.gpoAmt,l.notGpoNum,l.notGpoAmt,l.num,l.amt,sum(h.num) as outNum,sum(h.sum) as outSum from sup_his_monthlyPurchase l left outer join sup_medicine p on l.code=p.code "
				+ "left join sup_drug_analysis_hospital h on l.code=h.productCode and h.month=TO_CHAR(ADD_MONTHS(TO_DATE(l.month,'YYYY-MM'),-1),'YYYY-MM') and l.hospitalCode=h.hospitalCode group by "
				+ " l.month,l.hospitalCode,l.hospitalName,l.code,l.name,l.dosageFormname,l.model,l.packDesc,l.producerName,l.gpoNum,l.gpoAmt,l.notGpoNum,l.notGpoAmt,l.num,l.amt,p.isGPOPurchase order by l.month desc,l.hospitalCode asc, l.code asc";
		return super.listBySql2(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {
		String sql = "select l.month,l.hospitalCode,l.hospitalName,l.code,l.name,l.dosageFormname,l.model,l.packDesc,l.producerName,l.gpoNum,p.isGPOPurchase, "
				+ "l.gpoAmt,l.notGpoNum,l.notGpoAmt,l.num,l.amt,sum(h.num) as outNum,sum(h.sum) as outSum from sup_his_monthlyPurchase l left outer join sup_medicine p on l.code=p.code "
				+ "left join sup_drug_analysis_hospital h on l.code=h.productCode and h.month=TO_CHAR(ADD_MONTHS(TO_DATE(l.month,'YYYY-MM'),-1),'YYYY-MM') and l.hospitalCode=h.hospitalCode group by "
				+ " l.month,l.hospitalCode,l.hospitalName,l.code,l.name,l.dosageFormname,l.model,l.packDesc,l.producerName,l.gpoNum,l.gpoAmt,l.notGpoNum,l.notGpoAmt,l.num,l.amt,p.isGPOPurchase";
		return super.findBySql(sql, page, Map.class);
	}

}
