package com.shyl.msc.dm.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.entity.Goods;
/**
 * 商品DAO实现类
 * 
 * @author a_Q
 *
 */
@Repository
public class GoodsDao extends BaseDao<Goods, Long> implements IGoodsDao {

	@Override
	public DataGrid<Goods> pageByGPO(PageRequest pageable,String vendorCode) {
		String flag = "";
		if(pageable.getQuery()!=null){
			String name = (String)pageable.getQuery().get("G#NAME_M_LK");
			if(!name.equals("")){
				flag = "and (g.name like '%"+name+"%' or g.pinyin like '%"+name+"%')";
			}
		}
		String sql = "select g.id,g.name,g.biddingPrice from t_dm_goods g where g.vendorCode=?" + flag;
		return super.findBySql(sql, pageable, Map.class, vendorCode);
	}

	@Override
	public DataGrid<Goods> pageByEnabled(PageRequest pageable) {
		String flag = "";
		if(pageable.getQuery()!=null){
			String name = (String)pageable.getQuery().get("G#NAME_M_LK");
			if(!StringUtils.isEmpty(name)){
				flag = " where  (g.name like '%"+name+"%' or g.pinyin like '%"+name+"%')";
			}
		}
		String sql = "select g.id,g.name,g.biddingPrice from t_dm_goods g " + flag;
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public Goods getByProduct(String productCode) {
		String hql = "from Goods g where g.productCode=? and g.isDisabled=0";
		return super.getByHql(hql, productCode);
	}

	@Override
	public DataGrid<Goods> pageByHospital(PageRequest pageable, String hospitalCode) {
		String hql = "from Goods t where t.hospitalCode=? and t.isDisabled=0 ";
		return super.query(hql, pageable, hospitalCode);
	}

	public DataGrid<Map<String, Object>> pagePlaceByHospital(PageRequest pageable, String hospitalCode) {
		String sql = "select a.ID,b.id as GOODSPRICEID,t.CODE, t.NAME,t.pinyin, t.GENERICNAME, "
				+ "t.DOSAGEFORMNAME, t.MODEL, t.PACKDESC, t.PRODUCERNAME,t.UNITNAME,"
				+ " b.VENDORCODE,b.FINALPRICE, b.BEGINDATE, b.OUTDATE, c.fullname as vendorname"
				+ " from t_dm_goods a "
				+ " join t_dm_goods_price b on a.id = b.GOODSID "
				+ " left join t_dm_product t on a.PRODUCTCODE = t.CODE"
				+ " left join t_set_company c on b.vendorCode = c.CODE"
				+ " where a.HOSPITALCODE=? and a.ISDISABLED=0 "
				+ " and b.isDisabled=0 and b.isDisabledByH=0";
		Sort sort = new Sort(new Order(Direction.ASC,"t.CODE"));
		pageable.setSort(sort);
		return super.findBySql(sql, pageable, Map.class, hospitalCode);
	}
	
	@Override
	public Goods getByProductAndHospital(String productCode,String hospitalCode,int isDisabled) {
		String hql = " from Goods g where g.productCode=? and g.hospitalCode=? and g.isDisabled=?";

		return super.getByHql(hql, productCode,hospitalCode,isDisabled);
	}

	@Override
	public List<Map<String, Object>> listByHospital(String hospitalCode, String lastDate) {
		String sql = "select g.id,p.CODE,p.GENERICNAME,p.NAME,p.ENGLISHNAME,p.TRADENAME,p.DOSAGEFORMNAME,p.drugType,"
				+ " p.MODEL,p.PRODUCERNAME,p.AUTHORIZENO,p.PACKAGEMATERIAL,p.UNITNAME,p.CONVERTRATIO,"
				+ " p.PACKDESC,p.NOTES,p.isGPOPurchase,p.gpoId,p.isdisabled as p_isdisabled,g.isdisabled as g_isdisabled,p.importFileNo"
				+ " from t_dm_goods g left join t_dm_product p on g.productCode=p.code"
				+ " where g.hospitalCode=? and (to_char(p.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=? or to_char(g.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=?) ";
		return super.listBySql(sql, null, Map.class, hospitalCode, lastDate,lastDate);
	}

	@Override
	public Goods getByProductCodeAndHosiptal(String productCode, String hospitalCode) {
		String hql = " from Goods where productCode=? and hospitalCode=?";
		return super.getByHql(hql, productCode,hospitalCode);
	}


	@Override
	public DataGrid<Map<String, Object>> listForAutoOrder(String hospitalCode, String vendorCode,PageRequest pageable) {
		String sql = "select g.*,c.DOSAGEFORMNAME,c.MODEL,c.PACKDESC,p.id as GOODSPRICEID,p.finalPrice from t_dm_goods g "
				+ " left join t_dm_goods_price p on g.id = p.goodsId and p.isDisabled=0 and p.isDisabledByH=0 "
				+ " left join t_dm_product c on g.productCode = c.code "
				+ " where c.gpoid is null and g.hospitalCode = ? and p.vendorCode=?  "
				+ " order by  g.stockNum - g.stockDownLimit ";
		return super.findBySql(sql, pageable, Map.class, hospitalCode,vendorCode);
	}

	@Override
	public DataGrid<Map<String, Object>> hospitalListForAutoOrder(String vendorCode,PageRequest pageable) {

		String sql = "select distinct a.hospitalCode,h.fullname as hospitalname,b.NUM from t_dm_goods a "
				+ "left join (select g.hospitalCode,count(g.id) as num from t_dm_goods g "
				+ "left join t_dm_goods_price p on g.id = p.goodsId and p.isDisabled=0 and p.isDisabledByH=0 "
				+ "left join t_dm_product t on g.productCode = t.code "
				+ "where t.gpoid is null and p.vendorCode=? and g.stockNum < g.stockDownLimit "
				+ " group by g.hospitalCode ) b on a.hospitalCode = b.hospitalCode "
				+ " left join t_dm_goods_price c on a.id = c.goodsId and c.isDisabled=0 and c.isDisabledByH=0 "
				+ "left join t_set_hospital h on a.hospitalCode=h.code "
				+ " where c.vendorCode=? order by b.num ";
		System.out.println("sql===="+sql);
		return super.findBySql(sql, pageable, Map.class, vendorCode,vendorCode);
	}

	@Override
	public Goods getByCode(String productCode) {
		String hql = "from Goods g where g.productCode=? ";
		return super.getByHql(hql, productCode);
	}

	@Override
	public Goods getByProductIdAndType(String productCode) {
		String hql = "from Goods g where g.productCode=? ";
		return super.getByHql(hql, productCode);
	}

	@Override
	public List<Goods> findByPriceKey(String productCode, String vendorCode, String hospitalCode) {
		String hql = "from Goods where productId=? and vendorId=? ";
		if(hospitalCode != null){
			hql += " and hospitalCode='"+hospitalCode+"'";
		}else{
			hql += " hospitalCode is null ";
		}
		return super.listByHql(hql,null, productCode, vendorCode);
	}

	@Override
	public List<Map<String, Object>> listByHospital(String hospitalCode) {
		String sql = "select a.PRODUCTCODE, c.name as PRODUCTNAME, c.GENERICNAME, c.DOSAGEFORMNAME, c.MODEL,"
				+ " c.PACKDESC, c.PRODUCERNAME, a.ISDISABLED, b.VENDORCODE, d.fullname as VENDORNAME,"
				+ " b.FINALPRICE, b.BEGINDATE, b.OUTDATE from t_dm_goods a "
				+ " left join t_dm_goods_price b on a.id = b.GOODSID and b.isDisabled=0 and b.isDisabledByH=0"
				+ " left join t_dm_product c on a.PRODUCTCODE = c.CODE"
				+ " left join t_set_company d on b.VENDORCODE = d.CODE"
				+ " where a.HOSPITALCODE=? and a.ISDISABLED=0";
		Sort sort = new Sort(new Order(Direction.ASC,"a.PRODUCTCODE"));
		return super.listBySql(sql, sort, Map.class, hospitalCode);
	}

	@Override
	public List<Map<String, Object>> listByHospitalWithGPO(String hospitalCode) {
		String sql = "select a.PRODUCTCODE, c.name as PRODUCTNAME, c.GENERICNAME, c.DOSAGEFORMNAME, c.MODEL,"
				+ " c.PACKDESC, c.PRODUCERNAME, a.ISDISABLED, b.VENDORCODE, d.fullname as VENDORNAME,"
				+ " b.FINALPRICE, b.BEGINDATE, b.OUTDATE, c.UNITNAME"
				+ " from t_dm_goods a "
				+ " join t_dm_goods_price b on a.id = b.GOODSID "
				+ " left join t_dm_product c on a.PRODUCTCODE = c.CODE"
				+ " left join t_set_company d on b.VENDORCODE = d.CODE"
				+ " where a.HOSPITALCODE=? and a.ISDISABLED=0 "
				+ " and b.isDisabled=0 and b.isDisabledByH=0";
		Sort sort = new Sort(new Order(Direction.ASC,"a.PRODUCTCODE"));
		return super.listBySql(sql, sort, Map.class, hospitalCode);
	}

	@Override
	public List<Goods> listByProductCode(String productCode) {
		String hql = "from Goods t where t.productCode = ? and t.isDisabled =0 ";
		return super.listByHql(hql, null, productCode);
	}

	@Override
	public DataGrid<Map<String, Object>> queryHospitalAndProduct(PageRequest pageable) {
		String sql = " select h.fullname as HOSPITALNAME,g.PRODUCTCODE,p.name as PRODUCTNAME,p.DOSAGEFORMNAME,p.model,p.producerName,p.insuranceDrugType,p.baseDrugType "
				+ " from t_dm_goods g "
				+ " left join t_dm_product p on g.productId = p.id "
				+ " left join t_set_hospital h on g.hospitalCode = h.code "
				+ " where g.isDisabled = 0";
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> listByProductCodeMap(String productCode) {
		String sql = "select g.id,p.CODE,p.GENERICNAME,p.NAME,p.ENGLISHNAME,p.TRADENAME,p.DOSAGEFORMNAME,p.drugType,"
				+ " p.MODEL,p.PRODUCERNAME,p.AUTHORIZENO,p.PACKAGEMATERIAL,p.UNITNAME,p.CONVERTRATIO,"
				+ " p.PACKDESC,p.NOTES,p.isGPOPurchase,p.gpoId,p.isdisabled as p_isdisabled,g.isdisabled as g_isdisabled,p.importFileNo"
				+ " from t_dm_goods g left join t_dm_product p on g.productCode=p.code"
				+ " where p.code=?";
		return super.listBySql(sql, null, Map.class, productCode);
	}
}
