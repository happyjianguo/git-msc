package com.shyl.msc.set.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.ICompanyDao;
import com.shyl.msc.set.entity.Company;
/**
 * 中标企业DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class CompanyDao extends BaseDao<Company, Long> implements ICompanyDao {

	@Override
	public List<Map<String, Object>> listGPO() {
		//String sql = "select c.id,('('||c.pinyin||')'||c.shortName) as shortName from t_set_company c where c.isGPO=1";
		String sql = "select c.id,c.shortName, c.fullName from t_set_company c where c.isGPO=1";
		return super.listBySql(sql, null, Map.class);
	}

	@Override
	public List<Map<String, Object>> listVendor() {
		//String sql = "select c.id,('('||c.pinyin||')'||c.shortName) as shortName from t_set_company c where c.isVendor=1";
		String sql = "select c.id,c.shortName, c.fullName,c.code from t_set_company c where c.isVendor=1";
		return super.listBySql(sql, null, Map.class);
	}

	@Override
	public Company findByCode(String code) {
		String hql = "from Company where code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public Company findByCode(String code, String para) {
		
		String hql = "from Company where code=? and "+para;
		return super.getByHql(hql, code);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByType(PageRequest pageable,
			String name, String para) {
		String sql = "select c.*,rr.name as rname from t_set_company c "
				+ "left join t_set_regioncode r on c.regionCode = r.id "
				+ "left join t_set_regioncode rr on r.treePath  like rr.id||+',%' ";
		if(StringUtils.isEmpty(name) && !StringUtils.isEmpty(para)){
			sql = sql + " where " +para;
		}else if(!StringUtils.isEmpty(name) && StringUtils.isEmpty(para)){
			sql = sql + " where c.fullName like '%"+name+"%'";
		}else if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(para)){
			sql = sql + " wherec.fullName like '%"+name+"%' and "+para;
		}
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public Map<String, Object> count(String type) {
		String sql = "select count(*) as count from t_set_company where isDisabled=0 and "+type;
		return super.getBySql(sql, Map.class);
	}

	@Override
	public DataGrid<Company> pageByType(PageRequest pageable, String companyType) {
		String hql = "from Company t";
		if(!StringUtils.isEmpty(companyType)){
			hql += " where "+companyType;
		}
		return super.query(hql, pageable);
	}
	

	@Override
	public Company findByName(String name) {
		String hql = "from Company where fullName=?";
		return super.getByHql(hql, name);
	}

	@Override
	public String getMaxCode() {
		String code = (String)this.getBySql("select max(code) from t_set_company where code like '_____'", null);
		if (code == null) {
			code = "10001";
		} else {
			code = Integer.toString(Integer.valueOf(code) + 1);
		}
		return code;
	}

}
