package com.shyl.msc.dm.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IProductRegisterDao;
import com.shyl.msc.dm.dao.IProductRegisterDetailDao;
import com.shyl.msc.dm.entity.ProductRegister;
import com.shyl.msc.dm.entity.ProductRegister.OrgType;
import com.shyl.msc.dm.entity.ProductRegisterDetail;
import com.shyl.msc.dm.service.IProductRegisterService;
import com.shyl.sys.entity.User;

@Service
@Transactional(readOnly = true)
public class ProductRegisterService extends BaseService<ProductRegister, Long> implements IProductRegisterService {

	@Resource
	private IProductRegisterDetailDao productRegisterDetailDao;
	
	private IProductRegisterDao productRegisterDao;

	public IProductRegisterDao getProductRegisterDao() {
		return productRegisterDao;
	}

	@Resource
	public void setProductRegisterDao(IProductRegisterDao productRegisterDao) {
		this.productRegisterDao = productRegisterDao;
		super.setBaseDao(productRegisterDao);
	}

	@Override
	@Transactional
	public void add(String projectCode, User user, String rows) {
		ProductRegister productRegister = new ProductRegister();
		if(user.getOrganization().getOrgType() == 1){
			productRegister.setOrgType(OrgType.hospital);
		}else if(user.getOrganization().getOrgType() == 2){
			productRegister.setOrgType(OrgType.vendor);
		}
		productRegister.setOrgId(user.getOrganization().getOrgId());
		productRegister.setOrgName(user.getOrganization().getOrgName());
		productRegister.setStatus(ProductRegister.Status.unaudit);
		productRegisterDao.save(productRegister);
		List<ProductRegisterDetail> details = JSON.parseArray(rows, ProductRegisterDetail.class);
		for (ProductRegisterDetail productRegisterDetail : details) {
			productRegisterDetail.setProductRegister(productRegister);
			productRegisterDetailDao.save(productRegisterDetail);
		}
	}
}
