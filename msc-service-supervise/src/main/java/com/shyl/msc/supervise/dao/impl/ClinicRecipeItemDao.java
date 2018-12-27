package com.shyl.msc.supervise.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IClinicRecipeItemDao;
import com.shyl.msc.supervise.entity.ClinicRecipeItem;

@Repository
public class ClinicRecipeItemDao extends BaseDao<ClinicRecipeItem, Long> implements IClinicRecipeItemDao {

	@Override
	public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {
		String sql = "select r.productCode,r.productName,r.dosageFormName,r.model,r.packDesc,r.producerName,r.sum,r.frequencyName,r.dosaOne,"
				+ " r.dosaOneUnit,r.days,m.isGPOPurchase from sup_clinic_recipe_item r left join sup_medicine m on r.productCode=m.code";
		return this.findBySql(sql, page, Map.class);
	}

}
