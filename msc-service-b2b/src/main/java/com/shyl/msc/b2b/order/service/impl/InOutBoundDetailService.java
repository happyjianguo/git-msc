package com.shyl.msc.b2b.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IInOutBoundDetailDao;
import com.shyl.msc.b2b.order.entity.InOutBoundDetail;
import com.shyl.msc.b2b.order.service.IInOutBoundDetailService;
/**
 * 出入库明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class InOutBoundDetailService extends BaseService<InOutBoundDetail, Long> implements IInOutBoundDetailService {
	private IInOutBoundDetailDao inOutBoundDetailDao;

	public IInOutBoundDetailDao getInOutBoundDetailDao() {
		return inOutBoundDetailDao;
	}

	@Resource
	public void setInOutBoundDetailDao(IInOutBoundDetailDao inOutBoundDetailDao) {
		this.inOutBoundDetailDao = inOutBoundDetailDao;
		super.setBaseDao(inOutBoundDetailDao);
	}

	@Override
	@Transactional(readOnly=true)
	public List<InOutBoundDetail> listByInOutBound(String projectCode, Long id) {
		return inOutBoundDetailDao.listByInOutBound(id);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return inOutBoundDetailDao.pageByProductReport(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return inOutBoundDetailDao.pageByProductDetailReport(pageable);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return inOutBoundDetailDao.listByProductReport(pageable);
	}
	
	@Override
	public List<InOutBoundDetail> listByIsPass(String projectCode, int isPass){
		return inOutBoundDetailDao.listByIsPass(isPass);
	}

	@Override
	public List<Map<String,Object>> listGpoReport(String projectCode, String year, String month, int maxsize) {
		return inOutBoundDetailDao.listGpoReport(year, month, maxsize);
	}

	@Override
	public List<Map<String, Object>> listGpoReportByRegion(String projectCode, String year, String month, String treepath, int maxsize) {
		return inOutBoundDetailDao.listGpoReportByRegion(year, month, treepath, maxsize);
	}

	@Override
	public DataGrid<Map<String, Object>> queryByPage(String projectCode, PageRequest pageable) {
		return inOutBoundDetailDao.queryByPage(pageable);
	}

	@Override
	public List<Map<String, Object>> queryByAll(String projectCode, PageRequest pageable) {
		return inOutBoundDetailDao.queryByAll(pageable);
	}

	@Override
	public List<Map<String, Object>> queryInOutBoundDetail(String projectCode,PageRequest page,String beginMonth, String endMonth) {
		return inOutBoundDetailDao.queryInOutBoundDetail(page,beginMonth,endMonth);
	}

	@Override
	public DataGrid<InOutBoundDetail> queryByCode(String projectCode, PageRequest page) {
		return inOutBoundDetailDao.queryByCode(page);
	}

}
