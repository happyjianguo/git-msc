package com.shyl.msc.b2b.plan.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import jdk.nashorn.internal.runtime.ECMAException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.entity.Sort.Direction;
import com.shyl.common.entity.Sort.Order;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.plan.dao.IContractDetailDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.b2b.plan.entity.ContractDetail;


@Repository
public class ContractDetailDao extends BaseDao<ContractDetail, Long> implements IContractDetailDao {

    @Override
    public List<ContractDetail> listByContractId(Long contractId) {
        String hql = "from ContractDetail c where c.contract.id=?";
        return super.listByHql(hql, null, contractId);
    }

    @Override
    public List<ContractDetail> findByPID(Long pid) {
        String hql = "from ContractDetail t where t.contract.id=?";
        return super.listByHql(hql, null, pid);
    }

    @Override
    public Long countByPID(Long pid) {
        String sql = "select count(*) from t_plan_contract_detail where contractId = ?";
        Long count = super.countBySql(sql, pid);
        return count == null ? 0 : count;
    }


    @Override
    public List<ContractDetail> listByHospitalCode(Sort sort, String hospitalCode) {
        String hql = "from ContractDetail cd left join fetch cd.contract c left join fetch cd.product p"
                + " where c.status=? and c.hospitalCode=?";
        return super.listByHql(hql, sort, Contract.Status.noConfirm, hospitalCode);
    }

    @Override
    public ContractDetail getByKey(Long productId, String hospitalCode, String gpoCode, String vendorCode) {
        String hql = "from ContractDetail cd left join fetch cd.contract c"
                + " where c.status=? and cd.product.id=? and c.hospitalCode=? and c.gpoCode=? and c.vendorCode=?";
        return super.getByHql(hql, Contract.Status.noConfirm, productId, hospitalCode, gpoCode, vendorCode);
    }

    @Override
    public ContractDetail listByKey(String hospitalCode, String gpoCode, String vendorCode) {
        String hql = "from ContractDetail cd left join fetch cd.contract c"
                + " where c.status=?  and c.hospitalCode=? and c.gpoCode=? and c.vendorCode=?";
        return super.getByHql(hql, Contract.Status.noConfirm, hospitalCode, gpoCode, vendorCode);
    }


    @Override
    public DataGrid<ContractDetail> queryByH(String hospitalCode, PageRequest pageable) {
        String hql = "from ContractDetail t left join fetch t.product p where t.contract.hospitalCode=? and t.contract.status =? ";
        return super.query(hql, pageable, hospitalCode, Status.signed);
    }

