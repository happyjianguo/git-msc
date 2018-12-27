package com.shyl.msc.b2b.hospital.dao.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.hospital.dao.IPurchaseRecordDao;
import com.shyl.msc.b2b.hospital.entity.PurchaseRecord;
/**
 * 采购记录上报DAO实现类
 * 
 *
 */
@Repository
public class PurchaseRecordDao extends BaseDao<PurchaseRecord, Long> implements IPurchaseRecordDao {
	@Override
	public List<Map<String, Object>> query(String startMonth, String toMonth, String hospitalCode) {
		String sql = "select l.MONTH,l.HOSPITALNAME,p.CODE,p.NAME,p.DOSAGEFORMNAME,p.MODEL,p.PACKDESC,"
				+ "p.PRODUCERNAME,l.PRODUCTTRANID,l.PLATFORM,l.NUM,l.AMT from t_hospital_purchaserecord l "
				+ "left outer join t_dm_product p on l.productId=p.id "
				+ "where 1=1 ";
		if(startMonth != null && !("").equals(startMonth)){
			sql += "and l.month>='"+startMonth+"' ";
		}
		if(toMonth != null && !toMonth.equals("")){
			sql += "and l.month<='"+toMonth+"' ";
		}
		if(hospitalCode != null && !hospitalCode.equals("")){
			sql += "and l.hospitalCode='"+hospitalCode+"' ";
		}
		sql += "order by l.month desc,l.hospitalCode asc, l.productId asc";
		return super.listBySql(sql, null, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {
		String sql = "select l.MONTH,l.HOSPITALNAME,p.CODE,p.NAME,p.DOSAGEFORMNAME,p.MODEL,p.PACKDESC,"
				+ "p.PRODUCERNAME,l.PRODUCTTRANID,l.PLATFORM,l.NUM,l.AMT,l.HOSPITALCODE from t_hospital_purchaserecord l "
				+ "left outer join t_dm_product p on l.productId=p.id ";
		return this.findBySql(sql, page, Map.class);
	}

}
