package com.shyl.msc.b2b.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IReturnsOrderDetailDao;
import com.shyl.msc.b2b.order.entity.ReturnsOrderDetail;
import com.shyl.msc.b2b.order.service.IReturnsOrderDetailService;
/**
 * 退货单明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class ReturnsOrderDetailService extends BaseService<ReturnsOrderDetail, Long> implements IReturnsOrderDetailService {
	private IReturnsOrderDetailDao returnsOrderDetailDao;

	public IReturnsOrderDetailDao getReturnsOrderDetailDao() {
		return returnsOrderDetailDao;
	}

	@Resource
	public void setReturnsOrderDetailDao(
			IReturnsOrderDetailDao returnsOrderDetailDao) {
		this.returnsOrderDetailDao = returnsOrderDetailDao;
		super.setBaseDao(returnsOrderDetailDao);
	}

	@Override
	@Transactional(readOnly=true)
	public ReturnsOrderDetail findByCode(String projectCode, String code) {
		return returnsOrderDetailDao.findByCode(code);
	}

	@Override
	public List<ReturnsOrderDetail> listByReturnId(String projectCode, Long returnId) {
		return returnsOrderDetailDao.listByReturnId(returnId);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return returnsOrderDetailDao.pageByProductReport(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return returnsOrderDetailDao.pageByProductDetailReport(pageable);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return returnsOrderDetailDao.listByProductReport(pageable);
	}
	
	@Override
	public List<ReturnsOrderDetail> listByIsPass(String projectCode, int isPass){
		return returnsOrderDetailDao.listByIsPass(isPass);
	}
}
