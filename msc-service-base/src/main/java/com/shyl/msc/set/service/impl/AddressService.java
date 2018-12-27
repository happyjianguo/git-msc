package com.shyl.msc.set.service.impl;



import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IAddressDao;
import com.shyl.msc.set.entity.Address;
import com.shyl.msc.set.service.IAddressService;

@Service
@Transactional(readOnly=true)
public class AddressService extends BaseService<Address, Long> implements IAddressService {
	
	private IAddressDao addressDao;

	public IAddressDao getAddressDao() {
		return addressDao;
	}

	@Resource
	public void setAddressDao(IAddressDao addressDao) {
		this.addressDao = addressDao;
		super.setBaseDao(addressDao);
	}
	
	@Override
	public Address findByCode(String projectCode, String code) {
		return addressDao.findByIdCode(code);
	}

	@Override
	public DataGrid<Address> pageByPatient(String projectCode, PageRequest pageable, Long id) {
		return addressDao.pageByPatient(pageable, id);
	}

	@Override
	public List<Address> listByPid(String projectCode, Long pid) {
		return addressDao.listByPid(pid);
	}
}
