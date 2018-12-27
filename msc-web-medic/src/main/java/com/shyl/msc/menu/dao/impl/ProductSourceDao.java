package com.shyl.msc.menu.dao.impl;



import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.menu.dao.IProductSourceDao;
import com.shyl.msc.menu.entity.ProductSource;
/**
 * 产品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ProductSourceDao extends BaseDao<ProductSource, Long> implements IProductSourceDao {


	@Override
	public ProductSource getByCode(String code) {
		return this.getByHql("from ProductSource where code=?", code);
	}

	@Override
	public ProductSource getByStandardCodeOnly(String standardCode) {
		//return this.getByHql("from ProductSource where standardCode=? limit 1", standardCode);
		List<ProductSource> productSources = this.limitList("from ProductSource where standardCode=?",1,null,standardCode);
		return productSources.isEmpty() ? null : productSources.get(0);
	}

	@Override
	public ProductSource getByStandardCodeAndPack(String standardCode, String genericName, String model, String packDesc) {
		/*ProductSource source = this.getByHql("from ProductSource where standardCode=? and drug.genericName=? and model=? and packDesc=? limit 1",
				 standardCode,genericName,model, packDesc);*/

		List<ProductSource> productSources = this.limitList("from ProductSource where standardCode=? and drug.genericName=? and model=? and packDesc=?",1,null,standardCode,genericName,model, packDesc);
		return productSources.isEmpty()?null:productSources.get(0);
	}

	@Override
	public ProductSource getByNewcode(String code, String model, String packDesc) {
		return this.getByHql("from ProductSource where model=? and packDesc=? and  productGCode like ?",
				model, packDesc, code+"%");
	}

	@Override
	public String getMaxModelCode(String newcode, String model) {

		String sql = "select max(modelCode) from t_menu_product_source where model=? and productGCode like ?";
		String modelCode = this.getBySql(sql, null, model, newcode+"%");
		if (!StringUtils.isEmpty(modelCode)) {
			return modelCode;
		}
		
		sql = "select max(modelCode) from t_menu_product_source where productGCode like ?";
		modelCode = (String)this.getBySql(sql,
				null, newcode+"%");
		if (modelCode == null) {
			modelCode = "01";
		} else {
			modelCode = Integer.toString(Integer.valueOf(modelCode) + 1);
			//规格bocode
			for(int i = modelCode.length(); i < 2; i++) {
				modelCode = "0" + modelCode;
			}
		}
		return modelCode;
	}

	@Override
	public String getMaxPackCode(String newcode, String model, String packDesc) {

		String sql = "select max(packCode) from t_menu_product_source where model=? and packDesc=? and productGCode like ?";
		String packCode = this.getBySql(sql, null, model, packDesc, newcode + "%");
		if (!StringUtils.isEmpty(packCode)) {
			return packCode;
		}
		
		
		sql = "select max(packCode) from t_menu_product_source where model=? and  productGCode like ?";
		packCode = (String)this.getBySql(sql, null, model, newcode + "%");
		if (packCode == null) {
			packCode = "01";
		} else {
			packCode = Integer.toString(Integer.valueOf(packCode) + 1);
			for(int i = packCode.length(); i < 2; i++) {
				packCode = "0" + packCode;
			}
		}
		return packCode;
	}
	


	@Override
	public String getMaxCode() {
		String sql = "select max(code) from t_menu_product_source";
		String code = this.getBySql(sql, null);
		if (code == null) {
			code = "10001";
		} else {
			code = Integer.toString(Integer.valueOf(code) + 1);
			for(int i = code.length(); i < 5; i++) {
				code = "0" + code;
			}
		}
		return code;
	}

	@Override
	public Long getStandardCodeCount(String standardCode) {
		Long count = this.count("select count(1) from ProductSource where standardCode=?", standardCode);
		return count == null ? 0: count;
	}
	@Override
	public ProductSource getByYbdrugsNO(String ybdrugsNO, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from ProductSource where ybdrugsNO like ? and isError=99 ";
		if (models != null) {
			String where = "";
			for(String model : models) {
				if (!"".equals(where)) {
					where += " or ";
				}
				where += " lower(model) like '%" + model + "%'";
			}
			hql+=" and (" + where +")";
		}
		hql += " order by (case when nvl(convertRatio,1)=? then 1 else 0 end) desc";
		List<ProductSource> list = this.listByHql(hql, null, "%"+ybdrugsNO+"%",  new BigDecimal(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public ProductSource getByYjcode(String yjCode) {
		//按
		String hql = "from ProductSource where yjCode = ? and isError=99 ";
		List<ProductSource> list = this.listByHql(hql, null, yjCode);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public ProductSource getByPriceFileNo(String priceFileNo, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from ProductSource where priceFileNo like ? and isError=99 ";
		if (models != null) {
			String where = "";
			for(String model : models) {
				if (!"".equals(where)) {
					where += " or ";
				}
				where += " lower(model) like '%" + model + "%'";
			}
			hql+=" and (" + where +")";
		}
		hql += " order by (case when nvl(convertRatio,1)=? then 1 else 0 end) desc";
		List<ProductSource> list = this.listByHql(hql, null, "%"+priceFileNo+"%",  new BigDecimal(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public ProductSource getByStandardCode(String standardCode, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from ProductSource where standardCode like ? and isError=99 ";
		if (models != null) {
			String where = "";
			for(String model : models) {
				if (!"".equals(where)) {
					where += " or ";
				}
				where += " lower(model) like '%" + model + "%'";
			}
			hql+=" and (" + where +")";
		}
		hql += " order by (case when nvl(convertRatio,1)=? then 1 else 0 end) desc";
		List<ProductSource> list = this.listByHql(hql, null, "%"+standardCode+"%",  new BigDecimal(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public ProductSource getByAuthorizeNo(String licenseNumber, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from ProductSource where (authorizeNo=? or regeditNo=?)  and isError=99 ";
		if (models != null) {
			String where = "";
			for(String model : models) {
				if (!"".equals(where)) {
					where += " or ";
				}
				where += " lower(model) like '%" + model + "%'";
			}
			hql+=" and (" + where +")";
		}
		hql += " order by (case when nvl(convertRatio,1)=? then 1 else 0 end) desc";
		List<ProductSource> list = this.listByHql(hql, null, licenseNumber, licenseNumber, new BigDecimal(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public ProductSource getByGenericName(String genericName, String[] models,
			String producerName, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from ProductSource where genericName=? and producerName=? and isError=99 ";

		if (models != null) {
			String where = "";
			for(String model : models) {
				if (!"".equals(where)) {
					where += " or ";
				}
				where += " lower(model) like '%" + model + "%'";
			}
			hql+=" and (" + where +") ";
		}
		hql += " order by (case when nvl(convertRatio,1)=? then 1 else 0 end) desc";
		
		List<ProductSource> list = this.listByHql(hql, null, 
				genericName, producerName, new BigDecimal(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable) {
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.code,p.name,p.yjcode,p.genericName,p.packDesc,p.dosageFormName,p.model,p.producerName,p.YBDRUGSNO,p.REGEDITNO,p.STANDARDCODE,p.AUTHORIZENO ");
		sql.append(",p.productGCode from  t_menu_product_source p where ((isError in(0,99) ");
		//判断key是否为空
		if (productWords != null && productWords.size() > 0) {
			sql.append(" and ").append(this.getSeachKeySql("p.genericName", productWords));
		}
		if (dosageWords != null && dosageWords.size() > 0) {
			sql.append(" and ").append(this.getSeachKeySql("p.dosageFormName", dosageWords));
		}
		if (producerWords != null && producerWords.size() > 0) {
			sql.append(" and ").append(this.getSeachKeySql("p.producerName", producerWords));
		}
		
		Map<String,Object> query = pageable.getQuery();
		if (query != null && !StringUtils.isEmpty(query.get("p#authorizeNo_S_LK"))) {
			sql.append(" and p.authorizeNo like '").append(query.get("p#authorizeNo_S_LK")).append("'");
			query.remove("p#authorizeNo_S_LK");
		}
		sql.append(")");
		Sort sort = null;
		
		if (!StringUtils.isEmpty(productCode)) {
			sql.append(" or p.code='").append(productCode).append("' ");
			String order = "(case when p.code='"+productCode+"' then 0 else 1 end)";
			sort = new Sort(
					new Order(Direction.ASC, order),
					new Order(Direction.ASC,"p.genericName"),
					new Order(Direction.ASC,"p.packDesc"));
		} else {
			sort = new Sort(new Order(Direction.ASC,"p.genericName"),
					new Order(Direction.ASC,"p.packDesc"));
		}
		sql.append(")");
		pageable.setSort(sort);
		return super.findBySql(sql.toString(), pageable, Map.class);
	}

    /**
     * SQL分词
     * @param column
     * @param words
     */
    public String getSeachKeySql(String column, List<String> words) {
        StringBuffer sql = new StringBuffer();
        for (String key : words) {
        	if (sql.length() != 0) {
        		sql.append(" or ");
        	}
            sql.append(column).append(" like '%").append(key).append("%'");
        }
        sql.insert(0, "(").append(")");
        return sql.toString();
    }
    
    @Override
    public List<ProductSource> queryNotinProduct(int maxsize) {
    	if (maxsize <= 0) {
    		maxsize = 1;
    	}
    	String hql = "from ProductSource a where not exists(select 1 from Product b where a.code=b.code) and isError=99";
    	return super.limitList(hql, maxsize, null);
    }
    



	@Override
	public void deletByReset() {
		this.executeSql("update t_menu_product_source set productGcode='',modelCode='',packCode=''");
	}
	


	@Override
	public void clearCode() {
		this.executeSql("update t_menu_product_source set productgcode='',modelCode='',packCode=''");
	}
}
