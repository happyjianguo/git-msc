package com.shyl.msc.set.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.shyl.common.framework.exception.MyException;
import com.shyl.common.util.DateUtil;
import com.shyl.msc.dm.entity.Product;
import com.shyl.msc.dm.entity.ProductPrice;
import com.shyl.msc.dm.entity.ProductVendor;
import com.shyl.msc.enmu.TradeType;
import com.shyl.msc.set.entity.Hospital;
import com.shyl.sys.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.set.dao.ICompanyDao;
import com.shyl.msc.set.entity.Company;
import com.shyl.msc.set.service.ICompanyService;
import com.shyl.sys.entity.Organization;
import com.shyl.sys.service.IOrganizationService;
/**
 * 公司Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class CompanyService extends BaseService<Company, Long> implements ICompanyService {
	@Resource
	private IOrganizationService organizationService;
	
	private ICompanyDao companyDao;

	public ICompanyDao getCompanyDao() {
		return companyDao;
	}

	@Resource
	public void setCompanyDao(ICompanyDao companyDao) {
		this.companyDao = companyDao;
		super.setBaseDao(companyDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public Company save(String projectCode, Company entity) {
		return super.save(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public Company update(String projectCode, Company entity) {
		return super.update(projectCode, entity);
	}
	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public void delete(String projectCode, Long id) {
		super.delete(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "company")
	public Company getById(String projectCode, Long id) {
		return super.getById(projectCode, id);
	}
	
	@Override
	@Cacheable(value = "company",key="0")
	public List<Map<String, Object>> listGPO(String projectCode) {
		return companyDao.listGPO();
	}

	@Override
	@Cacheable(value = "company",key="1")
	public List<Map<String, Object>> listVendor(String projectCode) {
		return companyDao.listVendor();
	}

	@Override
	@Cacheable(value = "company")
	public Company findByCode1(String projectCode, String code) {
		return companyDao.findByCode(code);
	}

	@Override
	@Cacheable(value = "company")
	public Company findByCode(String projectCode, String code, String para) {
		return companyDao.findByCode(code,para);
	}
	
	
	@Override
	public DataGrid<Map<String, Object>> pageByType(String projectCode, PageRequest pageable,
			String name, String para) {
		return companyDao.pageByType(pageable, name, para);
	}

	@Override
	@Cacheable(value = "company")
	public Map<String, Object> count(String projectCode, String type) {
		return companyDao.count(type);
	}

	@Override
	@Cacheable(value = "company")
	public DataGrid<Company> pageByType(String projectCode, PageRequest pageable, String companyType) {
		return companyDao.pageByType(pageable, companyType);
	}

	@Override
	@Cacheable(value = "company")
	public Company findByName(String projectCode, String name) {
		return companyDao.findByName(name);
	}

	@Override
	public String getMaxCode(String projectCode) {
		return companyDao.getMaxCode();
	}

	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public void saveCompany(String projectCode, Company company) {
		company = companyDao.save(company);
		if(company.getIsVendor() != null &&company.getIsVendor().equals(1)){
			//新增organization
			Organization org = new Organization();
			org.setOrgCode(company.getCode());
			org.setOrgType(2);
			org.setOrgName(company.getFullName());
			org.setOrgId(company.getId());
			organizationService.save(projectCode, org);
		}else if(company.getIsGPO() != null &&company.getIsGPO().equals(1)){
			//新增organization
			Organization org = new Organization();
			org.setOrgCode(company.getCode());
			org.setOrgType(6);
			org.setOrgName(company.getFullName());
			org.setOrgId(company.getId());
			organizationService.save(projectCode, org);
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public void updateCompany(String projectCode, Company company) {
		company = companyDao.update(company);
		if(company.getIsVendor() != null && company.getIsVendor().equals(1)){
			//修改organization
			Organization org = organizationService.getByOrgId(projectCode, company.getId(),2);
			if(org != null){
				org.setOrgCode(company.getCode());
				org.setOrgName(company.getFullName());
				organizationService.update(projectCode, org);
			}else{
				org = new Organization();
				org.setOrgCode(company.getCode());
				org.setOrgType(2);
				org.setOrgName(company.getFullName());
				org.setOrgId(company.getId());
				organizationService.save(projectCode, org);
			}
		}else if(company.getIsGPO() != null && company.getIsGPO().equals(1)){
			//修改organization
			Organization org = organizationService.getByOrgId(projectCode, company.getId(),6);
			if(org != null){
				org.setOrgCode(company.getCode());
				org.setOrgName(company.getFullName());
				organizationService.update(projectCode, org);
			}else{
				org = new Organization();
				org.setOrgCode(company.getCode());
				org.setOrgType(6);
				org.setOrgName(company.getFullName());
				org.setOrgId(company.getId());
				organizationService.save(projectCode, org);
			}
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = "company", allEntries = true)
	public void deleteCompany(String projectCode, Company company) {
		companyDao.delete(company);
		if(company.getIsVendor()!=null && company.getIsVendor().equals("1")){
			Organization org = organizationService.getByOrgId(projectCode, company.getId(),2);
			if(org != null){
				organizationService.delete(projectCode, org);
			}
		}else if(company.getIsGPO()!=null && company.getIsGPO().equals("1")){
			Organization org = organizationService.getByOrgId(projectCode, company.getId(),6);
			if(org != null){
				organizationService.delete(projectCode, org);
			}
		}
	}

	@Override
	@Transactional
	public String doExcelH(String projectCode, String[][] excelarr, User user) throws Exception {
		System.out.println("excelarr="+excelarr);
		for (int i = 0; i < excelarr.length; i++) {
			String[] row = excelarr[i];
			//row 0,单位编码	1单位名称	2单位简称	3是否厂商	4是否供应商	5是否配送商	6是否gpo
			String code=row[0];
			String fullName=row[1];
			String shortName=row[2];
			String isProducer=row[3];
			String isVendor=row[4];
			String isSender=row[5];
			String isGPO=row[6];
			String isDisabled=row[7];
			isProducer=(isProducer=="")?"0":isProducer;
			isVendor=(isVendor=="")?"0":isVendor;
			isSender=(isSender=="")?"0":isSender;
			isGPO=(isGPO=="")?"0":isGPO;
			isDisabled=(isDisabled=="")?"0":isDisabled;
			if(StringUtils.isEmpty(code)){
				break;
			}
			Company company = companyDao.findByCode(code,"1=1");
			if(null==company){
				company=new Company();
			}
			company.setCode(code);
			company.setFullName(fullName);
			company.setIsProducer(Integer.valueOf(isProducer));
			company.setShortName(shortName);
			company.setIsVendor(Integer.valueOf(isVendor));
			company.setIsSender(Integer.valueOf(isSender));
			company.setIsGPO(Integer.valueOf(isGPO));
			company.setIsDisabled(Integer.valueOf(isDisabled));
			companyDao.saveOrUpdate(company);
		}
		return "成功导入"+excelarr.length+"笔数据";
	}
}
