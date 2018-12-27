package com.shyl.msc.b2b.plan.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.b2b.plan.dao.IContractClosedRequestDao;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest.ClosedObject;

@Repository
public class ContractClosedRequestDao extends BaseDao<ContractClosedRequest, Long> implements IContractClosedRequestDao {

	@Override
	public ContractClosedRequest findByContract(String code) {
//		String hql = "form ContractClosedRequest t where t.contractCode=?";
//		return super.getByHql(hql, code);
		String sql = "select * from t_plan_contract_closedrequest where contractCode=? and CLOSEDOBJECT=? and status != ?";
		return super.getBySql(sql, ContractClosedRequest.class, code,ClosedObject.contract.ordinal(),ContractClosedRequest.Status.disagree.ordinal());
	}

	@Override
	public ContractClosedRequest findByContractDetail(String code) {
		String sql = "select * from t_plan_contract_closedrequest where contractDetailCode=? and CLOSEDOBJECT=? and status != ?";
		return super.getBySql(sql, ContractClosedRequest.class, code,ClosedObject.contractDetail.ordinal(),ContractClosedRequest.Status.disagree.ordinal());
	}
	
	@Override
	public DataGrid<ContractClosedRequest> queryByOrg(PageRequest pageable, String hospitalCode, String vendorCode, String gpoCode) {
		String sql = "select t.* from t_plan_contract_closedrequest t left join t_plan_contract b on t.contractCode = b.code "
				+ " where 1=1 ";
		if(!StringUtils.isEmpty(hospitalCode)){
			sql += " and b.hospitalCode='"+hospitalCode+"'";
		}
		if(!StringUtils.isEmpty(vendorCode)){
			sql += " and b.vendorCode='"+vendorCode+"'";
		}
		if(!StringUtils.isEmpty(gpoCode)){
			sql += " and b.gpoCode='"+gpoCode+"'";
		}
		return super.findBySql(sql, pageable, ContractClosedRequest.class);
	}
	
	@Override
	public List<Map<String, Object>> listByDate(String code, String cxkssj, String cxjssj, boolean isGPO) {
		String sql = "select a.*,to_char(a.closedRequestDate,'yyyy-mm-dd hh24:mi:ss') as REQUESTDATE,"
				+ " b.hospitalCode, b.gpoCode, b.vendorCode"
				+ " from t_plan_contract_closedrequest a"
				+ " left join t_plan_contract b on a.contractCode = b.code "
				+ " where to_char(a.createDate,'yyyy-mm-dd hh24:mi:ss')>=?"
				+ " and to_char(a.createDate,'yyyy-mm-dd hh24:mi:ss')<=? ";
		if(isGPO){
			sql += " and b.gpoCode=? ";
		}else{
			sql += " and b.vendorCode=? ";
		}
		return super.listBySql(sql, null, Map.class, cxkssj, cxjssj, code);
	}

	@Override
	public ContractClosedRequest findByCode(String code) {
		String hql = "from ContractClosedRequest c where c.code=?";
		return super.getByHql(hql, code);
	}

	@Override
	public List<Map<String, Object>> listByCode(String code) {
		String sql = "select a.*,to_char(a.closedRequestDate,'yyyy-mm-dd hh24:mi:ss') as REQUESTDATE,"
				+ " b.hospitalCode, b.gpoCode, b.vendorCode"
				+ " from t_plan_contract_closedrequest a"
				+ " left join t_plan_contract b on a.contractCode = b.code "
				+ " where a.code=?";
		return super.listBySql(sql, null, Map.class, code);
	}

	
	
}
