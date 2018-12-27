package com.shyl.msc.set.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.dao.BaseDao;
import com.shyl.msc.set.dao.IAddressDao;
import com.shyl.msc.set.entity.Address;

@Repository
public class AddressDao extends BaseDao<Address, Long> implements IAddressDao {


	@Override
	public Address findByIdCode(String code) {
		return super.getByHql("from Address where code=?", code);
	}



	@Override
	public DataGrid<Address> pageByPatient(PageRequest pageable, Long id) {
		String hql = "from Address w where w.patient.id = ?";
		return super.query(hql, pageable, id);
	}



	@Override
	public List<Address> listByPid(Long pid) {
		String hql = "from Address a where a.patient.id = ?";
		return super.listByHql(hql, null, pid);
	}
}
