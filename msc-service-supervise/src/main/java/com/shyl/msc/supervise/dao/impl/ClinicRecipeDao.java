package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.supervise.dao.IClinicRecipeDao;
import com.shyl.msc.supervise.entity.ClinicRecipe;

@Repository
public class ClinicRecipeDao extends BaseDao<ClinicRecipe, Long> implements IClinicRecipeDao {

	public DataGrid<Map<String, Object>> queryRecipeAndReg(PageRequest pageable, Integer sumType) {
		String sql1 = "select '门诊' as type,a.sex,c.hospitalcode,outSno as sno,rpSno as rpSno,patientId,patienName,age,to_char(cdate,'yyyy-MM-dd hh24:mi') as cdate,sum,drugNum,drugSum,otherSum"
				+ " from sup_clinic_recipe a,sup_hospital_zone c where a.hospitalcode=c.hospitalcode ";
		String sql2 = "select '住院' as type,a.sex,c.hospitalcode,inSno as sno,null,patientId,patienName,age,to_char(cdate,'yyyy-MM-dd hh24:mi') as cdate,sum,drugNum,drugSum,otherSum"
				+ " from sup_his_reg a,sup_hospital_zone c where a.hospitalcode=c.hospitalcode";
		if (!StringUtils.isEmpty(pageable.getQuery().get("productCode_S_EQ"))) {
			sql1 +=" and exists(select 1 from sup_clinic_recipe_item d "
					+ "where a.outSno=d.outSno and d.productCode="
					+pageable.getQuery().get("productCode_S_EQ")+")";
			sql2 +=" and exists(select 1 from sup_his_reg_item d "
					+ "where a.inSno=d.inSno and d.productCode="
					+pageable.getQuery().get("productCode_S_EQ")+")";
			pageable.getQuery().remove("productCode_S_EQ");
		}
		
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(pageable.getQuery());
		//合并两个SQL，加快查询速度
		String sql = "";
		if (sumType == null) {
			sql = sql1 + hqlUtil.getWhereHql() + " union all " +sql2 + hqlUtil.getWhereHql();
		} else if (sumType == 0) {
			sql = sql1 + hqlUtil.getWhereHql() ;
		} else if (sumType == 1) {
			sql = sql2 + hqlUtil.getWhereHql();
		}
		//计数语句
		
		String cq = "select count(1) from ("+sql+")";
		//添加排序
		hqlUtil.setSort(pageable);
		sql = easyuiSort(sql, pageable.getSort(), pageable.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		//需要设置2次值
		setAliasParameter(sq, hqlUtil.getParams());
		if (sumType == null) {
			setAliasParameter(sq, hqlUtil.getParams());
		}
		//设置分页参数
		sq.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		DataGrid<Map<String, Object>> result = new DataGrid<Map<String, Object>>(sq.list(), pageable, countBySql(cq, hqlUtil.getParams()).longValue());
		return result;
	}

	protected void setAliasParameter(Query query, Map<String, Object> params) {
		if ((params != null) && (!params.isEmpty()))
			for (String key : params.keySet())
				query.setParameter(key, params.get(key));
	}

	protected String easyuiSort(String sql, String sort, String order) {
		if ((!StringUtils.isEmpty(sort)) && (!StringUtils.isEmpty(order))) {
			int index = sql.indexOf("order by");
			if(index!=-1){
				sql = sql.substring(0, index);
			}
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

	@Override
	public List<Map<String, Object>> queryRecipeAndRegAll(PageRequest pageable, Integer sumType) {

		String sql1 = "select '门诊' as type,c.hospitalcode,outSno as sno,patientId,patienName,to_char(cdate,'yyyy-MM-dd hh24:mi') as cdate,sum,drugNum,drugSum,otherSum"
				+ " from sup_clinic_recipe a,sup_hospital_zone c where a.hospitalcode=c.hospitalcode ";
		String sql2 = "select '住院' as type,c.hospitalcode,inSno as sno,patientId,patienName,to_char(cdate,'yyyy-MM-dd hh24:mi') as cdate,sum,drugNum,drugSum,otherSum"
				+ " from sup_his_reg a,sup_hospital_zone c where a.hospitalcode=c.hospitalcode";
		if (!StringUtils.isEmpty(pageable.getQuery().get("productCode_S_EQ"))) {
			sql1 +=" and exists(select 1 from sup_clinic_recipe_item d "
					+ "where a.outSno=d.outSno and d.productCode="
					+pageable.getQuery().get("productCode_S_EQ")+")";
			sql2 +=" and exists(select 1 from sup_his_reg_item d "
					+ "where a.inSno=d.inSno and d.productCode="
					+pageable.getQuery().get("productCode_S_EQ")+")";
			pageable.getQuery().remove("productCode_S_EQ");
		}
		
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(pageable.getQuery());
		//合并两个SQL，加快查询速度
		String sql = "";
		if (sumType == null) {
			sql = sql1 + hqlUtil.getWhereHql() + " union all " +sql2 + hqlUtil.getWhereHql();
		} else if (sumType == 0) {
			sql = sql1 + hqlUtil.getWhereHql() ;
		} else if (sumType == 1) {
			sql = sql2 + hqlUtil.getWhereHql();
		}
		//计数语句
		
		String cq = "select count(1) from ("+sql+")";
		//添加排序
		hqlUtil.setSort(pageable);
		sql = easyuiSort(sql, pageable.getSort(), pageable.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		//需要设置2次值
		setAliasParameter(sq, hqlUtil.getParams());
		if (sumType == null) {
			setAliasParameter(sq, hqlUtil.getParams());
		}
		//设置分页参数
		//sq.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		return new DataGrid<Map<String, Object>>(sq.list(), pageable, countBySql(cq, hqlUtil.getParams()).longValue()).getRows();
	}
}
