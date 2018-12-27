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
import com.shyl.msc.supervise.dao.IHisAnalysisDoctorDao;
import com.shyl.msc.supervise.entity.HisAnalysisDoctor;

@Repository
public class HisAnalysisDoctorDao extends BaseDao<HisAnalysisDoctor, Long> implements IHisAnalysisDoctorDao {
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
	public DataGrid<Map<String, Object>> groupByDoctor(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(a.hospitalCode) as hospitalCode,max(a.hospitalName) as hospitalName,a.departCode as departCode,max(substr(a.departname,instr(a.departname,'-')+1)) as departName, "
				+ "a.doctorCode as doctorCode,sum(a.sum) as sum,max(a.doctorName) as doctorName,sum(a.drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2)*100 as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_doctor a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.hospitalCode,a.departCode,a.doctorCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupHisDrugByDoctor(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.hospitalcode,a.hospitalName,a.departcode,a.departname,a.doctorcode,a.doctorname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(a.doctorcode) as doctorcode,max(a.hospitalcode) as hospitalcode,max(a.hospitalname) as hospitalname,max(a.departcode) as departcode,max(substr(a.departname,instr(a.departname,'-')+1)) as departname, sum(b.sum) as sum,max(a.doctorname) as doctorname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by a.hospitalcode,a.departcode,a.doctorcode) a,(select max(a.doctorcode) as doctorcode,max(z.orgLevel) as orgLevel,max(a.departcode) as departcode,max(a.hospitalcode) as hospitalcode,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists(select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by a.hospitalcode,a.departcode,a.doctorcode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.doctorcode,d.departcode,d.hospitalcode,sum(d.ddd) as ddd from (select a.patientid,max(a.doctorcode) as doctorcode,max(a.hospitalcode) as hospitalcode,max(a.departcode) as departcode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b ,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,a.hospitalcode,a.departcode,a.doctorcode) d group by d.hospitalcode,d.departcode,d.doctorcode) c,(select count(1) as num,e.doctorcode,e.hospitalcode,e.departcode as departcode from (select max(a.doctorcode) as doctorcode,max(a.departcode) as departcode,max(a.hospitalcode) as hospitalcode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.hospitalcode,e.departcode,e.doctorcode)  d  where a.hospitalcode=b.hospitalcode and a.hospitalcode= c.hospitalcode and a.hospitalcode= d.hospitalcode and a.doctorCode = b.doctorCode  and a.doctorCode = c.doctorCode and  a.doctorCode = d.doctorCode  and  a.departCode = b.departCode and a.departCode = c.departCode and a.departCode = d.departCode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("hospitalCode_S_EQ")) {
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
	public DataGrid<Map<String, Object>> groupRankingByDoctor(PageRequest page) {
		String sql = "select max(c.hospitalCode) as hospitalCode,max(h.doctorCode) as doctorCode,max(h.departCode) as departCode,max(h.hospitalName) as hospitalName,max(substr(h.departname,instr(h.departname,'-')+1)) as departName,"
				+ "max(h.doctorName) as doctorName,h.productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.model) as model,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num ,max(m.isGpoPurchase) as isGpoPurchase"
				+ " from sup_drug_analysis_doctor h LEFT JOIN sup_hospital_zone c on  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code "
				+ " group by h.productCode,c.hospitalCode,h.departCode,h.doctorCode";
		return this.findBySql(sql, page, Map.class);
	}

	// 导出

	@Override
	public List<Map<String, Object>> groupByDoctorAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql = "select max(a.hospitalCode) as hospitalCode,max(a.hospitalName) as hospitalName,a.departCode as departCode,max(substr(a.departname,instr(a.departname,'-')+1)) as departName, "
				+ "a.doctorCode as doctorCode,sum(a.sum) as sum,max(a.doctorName) as doctorName,sum(a.drugSum) as drugSum,"
				+ "sum(outNum) as outNum,round(sum(drugSum)/sum(outNum),2) as avgOutdrugSum,"
				+ "round(sum(a.sum)/sum(outNum),2) as avgSum,round(sum(drugSum)/sum(a.sum),2)*100 as rateDrugSum,"
				+ "sum(drugNum) as drugNum,round(sum(drugNum)/sum(outNum),2) as avgDrugNum,"
				+ "sum(dddSum) as dddSum,(round(sum(dddSum)/sum(daySum),2))*100 as avgDddSum,"
				+ "sum(absNum) as absNum,round(sum(absNum)/sum(outNum),2)*100 as rateAbsNum,"
				+ "sum(daySum) as daySum,round(sum(drugSum)/sum(daySum),2) as avgDaydrugSum"
				+ " from sup_his_analysis_doctor a LEFT JOIN sup_hospital_zone c ON  a.hospitalcode=c.hospitalcode"
				+ " group by c.hospitalCode,a.departCode,a.doctorCode";
		return this.listBySql2(sql, page, Map.class);
	}

