package com.shyl.msc.b2b.plan.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.common.framework.util.HqlUtil;
import com.shyl.msc.b2b.plan.dao.IContractDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;

@Repository
public class ContractDao extends BaseDao<Contract, Long> implements IContractDao {

	@Override
	public List<Contract> listByGPO(String gpoCode, boolean isGPO, Status status, String cxkssj, String cxjssj) {
		String hql = "";
		if (isGPO) {
			hql = "from Contract c where c.gpoCode=? and c.status=? and to_char(c.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(c.modifyDate,'yyyy-mm-dd hh24:mi:ss')<=?";
		} else {
			hql = "from Contract c where c.vendorCode=? and c.status=? and to_char(c.modifyDate,'yyyy-mm-dd hh24:mi:ss')>=? and to_char(c.modifyDate,'yyyy-mm-dd hh24:mi:ss')<=?";
		}
		return super.listByHql(hql, null, gpoCode, status, cxkssj, cxjssj);
	}

	@Override
	public Contract findByCode(String code) {
		String hql = "from Contract c where c.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public Contract getByKey(String hospitalCode, String gpoCode, String vendorCode) {
		String hql = "from Contract c where c.status=? and c.hospitalCode=? and c.gpoCode=? and c.vendorCode=?";
		return super.getByHql(hql, Contract.Status.noConfirm, hospitalCode, gpoCode, vendorCode);
	}

	@Override
	public List<Contract> listByHospital(String hospitalCode, Status status) {
		String hql = "from Contract c where c.hospitalCode=? and c.status=?";
		return super.listByHql(hql, null, hospitalCode, status);
	}

	@Override
	public DataGrid<Map<String, Object>> reportForHospitalContract(String projectCode, String startDate, String endDate,
			PageRequest pageable) {
		String sql = "select t.hospitalCode ,t.hospitalName ,rc.name as REGIONCODE,count(distinct t.id) as contractnum "
				+ " from t_plan_contract t " + " left join t_plan_contract_detail cd on t.id = cd.contractId "
				+ " left join t_dm_product p  on cd.productId = p.id "
				+ " left join t_set_hospital h on t.hospitalCode = h.code "
				+ " left join t_set_regioncode rc on h.regionCode = rc.id  " + " where (t.status = "
				+ Status.signed.ordinal() + " or t.status = " + Status.executed.ordinal() + ")   ";
		if (startDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')>='" + startDate + "'";
		}
		if (endDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')<='" + endDate + "'";
		}
		sql += " group by  rc.name,t.hospitalCode,t.hospitalName  ";
		System.out.println("sql = ====" + sql);
		return super.findBySql(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> reportForHospitalContract1(String projectCode, String hostipalCodes,
			String startDate, String endDate, PageRequest pageable) {
		String sql = "select t.hospitalCode ,count(distinct cd.productId) as productNum ,sum(cd.contractAmt) as contractAmt "
				+ " from t_plan_contract t " + " left join t_plan_contract_detail cd on t.id = cd.contractId "
				+ " left join t_dm_product p  on cd.productId = p.id "
				+ " left join t_set_hospital h on t.hospitalCode = h.code " + " where (t.status = "
				+ Status.signed.ordinal() + " or t.status = " + Status.executed.ordinal() + ") and t.hospitalCode in  "
				+ hostipalCodes;
		if (startDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')>='" + startDate + "'";
		}
		if (endDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')<='" + endDate + "'";
		}
		sql += " group by  t.hospitalCode";
		System.out.println("sql = 111====" + sql);
		return super.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public List<Map<String, Object>> reportForHospitalContract2(String projectCode, String hostipalCodes,
			String startDate, String endDate, PageRequest pageable) {
		String sql = "select t.hospitalCode ,count(distinct cd.productId) as productOrderNum ,sum(cd.purchaseAmt) as purchaseAmt "
				+ " from t_plan_contract t " + " left join t_plan_contract_detail cd on t.id = cd.contractId "
				+ " left join t_dm_product p  on cd.productId = p.id "
				+ " left join t_set_hospital h on t.hospitalCode = h.code " + " where (t.status = "
				+ Status.signed.ordinal() + " or t.status = " + Status.executed.ordinal()
				+ ") and cd.purchaseNum>0 and t.hospitalCode in  " + hostipalCodes;
		if (startDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')>='" + startDate + "'";
		}
		if (endDate != null) {
			sql += " and to_char(t.effectiveDate,'yyyy-mm-dd')<='" + endDate + "'";
		}
		sql += " group by  t.hospitalCode";
		System.out.println("sql = 222====" + sql);
		return super.listBySql2(sql, pageable, Map.class);
	}

	@Override
	public List<Contract> listByIsPass(int isPass) {
		String hql = "from Contract where isPass=? and (status=? or status=? or status=?)";
		return super.listByHql(hql, null, isPass, Contract.Status.signed, Contract.Status.executed,
				Contract.Status.stop);
	}

	@Override
	public DataGrid<Contract> listByContractAndDetail(PageRequest pageable) {
		String sql = "select distinct(t.code) as code,t.id,t.hospitalName,t.gpoName,t.vendorName,"
				+ " to_char(t.hospitalConfirmDate,'yyyy-MM-dd HH24:mi:ss') as hospitalConfirmDate,"
				+ " to_char(t.gpoConfirmDate,'yyyy-MM-dd HH24:mi:ss') as gpoConfirmDate,"
				+ " to_char(t.vendorConfirmDate,'yyyy-MM-dd HH24:mi:ss') as vendorConfirmDate,t.startValidDate,t.endValidDate,"
				+ "  to_char(t.effectiveDate,'yyyy-MM-dd HH24:mi:ss') as effectiveDate,t.filePath,t.status,"
				+ " t.pageNum,t.hospitalX,t.hospitalY,t.gpoX,t.gpoY,t.vendorX,t.vendorY "
				+ " from t_plan_contract t LEFT JOIN t_plan_contract_detail d ON t.id=d.contractId  left join  t_dm_product p on d.productId=p.id ";
		DataGrid<Contract> result = findBySql(sql, pageable, Map.class);
		Map<String, Object> params = pageable.getQuery();
		HqlUtil hqlUtil = new HqlUtil();
		hqlUtil.addFilter(params);
		params = hqlUtil.getParams();
		sql = sql + " where 1=1 " + hqlUtil.getWhereHql();
		String cq = "select count(1) from (" + sql + ")";
		result.setTotal(countBySql(cq, params));
		return result;
	}
}
