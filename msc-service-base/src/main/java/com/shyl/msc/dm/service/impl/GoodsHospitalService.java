package com.shyl.msc.dm.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.dm.dao.IGoodsDao;
import com.shyl.msc.dm.dao.IGoodsHospitalDao;
import com.shyl.msc.dm.dao.IGoodsHospitalSourceDao;
import com.shyl.msc.dm.dao.IProductDao;
import com.shyl.msc.dm.dao.IProductPriceDao;
import com.shyl.msc.dm.entity.GoodsHospital;
import com.shyl.msc.dm.service.IGoodsHospitalService;
import com.shyl.msc.set.dao.ICompanyDao;

/**
 * 
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class GoodsHospitalService extends BaseService<GoodsHospital, Long>
		implements IGoodsHospitalService {

	private IGoodsHospitalDao goodsHospitalDao;
	@Resource
	private IGoodsDao goodsDao;
	@Resource
	private IGoodsHospitalSourceDao goodsHospitalSourceDao;
	@Resource
	private IProductDao productDao;
	@Resource
	private IProductPriceDao productPriceDao;
	@Resource
	private ICompanyDao companyDao;

	public IGoodsHospitalDao getGoodsHospitalDao() {
		return goodsHospitalDao;
	}

	@Resource
	public void setGoodsHospitalDao(IGoodsHospitalDao goodsHospitalDao) {
		this.goodsHospitalDao = goodsHospitalDao;
		super.setBaseDao(goodsHospitalDao);
	}

	@Override
	@Transactional(readOnly = true)
	public GoodsHospital getByGoodsId(String projectCode, Long id) {
		return goodsHospitalDao.getByGoodsId(id);
	}

	@Override
	@Transactional
	public GoodsHospital getByInternalCode(String projectCode, String hospitalCode,
			String internalCode) {
		return goodsHospitalDao.getByInternalCode(hospitalCode, internalCode);
	}

	@Override
	@Transactional
	public GoodsHospital getByProductCode(String projectCode, String hospitalCode,
			String productCode) {
		return goodsHospitalDao.getByProductCode(hospitalCode, productCode);

	}

	@Override
	public List<Map<String, Object>> listByDate(String projectCode, String scgxsj) {
		return goodsHospitalDao.listByDate(scgxsj);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest page) {
		return goodsHospitalDao.queryByPage(page);
	}

	@Override
	public List<Map<String, Object>> queryByAll(String projectCode, PageRequest page) {
		return goodsHospitalDao.queryByAll(page);
	}

}
