package com.shyl.msc.set.dao;

import java.util.List;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.IBaseDao;
import com.shyl.msc.set.entity.Address;

public interface IAddressDao extends IBaseDao<Address, Long> {

	/**
	 * 查询IDcode
	 * @param idCode
	 * @return
	 */
	public Address findByIdCode(String idCode);

	/**
	 * 根据医院分页查询患者地址
	 * @param pageable
	 * @param id
	 * @return
	 */
	public DataGrid<Address> pageByPatient(PageRequest pageable, Long id);

	/**
	 * 
	 * @param pid
	 * @return
	 */
	public List<Address> listByPid(Long pid);

}
