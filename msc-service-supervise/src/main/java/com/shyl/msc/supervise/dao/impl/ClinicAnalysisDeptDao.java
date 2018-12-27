package com.shyl.msc.supervise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.supervise.dao.IClinicAnalysisDeptDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisDept;


@Repository
public class ClinicAnalysisDeptDao extends BaseDao<ClinicAnalysisDept, Long> implements IClinicAnalysisDeptDao{

	@Override
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		String sql = "select z.hospitalcode as HOSPITALCODE,DEPARTCODE,max(z.hospitalName) as HOSPITALNAME,subStr(max(departname),instr(max(departname),'-')+1) as DEPARTNAME,"
				+ " sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_dept c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode where nvl(c.sum,0)!=0 group by z.hospitalCode,departCode";
		return this.findBySql(sql, page, Map.class);
	}
	
	//大金额处方分析
	@Override
	public DataGrid<Map<String, Object>> groupLargeSum(PageRequest page) {
		String sql = "select z.hospitalCode as HOSPITALCODE,r.departcode as DEPARTCODE,max(z.hospitalName) as HOSPITALNAME,subStr(max(r.departname),instr(max(r.departname),'-')+1) as DEPARTNAME,"
				+ " count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode group by z.hospitalCode,r.departCode";
		return this.findBySql(sql, page, Map.class);
	}


	//抗菌药物使用占比和医院药品收入  科室
	@Override
	public DataGrid<Map<String, Object>> groupAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select c.countType,c.departCode,max(c.departName) as departName,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select '门诊' as countType,b.hospitalCode,b.hospitalName,a.departCode,a.departName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_dept a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select '住院' as countType,b.hospitalCode,b.hospitalName,a.departCode,a.departName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_dept a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.hospitalCode,c.departCode,c.countType";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(page.getQuery());
		String sql = "";
		if(countType==null){
			sql = sql1+ sql2+ hqlUtil.getWhereHql() + " union all " +sql3 + hqlUtil.getWhereHql()+sql4;
		}else if(countType==0){
			sql = sql1+ sql2+ hqlUtil.getWhereHql() +sql4;
		}else{
			sql = sql1+ sql3 + hqlUtil.getWhereHql()+sql4;
		}
		//计数语句
		String cq = "select count(1) from ("+sql+")";
		//添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		//需要设置2次值
		setAliasParameter(sq, hqlUtil.getParams());
		if (countType == null) {
			setAliasParameter(sq, hqlUtil.getParams());
		}
		//设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		DataGrid<Map<String, Object>> result = new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, hqlUtil.getParams()).longValue());
		return result;
	}
	//联合用药分析
	@Override
	public DataGrid<Map<String, Object>> groupCombDrugBy(PageRequest page) {
		String sql1 = "select a.hospitalCode,a.hospitalName,a.departCode,a.departName,a.recipeNum as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.departCode,subStr(max(z.departname),instr(max(z.departname),'-')+1) as DEPARTNAME,h.hospitalCode,max(h.hospitalName) as hospitalName,count(1) as recipeNum,sum(case when z.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when z.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
				+"sum(case when z.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when z.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
				+"sum(case when z.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when z.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
				+"from sup_clinic_recipe z,sup_hospital_zone h where z.hospitalcode = h.hospitalcode ";
		String	sql2 = "group by z.departCode,h.hospitalCode)a,(select sum(a.recipenum) as recipenum,a.departCode, z.hospitalcode from sup_clinic_analysis_dept a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode ";
		String sql3 ="group by a.departCode,z.hospitalCode)b where a.hospitalCode=b.hospitalCode and a.departCode=b.departCode";
		Map<String,Object> query0 = new HashMap<String,Object>();
		Map<String,Object> query1 = new HashMap<String,Object>();
		Map<String,Object> query2 = new HashMap<String,Object>();
		Map<String,Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if(("provinceCode_S_EQ").equals(key)||("cityCode_S_EQ").equals(key)||("countyCode_S_EQ").equals(key)||("orgLevel_S_EQ").equals(key)||("hospitalCode_S_EQ").equals(key)||("treePath_S_RLK").equals(key)){
				query1.put("z#"+key, entry.getValue());
				query2.put("z#"+key, entry.getValue());
			}else if(("cdate_D_GE").equals(key)||("cdate_D_LE").equals(key)){
				query1.put("z#"+key, entry.getValue());
				if(("cdate_D_GE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
					query2.put("month_S_GE",entry.getValue().toString().substring(0, 7));
				}else if(("cdate_D_LE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
					query2.put("month_S_LE",entry.getValue().toString().substring(0, 7));
				}				
			}else{
				query0.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query0);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query1);
		params.putAll(hqlUtil2.getParams());		
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query2);
		params.putAll(hqlUtil3.getParams());
		sql+=sql1+hqlUtil2.getWhereHql();
		if(query0.size()!=0&&(!"".equals((String)query0.get("insuranceDrugType_L_EQ"))||!"".equals((String)query0.get("baseDrugType_L_EQ"))||!"".equals((String)query0.get("absDrugType_L_EQ"))||!"".equals((String)query0.get("specialDrugType_L_EQ"))||!"".equals((String)query0.get("ypxz_S_EQ"))||!"".equals((String)query0.get("auxiliaryType_S_EQ")))){
			sql+="and exists(select 1 from sup_clinic_recipe_item b where a.outsno=b.outsno and a.rpSno=b.rpSno"+hqlUtil.getWhereHql()+")";
		}
		sql+=sql2+hqlUtil3.getWhereHql()+sql3;
		
		//计数语句
		String cq = "select count(1) from ("+sql+")";
		//添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		//需要设置2次值
		setAliasParameter(sq, params);
		//setAliasParameter(sq, params);
		//设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}
	
	protected void setAliasParameter(Query query, Map<String, Object> params) {
		if ((params != null) && (!params.isEmpty()))
			for (String key : params.keySet())
				query.setParameter(key, params.get(key));
	}

	protected String easyuiSort(String sql, String sort, String order) {
		if ((!StringUtils.isEmpty(sort)) && (!StringUtils.isEmpty(order))) {
			int index = sql.indexOf("order by");
			sql = sql.substring(0, index);
			sql = "select * from ("+sql + ") order by ";
			String[] ks = sort.split(",");
			String[] ds = order.split(",");
			for (int i = 0; i < ks.length; i++) {
				sql = sql + ks[i] + " " + ds[i] + ",";
			}
			sql = sql.substring(0, sql.length() - 1);
		}
		return sql;
	}
	//急诊,门诊药品使用分析页面查看
	@Override
	public DataGrid<Map<String, Object>> groupClinicMedicine(PageRequest page) {
		String sql = "select z.hospitalcode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,a.departCode,max(a.departName) as departName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,a.departCode,z.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}
	
	//急诊,门诊药品使用分析页面导出
		@Override
		public List<Map<String, Object>> groupClinicMedicineAll(PageRequest page) {
			String sql = "select z.hospitalcode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,a.departCode,max(a.departName) as departName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,a.departCode,z.hospitalCode";
			return this.listBySql2(sql, page, Map.class); 
		}
}
