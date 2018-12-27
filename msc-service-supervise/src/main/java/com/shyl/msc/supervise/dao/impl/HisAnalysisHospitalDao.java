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
import com.shyl.msc.supervise.dao.IHisAnalysisHospitalDao;
import com.shyl.msc.supervise.entity.HisAnalysisHospital;

@Repository
public class HisAnalysisHospitalDao extends BaseDao<HisAnalysisHospital, Long> implements IHisAnalysisHospitalDao {
	protected void setAliasParameter(Query query, Map<String, Object> params) {
		if ((params != null) && (!params.isEmpty()))
			for (String key : params.keySet())
				query.setParameter(key, params.get(key));
	}

	protected String easyuiSort(String sql, String sort, String order) {
		if ((!StringUtils.isEmpty(sort)) && (!StringUtils.isEmpty(order))) {
			int index = sql.indexOf("order by");
			sql = sql.substring(0, index);
			sql = "select * from (" + sql + ") order by ";
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
	public DataGrid<Map<String, Object>> groupBy(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(c.provinceCode) as provinceCode,sum(a.sum) as sum,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByProvince(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode,sum(a.sum) as sum,max(provinceName) as provinceName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByCity(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode as provinceCode,c.cityCode,sum(a.sum) as sum,max(cityName) as cityName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByCountry(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode,c.cityCode,c.countyCode,sum(a.sum) as sum,max(countyName)as countyName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode,c.countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByHospital(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(c.provinceCode) AS provinceCode,max(c.cityCode) AS cityCode,max(c.hospitalCode) as hospitalCode,sum(a.sum) as sum,max(c.hospitalName) as hospitalName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ "  from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode,c.countyCode,c.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugBy(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "a.sum")));
		String sql1 = "select c.outnum as outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum as absnum from (select sum(b.sum) as sum from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = ") a,(select sum(a.sum) as sum,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = ") b,  (select count(1) as outnum,sum(d.absnum) as absnum, sum(d.ddd) as ddd from (select max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd, nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate) d) c,(select count(1) as num from (select 1  from sup_his_reg a, sup_hospital_zone z,sup_his_reg_item b  where a.hospitalcode = z.hospitalcode and a.insno=b.insno and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql5 = "   group by b.insno||b.model||b.productname||b.dosageformname)) d";

		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ") || key.equals("countyCode_S_EQ")
					|| key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + ")" + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugByProvince(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.provincecode,a.provincename,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.provincecode) as provincecode, sum(b.sum) as sum,max(z.provincename) as provincename from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.provincecode) a,(select max(z.provincecode) as provincecode,select max(z.treepath) as treepath,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists(select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.provincecode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.provincecode,sum(d.ddd) as ddd from (select a.patientid,max(z.provincecode) as provincecode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.provincecode) d group by d.provincecode) c,(select count(1) as num,e.provincecode from (select max(z.provincecode) as provincecode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.provincecode)  d where a.provincecode =b.provincecode and a.provincecode=c.provincecode and a.provincecode =d.provincecode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ") || key.equals("countyCode_S_EQ")
					|| key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else if (key.equals("treePath_S_RLK")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugByCity(PageRequest page) {
		String sql1 = "select b.provincecode,a.treepath,a.citycode,a.cityname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.treepath) as treepath,max(z.citycode) as citycode, sum(b.sum) as sum,max(z.cityname) as cityname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.citycode) a,(select max(z.citycode) as citycode,max(z.orglevel) as orglevel,max(provincecode) as provincecode,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.citycode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.citycode,sum(d.ddd) as ddd from (select a.patientid,max(z.citycode) as citycode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.citycode) d group by d.citycode) c,(select count(1) as num,e.citycode from (select max(z.citycode) as citycode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.citycode)  d where a.citycode =b.citycode and a.citycode=c.citycode and a.citycode =d.citycode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("treePath_S_RLK")) {
				query0.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugByCountry(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.citycode,a.treepath,a.countycode,a.countyname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.treepath) as treepath,max(z.provincecode) as provincecode,max(z.citycode) as citycode,max(z.countycode) as countycode, sum(b.sum) as sum,max(z.countyname) as countyname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.countycode) a,(select max(z.countycode) as countycode, sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.countycode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.countycode,sum(d.ddd) as ddd from (select a.patientid,max(z.countycode) as countycode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.countycode) d group by d.countycode) c,(select count(1) as num,e.countycode from (select max(z.countycode) as countycode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.countycode)  d where a.countycode =b.countycode and a.countycode=c.countycode and a.countycode =d.countycode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("treePath_S_RLK") || key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ")
					|| key.equals("countyCode_S_EQ") || key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();

		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugByHospital(PageRequest page) {
		String sql1 = "select a.countycode, a.hospitalcode,a.hospitalname, c.outnum,  a.sum as drugsum,  b.sum as sum, round((b.sum / c.outnum),2) as avgsum, round((a.sum / b.sum),2)*100 as ratedrugsum, round((a.sum / c.outnum),2) as avgoutdrugsum, b.daysum as daysum,round((a.sum / b.daysum),2)  as avgdaydrugsum, d.num as drugnum,round((d.num / c.outnum),2) as avgdrugnum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum, c.absnum from (select max(z.countycode) as countycode, max(z.hospitalcode) as hospitalcode,sum(b.sum) as sum, max(z.hospitalname) as hospitalname from sup_his_reg a, sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.hospitalcode) a,(select max(z.hospitalcode) as hospitalcode,max(z.treepath) as treepath,max(z.cityCode) as citycode,max(z.countycode) as countycode,max(z.orglevel) as orglevel,max(z.provinceCode) as provinceCode, sum(a.sum) as sum,count(1) as num,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.hospitalcode) b,(select count(1) as outnum,sum(d.absnum) as absnum, d.hospitalcode, sum(d.ddd) as ddd from (select a.patientid, a.hospitalcode as hospitalcode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd, nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a, sup_his_reg_item b ,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode  ";
		String sql4 = " group by a.patientid,a.cdate, a.hospitalcode ) d group by hospitalcode) c,(select count(1) as num,e.hospitalcode from (select max(z.hospitalcode) as hospitalcode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.hospitalcode)  d where a.hospitalcode =b.hospitalcode and a.hospitalcode=c.hospitalcode and a.hospitalcode =d.hospitalcode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("treePath_S_RLK") || key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ")
					|| key.equals("countyCode_S_EQ") || key.equals("cityCode_S_EQ")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue());
	}

	@Override
	public DataGrid<Map<String, Object>> groupRankingBy(PageRequest page) {
		String sql = "select h.productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode  =m.code group by h.productCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupRankingByProvince(PageRequest page) {

		String sql = "select h.productCode,max(h.productName) as productname,max(c.provinceCode) as provinceCode,max(c.provinceName) as provinceName,"
				+ "max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num ,max(m.isGpoPurchase) as isGpoPurchase"
				+ " from sup_drug_analysis_hospital h LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.provinceCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupRankingByCity(PageRequest page) {
		String sql = "select h.productCode,max(h.productName) as productname,max(c.provinceCode) as provinceCode,max(c.provinceName) as provinceName,"
				+ "max(c.cityCode) as cityCode,max(c.cityName) as cityName,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num ,max(m.isGpoPurchase) as isGpoPurchase"
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupRankingByCountry(PageRequest page) {
		String sql = "select max(c.provinceCode) as provinceCode,max(c.cityCode) as cityCode,max(c.countyCode) as countyCode,"
				+ "max(c.countyName) as countyName,h.productCode as productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupRankingByHospital(PageRequest page) {
		String sql = "select  max(c.provinceCode) as provinceCode,max(c.cityCode) as cityCode,max(c.countyCode) as countyCode,"
				+ "max(c.hospitalCode) as hospitalCode,max(c.hospitalName) as hospitalName,h.productCode as productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}

	// 导出

	@Override
	public List<Map<String, Object>> groupByAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(c.provinceCode) as provinceCode,sum(a.sum) as sum,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupByProvinceAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode,sum(a.sum) as sum,max(provinceName) as provinceName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupByCityAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode as provinceCode,c.cityCode,sum(a.sum) as sum,max(cityName) as cityName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupByCountryAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select c.provinceCode,c.cityCode,c.countyCode,sum(a.sum) as sum,max(countyName)as countyName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode,c.countyCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupByHospitalAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(c.provinceCode) AS provinceCode,max(c.cityCode) AS cityCode,max(c.hospitalCode) as hospitalCode,sum(a.sum) as sum,max(c.hospitalName) as hospitalName,sum(drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2) as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ "  from sup_his_analysis_hospital a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.provinceCode,c.cityCode,c.countyCode,c.hospitalCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByAll(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "a.sum")));
		String sql1 = "select c.outnum as outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum as absnum from (select sum(b.sum) as sum from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = ") a,(select sum(a.sum) as sum,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = ") b,  (select count(1) as outnum,sum(d.absnum) as absnum, sum(d.ddd) as ddd from (select max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd, nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate) d) c,(select count(1) as num from (select 1  from sup_his_reg a, sup_hospital_zone z,sup_his_reg_item b  where a.hospitalcode = z.hospitalcode and a.insno=b.insno and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql5 = "   group by b.insno||b.model||b.productname||b.dosageformname)) d";

		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ") || key.equals("countyCode_S_EQ")
					|| key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + ")" + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		// sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue()).getRows();
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByProvinceAll(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.provincecode,a.provincename,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.provincecode) as provincecode, sum(b.sum) as sum,max(z.provincename) as provincename from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.provincecode) a,(select max(z.provincecode) as provincecode,select max(z.treepath) as treepath,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists(select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.provincecode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.provincecode,sum(d.ddd) as ddd from (select a.patientid,max(z.provincecode) as provincecode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.provincecode) d group by d.provincecode) c,(select count(1) as num,e.provincecode from (select max(z.provincecode) as provincecode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.provincecode)  d where a.provincecode =b.provincecode and a.provincecode=c.provincecode and a.provincecode =d.provincecode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ") || key.equals("countyCode_S_EQ")
					|| key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else if (key.equals("treePath_S_RLK")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		// sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue()).getRows();
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByCityAll(PageRequest page) {
		String sql1 = "select b.provincecode,a.treepath,a.citycode,a.cityname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.treepath) as treepath,max(z.citycode) as citycode, sum(b.sum) as sum,max(z.cityname) as cityname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.citycode) a,(select max(z.citycode) as citycode,max(z.orglevel) as orglevel,max(provincecode) as provincecode,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.citycode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.citycode,sum(d.ddd) as ddd from (select a.patientid,max(z.citycode) as citycode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.citycode) d group by d.citycode) c,(select count(1) as num,e.citycode from (select max(z.citycode) as citycode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.citycode)  d where a.citycode =b.citycode and a.citycode=c.citycode and a.citycode =d.citycode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("provinceCode_S_EQ")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("treePath_S_RLK")) {
				query0.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		// sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue()).getRows();
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByCountryAll(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.citycode,a.treepath,a.countycode,a.countyname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(z.treepath) as treepath,max(z.provincecode) as provincecode,max(z.citycode) as citycode,max(z.countycode) as countycode, sum(b.sum) as sum,max(z.countyname) as countyname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.countycode) a,(select max(z.countycode) as countycode, sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.countycode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.countycode,sum(d.ddd) as ddd from (select a.patientid,max(z.countycode) as countycode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,z.countycode) d group by d.countycode) c,(select count(1) as num,e.countycode from (select max(z.countycode) as countycode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.countycode)  d where a.countycode =b.countycode and a.countycode=c.countycode and a.countycode =d.countycode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("treePath_S_RLK") || key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ")
					|| key.equals("countyCode_S_EQ") || key.equals("cityCode_S_EQ")) {
				query0.put("a#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();

		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		// sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue()).getRows();
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByHospitalAll(PageRequest page) {
		String sql1 = "select a.countycode, a.hospitalcode,a.hospitalname, c.outnum,  a.sum as drugsum,  b.sum as sum, round((b.sum / c.outnum),2) as avgsum, round((a.sum / b.sum),2)*100 as ratedrugsum, round((a.sum / c.outnum),2) as avgoutdrugsum, b.daysum as daysum,round((a.sum / b.daysum),2)  as avgdaydrugsum, d.num as drugnum,round((d.num / c.outnum),2) as avgdrugnum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum, c.absnum from (select max(z.countycode) as countycode, max(z.hospitalcode) as hospitalcode,sum(b.sum) as sum, max(z.hospitalname) as hospitalname from sup_his_reg a, sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by z.hospitalcode) a,(select max(z.hospitalcode) as hospitalcode,max(z.treepath) as treepath,max(z.cityCode) as citycode,max(z.countycode) as countycode,max(z.orglevel) as orglevel,max(z.provinceCode) as provinceCode, sum(a.sum) as sum,count(1) as num,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z where a.hospitalcode = z.hospitalcode and exists (select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by z.hospitalcode) b,(select count(1) as outnum,sum(d.absnum) as absnum, d.hospitalcode, sum(d.ddd) as ddd from (select a.patientid, a.hospitalcode as hospitalcode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd, nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a, sup_his_reg_item b ,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode  ";
		String sql4 = " group by a.patientid,a.cdate, a.hospitalcode ) d group by hospitalcode) c,(select count(1) as num,e.hospitalcode from (select max(z.hospitalcode) as hospitalcode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.hospitalcode)  d where a.hospitalcode =b.hospitalcode and a.hospitalcode=c.hospitalcode and a.hospitalcode =d.hospitalcode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("treePath_S_RLK") || key.equals("provinceCode_S_EQ") || key.equals("hospitalCode_S_EQ")
					|| key.equals("countyCode_S_EQ") || key.equals("cityCode_S_EQ")) {
				query0.put("b#" + key, entry.getValue());
			} else if (key.equals("orgLevel_S_EQ")) {
				query2.put("z#" + key, entry.getValue());
			} else if (("cdate_D_GE").equals(key) || ("cdate_D_LE").equals(key)) {
				query2.put("a#" + key, entry.getValue());
			} else {
				query1.put(key, entry.getValue());
			}
		}
		String sql = "";
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(query1);
		params = hqlUtil.getParams();
		HqlUtil hqlUtil2 = new HqlUtil();
		hqlUtil2.addFilter(query2);
		params.putAll(hqlUtil2.getParams());
		sql = sql1 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql2 + hqlUtil.getWhereHql() + ")"
				+ hqlUtil2.getWhereHql() + sql3 + hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql4
				+ hqlUtil.getWhereHql() + hqlUtil2.getWhereHql() + sql5;
		HqlUtil hqlUtil3 = new HqlUtil();
		hqlUtil3.addFilter(query0);
		params.putAll(hqlUtil3.getParams());
		sql += hqlUtil3.getWhereHql();
		// 计数语句
		String cq = "select count(1) from (" + sql + ")";
		// 添加排序
		hqlUtil.setSort(page);
		sql = easyuiSort(sql, page.getSort(), page.getOrder());
		SQLQuery sq = getSession().createSQLQuery(sql);
		// 需要设置2次值
		setAliasParameter(sq, params);
		// 设置分页参数
		// sq.setFirstResult(page.getOffset()).setMaxResults(page.getPageSize());
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return new DataGrid<Map<String, Object>>(sq.list(), page, countBySql(cq, params).longValue()).getRows();
	}

	@Override
	public List<Map<String, Object>> groupRankingByAll(PageRequest page) {
		String sql = "select h.productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase  "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode  =m.code group by h.productCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupRankingByProvinceAll(PageRequest page) {

		String sql = "select h.productCode,max(h.productName) as productname,max(c.provinceCode) as provinceCode,max(c.provinceName) as provinceName,"
				+ "max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.provinceCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupRankingByCityAll(PageRequest page) {
		String sql = "select h.productCode,max(h.productName) as productname,max(c.provinceCode) as provinceCode,max(c.provinceName) as provinceName,"
				+ "max(c.cityCode) as cityCode,max(c.cityName) as cityName,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.cityCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupRankingByCountryAll(PageRequest page) {
		String sql = "select max(c.provinceCode) as provinceCode,max(c.cityCode) as cityCode,max(c.countyCode) as countyCode,"
				+ "max(c.countyName) as countyName,h.productCode as productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.countyCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupRankingByHospitalAll(PageRequest page) {
		String sql = "select  max(c.provinceCode) as provinceCode,max(c.cityCode) as cityCode,max(c.countyCode) as countyCode,"
				+ "max(c.hospitalCode) as hospitalCode,max(c.hospitalName) as hospitalName,h.productCode as productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_hospital h  LEFT JOIN sup_hospital_zone c ON  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code group by h.productCode,c.hospitalCode";
		return this.listBySql2(sql, page, Map.class);
	}

	// 住院药品分析新算法
	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineBy(PageRequest page) {
		String sql = "select sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.insno) ";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineByProvince(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "provincecode")));
		String sql = "select  provincecode,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "a.insno) group by provinceCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineByCity(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "cityCode")));
		String sql = "select  provincecode,cityCode,max(cityName) as cityName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,max(z.cityName) as cityName,max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,a.insno) group by provinceCode,cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineByCountry(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "countyCode")));
		String sql = "select  provincecode,cityCode,countyCode,max(countyName) as countyName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,z.countyCode as countyCode,max(z.countyName) as countyName,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,z.countyCode,a.insno) group by provinceCode,cityCode,countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineByHospital(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "hospitalcode")));
		String sql = "select  provincecode,cityCode,countyCode,hospitalcode,max(hospitalName) as hospitalName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,z.countyCode as countyCode,a.hospitalcode as hospitalcode,max(a.hospitalName) as hospitalName,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,z.countyCode,a.hospitalCode,a.insno) group by provinceCode,cityCode,countyCode,hospitalcode";

		return this.findBySql(sql, page, Map.class);
	}

	// 住院药品分析新算法---导出
	@Override
	public List<Map<String, Object>> groupHisMedicineByAll(PageRequest page) {
		String sql = "select sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.insno) ";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisMedicineByProvinceAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "provincecode")));
		String sql = "select  provincecode,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "a.insno) group by provinceCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisMedicineByCityAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "cityCode")));
		String sql = "select  provincecode,cityCode,max(cityName) as cityName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,max(z.cityName) as cityName,max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,a.insno) group by provinceCode,cityCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisMedicineByCountryAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "countyCode")));
		String sql = "select  provincecode,cityCode,countyCode,max(countyName) as countyName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,z.countyCode as countyCode,max(z.countyName) as countyName,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,z.countyCode,a.insno) group by provinceCode,cityCode,countyCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisMedicineByHospitalAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "hospitalcode")));
		String sql = "select  provincecode,cityCode,countyCode,hospitalcode,max(hospitalName) as hospitalName,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ "count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ "round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ "round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ "round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select z.provincecode as provincecode,"
				+ "z.cityCode as cityCode,z.countyCode as countyCode,a.hospitalcode as hospitalcode,max(a.hospitalName) as hospitalName,"
				+ "max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ "count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ "max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ "sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by z.provinceCode,"
				+ "z.cityCode,z.countyCode,a.hospitalCode,a.insno) group by provinceCode,cityCode,countyCode,hospitalcode";
		return this.listBySql2(sql, page, Map.class);
	}
	
	public List<Map<String, Object>> groupMonthByHospital(PageRequest page) {
		return this.listBySql2("select sum(outnum) as outnum,sum(sum) as sum,sum(daysum) as daysum,sum(drugSum) as drugSum,"
				+ "round(sum(sum)/sum(outnum),2) as rjOutSum,round(sum(drugSum)/sum(outnum),2) as rjDrugOutSum,"
				+ "round(sum(sum)/sum(daysum),2) as rjDaySum,round(sum(drugSum)/sum(daysum),2) as rjDrugDaySum,"
				+ "z.hospitalCode as code,max(z.hospitalName) as name,month "
				+ " from sup_his_analysis_hospital a,sup_hospital_zone z where a.hospitalcode=z.hospitalcode group by z.hospitalcode,month order by z.hospitalcode,month", page, Map.class);
	}
	
	public List<Map<String, Object>> groupMonthByCountyCode(PageRequest page) {
		return this.listBySql2("select sum(outnum) as outnum,sum(sum) as sum,sum(daysum) as daysum,sum(drugSum) as drugSum,"
				+ "round(sum(sum)/sum(outnum),2) as rjOutSum,round(sum(drugSum)/sum(outnum),2) as rjDrugOutSum,"
				+ "round(sum(sum)/sum(daysum),2) as rjDaySum,round(sum(drugSum)/sum(daysum),2) as rjDrugDaySum,"
				+ "z.countyCode as code,max(z.countyName) as name,month "
				+ " from sup_his_analysis_hospital a,sup_hospital_zone z where a.hospitalcode=z.hospitalcode group by z.countyCode,a.month order by z.countyCode,month", page, Map.class);
		
	}

	@Override
	public DataGrid<Map<String, Object>> groupByColumn(PageRequest page, String groupColumn) {
		return this.findBySql("select "+groupColumn+" as code,max(z.provinceCode) as provinceCode,max(z.cityCode) as cityCode,max(countyCode) as countyCode,"
				+"max(z.provinceName) as provinceName,max(z.cityName) as cityName,max(countyName) as countyName,"
				+"max(a.hospitalCode) as hospitalCode,max(a.departCode) as departCode,max(a.doctorCode) as doctorCode, "
				+"max(a.hospitalName) as hospitalName,max(a.departName) as departName,max(a.doctorName) as doctorName "
				+"from sup_his_analysis_doctor a inner join sup_hospital_zone z on a.hospitalcode=z.hospitalcode group by "+groupColumn, page, Map.class);
	}

	@Override
	public BigDecimal getAbsNum(PageRequest page) {
		String sql = "select count(distinct(a.insno)) as absNum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode and a.absDrugType is not null";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("ABSNUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDrugNum(PageRequest page) {
		String sql = "select count(distinct(a.insno||a.pzm)) as drugNum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("DRUGNUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getSum(PageRequest page) {
		String sql = "select sum(a.sum) as sum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("SUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDaySum(PageRequest page) {
		String sql = "select sum(daySum) as daySum from (select max(a.daySum) as daySum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by a.insno)";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("DAYSUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDddSum(PageRequest page) {
		String sql = "select sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("DDDSUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getDrugSum(PageRequest page) {
		String sql = "select sum(a.drugSum) as drugSum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("DRUGSUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public BigDecimal getOutNum(PageRequest page) {
		String sql = "select count(distinct(a.insno)) as outNum from sup_his_medicine_analysis a,sup_hospital_zone z where a.hospitalCode = z.hospitalCode";
		List<Map<String, Object>> list = this.listBySql2(sql, page, Map.class);
		if(list.size()>0){
			return (BigDecimal)list.get(0).get("OUTNUM");
		}else{
			return new BigDecimal(0d);
		}
	}

	@Override
	public List<Map<String, Object>> getCombInDurgNum(PageRequest page) {
		Map<String, Object> query = page.getQuery();
		String sql = "select count(1) as recipeNum,sum(case when a.drugnum=1 then 1 else 0 end) as COMBINEDNUM1,sum(case when a.drugnum=2 then 1 else 0 end) as COMBINEDNUM2, "
					+"sum(case when a.drugnum=3 then 1 else 0 end) as COMBINEDNUM3, sum(case when a.drugnum=4 then 1 else 0 end) as COMBINEDNUM4, sum(case when a.drugnum=5 then 1 else 0 end) as COMBINEDNUM5, "
					+"sum(case when a.drugnum>=6 then 1 else 0 end) as COMBINEDNUM6  from sup_his_reg a,sup_hospital_zone z  where a.hospitalcode=z.hospitalcode ";
		Map<String,Object> params = page.getQuery();
		if(params.size()!=0&&(!"".equals((String)params.get("insuranceDrugType_L_EQ"))||!"".equals((String)params.get("baseDrugType_L_EQ"))||!"".equals((String)params.get("absDrugType_L_EQ"))||!"".equals((String)params.get("specialDrugType_L_EQ"))||!"".equals((String)params.get("ypxz_S_EQ"))||!"".equals((String)params.get("auxiliaryType_S_EQ")))){
			sql+="and exists(select 1 from sup_his_reg_item b where a.inSno=b.inSno";
		}
		return this.listBySql2(sql, page, Map.class);
	}

}
