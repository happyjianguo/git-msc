package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IDrugDao;
import com.shyl.msc.dm.entity.Drug;
/**
 * 药品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class DrugDao extends BaseDao<Drug, Long> implements IDrugDao {

	@Override
	public Drug getByCode(String code) {
		return this.getByHql("from Drug where code=?", code);
	}
	@Override
	public Drug getByName(String name, String dosageFormName) {
		return this.getByHql("from Drug where genericName=? and dosageFormName=?", name, dosageFormName);
	}
	
	@Override
	public String getMaxCode(String genericName) {
		List<Drug> drugs = this.limitList("from Drug where genericName =? and length(genericCode)=5",1,null,genericName);
		if(CollectionUtils.isNotEmpty(drugs)){
			return drugs.get(0).getGenericCode();
		}
		String code = (String)this.getBySql("select max(genericCode) from t_dm_drug where genericCode like '1____'", null);
		if (code == null) {
			code = "10001";
		} else {
			code = Integer.toString(Integer.valueOf(code) + 1);
		}
		return code;
	}

	@Override
	public Drug getByNameOnly(String name) {
		List<Drug> drugs = this.limitList("from Drug where genericName=?",1,null,name);
		if(CollectionUtils.isNotEmpty(drugs)){
			return drugs.get(0);
		}
		return null;
	}

	@Override
	public Map<String, String> queryDrugInfoByName(String name, String productCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("select e.name as drugtype0,d.name as drugtype1,c.name as drugtype2,a.name as drugtype3,");
		sql.append("z.baseDrugTypeName,to_char(z.dose) as dose,z.doseunit,z.minunit,z.name as productName,");
		sql.append("f.name as specialdrugtypeName,g.name as absdrugtypeName,z.ddd as ddd,");
		sql.append("x.field2 as ypxz,b.genericName as genericName ");
		sql.append("from t_dm_drug_type a,t_dm_drug b,t_dm_drug_type c,t_dm_drug_type d, ");
		sql.append("t_dm_drug_type e,t_dm_drug_type f,t_dm_drug_type g, t_dm_product_source z,t_set_attribute_item x ");
		sql.append("where a.id=b.pharmacologytype and z.drugid=b.id ");
		sql.append("and c.code = substr(a.code,0,6) ");
		sql.append("and d.code = substr(a.code,0,4) and e.code = substr(a.code,0,2) ");
		sql.append("and b.specialdrugtype = f.id(+) and b.absdrugtype = g.id(+) ");
		sql.append("and z.drugid=b.id  and b.drugtype = x.id and rownum=1 ");
		
		if (StringUtils.isNotEmpty(productCode)) {
			sql.append(" and z.code='").append(productCode).append("' ");
			return this.getBySql(sql.toString(), Map.class);
		} else {
			sql.append(" and ? like '%'||b.genericname||'%' ");
			return this.getBySql(sql.toString(), Map.class, name);
		}
	} 

}
