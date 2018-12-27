package com.shyl.msc.supervise.dao.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import com.shyl.common.entity.Sort;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.supervise.dao.IDiseaseAnalysisItemDao;
import com.shyl.msc.supervise.entity.DiseaseAnalysisItem;
import com.shyl.msc.supervise.entity.DrugAnalysis;
import com.shyl.msc.supervise.service.impl.DiseaseAnalysisService;

@Repository
public class DiseaseAnalysisItemDao extends BaseDao<DiseaseAnalysisItem, Long> implements IDiseaseAnalysisItemDao {
  
	public int getDrugNum(PageRequest page) {

	    HqlUtil hqlUtil = new HqlUtil();
	    hqlUtil.addFilter(page.getQuery());
		String sql = "select count(1) from (select 1 from sup_disease_analysis_hospital a,sup_disease_analysis_item b,sup_hospital_zone c "
				+ "where a.hospitalcode=c.hospitalcode and a.code=b.diseaseAnalysisCode "
				+ hqlUtil.getWhereHql()
				+ "group by diseaseCode,isOperation,productcode)";

	    sql = sql.replace("upper(", "").replaceAll("\\)  =  :", "  =  :").replaceAll("\\)  like  :", "  like  :")
	    		.replaceAll("\\)  <=  :", "  <=  :").replaceAll("\\)  >=  :", "  >=  :").replaceAll("a.diseaseCode  =", "upper\\(a.diseaseCode\\)  =");

	    SQLQuery query = getSession().createSQLQuery(sql);
	    setAliasParameter(query, hqlUtil.getParams());
	    return ((BigDecimal)query.uniqueResult()).intValue();
	}
	
	protected void setAliasParameter(Query query, Map<String, Object> params) {
		if ((params != null) && (!params.isEmpty()))
			for (String key : params.keySet())
				query.setParameter(key, params.get(key));
	}
	
	public DataGrid<DiseaseAnalysisItem> groupProductBy(PageRequest page) {
		String type = (String)page.getQuery().get("a#type_L_EQ");
		//根据药品用量倒叙排序
		page.setSort(new Sort(Sort.Direction.DESC,"b.num"));
	    if (!StringUtils.isBlank(type)) {
			page.getQuery().put("a#type_L_EQ", DrugAnalysis.Type.values()[Integer.valueOf(type)]);
		}
		String hql =  "select b from DiseaseAnalysisItem b,DiseaseAnalysisHospital a,HospitalZone c where a.hospitalCode=c.hospitalCode and a.code=b.diseaseAnalysisCode and b.num is not null ";

		return this.query(hql, page);
	}
}
