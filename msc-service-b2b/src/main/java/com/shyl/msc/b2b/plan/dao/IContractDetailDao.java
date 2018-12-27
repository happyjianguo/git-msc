package com.shyl.msc.b2b.plan.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.entity.Sort;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.b2b.plan.entity.ContractDetail;

public interface IContractDetailDao extends IBaseDao<ContractDetail, Long> {

	public List<ContractDetail> listByContractId(Long contractId);

	List<ContractDetail> findByPID(Long pid);

	//ContractDetail findByKey(String productCode, String vendorCode, String hospitalCode);

	public List<ContractDetail> listByHospitalCode(Sort sort, String hospitalCode);

	public ContractDetail getByKey(Long productId, String hospitalCode, String gpoCode, String vendorCode);

	public ContractDetail listByKey( String hospitalCode, String gpoCode, String vendorCode);
	
	public DataGrid<ContractDetail> queryByH(String hospitalCode, PageRequest pageable);

	public List<ContractDetail> findOlder(String hospitalCode, String vendorCode, Long productId);

	public List<Map<String, Object>> listByHospitalSigned(String hospitalCode);

	public List<ContractDetail> listBySigned();

	public List<ContractDetail> listBySigned(Long productId, String hospitalCode, String vendorCode);

	public DataGrid<Map<String, Object>> tradeByProduct(PageRequest pageable, String startDate, String endDate);

	public DataGrid<Map<String, Object>> tradeDetailByProduct(PageRequest pageable, String startDate, String endDate);

	public DataGrid<ContractDetail> pageByHospitalCode(PageRequest pageRequest, String hospitalCode);

	public List<ContractDetail> listBySigned(String hospitalCode);
	
	public List<ContractDetail> listByCodes(String hospitalCode, String ContractDetailCtrodeStr);

	public ContractDetail findByCode(String code);

	public DataGrid<Map<String, Object>> pageByProductReport(PageRequest pageable);

	public DataGrid<Map<String, Object>> pageByProductDetailReport(PageRequest pageable);

	public List<Map<String, Object>> listByProductReport(PageRequest pageable);

	public List<ContractDetail> listByExecution(PageRequest pageable);

	public DataGrid<ContractDetail> pageByExecution(PageRequest pageable);

	public List<ContractDetail> listUnClosedByHospital(String hospitalCode);

	/**
	 * 查询未过账数据
	 * @param isPass
	 * @return
	 */
	public List<ContractDetail> listByIsPass(int isPass);

	/**
	 * 更新采购计划
	 * @param contractId
	 * @param num
	 * @return
	 */
	public int updatePurchasePlanNum(Long contractId, BigDecimal num);

	/**
	 * 获取明细数
	 * @param pid
	 * @return
	 */
	public Long countByPID(Long pid);
}
