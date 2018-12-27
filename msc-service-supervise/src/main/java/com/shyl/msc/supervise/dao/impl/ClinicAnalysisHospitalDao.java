
package com.shyl.msc.supervise.dao.impl;

import java.math.BigDecimal;
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
import com.shyl.msc.supervise.dao.IClinicAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisHospital;

@Repository
public class ClinicAnalysisHospitalDao extends BaseDao<ClinicAnalysisHospital, Long> implements IClinicAnalysisHospitalDao{
	
	@Override
	public DataGrid<Map<String, Object>> selectBy(PageRequest page) {
		String sql = "select sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_hospital c left join sup_hospital_zone z  on c.hospitalCode=z.hospitalCode ";
		return this.findBySql(sql, page, Map.class);
	}
	
	@Override
	public DataGrid<Map<String, Object>> groupProvinceBy(PageRequest page) {
		String sql = "select z.provinceCode as PROVINCECODE,max(z.provinceName) as PROVINCENAME, sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_hospital c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode group by z.provinceCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupCityBy(PageRequest page) {
		String sql = "select z.cityCode as CITYCODE,max(z.cityName) as CITYNAME, sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ "sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_hospital c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode  group by z.cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupCountyBy(PageRequest page) {
		String sql = "select z.countycode as COUNTYCODE,max(z.countyName) as COUNTYNAME, sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_hospital c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode group by z.countyCode";
		return this.findBySql(sql, page, Map.class);
	}
	
	@Override
	public DataGrid<Map<String, Object>> groupHospitalBy(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "drugSum")));
		String sql = "select z.hospitalcode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION, sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(absRecipe3Num) as ABSRECIPE3NUM,sum(c.sum) as SUM from sup_clinic_analysis_hospital c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode group by z.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}
	//大金额处方分析
	@Override
	public DataGrid<Map<String, Object>> selectLargeSum(PageRequest page) {
		String sql = "select count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalCode=z.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupProvinceLargeSum(PageRequest page) {
		String sql = "select z.provinceCode,max(z.provinceName) as provinceName,count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode "
				+ " group by z.provinceCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupCityLargeSum(PageRequest page) {
		String sql = "select z.citycode as CITYCODE,max(z.cityName) as CITYNAME,count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode "
				+ " group by z.cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupCountyLargeSum(PageRequest page) {
		String sql = "select z.countycode as COUNTYCODE,max(z.countyName) as COUNTYNAME,count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode "
				+ " group by z.countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHospitalLargeSum(PageRequest page) {
		String sql = "select z.hospitalCode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode "
				+ " group by z.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}
	
	@Override
	public List<Map<String, Object>> selectRecipeDefault() {
		String sql = "select sum(recipeNum) as RECIPENUM from sup_clinic_analysis_hospital where month >='2013-01' and month<='2017-06'";
		return this.listBySql(sql, null, Map.class,null);
	}
	//抗菌药物使用占比和医院药品收入  全部
	@Override
	public DataGrid<Map<String, Object>> selectAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c";
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
	
	//抗菌药物使用占比和医院药品收入  省
	@Override
	public DataGrid<Map<String, Object>> groupProvinceAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select c.provinceCode,max(c.provinceName) as provinceName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select b.provinceCode,b.provinceName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select b.provinceCode,b.provinceName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.provinceCode";
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
	
	//抗菌药物使用占比和医院药品收入  市
	@Override
	public DataGrid<Map<String, Object>> groupCityAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select c.provinceCode,max(c.provinceName) as provinceName,c.citycode,max(c.cityName) as cityName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.provinceCode,c.cityCode";
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
		
	//抗菌药物使用占比和医院药品收入  区
	@Override
	public DataGrid<Map<String, Object>> groupCountyAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select c.provinceCode,max(c.provinceName) as provinceName,c.citycode,max(c.cityName) as cityName,c.countycode,max(c.countyName)as countyName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.provinceCode,c.cityCode,c.countycode";
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
	//抗菌药物使用占比和医院药品收入  医院
	@Override
	public DataGrid<Map<String, Object>> groupHospitalAbsDrugUser(PageRequest page,Integer countType) {
		String sql1 = " select c.provinceCode,max(c.provinceName) as provinceName,c.citycode,max(c.cityName) as cityName,c.countycode,max(c.countyName) as countyName,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,b.hospitalCode,b.hospitalName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,b.hospitalCode,b.hospitalName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.provinceCode,c.cityCode,c.countycode,c.hospitalCode";
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
	//抗菌药物使用占比和医院药品收入 月份
	@Override
	public DataGrid<Map<String, Object>> groupMonthBy(PageRequest page,Integer countType) {
		String sql1 = " select c.month,c.provinceCode,max(c.provinceName) as provinceName,c.citycode,max(c.cityName) as cityName,c.countycode,max(c.countyName) as countyName,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
		String sql2 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,b.hospitalCode,b.hospitalName,a.month,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode ";
		String sql3 = "select b.provinceCode,b.provinceName,b.citycode,b.cityName,b.countycode,b.countyName,b.hospitalCode,b.hospitalName,a.month,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_hospital a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
		String sql4 = ") c group by c.provinceCode,c.cityCode,c.countycode,c.hospitalCode,c.month";
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
	public DataGrid<Map<String, Object>> selectCombDrugBy(PageRequest page) {
		Map<String, Object> query = page.getQuery();
		String sql1 = "select a.recipeNum    as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2, "
					+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3, sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5, "
					+"sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6  from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		String sql2 = ") a,(select sum(a.recipenum) as recipenum from sup_clinic_analysis_hospital a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode";
		String sql3=")b";
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
				query1.put("a#"+key, entry.getValue());
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
			sql+="and exists(select 1 from sup_clinic_recipe_item b where a.outsno=b.outsno and a.rpSno=b.rpSno "+hqlUtil.getWhereHql()+")";
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

	@Override
	public DataGrid<Map<String, Object>> groupProvinceCombDrugBy(PageRequest page) {
		String sql1 = "select a.provinceCode,a.provinceName,a.recipeNum as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.provinceCode,max(z.provinceName) as provinceName,count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
				+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
				+"sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
				+" from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		String sql2 = "group by z.provinceCode) a,(select sum(a.recipenum) as recipenum, z.provinceCode from sup_clinic_analysis_hospital a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode ";
		String sql3 = "group by z.provinceCode) b where a.provinceCode=b.provinceCode";
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
				query1.put("a#"+key, entry.getValue());
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
			sql+="and exists(select 1 from sup_clinic_recipe_item b where a.outsno=b.outsno and a.rpSno=b.rpSno "+hqlUtil.getWhereHql()+")";
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

	@Override
	public DataGrid<Map<String, Object>> groupCityCombDrugBy(PageRequest page) {
		String sql1 = "select a.cityCode,a.cityName as cityName,a.recipeNum    as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.cityCode,max(z.cityName) as cityName,z.provinceCode,count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
				+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
				+"sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
				+"from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		String sql2 = "group by z.provinceCode,z.cityCode)a,(select sum(a.recipenum) as recipenum, z.cityCode,z.provinceCode from sup_clinic_analysis_hospital a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode";
		String sql3 = "group by z.provinceCode,z.cityCode) b where a.provinceCode=b.provinceCode and a.cityCode=b.cityCode";
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
				query1.put("a#"+key, entry.getValue());
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

	@Override
	public DataGrid<Map<String, Object>> groupCountyCombDrugBy(PageRequest page) {
		String sql1 = "select a.countyCode,a.countyName,a.recipeNum as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.countyCode,max(z.countyName) as countyName,z.provinceCode,z.cityCode,count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
				+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
				+"sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
				+"from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";			
		String sql2 ="group by z.provinceCode,z.cityCode,z.countyCode)a,( select sum(a.recipenum) as recipenum,z.countyCode,z.provinceCode,z.cityCode from sup_clinic_analysis_hospital a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode";
		String sql3 = "group by z.provinceCode,z.cityCode,z.countyCode)b where a.provinceCode=b.provinceCode and a.countyCode=b.countyCode";
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
				query1.put("a#"+key, entry.getValue());
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

	@Override
	public DataGrid<Map<String, Object>> groupHospitalCombDrugBy(PageRequest page) {
		String sql1 = "select a.hospitalCode,a.hospitalName,a.recipeNum as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.hospitalcode,max(z.hospitalName) as hospitalName,z.cityCode,count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
				+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
				+"sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
				+"from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		String sql2 ="group by z.hospitalcode,z.cityCode)a,(select sum(a.recipenum) as recipenum,z.cityCode, z.hospitalcode from sup_clinic_analysis_hospital a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode ";
		String sql3 = "group by z.hospitalcode,z.cityCode)b where a.cityCode=b.cityCode and a.hospitalCode=b.hospitalcode";
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
				query1.put("a#"+key, entry.getValue());
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
	
	//总处方数
	@Override
	public List<Map<String, Object>> selectRecipeNum(String monthBegin,String monthEnd) {
		String sql = "select count(1) as RECIPENUM from sup_clinic_recipe where to_char(cdate,'yyyy-mm-dd') >=? and to_char(cdate,'yyyy-mm-dd')<=?";
		return this.listBySql(sql, null, Map.class, monthBegin,monthEnd);
	}
	//急诊,门诊药品使用分析页面查看
	@Override
	public DataGrid<Map<String, Object>> selectClinicMedicine(PageRequest page) {
		String sql = "select count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType";
		return this.findBySql(sql, page, Map.class); 
	}

	@Override
	public DataGrid<Map<String, Object>> groupProvinceClinicMedicine(PageRequest page) {
		String sql = "select z.provinceCode,max(z.provinceName) as provinceName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.provinceCode";
		return this.findBySql(sql, page, Map.class); 
	}

	@Override
	public DataGrid<Map<String, Object>> groupCityClinicMedicine(PageRequest page) {
		String sql = "select z.cityCode,max(z.cityName) as cityName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.cityCode";
		return this.findBySql(sql, page, Map.class); 
	}

	@Override
	public DataGrid<Map<String, Object>> groupCountyClinicMedicine(PageRequest page) {
		String sql = "select z.countyCode,max(z.countyName) as countyName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.countyCode";
		return this.findBySql(sql, page, Map.class); 
	}

	@Override
	public DataGrid<Map<String, Object>> groupHospitalClinicMedicine(PageRequest page) {
		String sql = "select z.hospitalCode,max(z.hospitalName) as hospitalName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno, 1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.hospitalCode";
		return this.findBySql(sql, page, Map.class); 
	}
	
	//急诊,门诊药品使用分析页面导出功能
	@Override
	public List<Map<String, Object>> selectClinicMedicineAll(PageRequest page) {
		String sql = "select count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupProvinceClinicMedicineAll(PageRequest page) {
		String sql = "select z.provinceCode,max(z.provinceName) as provinceName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.provinceCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupCityClinicMedicineAll(PageRequest page) {
		String sql = "select z.cityCode,max(z.cityName) as cityName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.cityCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupCountyClinicMedicineAll(PageRequest page) {
		String sql = "select z.countyCode,max(z.countyName) as countyName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.countyCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHospitalClinicMedicineAll(PageRequest page) {
		String sql = "select z.hospitalCode,max(z.hospitalName) as hospitalName,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,z.hospitalCode";
		return this.listBySql2(sql, page, Map.class); 
	}
	
	public List<Map<String, Object>> groupMonthByHospital(PageRequest page) {
		return this.listBySql2("select sum(visitorNum) as outNum,sum(sum) as sum,sum(drugSum) as drugSum,"
				+ "sum(sum)/sum(visitorNum) as rjsum,sum(drugSum)/sum(visitorNum) as rjDrugSum,"
				+ "z.hospitalCode as code,max(z.hospitalName) as name,month "
				+ " from sup_clinic_analysis_hospital a,sup_hospital_zone z where a.hospitalcode=z.hospitalcode group by z.hospitalcode,month order by z.hospitalcode,month", page, Map.class);
	}
	
	public List<Map<String, Object>> groupMonthByCountyCode(PageRequest page) {
		return this.listBySql2("select sum(visitorNum) as outNum,sum(sum) as sum,sum(drugSum) as drugSum,"
				+ "sum(sum)/sum(visitorNum) as rjsum,sum(drugSum)/sum(visitorNum) as rjDrugSum,"
				+ "z.countyCode as code,max(z.countyName) as name,month "
				+ " from sup_clinic_analysis_hospital a,sup_hospital_zone z where a.hospitalcode=z.hospitalcode group by z.countyCode,month order by z.countyCode,month", page, Map.class);
		
	}
	
	public DataGrid<Map<String, Object>> groupByColumn(PageRequest page, String groupColumn) {
		return this.findBySql("select "+groupColumn+" as code,max(z.provinceCode) as provinceCode,max(z.cityCode) as cityCode,max(countyCode) as countyCode"
				+ ",max(z.provinceName) as provinceName,max(z.cityName) as cityName,max(countyName) as countyName,"
				+ "max(a.hospitalCode) as hospitalCode,max(a.departCode) as departCode,max(a.doctorCode) as doctorCode, "
				
				+ "max(a.hospitalName) as hospitalName,max(a.departName) as departName,max(a.doctorName) as doctorName "
				+ "from sup_clinic_analysis_doctor a inner join sup_hospital_zone z on a.hospitalcode=z.hospitalcode group by "+groupColumn, page, Map.class);
	}

	@Override
	public BigDecimal getVisitorNum(PageRequest page) {
		String sql = "select count(distinct(a.outsno)) as visitorNum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if (list.size()>0) {
			return (BigDecimal)list.get(0).get("VISITORNUM");
		} else {
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getRegRecipeNum(PageRequest page) {
		String sql = "select count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if (list.size()>0) {
			return (BigDecimal)list.get(0).get("REGRECIPENUM");
		} else {
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDrugNum(PageRequest page) {
		String sql = "select count(distinct a.outsno||a.pzm) as drugNum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if (list.size()>0) {
			return (BigDecimal)list.get(0).get("DRUGNUM");
		} else {
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDrugSum(PageRequest page) {
		String sql = "select sum(a.drugSum) as drugSum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if (list.size()>0) {
			return (BigDecimal)list.get(0).get("DRUGSUM");
		} else {
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getSum(PageRequest page) {
		String sql = "select sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if (list.size()>0) {
			return (BigDecimal)list.get(0).get("SUM");
		} else {
			return new BigDecimal(0d);
		}
	}

	@Override
	public List<Map<String, Object>> getCombDurgNum(PageRequest page) {
		String sql = "select count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2, "
					+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3, sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5, "
					+"sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6  from sup_clinic_recipe a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		Map<String,Object> params = page.getQuery();
		Map<String,Object> query = new HashMap<String,Object>();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if(("month_S_GE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
				query.put("cdate_D_GE",entry.getValue().toString()+"-01");
			}else if(("month_S_LE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
				query.put("cdate_D_LE",entry.getValue().toString()+"-30");
			}else{
				query.put(key, entry.getValue());
			}
		}
		page.setQuery(query);
			if(query.size()!=0&&(null!=query.get("insuranceDrugType_L_EQ")||null!=query.get("baseDrugType_L_EQ")||null!=query.get("absDrugType_L_EQ")||null!=query.get("specialDrugType_L_EQ")||null!=query.get("ypxz_S_EQ")||null!=query.get("auxiliaryType_S_EQ"))){
			sql+="and exists(select 1 from sup_clinic_recipe_item b where a.outsno=b.outsno and a.rpSno=b.rpSno) ";
		}
		List<Map<String, Object>> rel = this.listBySql2(sql, page, Map.class);
		return rel;
	}

	@Override
	public BigDecimal getRecipeNum(PageRequest page) {
		String sql = "select sum(a.recipeNum) as recipenum from sup_clinic_analysis_doctor a, sup_hospital_zone z where a.hospitalcode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		Object  recipenum = list.get(0).get("RECIPENUM");
		if (list.size() > 0) {
			if(recipenum!=null){
				return (BigDecimal)recipenum;
			}else{
				return new BigDecimal(0d);
			}
		} else {
			return new BigDecimal(0d);
		}

	}
	@Override
	public BigDecimal getLargeRecipeNum(PageRequest page) {
		String sql = "select count(*) as largeSumRecipe from sup_clinic_recipe a,sup_hospital_zone z where a.hospitalCode=z.hospitalCode";
		Map<String,Object> params = page.getQuery();
		Map<String,Object> query = new HashMap<String,Object>();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if(("month_S_GE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
				query.put("cdate_D_GE",entry.getValue().toString()+"-01");
			}else if(("month_S_LE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
				query.put("cdate_D_LE",entry.getValue().toString()+"-30");
			}else{
				query.put(key, entry.getValue());
			}
		}
		page.setQuery(query);

		List<Map<String,Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("LARGESUMRECIPE");
		}else{
			return new BigDecimal(0d);
		}	
	}
}
