package com.shyl.msc.dm.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IDosageFormDao;
import com.shyl.msc.dm.entity.DosageForm;
/**
 * 剂型DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class DosageFormDao extends BaseDao<DosageForm, Long> implements IDosageFormDao {

	public DosageForm getByName(String name, String parentName) {
		String hql = "select * from t_dm_dosageform a,t_dm_dosageform b where a.name=? and a.parentId=b.id";
		if (!StringUtils.isEmpty(parentName)) {
			hql+=" and b.name='"+parentName+"'";
		}
		return this.getBySql(hql, DosageForm.class, name);
	}
	
	public DosageForm getByCode(String code) {
		return super.getByHql("from DosageForm where code=?", code);
	}

	public String getMaxCode() {
		String code = (String)this.getBySql("select max(code) from t_dm_dosageform where code like '______'", null);
		if (code == null) {
			code = "001001";
		} else {
			code = Integer.toString(Integer.valueOf(code) + 1);
			for(int i = code.length();i<6;i++) {
				code = "0" + code;
			}
		}
		return code;
	}
	
}
