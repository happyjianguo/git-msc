package com.shyl.msc.dm.dao.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.entity.Product;
/**
 * 产品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class ProductDao extends BaseDao<Product, Long> implements IProductDao {

	@Override
	public Product getByCode(String code) {
		String hql = "from Product p where p.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public DataGrid<Map<String, Object>> npquery(String code ,String name,PageRequest pageable) {
		String sql = "select p.id,p.code,p.name,p.pinyin,p.packDesc,p.DOSAGEFORMNAME,p.model,p.PRODUCERNAME from t_dm_product p"
				+ " where p.code like ? and p.name like ? and p.isDisabled = 0";
		pageable.setSort(new Sort(new Order(Direction.ASC, "p.code")));
		return super.findBySql(sql, pageable, Map.class,"%"+code+"%","%"+name+"%");
	}

	@Override
	public Map<String, Object> count() {
		String sql = "select count(*) as count from t_dm_product p where p.isDisabled=0";
		return super.getBySql(sql, Map.class);
	}



	@Override
	public List<Map<String, Object>> getCentralizedPercent(int maxsize,String year,String month) {
		StringBuffer sql = new StringBuffer();
		//判斷是否年份為空，為空則處理年份
		if (StringUtils.isEmpty(year)) {
			year = new SimpleDateFormat("yyyy").format(new Date());
		}
		//默認日期查詢條件
		String dateQuerySQL = " and to_char(d.orderDate,'yyyy') = '" + year+"'";
		//如果有日期的情況日期條件處理
		if (!StringUtils.isEmpty(month)) {
			dateQuerySQL = " and to_char(d.orderDate,'yyyyMM') = '" + year + month+"'";
		}
		sql.append(" select a.genericname as name,round(nvl(b.num,0)*100/a.num,2) as value from  ");
		sql.append(" (select a.genericname,sum(d.goodssum) as num from t_dm_drug a, t_dm_product b,t_order_inoutbound_detail d  ");
		sql.append(" where a.id=b.drugid and  b.code = d.productcode  ");
		sql.append(dateQuerySQL);
		sql.append(" group by a.genericname) a, ");
		sql.append(" (select a.genericname,sum(d.goodssum) as num from t_dm_drug a, t_dm_product b,t_order_inoutbound_detail d  ");
		sql.append(" where a.id=b.drugid  and b.isgpopurchase=1  and b.code = d.productcode  ");
		sql.append(dateQuerySQL);
		sql.append(" group by a.genericname) b ");
		sql.append(" where a.genericname=b.genericname(+) order by value desc ");

		return super.limitBySql(sql.toString(), maxsize, null, Map.class);
	}

	@Override
	public BigDecimal getGpoPercent() {
		String hql0 = "select count(1) from Product";
		String hql1 = "select count(1) from Product where isGPOPurchase=1";
		Long count0 = (Long)super.count(hql0);
		Long count1 = (Long)super.count(hql1);
		if (count0 == null || count1 == null) {
			return new BigDecimal(0);
		}
		return new BigDecimal(count1).multiply(new BigDecimal(100))
				.divide(new BigDecimal(count0), 2, BigDecimal.ROUND_HALF_UP);
	}
	
	@Override
	public DataGrid<Map<String, Object>> npquery(List<String> productWords, List<String> dosageWords, 
			List<String> producerWords, String productCode, PageRequest pageable) {
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.code,p.name,p.genericName,p.packDesc,p.dosageFormName,p.model,");
		sql.append("p.producerName,p.YBDRUGSNO,p.importFileNo,p.STANDARDCODE,p.AUTHORIZENO ");
		sql.append(",p.productGCode from  t_dm_product p where ((1=1 ");
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
			sql.append(" and p.authorizeNo like '%").append(query.get("p#authorizeNo_S_LK")).append("%'");
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
	public DataGrid<Product> pageByGPO(PageRequest pageable, List<Long> gpoIds) {
		String hql = "from Product p where p.gpoId is not null ";
		int i = 0;
		for(Long gpoId:gpoIds){
			if(i==0){
				hql += " and p.gpoId = "+ gpoId;
			}else{
				hql += " or p.gpoId = "+ gpoId;
			}
			i++;
		}
		return super.query(hql, pageable);
	}

	@Override
	public List<Product> listByGPOS(PageRequest pageRequest, List<Long> gpoIds) {
		String hql = "from Product p where p.gpoId is not null ";
		int i = 0;
		for(Long gpoId:gpoIds){
			if(i==0){
				hql += " and p.gpoId = "+ gpoId;
			}else{
				hql += " or p.gpoId = "+ gpoId;
			}
			i++;
		}
		return super.list(hql, pageRequest);
	}
	
	@Override
	public DataGrid<Product> pageInAttribute(PageRequest pageable){
		String sql = "select p.* from t_dm_product p "
				+ "where p.isdisabled=0 and p.gpocode in (select ai.field1 from t_set_attribute_item ai "
				+ "left join t_set_attribute a on ai.attributeid = a.id where a.attributeNo = 'contract_gpo')";
		return super.findBySql(sql, pageable, Product.class);
	}
	
	@Override
	public DataGrid<Product> pageInProductVendor(PageRequest pageable, String hospitalCode, Integer isInProductVendor) {
		String sql = "select p.* from t_dm_product p "
				+ "where p.isdisabled=0 and p.gpocode in (select ai.field1 from t_set_attribute_item ai "
				+ "left join t_set_attribute a on ai.attributeid = a.id where a.attributeNo = 'contract_gpo')";
		if (!StringUtils.isEmpty(hospitalCode)) {
			if (isInProductVendor!=null && isInProductVendor==1) {
				sql+= " and exists(select 1 from t_dm_product_detail b where p.id= b.productid and b.hospitalCode='"+hospitalCode+"')";
			} else if (isInProductVendor!=null && isInProductVendor==2) {
				sql+= " and not exists(select 1 from t_dm_product_detail b where p.id= b.productid and b.hospitalCode='"+hospitalCode+"')";
				
			}
		}
		
		
		return super.findBySql(sql, pageable, Product.class);
	}

	@Override
	public List<Map<String, Object>> listByGPO(Long gpoId, String scgxsj) {
		String sql = "select p.* from t_dm_product p "
				+ " where p.gpoId=? and to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?";
		return super.listBySql(sql, null, Map.class, gpoId, scgxsj);
	}

	@Override
	public List<Map<String, Object>> listByDate(String scgxsj) {
		String sql = "select p.* from t_dm_product p "
				+ " where to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?";
		return super.listBySql(sql, null, Map.class, scgxsj);
	}

	@Override
	public String getMaxCode() {
		String sql = "select max(code) from t_dm_product";
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
	public String getMaxModelCode(String newcode, String model) {

		String sql = "select max(modelCode) from t_dm_product where model=? and productGCode like ?";
		String modelCode = this.getBySql(sql, null, model, newcode+"%");
		if (!StringUtils.isEmpty(modelCode)) {
			return modelCode;
		}
		
		sql = "select max(modelCode) from t_dm_product where productGCode like ?";
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

		String sql = "select max(packCode) from t_dm_product where model=? and packDesc=? and productGCode like ?";
		String packCode = this.getBySql(sql, null, model, packDesc, newcode + "%");
		if (!StringUtils.isEmpty(packCode)) {
			return packCode;
		}
		
		
		sql = "select max(packCode) from t_dm_product where model=? and  productGCode like ?";
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
	public Product getByPriceFileNo(String priceFileNo, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from Product where priceFileNo like ? ";
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
		List<Product> list = this.listByHql(hql, null, "%"+priceFileNo+"%", Integer.valueOf(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Product getByStandardCode(String standardCode, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from Product where standardCode like ? ";
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
		List<Product> list = this.listByHql(hql, null, "%"+standardCode+"%",  Integer.valueOf(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Product getByAuthorizeNo(String licenseNumber, String[] models, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from Product where (authorizeNo=? or importFileNo=?)  ";
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
		
		
		List<Product> list = this.listByHql(hql, null, licenseNumber, licenseNumber, Integer.valueOf(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public Product getByGenericName(String genericName, String[] models,
			String producerName, String convertRatio) {
		if (StringUtils.isEmpty(convertRatio)) {
			convertRatio = "1";
		}
		//按
		String hql = "from Product where genericName=? and producerName=?";

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
		
		List<Product> list = this.listByHql(hql, null, 
				genericName, producerName, Integer.valueOf(convertRatio));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> listByGPOAndCode(Long gpoId, String code) {
		String sql = "select p.* from t_dm_product p "
				+ " where p.gpoId=? and p.code=?";
		return super.listBySql(sql, null, Map.class, gpoId, code);
	}

	@Override
	public List<Map<String, Object>> producerComb(String projectCode, String productName) {
		String sql = "select t.producerId,t.producerName from t_dm_product t "
				+ " where t.name like '%"+productName+"%' and t.isDisabled=0 "
				+ " group by t.producerId,t.producerName ";
		return super.listBySql(sql, null, Map.class);
	}

	@Override
	public List<Map<String, Object>> productPiciMap() {
		String sql = "select t.id as PRODUCTID,d.BATCH from t_dm_product t join t_dm_directory d on t.directoryId = d.id ";
		return super.listBySql(sql, null, Map.class);
	}
	
	@Override
	public DataGrid<Product> listByVendorAndDate(String vendorCode, String startDate, String endDate, PageParam pageable) {
		String sql = "from Product where code in "
				+ "(select distinct productCode from ProductPriceRecordHis "
				+ "where vendorCode=? and to_char(createDate,'yyyy-MM-dd')>=? and to_char(createDate,'yyyy-MM-dd')<=?)";
		return super.query(sql, pageable, vendorCode, startDate, endDate);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByGoodsHospital(String scgxsj, PageRequest page) {
		String sql = " select t.code,t.genericName,t.name,t.englishName,t.tradeName,t.dosageFormName,t.model, " +
				" t.producerName,t.AuthorizeNo,t.ImportFileNo,d.Field1 as zxyfl,t.PackageMaterial,t.UnitName,t.ConvertRatio, " +
				" t.PackDesc,t.Notes,t.IsDisabled,t.Minunit,t.DoseUnit,t.YbdrugsNO,g.hospitalcode,g.hospitalName " +
				" from t_dm_product t,t_dm_goods_Hospital g,t_set_attribute_item d where t.id=g.productId and t.drugtype=d.id ";

		if (StringUtils.isEmpty(scgxsj)) {
			sql+="(to_char(t.modifyDate,'yyyy-MM-dd hh24:mi:ss')>? or to_char(g.modifyDate,'yyyy-MM-dd hh24:mi:ss')>?)";
			return super.findBySql(sql, page, Map.class, scgxsj, scgxsj);
		}
		return super.findBySql(sql, page, Map.class);
	}
}
