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
import com.shyl.msc.supervise.dao.IClinicAnalysisDoctorDao;
import com.shyl.msc.supervise.entity.ClinicAnalysisDoctor;

@Repository
public class ClinicAnalysisDoctorDao extends BaseDao<ClinicAnalysisDoctor, Long> implements IClinicAnalysisDoctorDao{

	@Override
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		String sql = "select z.hospitalcode as HOSPITALCODE,DEPARTCODE,DOCTORCODE,max(z.hospitalName) as HOSPITALNAME,subStr(max(departname),instr(max(departname),'-')+1) as DEPARTNAME,"
				+ " max(doctorName) as DOCTORNAME,sum(drugSum) as DRUGSUM,sum(visitorNum) as VISITORNUM,round(sum(drugSum)/sum(visitorNum),1) as AVGDRUGSUM,"
				+ " sum(recipeNum) as RECIPENUM,sum(injectionNum) as INJECTIONNUM,round(sum(injectionNum)*100/sum(recipeNum),1) as INJECTIONPROPORTION,"
				+ " sum(intraInjectionNum)as INTRAINJECTIONNUM,round(sum(intraInjectionNum)*100/sum(visitorNum),1) as INTRANPROPORTION,sum(absRecipeNum) as ABSRECIPENUM,sum(absRecipe1Num) as ABSRECIPE1NUM,"
				+ " sum(absRecipe2Num) as ABSRECIPE2NUM,sum(c.absRecipe3Num) as ABSRECIPE3NUM,sum(sum) as SUM from sup_clinic_analysis_doctor c left join sup_hospital_zone z on c.hospitalCode=z.hospitalCode group by z.hospitalCode,departCode,doctorCode";
		return this.findBySql(sql, page, Map.class);
	}
	
	/*@Override
	public DataGrid<Map<String, Object>> groupByUrgent(PageRequest page) {
		String sql1 = "select a.doctorCode as doctorCode,a.departCode as departCode,a.departName as departName,a.hospitalCode as hospitalcode,a.hospitalName as hospitalName,a.doctorName as doctorName, a.sum as sum,a.drugsum AS drugSum,c.regRecipeNum AS regRecipeNum, a.drugsum/a.sum as drugRatio,a.ClinicType,b.drugNum,c.visitorNum from (select a.doctorCode as doctorCode,a.departCode as departCode,subStr(max(departname),instr(max(departname),'-')+1) as DEPARTNAME,max(a.doctorName) as doctorName,max(a.hospitalName) as hospitalName,a.hospitalCode as hospitalCode, sum(a.sum) as sum,sum(a.drugSum) as drugSum, a.ClinicType from sup_clinic_recipe a, sup_hospital_zone z where a.hospitalcode = z.hospitalcode  and exists (select 1 from sup_clinic_recipe_item b  where a.outsno = b.outsno";
		String sql2 = " group by a.doctorCode, a.ClinicType,a.departCode,a.hospitalCode) a,(select doctorCode, count(1) as drugNum, clinicType,departCode,hospitalCode from (select a.doctorCode,a.clinicType,a.departCode,a.hospitalCode from sup_clinic_recipe_item m,sup_hospital_zone  z,sup_clinic_recipe  a where a.hospitalcode = z.hospitalcode and m.outsno = a.outsno and exists (select 1 from sup_clinic_recipe_item b where a.outsno = b.outsno";
		String sql3 = " group by a.doctorCode,m.outsno||m.productName||m.model||m.dosageformname,a.clinicType,a.departCode,a.hospitalCode) group by doctorCode, clinicType,departCode,hospitalCode) b,(select sum(visitorNum) as visitorNum,sum(regRecipeNum) as regRecipeNum, a.clinicType, a.hospitalCode, a.doctorCode,a.departCode from sup_clinic_analysis_doctor a, sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		String sql4 = " group by a.clinicType, a.departCode, a.hospitalCode,a.doctorCode) c where a.hospitalcode=b.hospitalcode and a.hospitalcode= c.hospitalcode and a.doctorCode = b.doctorCode  and a.doctorCode = c.doctorCode and  a.departCode = b.departCode and a.departCode = c.departCode and a.ClinicType = b.ClinicType  and a.ClinicType = c.ClinicType";
		Map<String,Object> query0 = new HashMap<String,Object>();
		Map<String,Object> query1 = new HashMap<String,Object>();
		Map<String,Object> query2 = new HashMap<String,Object>();
		Map<String,Object> query3 = new HashMap<String,Object>();
		Map<String,Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if(("provinceCode_S_EQ").equals(key)||("cityCode_S_EQ").equals(key)||("countyCode_S_EQ").equals(key)){
				query1.put(key, entry.getValue());
			}else if(("treePath_S_RLK").equals(key)||("hospitalCode_S_EQ").equals(key)||("clinicType_L_EQ").equals(key)){
				query1.put("a#"+key, entry.getValue());
			}else if(("cdate_D_GE").equals(key)||("cdate_D_LE").equals(key)){
				query2.put("a#"+key, entry.getValue());
				if(("cdate_D_GE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
					query3.put("month_S_GE",entry.getValue().toString().substring(0, 7));
				}else if(("cdate_D_LE").equals(key)&&entry.getValue()!=null&&entry.getValue()!=""){
					query3.put("month_S_LE",entry.getValue().toString().substring(0, 7));
				}
			}else if(("orgLevel_S_EQ").equals(key)){
				query2.put("z#"+key, entry.getValue());
			}else{
				query0.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query0);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query3);
		params.putAll(hqlUtil3.getParams());
		sql= sql1+hqlUtil.getWhereHql()+")"+hqlUtil2.getWhereHql()+sql2+hqlUtil.getWhereHql()+")"+
		hqlUtil2.getWhereHql()+sql3+hqlUtil3.getWhereHql()+sql4;
		HqlUtil hqlUtil4 = new HqlUtil();
		hqlUtil4.addFilter(query1);
		params.putAll(hqlUtil4.getParams());
		sql+=hqlUtil4.getWhereHql();		//计数语句
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
	}*/

	//大金额处方分析
		@Override
		public DataGrid<Map<String, Object>> groupLargeSum(PageRequest page) {
			String sql = "select z.hospitalCode as HOSPITALCODE,r.departcode as DEPARTCODE,max(z.hospitalName) as HOSPITALNAME,subStr(max(r.departname),instr(max(r.departname),'-')+1) as DEPARTNAME,r.doctorcode as DOCTORCODE,max(r.doctorName) as DOCTORNAME, "
					+ " count(*) as largeSumRecipe from sup_clinic_recipe r,sup_hospital_zone z where r.hospitalcode=z.hospitalCode group by z.hospitalCode,r.departCode,r.doctorCode";
			return this.findBySql(sql, page, Map.class);
		}

		/*@Override
		public DataGrid<Map<String, Object>> groupAbsDrugUser(PageRequest page) {
			String sql = "select c.doctorcode as DOCTORCODE,max(c.doctorName) as DOCTORNAME,z.hospitalCode as HOSPITALCODE,c.departcode as DEPARTCODE,max(z.hospitalName) as HOSPITALNAME,subStr(max(c.departname),instr(max(c.departname),'-')+1) as DEPARTNAME,sum(c.absDrugSum) as CLINICABSDRUGSUM,sum(c.drugSum) as CLINICDRUGSUM,sum(h.absDrugSum) as HISABSDRUGSUM, (sum(c.sum)+sum(h.sum)) as ALLSUM, "
					+ " sum(h.drugSum) as HISDRUGSUM, (sum(c.absDrugSum)+sum(h.absDrugSum)) as ALLABSDRUGSUM, (sum(c.drugSum)+sum(h.drugSum)) as ALLDRUGSUM, "
					+ " (sum(c.otherSum)+sum(h.otherSum)) as ALLOTHERSUM, (sum(c.absType1Sum)+sum(h.absType1Sum)) as ALLABSTYPE1SUM,(sum(c.absType2Sum)+sum(h.absType2Sum)) as ALLABSTYPE2SUM,(sum(c.absType3Sum)+sum(h.absType3Sum)) as ALLABSTYPE3SUM, "
					+ " sum(c.absType1Sum) as CLINICABSTYPE1SUM, sum(c.absType2Sum) as CLINICABSTYPE2SUM,sum(c.absType3Sum) as CLINICABSTYPE3SUM, "
					+ " sum(h.absType1Sum) as HISABSTYPE1SUM, sum(h.absType2Sum) as HISABSTYPE2SUM,sum(h.absType3Sum) as HISABSTYPE3SUM from sup_clinic_analysis_doctor c,sup_his_analysis_doctor h,sup_hospital_zone z where c.hospitalCode=h.hospitalCode and c.hospitalCode=z.hospitalCode and nvl(c.drugsum,0)!=0 and c.month=h.month"
					+ " group by z.hospitalCode,c.departCode,c.doctorCode";
			return this.findBySql(sql, page, Map.class);
		}*/
		//抗菌药物使用占比和医院药品收入  医生
		@Override
		public DataGrid<Map<String, Object>> groupAbsDrugUser(PageRequest page,Integer countType) {
			String sql1 = " select c.countType,c.doctorCode,max(c.doctorName)as doctorName,c.departCode,max(c.departName) as departName,c.hospitalCode,max(c.hospitalName) as hospitalName,sum(c.absDrugSum) as absDrugSum, sum(c.absType1Sum) as absType1Sum,sum(c.absType2Sum) as absType2Sum, sum(c.absType3Sum) as absType3Sum,sum(c.drugSum) as drugSum, sum(c.sum) as sum,sum(c.othersum) as othersum from (";
			String sql2 = "select '门诊' as countType,b.hospitalCode,b.hospitalName,a.departCode,a.departName,a.doctorCode,a.doctorName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_clinic_analysis_doctor a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
			String sql3 = "select '住院' as countType,b.hospitalCode,b.hospitalName,a.departCode,a.departName,a.doctorCode,a.doctorName,a.absDrugSum,a.absType1Sum, a.absType2Sum,a.absType3Sum, a.drugSum,a.sum,a.othersum from sup_his_analysis_doctor a, sup_hospital_zone b where a.hospitalcode = b.hospitalcode";
			String sql4 = ") c group by c.hospitalCode,c.departCode,c.doctorCode,c.countType";
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
			String sql1 = "select a.hospitalCode,a.hospitalName,a.departCode,a.departName,a.doctorCode,a.doctorName,a.recipeNum as recipenum, a.COMBINEDNUM1, a.COMBINEDNUM2, a.COMBINEDNUM3,a.COMBINEDNUM4,a.COMBINEDNUM5, a.COMBINEDNUM6, b.recipenum  as allrecipenum from (select z.doctorCode,max(z.doctorName) as doctorName,z.departCode,subStr(max(z.departname),instr(max(z.departname),'-')+1) as DEPARTNAME,h.hospitalCode,max(h.hospitalName) as hospitalName,count(1) as recipeNum,sum(case when z.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when z.drugnum=2 then 1 else 0 end) as COMBINEDNUM2,"
					+"sum(case when z.drugnum=3 then 1 else 0 end) as COMBINEDNUM3,sum(case when z.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, "
					+"sum(case when z.drugnum=5 then 1 else 0 end) as COMBINEDNUM5,sum(case when z.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6 "
					+"from sup_clinic_recipe z,sup_hospital_zone h where z.hospitalcode = h.hospitalcode ";
			String	sql2 = "group by z.departCode,h.hospitalCode,z.doctorCode)a,(select sum(z.recipenum) as recipenum,z.departCode,z.doctorCode, z.hospitalcode from sup_clinic_analysis_doctor z, sup_hospital_zone a where a.hospitalcode = z.hospitalCode ";
			String sql3 ="group by z.departCode,z.hospitalCode,z.doctorCode)b where a.hospitalCode=b.hospitalCode and a.departCode=b.departCode and a.doctorCode=b.doctorCode";
			Map<String,Object> query0 = new HashMap<String,Object>();
			Map<String,Object> query1 = new HashMap<String,Object>();
			Map<String,Object> query2 = new HashMap<String,Object>();
			Map<String,Object> params = page.getQuery();
			for (Entry<String, Object> entry : params.entrySet()) {
				String key = entry.getKey();
				if(("provinceCode_S_EQ").equals(key)||("cityCode_S_EQ").equals(key)||("countyCode_S_EQ").equals(key)||("orgLevel_S_EQ").equals(key)||("hospitalCode_S_EQ").equals(key)||("treePath_S_RLK").equals(key)||("departCode_S_EQ").equals(key)){
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
			String sql = "select z.hospitalcode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,a.departCode,max(a.departName) as departName,a.doctorCode as doctorCode,max(a.doctorName) as doctorName ,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,a.departCode,a.doctorCode,z.hospitalCode";
			return this.findBySql(sql, page, Map.class);
		}
		//急诊,门诊药品使用分析页面导出
		@Override
		public List<Map<String, Object>> groupClinicMedicineAll(PageRequest page) {
			String sql = "select z.hospitalcode as HOSPITALCODE,max(z.hospitalName) as HOSPITALNAME,a.departCode,max(a.departName) as departName,a.doctorCode as doctorCode,max(a.doctorName) as doctorName ,count(distinct(a.outsno)) as visitorNum,count(distinct nvl(a.rpSno,1))- max(case when rpSno is null then 1 else 0 end) as regRecipeNum,count(distinct a.outsno||a.pzm) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum from sup_clinic_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.clinicType,a.departCode,a.doctorCode,z.hospitalCode";
			return this.listBySql2(sql, page, Map.class);
		}
}
