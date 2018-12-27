package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IGoodsHospitalSourceDao;
import com.shyl.msc.dm.entity.GoodsHospitalSource;
/**
 * 
 * 
 * @author a_Q
 *
 */
@Repository
public class GoodsHospitalSourceDao extends BaseDao<GoodsHospitalSource, Long> implements IGoodsHospitalSourceDao {

	@Override
	public GoodsHospitalSource getByCode(String internalCode, String productName, String producerName, 
			String hospitalName, String model) {
		
		return this.getByHql("from GoodsHospitalSource where nvl(internalCode,'#')=nvl(?,'#') and nvl(productName,'#')=? and nvl(producerName,'#')=nvl(?,'#') " +
				"and hospitalName=? and nvl(model,'#')=nvl(?,'#')", internalCode, productName, producerName, hospitalName, model);
	}


	@Override
	public DataGrid<Map<String, Object>> npquery(PageRequest page) {
		String sql = "select t.id,t.HOSPITALNAME,t.InternalCode,t.Yyjgcode,t.YbdrugsNO,t.AuthorizeNo,t.status," 
				+ "t.YjCode,t.StandardCode,t.PriceFileNo,t.GenericName,t.ProductName,t.model,"
				+ "to_char(t.ConvertRatio0) as ConvertRatio0,to_char(t.ConvertRatio) as CONVERTRATIO,t.UnitName,t.ProducerName,t.DosageFormName,"
				+ "to_char(t.FinalPrice) as FinalPrice,t.VendorName,t.BiddingPrice,t.BaseMark,t.ProductCode,"
				+ "b.GenericName as GenericName0,b.Name as productname0,b.Model as model0,b.PackDesc as PackDesc0,"
				+ "b.ProducerName as ProducerName0,to_char(b.ConvertRatio) as ConvertRatio1"
				+ ",b.standardCode as standardCode0,b.authorizeNo as authorizeNo0,b.importFileNo as importFileNo,b.ENGLISHNAME,b.TRADENAME,t.minUnit as minunit,b.minUnit as minunit0"
				+ " from t_dm_goods_hospital_source t,t_dm_product b where t.PRODUCTCODE=b.CODE(+)";
		return this.findBySql(sql, page, Map.class);
	}

	@Override
	public DataGrid<GoodsHospitalSource> npquery(List<String> productWords, List<String> dosageWords, List<String> producerWords, PageRequest pageable) {
		StringBuffer hql = new StringBuffer(" from GoodsHospitalSource where 1=1 ");
		//判断key是否为空
		if (productWords != null && productWords.size() > 0) {
			hql.append(" and ").append(this.getSeachKeySql("productName", productWords));
		}
		if (dosageWords != null && dosageWords.size() > 0) {
			hql.append(" and ").append(this.getSeachKeySql("dosageFormName", dosageWords));
		}
		if (producerWords != null && producerWords.size() > 0) {
			hql.append(" and ").append(this.getSeachKeySql("producerName", producerWords));
		}
		return super.query(hql.toString(), pageable);
	}



    /**
     * SQL分词
     * @param operator
     * @param column
     * @param key
     * @param words最终分词的List
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
    public int updateByProductCode(String newCode, String oldCode) {
    	return this.executeHql("update GoodsHospitalSource set productCode=? where productCode=?", newCode, oldCode);
    }

    
    @Override
    public List<GoodsHospitalSource> queryNotinGoods(int maxsize, Long hospitalId, Long status) {
    	if (maxsize <= 0) {
    		maxsize = 1;
    	}
    	if (status == null) {
    		status = 99L;
    	}
    	String hql = "from GoodsHospitalSource a where status=? and hospitalId=? and not exists(select 1 from Goods b where a.productCode=b.product.code and a.hospitalId=b.hospitalId) ";
    	return super.limitList(hql, maxsize, null,status, hospitalId);
    }

    @Override
    public int updateStatusByIds(String ids, Long status, Long hospitalId) {
    	return this.executeHql("update GoodsHospitalSource set status=? where hospitalId=? and id in("+ids+")", status, hospitalId);
    }
}