	@Override
	public List<Map<String, Object>> groupHisDrugByDoctorAll(PageRequest page) {
		// page.setMySort(new Sort(new Order(Direction.DESC, "sum")));
		String sql1 = "select a.hospitalcode,a.hospitalName,a.departcode,a.departname,a.doctorcode,a.doctorname,c.outnum,a.sum as drugsum,b.sum as sum,round((b.sum/c.outnum),2) as avgSum,round((a.sum/b.sum),2)*100 as rateDrugSum,round((a.sum/c.outnum),2) as avgOutdrugSum,b.daysum as daysum, round((a.sum/b.daysum),2) as avgDaydrugSum,d.num as drugnum,round((d.num/c.outnum),2) as avgDrugNum,c.ddd as ddd,round((c.ddd/b.daysum),2)*100 as  avgdddsum,round((c.absnum/c.outnum),2)*100 as rateabsnum,c.absnum from (select max(a.doctorcode) as doctorcode,max(a.hospitalcode) as hospitalcode,max(a.hospitalname) as hospitalname,max(a.departcode) as departcode,max(substr(a.departname,instr(a.departname,'-')+1)) as departname, sum(b.sum) as sum,max(a.doctorname) as doctorname from sup_his_reg a,sup_his_reg_item b, sup_hospital_zone z where a.insno = b.insno  and a.hospitalcode = z.hospitalcode ";
		String sql2 = " group by a.hospitalcode,a.departcode,a.doctorcode) a,(select max(a.doctorcode) as doctorcode,max(z.orgLevel) as orgLevel,max(a.departcode) as departcode,max(a.hospitalcode) as hospitalcode,sum(a.sum) as sum, count(1) as num ,sum(a.daysum) as daysum from sup_his_reg a, sup_hospital_zone z  where a.hospitalcode = z.hospitalcode and exists(select 1 from sup_his_reg_item b where a.insno = b.insno ";
		String sql3 = " group by a.hospitalcode,a.departcode,a.doctorcode) b,(select count(1) as outnum,sum(d.absnum) as absnum,d.doctorcode,d.departcode,d.hospitalcode,sum(d.ddd) as ddd from (select a.patientid,max(a.doctorcode) as doctorcode,max(a.hospitalcode) as hospitalcode,max(a.departcode) as departcode,max(case when b.ddd is null then 0 else b.num/b.ddd end)  as ddd,nvl(max(case  when a.absdrugsum !=0 then 1 else 0 end ),0) as absnum from sup_his_reg a,sup_his_reg_item b ,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql4 = "group by a.patientid,a.cdate,a.hospitalcode,a.departcode,a.doctorcode) d group by d.hospitalcode,d.departcode,d.doctorcode) c,(select count(1) as num,e.doctorcode,e.hospitalcode,e.departcode as departcode from (select max(a.doctorcode) as doctorcode,max(a.departcode) as departcode,max(a.hospitalcode) as hospitalcode from sup_his_reg a,sup_his_reg_item b,sup_hospital_zone z where a.hospitalcode=b.hospitalcode and a.insno=b.insno and a.hospitalcode=z.hospitalcode ";
		String sql5 = " group by b.insno || b.model || b.productname ||b.dosageformname) e group by e.hospitalcode,e.departcode,e.doctorcode)  d  where a.hospitalcode=b.hospitalcode and a.hospitalcode= c.hospitalcode and a.hospitalcode= d.hospitalcode and a.doctorCode = b.doctorCode  and a.doctorCode = c.doctorCode and  a.doctorCode = d.doctorCode  and  a.departCode = b.departCode and a.departCode = c.departCode and a.departCode = d.departCode";
		Map<String, Object> query0 = new HashMap<String, Object>();
		Map<String, Object> query1 = new HashMap<String, Object>();
		Map<String, Object> query2 = new HashMap<String, Object>();
		Map<String, Object> params = page.getQuery();
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals("hospitalCode_S_EQ")) {
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
	public List<Map<String, Object>> groupRankingByDoctorAll(PageRequest page) {
		String sql = "select max(c.hospitalCode) as hospitalCode,max(h.doctorCode) as doctorCode,max(h.departCode) as departCode,max(h.hospitalName) as hospitalName,max(substr(h.departname,instr(h.departname,'-')+1)) as departName,"
				+ "max(h.doctorName) as doctorName,h.productCode,max(h.productName) as productname,max(h.dosageformname) as dosageformname,"
				+ "max(h.packdesc) as packdesc,max(h.producername) as producername ,max(m.authorizeNo) as authorizeNo,"
				+ " nvl(max(h.absDrugType),0) as absDrugType,nvl(max(h.auxiliaryType),0) AS auxiliaryType,"
				+ "sum(h.sum) as sum,sum(h.num) as num ,max(h.model) as model,max(m.isGpoPurchase) as isGpoPurchase "
				+ " from sup_drug_analysis_doctor h LEFT JOIN sup_hospital_zone c on  h.hospitalcode=c.hospitalcode left join sup_medicine m ON h.productCode =m.code "
				+ " group by h.productCode,c.hospitalCode,h.departCode,h.doctorCode";
		return this.listBySql2(sql, page, Map.class);
	}

	// 住院药品分析新算法
	@Override
	public DataGrid<Map<String, Object>> groupHisMedicineByDoctor(PageRequest page) {
		 page.setMySort(new Sort(new Order(Direction.ASC, "doctorCode")));
		String sql = "select  max(hospitalname) as hospitalname,max(hospitalCode) as hospitalCode,max(doctorCode) as doctorCode,"
				+ "max(doctorName) as doctorName,max(departname) as departname,max(departCode) as departCode,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ " count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ " round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ " round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ " round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select "
				+ " a.hospitalcode as hospitalcode,max(a.hospitalname) as hospitalname,a.departCode as departCode,max(departName) as departName,"
				+ "a.doctorCode as doctorCode,max(a.doctorName) as doctorName,max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ " count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ " max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ " sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by "
				+ " a.hospitalCode,a.departCode,a.doctorCode,a.insno) group by hospitalcode,departCode,doctorCode";
		return this.findBySql(sql, page, Map.class);
	}

	// 住院药品分析新算法----导出
	@Override
	public List<Map<String, Object>> groupHisMedicineByDoctorAll(PageRequest page) {
		page.setMySort(new Sort(new Order(Direction.ASC, "doctorCode")));
		String sql = "select  max(hospitalname) as hospitalname,max(hospitalCode) as hospitalCode,max(doctorCode) as doctorCode,"
				+ "max(doctorName) as doctorName,max(departname) as departname,max(departCode) as departCode,sum(absnum) as absnum,sum(drugNum) as drugnum,"
				+ " sum(sum)  as sum,sum(daySum) as daySum,sum(dddSum) as dddSum,sum(drugSum) as drugSum,"
				+ " count(1) as outNum,round((sum(sum)/count(1)),2) as avgSum,round((sum(drugSum)/sum(sum)),2)*100 as rateDrugSum,"
				+ " round((sum(drugSum)/count(1)),2) as avgOutdrugSum,round((sum(drugSum)/sum(daysum)),2) as avgDaydrugSum,"
				+ " round((sum(drugnum)/count(1)),2) as avgDrugNum,round((sum(dddSum)/sum(daySum)),2)*100 as  avgdddsum,"
				+ " round((sum(absnum)/count(1)),2)*100 as rateabsnum from (select "
				+ " a.hospitalcode as hospitalcode,max(a.hospitalname) as hospitalname,a.departCode as departCode,max(departName) as departName,"
				+ "a.doctorCode as doctorCode,max(a.doctorName) as doctorName,max(case when a.absDrugType is not null then 1 else 0 end) as absnum ,"
				+ " count(distinct(a.pzm)) as drugNum,sum(a.drugSum) as drugSum,sum(a.sum) as sum,"
				+ " max(a.daySum) as daySum,sum(a.dddSum) as dddSum from sup_his_medicine_analysis a,"
				+ " sup_hospital_zone z where a.hospitalCode = z.hospitalCode group by "
				+ " a.hospitalCode,a.departCode,a.doctorCode,a.insno) group by hospitalcode,departCode,doctorCode";
		return this.listBySql2(sql, page, Map.class);
	}
}
