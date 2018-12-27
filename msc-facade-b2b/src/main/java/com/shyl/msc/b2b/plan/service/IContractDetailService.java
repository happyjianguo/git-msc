package com.shyl.msc.b2b.plan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.entity.ContractDetail;

public interface IContractDetailService extends IBaseService<ContractDetail, Long> {

	public List<ContractDetail> listByContractId(@ProjectCodeFlag String projectCode, Long contractId);

	//ContractDetail findByKey(String productCode, String vendorCode, String hospitalCode);

	public List<ContractDetail> findByPID(@ProjectCodeFlag String projectCode, Long id);

	public List<ContractDetail> listByHospitalCode(@ProjectCodeFlag String projectCode, Sort sort, String hospitalCode);

	public ContractDetail getByKey(@ProjectCodeFlag String projectCode, Long productId, String hospitalCode, String gpoCode, String vendorCode);
	
	public DataGrid<ContractDetail> queryByH(@ProjectCodeFlag String projectCode, String hospitalCode, PageRequest pageable);

	public List<Map<String, Object>> listByHospitalSigned(@ProjectCodeFlag String projectCode, String hospitalCode);

	public List<ContractDetail> findOlder(@ProjectCodeFlag String projectCode, String hospitalCode, String vendorCode, Long productId);

	public void contractToGoodsPrice(@ProjectCodeFlag String projectCode);

	public void contractToGoodsPrice(@ProjectCodeFlag String projectCode, Contract c);

	public void gpoCheck(@ProjectCodeFlag String projectCode, Long id);
	
	public void gpoCheck(@ProjectCodeFlag String projectCode, Contract c);

	public DataGrid<Map<String, Object>> tradeByProduct(@ProjectCodeFlag String projectCode, PageRequest pageable, String startDate, String endDate);

	public DataGrid<Map<String, Object>> tradeDetailByProduct(@ProjectCodeFlag String projectCode, PageRequest pageable, String startDate, String endDate);

	public DataGrid<ContractDetail> pageByHospitalCode(@ProjectCodeFlag String projectCode, PageRequest pageRequest, String hospitalCode);

	public List<ContractDetail> listBySigned(@ProjectCodeFlag String projectCode, String hospitalCode);

	public ContractDetail findByCode(@ProjectCodeFlag String projectCode, String code);

	public DataGrid<Map<String, Object>> pageByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<ContractDetail> listByExecution(@ProjectCodeFlag String projectCode, PageRequest pageable);
	
	public DataGrid<ContractDetail> pageByExecution(@ProjectCodeFlag String projectCode, PageRequest pageable);

	public List<ContractDetail> listUnClosedByHospital(@ProjectCodeFlag String projectCode, String hospitalCode);

	/**
	 * 查询未过账数据
	 * @param isPass
	 * @return
	 */
	public List<ContractDetail> listByIsPass(@ProjectCodeFlag String projectCode, int isPass);
	
	public String checkGPOContractNum(String projectCode, String hospitalCode, Map<String, JSONObject> slmap);

	DataGrid<ContractDetail> mxlist(String projectCode, PageRequest page);
}