    @Override
    public List<ContractDetail> findOlder(String hospitalCode, String vendorCode, Long productId) {
        String hql = "from ContractDetail t left join fetch t.contract left join fetch t.product where"
                + " t.contract.status=? and t.contract.hospitalCode=? and"
                + " t.contract.vendorCode=? and"
                + " t.product.id=? and t.contract.startValidDate<=? and  t.contract.endValidDate>=? and "
                + " t.contractNum-t.purchasePlanNum>0";
        Sort sort = new Sort(new Order(Direction.ASC, "t.contract.startValidDate"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        return super.listByHql(hql, sort, Contract.Status.signed, hospitalCode, vendorCode, productId, date, date);
    }

    @Override
    public List<Map<String, Object>> listByHospitalSigned(String hospitalCode) {
        String sql = "select a.PRODUCTID,a.code as CONTRACTCODE,c.code as PRODUCTCODE,a.price,c.UNITNAME,c.name as PRODUCTNAME,c.GENERICNAME,c.DOSAGEFORMNAME,c.MODEL,c.PACKDESC,c.PRODUCERNAME,b.VENDORCODE,b.VENDORNAME,b.endValidDate,(a.contractNum-a.purchasePlanNum-a.cartNum) as LASTNUM,a.cartNum "
                + " from t_plan_contract_detail a "
                + " left join t_plan_contract b on a.contractId = b.id "
                + " left join t_dm_product c on a.productId = c.id "
                + " where b.hospitalCode=? and b.status=? and b.startValidDate<=? and b.endValidDate>=? and a.contractNum-a.purchasePlanNum-a.cartNum>0 order by c.code ";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        return super.listBySql(sql, null, Map.class, hospitalCode, 4, date, date);
    }

    @Override
    public List<ContractDetail> listBySigned() {
        String hql = "from ContractDetail t "
                + " where t.contract.status=? and t.status!=? and t.contract.startValidDate<=? and t.contract.endValidDate>=? and t.contractNum-t.purchasePlanNum>0 order by t.contract.effectiveDate";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        return super.listByHql(hql, null, Contract.Status.signed, ContractDetail.Status.stop, date, date);
    }

    @Override
    public List<ContractDetail> listBySigned(Long productId, String hospitalCode, String vendorCode) {
        String hql = "from ContractDetail t "
                + " where t.product.id=? and t.contract.hospitalCode=? and t.contract.vendorCode=? " +
                " and t.contract.status=? and t.contract.startValidDate<=? and t.contract.endValidDate>=? " +
                "and t.contractNum-t.purchasePlanNum>0 order by t.contract.effectiveDate";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        return super.listByHql(hql, null, productId, hospitalCode, vendorCode, Contract.Status.signed, date, date);
    }

    @Override
    public DataGrid<Map<String, Object>> tradeByProduct(PageRequest pageable, String startDate, String endDate) {
        String sql = "select p.CODE,p.NAME,p.DOSAGEFORMNAME,p.MODEL,p.PACKDESC,"
                + "SUM(cd.CONTRACTNUM) as CONTRACTNUM, SUM(cd.PURCHASEPLANNUM) as PURCHASEPLANNUM,"
                + " SUM(cd.PURCHASENUM) as PURCHASENUM, SUM(cd.DELIVERYNUM) as DELIVERYNUM, SUM(cd.RETURNSNUM) as RETURNSNUM"
                + " from t_plan_contract_detail cd "
                + " left join t_dm_product p on cd.PRODUCTID=p.ID"
                + " left join t_plan_contract c on cd.CONTRACTID=c.ID"
                + " where c.status=? ";
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            sql += " and ((c.startValidDate>='" + startDate + "' and  c.endValidDate<='" + endDate + "'))  ";
        } else {
            if (!StringUtils.isEmpty(startDate)) {
                sql += " and c.startValidDate>='" + startDate + "'";
            }
            if (!StringUtils.isEmpty(endDate)) {
                sql += " and c.endValidDate<='" + endDate + "'";
            }
        }
        sql += " group by p.CODE,p.NAME, p.DOSAGEFORMNAME, p.MODEL, p.PACKDESC";
        return super.findBySql(sql, pageable, Map.class, Contract.Status.signed.ordinal());
    }

    @Override
    public DataGrid<Map<String, Object>> tradeDetailByProduct(PageRequest pageable, String startDate, String endDate) {
        String sql = "select p.CODE,p.NAME,p.DOSAGEFORMNAME,p.MODEL,p.PACKDESC,"
                + "cd.CONTRACTNUM, cd.PURCHASEPLANNUM, "
                + " cd.PURCHASENUM, cd.DELIVERYNUM, cd.RETURNSNUM, "
                + " cd.code as detailCode, c.hospitalName, c.vendorName"
                + " from t_plan_contract_detail cd "
                + " left join t_dm_product p on cd.PRODUCTID=p.ID"
                + " left join t_plan_contract c on cd.CONTRACTID=c.ID"
                + " where c.status=? ";
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            sql += " and ((c.startValidDate<='" + startDate + "' and  c.endValidDate>='" + startDate + "') or "
                    + " (c.startValidDate<='" + endDate + "' and c.endValidDate>='" + endDate + "') or"
                    + " (c.startValidDate>='" + startDate + "' and c.endValidDate<='" + endDate + "')) ";
        } else {
            if (!StringUtils.isEmpty(startDate)) {
                sql += " and c.endValidDate>='" + startDate + "'";
            }
            if (!StringUtils.isEmpty(endDate)) {
                sql += " and c.startValidDate<='" + endDate + "'";
            }
        }
        return super.findBySql(sql, pageable, Map.class, Contract.Status.signed.ordinal());
    }

    @Override
    public DataGrid<ContractDetail> pageByHospitalCode(PageRequest pageRequest, String hospitalCode) {
        String hql = "from ContractDetail t left join fetch t.contract c left join fetch t.product p"
                + " where c.status=? and c.hospitalCode=?";
        return super.query(hql, pageRequest, Contract.Status.noConfirm, hospitalCode);
    }

