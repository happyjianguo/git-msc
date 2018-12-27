package com.shyl.msc.supervise.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.supervise.dao.IBaseDrugProgressDao;
import com.shyl.msc.supervise.entity.BaseDrugProgress;
import com.shyl.msc.supervise.entity.BaseDrugProvide;

@Repository
public class BaseDrugProgressDao extends BaseDao<BaseDrugProgress, Long> implements IBaseDrugProgressDao {

	@Override
	public BaseDrugProgress findUnique(String month, String hospitalCode) {
		String hql = "from BaseDrugProgress where month=? and hospitalCode=?";
		return super.getByHql(hql, month,hospitalCode);
	}

	@Override
	public BaseDrugProgress findByType(Integer healthStationType) {
		String hql = "from BaseDrugProgress where HealthStationType=? and rownum=1";
		return this.getByHql(hql, healthStationType);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByCity(PageRequest page) {
		String sql = "select b.cityCode,max(b.cityName) as CITYNAME,sum(case when b.healthStationType=0 then 1 else 0 end) as HEALTHSTATIONNUM,sum(case when b.isHighSixty = 1 then 1 else 0 end) as ISHIGHSIXTY,"
      +" sum(case when b.isImplementedStation = 1 then 1 else 0 end) as ISIMPLEMENTEDSTATION,sum(case when b.isGeneralStation = 1 then 1 else 0 end) as ISGENERALSTATION,"
      +" sum(case when b.isThirdHealthStation = 1 then 1 else 0 end) as ISTHIRDHEALTHSTATION,sum(case when b.isInHealthInsurance = 1 then 1 else 0 end) as ISINHEALTHINSURANCE from t_hospital_baseDrugProgress b group by b.cityCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByCounty(PageRequest page) {
		String sql = "select b.cityCode,max(b.cityName) as CITYNAME,b.countyCode,max(countyName) as COUNTYNAME,sum(case when b.healthStationType=0 then 1 else 0 end) as HEALTHSTATIONNUM,sum(case when b.isHighSixty = 1 then 1 else 0 end) as ISHIGHSIXTY,"
			      +" sum(case when b.isImplementedStation = 1 then 1 else 0 end) as ISIMPLEMENTEDSTATION,sum(case when b.isGeneralStation = 1 then 1 else 0 end) as ISGENERALSTATION,"
			      +" sum(case when b.isThirdHealthStation = 1 then 1 else 0 end) as ISTHIRDHEALTHSTATION,sum(case when b.isInHealthInsurance = 1 then 1 else 0 end) as ISINHEALTHINSURANCE "
			      +" from t_hospital_baseDrugProgress b group by b.cityCode,b.countyCode";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<Map<String, Object>> groupByHealth(PageRequest page) {
		page.setSort(new Sort(new Order(Direction.DESC, "b.month")));
		String sql = "select b.cityCode,max(b.cityName) as CITYNAME,b.countyCode,max(countyName) as COUNTYNAME,b.month,sum(case when b.healthStationType=0 then 1 else 0 end) as HEALTHSTATIONNUM,sum(case when b.isHighSixty = 1 then 1 else 0 end) as ISHIGHSIXTY,"
			      +" sum(case when b.isImplementedStation = 1 then 1 else 0 end) as ISIMPLEMENTEDSTATION,sum(case when b.isGeneralStation = 1 then 1 else 0 end) as ISGENERALSTATION,"
			      +" sum(case when b.isThirdHealthStation = 1 then 1 else 0 end) as ISTHIRDHEALTHSTATION,sum(case when b.isInHealthInsurance = 1 then 1 else 0 end) as ISINHEALTHINSURANCE "
			      +" from t_hospital_baseDrugProgress b group by b.cityCode,b.countyCode,b.month";
		return this.findBySql(sql, page, Map.class);
	}

	

}
