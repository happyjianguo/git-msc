package com.shyl.msc.supervise.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IBaseDrugProvideDao;
import com.shyl.msc.supervise.entity.BaseDrugProvide;
/**
 * 医院基本药物配备使用比例DAO实现类
 * 
 *
 */
@Repository
public class BaseDrugProvideDao extends BaseDao<BaseDrugProvide, Long> implements IBaseDrugProvideDao {

	@Override
	public BaseDrugProvide findUnique(String month, String hospitalCode) {
		String hql = "from BaseDrugProvide where month=? and hospitalCode=?";
		return super.getByHql(hql, month,hospitalCode);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByCounty(PageRequest page) {
		String sql = "select b.countyCode as COUNTYCODE,max(b.countyName) as COUNTYNAME,sum(baseDrugTotal) as BASEDRUGTOTAL,sum(drugTotal) as DRUGTOTAL,sum(baseDrugTrade) as "
				+" BASEDRUGTRADE,sum(drugTrade) as DRUGTRADE from t_hospital_basedrugprovide b group by b.countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByHospital(PageRequest page) {
		String sql = "select b.countyCode as COUNTYCODE,max(b.countyName) as COUNTYNAME,b.hospitalCode as HOSPITALCODE,max(b.hospitalName) as HOSPITALNAME,max(b.isreformhospital) as ISREFORMHOSPITAL,sum(baseDrugTotal) "
				+" as BASEDRUGTOTAL,sum(drugTotal) as DRUGTOTAL,sum(baseDrugTrade) as BASEDRUGTRADE,sum(drugTrade) as DRUGTRADE from t_hospital_basedrugprovide b group by b.countyCode,b.hospitalCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByMonth(PageRequest page) {
		String sql = "select b.countyCode as COUNTYCODE,max(b.countyName) as COUNTYNAME,b.hospitalCode as HOSPITALCODE,max(b.hospitalName) as HOSPITALNAME,b.month,max(b.isreformhospital) as ISREFORMHOSPITAL, "
				+" sum(baseDrugTotal) as BASEDRUGTOTAL,sum(drugTotal) as DRUGTOTAL,sum(baseDrugTrade) as "
				+" BASEDRUGTRADE,sum(drugTrade) as DRUGTRADE from t_hospital_basedrugprovide b group by b.countyCode,b.hospitalCode,b.month";
		return this.findBySql(sql, page, Map.class);
	}

}
