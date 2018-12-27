package com.shyl.msc.set.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IRegionCodeDao;
import com.shyl.msc.set.entity.RegionCode;

@Repository
public class RegionCodeDao extends BaseDao<RegionCode,Long> implements IRegionCodeDao{

	@Override
	public List<RegionCode> getChlidList(Long pid) {
		String hql = "from RegionCode where parentId= "+pid;
		if(pid == null){
			hql = "from RegionCode where parentId is null ";
		}
		
		hql += " order by name ";
		return super.listByHql(hql,null);
	}

	@Override
	public List<RegionCode> getLvlone() {
		String hql = "from RegionCode where parentId is null order by name ";
		return super.listByHql(hql, null);
	}

	@Override
	public RegionCode findByCode(String code) {
		String hql = "from RegionCode where code=?";
		return super.getByHql(hql, code);
	}

}
