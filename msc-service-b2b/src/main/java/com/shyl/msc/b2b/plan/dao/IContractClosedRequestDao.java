package com.shyl.msc.b2b.plan.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;

public interface IContractClosedRequestDao extends IBaseDao<ContractClosedRequest, Long> {

	public ContractClosedRequest findByContract(String code);

	public DataGrid<ContractClosedRequest> queryByOrg(PageRequest pageable, String hospitalCode, String vendorCode, String gpoCode);

	public List<Map<String, Object>> listByDate(String code, String cxkssj, String cxjssj, boolean isGPO);

	public ContractClosedRequest findByCode(String code);

	public List<Map<String, Object>> listByCode(String code);

	public ContractClosedRequest findByContractDetail(String code);

	
}
