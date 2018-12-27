package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.supervise.entity.Medicine;
import com.shyl.msc.supervise.entity.MedicineHospital;
import com.shyl.msc.supervise.dao.IMedicineHospitalDao;

@Repository
public class MedicineHospitalDao extends BaseDao<MedicineHospital, Long> implements IMedicineHospitalDao {

	public DataGrid<Map<String, Object>> countBaseDrugByHospital(PageRequest page) {

		String basekey = " basedrugtype is null";
		// 判断基药
		if (page.getQuery() != null && !StringUtils.isEmpty(page.getQuery().get("baseDrugType_L_EQ"))) {
			basekey = " nvl(basedrugtype,0) != " + page.getQuery().get("baseDrugType_L_EQ");
			page.getQuery().remove("baseDrugType_L_EQ");
		}
		HqlUtil util = new HqlUtil();
		util.addFilter(page.getQuery());
		String where = util.getWhereHql();
		Map<String, Object> map = util.getParams();

		String sql = "select max(b.hospitalName) as hospitalName,b.hospitalcode,max(c.countyName) as countyName,c.countycode,nvl(sum(case when " + basekey
				+ " then 0 else 1 end),0) as basedrugnum,count(1) as num," + " nvl(round(sum(case when " + basekey
				+ "  then 0 else 1 end)/count(1)*100,3),0) as jyzb"
				+ " from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c where a.code=b.productcode and b.hospitalcode=c.hospitalcode "
				+ where + " group by b.hospitalcode,c.countyCode";
		SQLQuery query = getSession().createSQLQuery(sql);
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					query.setParameter(key, map.get(key));
		}
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		query.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid(query.list(), page, countBySql(cq, map, null).longValue());
	}

	public DataGrid<Map<String, Object>> countBaseDrugByCounty(PageRequest page) {

		String basekey = " basedrugtype is null";
		// 判断基药
		if (page.getQuery() != null && !StringUtils.isEmpty(page.getQuery().get("baseDrugType_L_EQ"))) {
			basekey = " nvl(basedrugtype,0) != " + page.getQuery().get("baseDrugType_L_EQ");
			page.getQuery().remove("baseDrugType_L_EQ");
		}
		HqlUtil util = new HqlUtil();
		util.addFilter(page.getQuery());
		String where = util.getWhereHql();
		Map<String, Object> map = util.getParams();

		String sql = "select max(c.countyName) as countyName,c.countycode,nvl(sum(case when " + basekey
				+ " then 0 else 1 end),0) as basedrugnum,count(1) as num," + " nvl(round(sum(case when " + basekey
				+ "  then 0 else 1 end)/count(1)*100,3),0) as jyzb"
				+ " from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c where a.code=b.productcode and b.hospitalcode=c.hospitalcode "
				+ where + " group by c.countycode";
		SQLQuery query = getSession().createSQLQuery(sql);
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					query.setParameter(key, map.get(key));
		}
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		query.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid(query.list(), page, countBySql(cq, map, null).longValue());
	}
	
	public DataGrid<Map<String, Object>> countBaseDrugByZone(PageRequest page) {

		String basekey = " basedrugtype is null";
		// 判断基药
		if (page.getQuery() != null && !StringUtils.isEmpty(page.getQuery().get("baseDrugType_L_EQ"))) {
			basekey = " nvl(basedrugtype,0) != " + page.getQuery().get("baseDrugType_L_EQ");
			page.getQuery().remove("baseDrugType_L_EQ");
		}
		String sql = "select nvl(sum(case when " + basekey + " then 0 else 1 end),0) as basedrugnum,count(1) as num, "
				+ "nvl(round(sum(case when " + basekey + " then 0 else 1 end)/count(1)*100,3),0) as jyzb "
				+ "from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c "
				+ "where a.code=b.productcode  and b.hospitalcode=c.hospitalcode";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(page.getQuery());
		String where = hqlUtil.getWhereHql();
		sql+=where;
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		SQLQuery sq = getSession().createSQLQuery(sql);
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> map = hqlUtil.getParams();
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					sq.setParameter(key, map.get(key));
		}
		return new DataGrid(sq.list(), page, countBySql(cq,hqlUtil.getParams()).longValue());
	}

	public DataGrid<Map<String, Object>> queryByPage(PageRequest page) {

		String sql = "select t.id,t.INTERNALCODE,t.hospitalName,t.productCode,t.productname,t.genericName,t.dosageFormName,t.producerName,t.model,t.packDesc,"
				+ " a.authorizeNo,a.importFileNo,t.auxiliaryType,a.isGPOPurchase,a.gpoName,a.baseDrugType,a.absDrugType,a.isUrgent,a.isDisabled,a.ddd,(case when a.code is null then 0 else 1 end) as iscompare "
				+ " from sup_medicine_hospital t left join sup_hospital_zone c on  t.hospitalCode=c.hospitalCode "
				+ " left join  sup_medicine a on t.productcode=a.code ";
		return this.findBySql(sql, page, Map.class);
	}
	
	//医院药品目录导出
	@Override
	public List<Map<String, Object>> queryByAll(PageRequest page) {
		String sql = "select t.id,t.INTERNALCODE,t.hospitalName,t.productCode,t.productname,t.genericName,t.dosageFormName,t.producerName,t.model,t.packDesc,"
				+ " a.authorizeNo,a.importFileNo,t.auxiliaryType,a.isGPOPurchase,a.gpoName,a.baseDrugType,a.absDrugType,a.isUrgent,a.isDisabled,a.ddd,(case when t.productcode is null then 0 else 1 end) as iscompare "
				+ " from sup_medicine_hospital t left join sup_hospital_zone c on  t.hospitalCode=c.hospitalCode "
				+ " left join  sup_medicine a on t.productcode=a.code ";
		return this.listBySql2(sql, page, Map.class);
	}
		
	@Override
	public DataGrid<Map<String, Object>> countDrugCatalogByCounty(PageRequest page) {
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(page.getQuery());
		String where = hqlUtil.getWhereHql();
		String sql = "select nvl(sum(case when a.drugtype='5131' then 1 else 0 end),0) as chinadrugnum,"
				+ " nvl(sum(case when a.drugtype='5130' then 1 else 0 end),0) as westdrugnum,"
				+ " max(c.countyCode) as countyCode,max(c.countyName) as countyName,"
				+ " count(1) as num 	from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c "
				+ " where a.code=b.productcode  and b.hospitalcode=c.hospitalcode "+where+ " group by c.countyCode";
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		SQLQuery sq = getSession().createSQLQuery(sql);
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> map = hqlUtil.getParams();
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					sq.setParameter(key, map.get(key));
		}
		return new DataGrid(sq.list(), page, countBySql(cq,hqlUtil.getParams()).longValue());
		// return this.findBySql(sql, page, Map.class);
	}
	@Override
	public DataGrid<Map<String, Object>> countDrugCatalogByHospital(PageRequest page) {
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(page.getQuery());
		String where = hqlUtil.getWhereHql();
		String sql = "select nvl(sum(case when a.drugtype='5131' then 1 else 0 end),0) as chinadrugnum,"
				+ " nvl(sum(case when a.drugtype='5130' then 1 else 0 end),0) as westdrugnum,"
				+ " max(b.hospitalcode) as hospitalcode,max(b.hospitalname) as hospitalname,max(c.countyCode) as countyCode,max(c.countyName) as countyName,"
				+ " count(1) as num 	from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c "
				+ " where a.code=b.productcode  and b.hospitalcode=c.hospitalcode "+where+ " group by b.hospitalcode,c.countyCode";
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		SQLQuery sq = getSession().createSQLQuery(sql);
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> map = hqlUtil.getParams();
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					sq.setParameter(key, map.get(key));
		}
		return new DataGrid(sq.list(), page, countBySql(cq,hqlUtil.getParams()).longValue());
		// return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> countDrugCatalogByZone(PageRequest page) {
		String sql = "select count(1) as num, nvl(sum(case when a.drugtype='5131' then 1 else 0 end),0) as chinadrugnum,"
				+ " nvl(sum(case when a.drugtype='5130' then 1 else 0 end),0) as westdrugnum"
				+ " from sup_medicine a,sup_medicine_hospital b,sup_hospital_zone c"
				+ " where a.code=b.productcode  and b.hospitalcode=c.hospitalcode ";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(page.getQuery());
		sql+=hqlUtil.getWhereHql();
		String cq = getCountHql("select count(1) from (" + sql + ")", true);
		SQLQuery sq = getSession().createSQLQuery(sql);
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> map = hqlUtil.getParams();
		if ((map != null) && (map.size() > 0)) {
			if ((map != null) && (!map.isEmpty()))
				for (String key : map.keySet())
					sq.setParameter(key, map.get(key));
		}
		return new DataGrid(sq.list(), page, countBySql(cq,hqlUtil.getParams()).longValue());
		// return this.findBySql(sql, page, Map.class);
	}
	
	@Override
	public MedicineHospital getByCode(String code) {
		String hql = "from MedicineHospital a where a.productCode = ?";
		return this.getByHql(hql, code);
	}
	
}
