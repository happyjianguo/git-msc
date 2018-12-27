package com.shyl.msc.b2b.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.entity.PurchaseOrderDetail;
import com.shyl.msc.b2b.order.service.IPurchaseOrderDetailService;
/**
 * 补货订单明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class PurchaseOrderDetailService extends BaseService<PurchaseOrderDetail, Long> implements IPurchaseOrderDetailService {
	
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;

	public IPurchaseOrderDetailDao getPurchaseOrderDetailDao() {
		return purchaseOrderDetailDao;
	}

	@Resource
	public void setPurchaseOrderDetailDao(
			IPurchaseOrderDetailDao purchaseOrderDetailDao) {
		this.purchaseOrderDetailDao = purchaseOrderDetailDao;
		super.setBaseDao(purchaseOrderDetailDao);
	}
	

	@Override
	public DataGrid<PurchaseOrderDetail> pageByOrderId(String projectCode, PageRequest pageable, Long orderId) {
		return purchaseOrderDetailDao.pageByOrderId(pageable, orderId);
	}

	@Override
	public PurchaseOrderDetail findByCode(String projectCode, String code) {
		return purchaseOrderDetailDao.findByCode(code);
	}

	@Override
	public List<PurchaseOrderDetail> listByOrderId(String projectCode, Long orderId) {
		return purchaseOrderDetailDao.listByOrderId(orderId);
	}

	@Override
	public DataGrid<Map<String, String>> report1(String projectCode, String year, PageRequest pageable) {
		return purchaseOrderDetailDao.report1(year,pageable);
	}
	
	@Override
	public DataGrid<Map<String, String>> report2(String projectCode, String name, String dataS, String dataE, PageRequest pageable) {
		return purchaseOrderDetailDao.report2(name,dataS,dataE,pageable);
	}

	@Override
//	@Cacheable(value = "purchaseOrderDetail",key = "'a'+#name+'a'+#pageable.pageNumber+'a'+#pageable.query.keySet()+'a'+#pageable.query.values()")
	public DataGrid<Map<String, Object>> report4(String projectCode, String name, PageRequest pageable) {
		return purchaseOrderDetailDao.report4(name,pageable);
	}
 
	@Override
	public DataGrid<Map<String, Object>> report5(String projectCode, String name, String month, PageRequest pageable) {
		return purchaseOrderDetailDao.report5(name,month,pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> reportProducerTrade(String projectCode, PageRequest pageable, String name, String year) {
		return purchaseOrderDetailDao.reportProducerTrade(pageable, name, year);
	}

	@Override
	public DataGrid<Map<String, Object>> report5mx(String projectCode, String hospitalcode, String month, PageRequest pageable) {
		return purchaseOrderDetailDao.report5mx(hospitalcode,month,pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> reportVendorTrade(String projectCode, PageRequest pageable, String name, String year) {
		return purchaseOrderDetailDao.reportVendorTrade(pageable, name, year);
	}

	@Override
	public DataGrid<Map<String, Object>> report7(String projectCode, String name, String model, String producerName, String vendorName,
			PageRequest pageable) {
		return purchaseOrderDetailDao.report7(name, model, producerName, vendorName,pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> reportGoodsTradeMX(String projectCode, Long id, String dateS, String dateE, PageRequest pageable) {
		return purchaseOrderDetailDao.reportGoodsTradeMX(id, dateS, dateE,pageable);
	}

	@Override
	public List<Map<String, Object>> chart1(String projectCode, String year) {
		return purchaseOrderDetailDao.chart1(year);
	}

	@Override
	public List<Map<String, Object>> reportProducerTrade(String projectCode, Long producerId,
			String year) {
		return purchaseOrderDetailDao.reportProducerTrade(producerId, year);
	}

	@Override
	public List<Map<String, Object>> chart4(String projectCode, String name, String dateS, String dateE) {
		return purchaseOrderDetailDao.chart4(name, dateS, dateE);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return purchaseOrderDetailDao.pageByProductReport(pageable);
	}

	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return purchaseOrderDetailDao.pageByProductDetailReport(pageable);
	}

	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return purchaseOrderDetailDao.listByProductReport(pageable);
	}

	@Override
//	@Cacheable(value = "purchaseOrderDetail")
	public DataGrid<Map<String, Object>> reportForProductOrder(String projectCode, String startDate, String endDate,
			PageRequest pageable) {
		return purchaseOrderDetailDao.reportForProductOrder( projectCode,  startDate,  endDate,pageable);
	}

	@Override
//	@Cacheable(value = "purchaseOrderDetail")
	public DataGrid<Map<String, Object>> reportDetailForProductOrder(String projectCode, String startDate,
			String endDate, PageRequest pageable) {
		return purchaseOrderDetailDao.reportDetailForProductOrder( projectCode,  startDate,
				 endDate,  pageable);
	}
	
	@Override
	public List<PurchaseOrderDetail> listByIsPass(String projectCode, int isPass){
		return purchaseOrderDetailDao.listByIsPass(isPass);
	}

	@Override
	public DataGrid<PurchaseOrderDetail> queryBycode(String projectCode, PageRequest pageable) {
		return purchaseOrderDetailDao.queryBycode(pageable);
	}

	@Override
	public List<Map<String, Object>> reportAll(String projectCode, String name, String month, PageRequest pageable) {
		return purchaseOrderDetailDao.reportAll(name,month,pageable);
	}
}
