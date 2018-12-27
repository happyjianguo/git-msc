package com.shyl.msc.b2b.plan.service;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.ContractClosedRequest;

public interface IContractClosedRequestService extends IBaseService<ContractClosedRequest, Long> {

	public ContractClosedRequest findByContract(@ProjectCodeFlag String projectCode, String code);

	public DataGrid<ContractClosedRequest> queryByOrg(@ProjectCodeFlag String projectCode, PageRequest pageable, String hospitalCode, String vendorCode, String gpoCode);

	public List<Map<String, Object>> listByDate(@ProjectCodeFlag String projectCode, String code, String cxkssj, String cxjssj, boolean isGPO);

	public ContractClosedRequest findByCode(@ProjectCodeFlag String projectCode, String code);

	public void saveRequest(@ProjectCodeFlag String projectCode, ContractClosedRequest contractClosedRequest);

	public List<Map<String, Object>> listByCode(@ProjectCodeFlag String projectCode, String code);

	public ContractClosedRequest findByContractDetail(@ProjectCodeFlag String projectCode, String code);

	
}
