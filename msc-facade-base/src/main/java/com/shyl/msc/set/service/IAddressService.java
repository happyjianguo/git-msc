package com.shyl.msc.set.service;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.service.IBaseService;
import com.shyl.msc.set.entity.Address;

/**
 * 患者收货地址查询
 * @author lenovo
 *
 */
public interface IAddressService extends IBaseService<Address, Long> {

	/**
	 * 查询code
	 * @param code
	 * @return
	 */
	public Address findByCode(@ProjectCodeFlag String projectCode, String code);
	
	/**
	 * 根据医院分页查询患者地址
	 * @param pageable
	 * @param id
	 * @return
	 */
	public DataGrid<Address> pageByPatient(@ProjectCodeFlag String projectCode, PageRequest pageable, Long id);

	/**
	 * 
	 * @param pid
	 * @return
	 */
	public List<Address> listByPid(@ProjectCodeFlag String projectCode, Long pid);
}
