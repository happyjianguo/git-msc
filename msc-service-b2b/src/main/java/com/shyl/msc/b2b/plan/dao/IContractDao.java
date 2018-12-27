package com.shyl.msc.b2b.plan.dao;

import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;

public interface IContractDao extends IBaseDao<Contract, Long> {

	public List<Contract> listByGPO(String gpoCode, boolean isGPO, Status status, String cxkssj, String cxjssj);

	public Contract findByCode(String code);

	public Contract getByKey(String hospitalCode, String gpoCode, String vendorCode);

	public List<Contract> listByHospital(String hospitalCode, Status status);

	public DataGrid<Map<String, Object>> reportForHospitalContract(String projectCode, String startDate, String endDate,
			PageRequest pageable);

	public List<Map<String, Object>> reportForHospitalContract1(String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable);

	public List<Map<String, Object>> reportForHospitalContract2(String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable);

	/**
	 * 查询未过账数据
	 * @param isPass
	 * @return
	 */
	public List<Contract> listByIsPass(int isPass);

	public DataGrid<Contract> listByContractAndDetail(PageRequest page);
}
