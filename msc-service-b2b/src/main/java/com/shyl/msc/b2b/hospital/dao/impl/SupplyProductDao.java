package com.shyl.msc.b2b.hospital.dao.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.hospital.dao.ISupplyProductDao;
import com.shyl.msc.b2b.hospital.entity.SupplyProduct;
/**
 * 供应药品上报DAO实现类
 * 
 *
 */
@Repository
public class SupplyProductDao extends BaseDao<SupplyProduct, Long> implements ISupplyProductDao {

	@Override
	public List<Map<String, Object>> query(String startMonth, String toMonth, String hospitalCode) {
		String sql = "select l.MONTH,l.HOSPITALNAME,p.CODE,p.NAME,p.DOSAGEFORMNAME,p.MODEL,p.PACKDESC,p.ISGPOPURCHASE, "
				+ "p.PRODUCERNAME,l.VENDORCODE,l.VENDORNAME,(case when t.internalcode is null then 0 else 1 end) as iscompare from t_hospital_supplyproduct l "
				+ "left outer join t_dm_product p on l.productId=p.id left join t_dm_goods_hospital t on p.code=t.productcode and l.hospitalCode=t.hospitalCode "
				+ "where 1=1 ";
		if(startMonth != null && !startMonth.equals("")){
			sql += "and l.month>=='"+startMonth+"' ";
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
		String sql = "select l.id,l.month,l.hospitalName,p.code,p.name,p.dosageFormName,p.model,p.packDesc,p.producerName,p.isGpoPurchase,"
				+ " l.vendorCode,l.vendorName,t.internalcode from t_hospital_supplyproduct l left outer join t_dm_product p on l.productId=p.id "
				+ " left join t_dm_goods_hospital t on p.code=t.productcode and l.hospitalCode=t.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}
}