    @Override
    public List<ContractDetail> listBySigned(String hospitalCode) {
        String hql = "from ContractDetail t left join fetch t.contract left join fetch t.product where"
                + " t.contract.status in(?,?) and t.status=? and t.contract.hospitalCode=? and"
                + " t.contract.startValidDate<=? and  t.contract.endValidDate>=? and "
                + " t.contractNum-t.purchasePlanNum>0";
        Sort sort = new Sort(new Order(Direction.ASC, "t.contract.startValidDate"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        return super.listByHql(hql, sort, Contract.Status.signed, Contract.Status.executed, ContractDetail.Status.effect, hospitalCode, date, date);
    }

    @Override
    public List<ContractDetail> listByCodes(String hospitalCode, String ContractDetailCtrodeStr) {
        String hql = "from ContractDetail t left join fetch t.contract left join fetch t.product where"
                + " t.contract.hospitalCode=? and t.code in(" + ContractDetailCtrodeStr + ") ";
        Sort sort = new Sort(new Order(Direction.ASC, "t.contract.startValidDate"));
        return super.listByHql(hql, sort, hospitalCode);
    }

    @Override
    public ContractDetail findByCode(String code) {
        String hql = "from ContractDetail cd where cd.code=?";
        return super.getByHql(hql, code);
    }

    @Override
    public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable) {
        String sql = "select b.CODE as PRODUCTCODE,"
                + " b.NAME as PRODUCTNAME,b.GENERICNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,"
                + " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(cd.CONTRACTAMT)as AMT,SUM(cd.CONTRACTNUM) as NUM,b.AUTHORIZENO as AUTHORIZENO,max(cd.price) as price "
                + " from t_plan_contract_detail cd "
                + " left join t_dm_product b on cd.PRODUCTID=b.ID"
                + " left join t_plan_contract c on cd.CONTRACTID=c.ID"
                + " where c.status=? "
                + " group by b.CODE, b.NAME, b.DOSAGEFORMNAME, b.MODEL, b.GENERICNAME, b.PACKDESC, b.UNITNAME, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
        return super.findBySql(sql, pageable, Map.class, Contract.Status.signed.ordinal());
    }

    @Override
    public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable) {
        String sql = "select p.CODE as PRODUCTCODE,"
                + " p.NAME as PRODUCTNAME,p.DOSAGEFORMNAME,p.UNITNAME,p.ISGPOPURCHASE,"
                + " p.MODEL,cd.CONTRACTAMT as AMT,cd.CONTRACTNUM as NUM,cd.CODE,c.VENDORNAME,c.HOSPITALNAME, c.code as CONTRACTCODE,p.AUTHORIZENO as AUTHORIZENO"
                + " from t_plan_contract_detail cd "
                + " left join t_dm_product p on cd.PRODUCTID=p.ID"
                + " left join t_plan_contract c on cd.CONTRACTID=c.ID"
                + " where c.status=? ";
        return super.findBySql(sql, pageable, Map.class, Contract.Status.signed.ordinal());
    }

    @Override
    public List<Map<String, Object>> listByProductReport(PageRequest pageable) {
        String sql = "select b.CODE as PRODUCTCODE,"
                + " b.NAME as PRODUCTNAME,b.GENERICNAME,b.DOSAGEFORMNAME,b.UNITNAME,b.ISGPOPURCHASE,"
                + " b.MODEL,b.PACKDESC,b.PRODUCERNAME,SUM(cd.CONTRACTAMT)as AMT,SUM(cd.CONTRACTNUM) as NUM,b.AUTHORIZENO as AUTHORIZENO,max(cd.price) as price "
                + " from t_plan_contract_detail cd "
                + " left join t_dm_product b on cd.PRODUCTID=b.ID"
                + " left join t_plan_contract c on cd.CONTRACTID=c.ID"
                + " where c.status=? "
                + " group by b.CODE, b.NAME, b.DOSAGEFORMNAME, b.MODEL, b.GENERICNAME, b.PACKDESC, b.UNITNAME, b.PRODUCERNAME,b.ISGPOPURCHASE,b.AUTHORIZENO";
        return super.listBySql2(sql, pageable, Map.class, Contract.Status.signed.ordinal());
    }

    @Override
    public List<ContractDetail> listByExecution(PageRequest pageable) {
        String hql = "from ContractDetail t where t.status!=2 and t.contract.status in(4,5)";
        return super.list(hql, pageable);
    }

    @Override
    public DataGrid<ContractDetail> pageByExecution(PageRequest pageable) {
        String hql = "from ContractDetail t where t.status!=2 and t.contract.status in(4,5)";
        return super.query(hql, pageable);
    }

    @Override
    public List<ContractDetail> listUnClosedByHospital(String hospitalCode) {
        String hql = "from ContractDetail cd where cd.status!=? and cd.contract.status=? and cd.contract.hospitalCode=? and cd.contractNum-cd.purchasePlanNum>0 and cd.contract.endValidDate>=to_char(sysdate,'yyyy-MM-dd')";
        return super.listByHql(hql, null, ContractDetail.Status.stop, Contract.Status.signed, hospitalCode);
    }

    @Override
    public List<ContractDetail> listByIsPass(int isPass) {
        String hql = "from ContractDetail where isPass=? and "
                + "(contract.status=? or contract.status=? or contract.status=?)";
        return super.listByHql(hql, null, isPass,
                Contract.Status.signed, Contract.Status.executed, Contract.Status.stop);
    }

    @Override
    public int updatePurchasePlanNum(Long contractId, BigDecimal purchasePlanNum) {
        String sql = "update t_plan_contract_detail set purchasePlanNum=purchasePlanNum+?,purchasePlanAmt=purchasePlanAmt+(?*price) where id=? and contractNum-?>=purchasePlanNum";
        return super.executeSql(sql, purchasePlanNum, purchasePlanNum, contractId, purchasePlanNum);
    }
}
