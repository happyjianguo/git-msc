package com.shyl.msc.set.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IHospitalDao;
import com.shyl.msc.set.entity.Hospital;
/**
 * 医疗机构DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class HospitalDao extends BaseDao<Hospital, Long> implements IHospitalDao {

	@Override
	public List<Map<String, Object>> listAll() {
		String sql = "select h.id,h.code,h.shortName,h.fullName,r.name as regionCodeName  from t_set_hospital h "
					+ "left outer join t_set_regioncode r on h.regionCode=r.id ";
		return super.listBySql(sql, null, Map.class);
	}

	
	@Override
	public List<Hospital> list(PageRequest pageable,Long province, Long city, Long county) {
		String hql = "select t from Hospital t,RegionCode r where t.regionCode=r.id";
		Object[] obj = null;
		if (!StringUtils.isEmpty(county)) {
			hql+=" and t.regionCode=?";
			obj = new Object[]{county};
		} else if (!StringUtils.isEmpty(city)) {
			hql+=" and exists(select 1 from RegionCode b where b.id=? and r.treePath like b.treePath||','||b.id||'%')";
			obj = new Object[]{city};
		} else if (!StringUtils.isEmpty(province)) {
			hql+=" and exists(select 1 from RegionCode b where b.id=? and r.treePath like b.id||'%')";
			obj = new Object[]{province};
		}
		System.out.println("hql+:"+hql);
		return super.list(hql, pageable, obj);
	}
	
	@Override
	public Hospital findByCode(String code) {
		String hql = "from Hospital where code=?";
		return super.getByHql(hql, code);
	}
	
	@Override
	public Hospital findByIocode(String iocode) {
		String hql = "from Hospital where iocode=?";
		return super.getByHql(hql, iocode);
	}

	@Override
	public Map<String, Object> count() {
		String sql = "select count(*) as count from t_set_hospital h where h.isDisabled=0";
		return super.getBySql(sql, Map.class);
	}
	
	@Override
	public Hospital findByName(String name) {
		String hql = "from Hospital where fullName=? and rownum=1";
		return super.getByHql(hql, name);
	}
}
