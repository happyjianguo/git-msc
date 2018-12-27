package com.shyl.msc.b2b.plan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.Contract.Status;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.set.entity.Company;
import com.shyl.sys.entity.User;

public interface IContractService extends IBaseService<Contract, Long> {

	public List<Contract> listByGPO(@ProjectCodeFlag String projectCode, String gpoCode, boolean isGPO, Status status, String cxkssj, String cxjssj);

	public Contract findByCode(@ProjectCodeFlag String projectCode, String code);

	/**
	 * 医院签订
	 * @param id
	 * @param user 
	 */
	public void hospitalSigned(@ProjectCodeFlag String projectCode, Long id, User user);

	/**
	 * 
	 * @param hospitalCode
	 * @param gpoCode
	 * @param vendorCode
	 * @return
	 */
	public Contract getByKey(@ProjectCodeFlag String projectCode, String hospitalCode, String gpoCode, String vendorCode);

	public List<Contract> listByHospital(@ProjectCodeFlag String projectCode, String hospitalCode, Status status);

	public void saveContract(@ProjectCodeFlag String projectCode, String hospitalCode, Product product, String gpoCode, String vendorCode, BigDecimal num) throws MyException;

	public String importExcel(@ProjectCodeFlag String projectCode, String[][] upExcel, User user) throws MyException;

	public JSONArray getToGPO(@ProjectCodeFlag String projectCode, Company gpo, boolean isGPO);

	public void saveContractBatch(@ProjectCodeFlag String projectCode, String hospitalCode, String data);

	public DataGrid<Map<String, Object>> reportForHospitalContract(@ProjectCodeFlag String projectCode, String startDate, String endDate,
			PageRequest pageable);

	public List<Map<String, Object>> reportForHospitalContract1(@ProjectCodeFlag String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable);

	public List<Map<String, Object>> reportForHospitalContract2(@ProjectCodeFlag String projectCode, String hospitalCodes,
			String startDate, String endDate, PageRequest pageable);
	/**
	 * 查询未过账数据
	 * @param isPass
	 * @return
	 */
	public List<Contract> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);

	public DataGrid<Contract> listByContractAndDetail(String projectCode, PageRequest page);

	DataGrid<Contract>  pageContract(String projectCode, PageRequest page);
}
