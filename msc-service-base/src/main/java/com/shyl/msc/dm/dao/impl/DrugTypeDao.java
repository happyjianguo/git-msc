package com.shyl.msc.dm.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IDrugTypeDao;
import com.shyl.msc.dm.entity.DrugType;

/**
 * 药品分类DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class DrugTypeDao extends BaseDao<DrugType, Long> implements IDrugTypeDao {
	@Override
	public List<DrugType> listByParentId(Long parentId) {
		String hql = "from DrugType where treepath||',' like ? and nvl(isDisabled,0)=0 order by code ";
		return super.listByHql(hql,null, parentId+",%");
	}

	@Override
	public DrugType findByCode(String code) {
		String hql = "from DrugType where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public DrugType findByTree(String name, String treepath) {
		String hql = "from DrugType where name=? ";
		if (!StringUtils.isEmpty(treepath)) {
			hql += " and treePath = '"+treepath+"'";
		} else {
			hql += " and treePath is null";
		}
		return super.getByHql(hql, name);
	}

	@Override
	public String getMaxCode(String type) {
		String code = (String)this.getBySql("select max(code) from t_dm_drug_type where code like '"
					+ type + "__'", null);
		if (code == null) {
			code = type + "01";
		} else {
			//拆除开头
			String newcode = code.substring(type.length(), code.length());
			//加1
			code = Integer.toString(Integer.valueOf(newcode) + 1);
			for(int i= code.length();i<2;i++) {
				code = "0"+code;
			}
			code = type + code;
		}
		return code;
	}
	
}
