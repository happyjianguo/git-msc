package com.shyl.msc.dm.dao.impl;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.Entity;
import com.shyl.common.entity.PageParam;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.dm.dao.IGoodsCountryDao;
import com.shyl.msc.dm.entity.GoodsCountry;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class GoodsCountryDao  extends BaseDao<GoodsCountry, Long> implements IGoodsCountryDao {

    /**
     * 数据过滤下的查询
     * @param pageable
     * @return DataGrid
     */
    @Override
    public DataGrid<Map<String, Object>> selectAll(PageRequest pageable) {
        String sql = "select t.* FROM (select a.hospitalcode,a.hospitalName,a.internalCode,a.productName,a.packDesc,a.model,a.dosageFormName," +
                "  a.producerName,p.code SproductCode,p.productGcode SproductGcode,p.name SproductName,p.packDesc SpackDesc," +
                "  p.model Smodel,p.dosageFormName SdosageFormName,p.producerName SproducerName,p.authorizeNo SauthorizeNo," +
                "  p.nationalCode SnationalCode,p.isGPOPurchase SisGPOPurchase,"+
                "  b.countryProductCode,b.countryProductName,b.countryProducerCode,b.countryProducerName,b.countryAuthorizeNo," +
                "  b.parentTypeName,b.parentTypeCode,b.medicTypeName,b.medciTypeCode,b.varietyName,b.varietyCode,b.acidSalt," +
                "  b.acidSaltCode,b.countryDosageFormName,b.countryDosageFormCode,b.countryModel,b.countryModelCode,b.packdesc CpackDesc," +
                "  b.countryConvertRatio,b.countryConvertRatioCode,b.material,b.minPackageUnit,b.minPreparationUnit," +
                "  b.countryProductGCode" +
                " from t_dm_goods_hospital a left join t_dm_product p on a.productId=p.id" +
                " left join t_dm_goods_country b on a.productId = b.productId " +
                " order by a.hospitalcode) t";
         return this.findBySql(sql,pageable,Map.class);
    }

    /**
     * 重写BaseDao查询
     * @param sql
     * @param pageable
     * @param clz
     * @param args
     * @param <N>
     * @return DataGrid
     */
    @Override
    public  <N> DataGrid<N> findBySql(String sql, PageParam pageable, Class<?> clz, Object... args) {
        System.out.println("findBySql=====");
        if (pageable == null) {
            pageable = new PageRequest();
        }

        int gb2 = sql.lastIndexOf("t");
        String preSql2 = sql;
        preSql2 = preSql2 + " where 1=1 ";

        HqlUtil hqlUtil = new HqlUtil();
        hqlUtil.addFilter(((PageParam)pageable).getQuery());
        sql = preSql2 + hqlUtil.getWhereHql();
        String cq = this.getCountSql_self(sql);
        hqlUtil.setSort((PageParam)pageable);
        sql = this.initSort(sql, ((PageParam)pageable).getMySort());
        sql = this.easyuiSort(sql, ((PageParam)pageable).getSort(), ((PageParam)pageable).getOrder());
        System.out.println("sql==========88888888====" + sql);
        SQLQuery sq = this.getSession().createSQLQuery(sql);
        this.setParameter(sq, args);
        this.setAliasParameter(sq, hqlUtil.getParams());
        sq.setFirstResult(((PageParam)pageable).getOffset()).setMaxResults(((PageParam)pageable).getPageSize());
        if (clz != null) {
            if (Map.class.isAssignableFrom(clz)) {
                sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            } else if (Entity.class.isAssignableFrom(clz)) {
                sq.addEntity(clz);
            } else {
                sq.setResultTransformer(Transformers.aliasToBean(clz));
            }
        }

        DataGrid<N> page = new DataGrid(sq.list(), (PageParam)pageable,  super.countBySql(cq, hqlUtil.getParams(), args).longValue());
        return page;
    }

    /**
     * 计数
     * @param sql
     * @return
     */
    private String getCountSql_self(String sql){

        String countSQL = "select count(1) from (" + sql + ") tmp_count_t";
        return countSQL;
    }

    /**
     * 导出查询
     * @param pageable
     * @return List
     */
    @Override
    public List<Map<String, Object>> selectAllforExport(PageRequest pageable) {
        String sql = "select t.* FROM (select a.hospitalcode,a.hospitalName,a.internalCode,a.productName,a.packDesc,a.model,a.dosageFormName," +
                "  a.producerName,p.code SproductCode,p.productGcode SproductGcode,p.name SproductName,p.packDesc SpackDesc," +
                "  p.model Smodel,p.dosageFormName SdosageFormName,p.producerName SproducerName,p.authorizeNo SauthorizeNo," +
                "  nvl2(p.nationalCode,'1','0') as SnationalCode,p.isGPOPurchase SisGPOPurchase,"+
                "  b.countryProductCode,b.countryProductName,b.countryProducerCode,b.countryProducerName,b.countryAuthorizeNo," +
                "  b.parentTypeName,b.parentTypeCode,b.medicTypeName,b.medciTypeCode,b.varietyName,b.varietyCode,b.acidSalt," +
                "  b.acidSaltCode,b.countryDosageFormName,b.countryDosageFormCode,b.countryModel,b.countryModelCode,b.packdesc CpackDesc," +
                "  b.countryConvertRatio,b.countryConvertRatioCode,b.material,b.minPackageUnit,b.minPreparationUnit," +
                "  b.countryProductGCode" +
                " from t_dm_goods_hospital a left join t_dm_product p on a.productId=p.id" +
                " left join t_dm_goods_country b on a.productId = b.productId " +
                " order by a.hospitalcode) t";
        return this.listBySql2(sql,pageable,Map.class);
    }

    /**
     * 重写BaseDao导出查询
     * @param sql
     * @param pageable
     * @param clz
     * @param args
     * @param <N>
     * @return List
     */
    @Override
    public <N> List<N> listBySql2(String sql, PageParam pageable, Class<?> clz, Object... args) {
        System.out.println("listBySql2===============");
        if (pageable == null) {
            pageable = new PageRequest();
        }
        int gb = sql.lastIndexOf("t");
        String preSql = sql + " where 1 = 1";

        HqlUtil hqlUtil = new HqlUtil();
        hqlUtil.addFilter(((PageParam)pageable).getQuery());

        sql = preSql + hqlUtil.getWhereHql() ;
        sql = this.initSort(sql, ((PageParam)pageable).getMySort());
        System.out.println("list sql==========**************========="+sql);
        SQLQuery sq = this.getSession().createSQLQuery(sql);
        if (clz != null) {
            if (Map.class.isAssignableFrom(clz)) {
                sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            } else if (Entity.class.isAssignableFrom(clz)) {
                sq.addEntity(clz);
            } else {
                sq.setResultTransformer(Transformers.aliasToBean(clz));
            }
        }

        this.setParameter(sq, args);
        this.setAliasParameter(sq, hqlUtil.getParams());
        return sq.list();
    }

   @Override
   public List<Map<String,Object>> getCountryDrugByCode(PageRequest pageable){
        String sql = "select COUNTRYPRODUCTCODE,COUNTRYPRODUCTNAME,COUNTRYPRODUCERCODE,COUNTRYPRODUCERNAME,COUNTRYAUTHORIZENO,COUNTRYDOSAGEFORMCODE," +
                " COUNTRYDOSAGEFORMNAME,COUNTRYMODEL,COUNTRYMODELCODE,COUNTRYCONVERTRATIO,COUNTRYCONVERTRATIOCODE,COUNTRYPRODUCTGCODE from t_dm_country_drug_back ";
        return listBySql2(sql,pageable,Map.class);
   }
}
