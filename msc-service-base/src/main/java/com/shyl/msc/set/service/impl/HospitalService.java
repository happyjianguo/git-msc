package com.shyl.msc.set.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.annotation.ProjectCodeFlag;
import com.shyl.common.framework.exception.MyException;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.IHospitalDao;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.msc.set.service.IHospitalService;
/**
 * 医疗机构Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class HospitalService extends BaseService<Hospital, Long> implements IHospitalService {
	private IHospitalDao hospitalDao;

	public IHospitalDao getHospitalDao() {
		return hospitalDao;
	}

	@Resource
	public void setHospitalDao(IHospitalDao hospitalDao) {
		this.hospitalDao = hospitalDao;
		super.setBaseDao(hospitalDao);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "hospital", allEntries = true)
	public Hospital checkAndSave(String projectCode, Hospital hospital) throws Exception{	
		Hospital obj1 =  hospitalDao.findByCode(hospital.getCode());
		if(obj1 != null){
			throw new MyException("编码"+hospital.getCode()+"已存在");
		}
		Hospital obj2 =  hospitalDao.findByIocode(hospital.getIocode());
		if(obj2 != null){
			throw new MyException("接口编码"+hospital.getCode()+"已存在");
		}
		return hospitalDao.save(hospital);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "hospital", allEntries = true)
	public Hospital checkAndUpdate(String projectCode, Hospital hospital) throws Exception{	
		Hospital obj =  hospitalDao.findByIocode(hospital.getIocode());
		if(obj != null&&!obj.getId().equals(hospital.getId())){
			throw new MyException("接口编码"+hospital.getCode()+"已存在");
		}
		hospitalDao.clear();
		return hospitalDao.update(hospital);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "hospital", allEntries = true)
	public Hospital save(String projectCode, Hospital entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "hospital", allEntries = true)
	public Hospital update(String projectCode, Hospital entity) {
		return super.update(projectCode, entity);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "hospital", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}

	@Override
	public List<Map<String, Object>> listAll(String projectCode) {
		return hospitalDao.listAll();
	}
	
	@Override
	@Cacheable(value = "hospital")
	public List<Hospital> list(@ProjectCodeFlag String projectCode, PageRequest pageable,Long province, Long city, Long county) {
		return hospitalDao.list(pageable, province, city, county);
	}
	
	@Override
	@Cacheable(value = "hospital")
	public Hospital findByCode(String projectCode, String code) {		
		return hospitalDao.findByCode(code);
	}

	@Override
	@Cacheable(value = "hospital")
	public Map<String, Object> count(String projectCode) {
		return hospitalDao.count();
	}
}
