package com.shyl.msc.b2b.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.entity.DataGrid;
import com.shyl.common.entity.PageRequest;
import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IDeliveryOrderDetailDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDao;
import com.shyl.msc.b2b.order.dao.IInOutBoundDetailDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDao;
import com.shyl.msc.b2b.order.dao.IPurchaseOrderDetailDao;
import com.shyl.msc.b2b.order.dao.ISnDao;
import com.shyl.msc.b2b.order.entity.DeliveryOrderDetail;
import com.shyl.msc.b2b.order.service.IDeliveryOrderDetailService;
/**
 * 配送单明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly=true)
public class DeliveryOrderDetailService extends BaseService<DeliveryOrderDetail, Long> implements IDeliveryOrderDetailService {
	@Resource
	private IPurchaseOrderDetailDao purchaseOrderDetailDao;
	@Resource
	private IPurchaseOrderDao purchaseOrderDao;
	@Resource
	private IInOutBoundDao inOutBoundDao;
	@Resource
	private IInOutBoundDetailDao inOutBoundDetailDao;
	@Resource
	private ISnDao snDao;
	
	private IDeliveryOrderDetailDao deliveryOrderDetailDao;
	@Resource
	public void setDeliveryOrderDetailDao(
			IDeliveryOrderDetailDao deliveryOrderDetailDao) {
		this.deliveryOrderDetailDao = deliveryOrderDetailDao;
		super.setBaseDao(deliveryOrderDetailDao);
	}
	public IDeliveryOrderDetailDao getDeliveryOrderDetailDao() {
		return deliveryOrderDetailDao;
	}
	@Override
	public DeliveryOrderDetail findByCode(String projectCode, String code) {
		return deliveryOrderDetailDao.findByCode(code);
	}

	@Override
	public DataGrid<DeliveryOrderDetail> pageNotCloseByHospital(String projectCode, PageRequest pageable, String hospitalCode) {
		return deliveryOrderDetailDao.pageNotCloseByHospital(pageable, hospitalCode);
	}

	@Override
	public List<DeliveryOrderDetail> listByDeliveryOrder(String projectCode, Long id) {
		return deliveryOrderDetailDao.listByDeliveryOrder(id);
	}
	
	@Override
	public DataGrid<DeliveryOrderDetail> pageByHospital(String projectCode, PageRequest pageable, String hospitalCode) {
		return deliveryOrderDetailDao.pageByHospital(pageable, hospitalCode);
	}
	
	@Override
	public DataGrid<DeliveryOrderDetail> pageByHospitalNotIn(String projectCode, PageRequest pageable, String hospitalCode) {
		return deliveryOrderDetailDao.pageByHospitalNotIn(pageable, hospitalCode);
	}
	@Override
	public DataGrid<Map<String, Object>> pageByProductReport(String projectCode, PageRequest pageable) {
		return deliveryOrderDetailDao.pageByProductReport(pageable);
	}
	@Override
	public DataGrid<Map<String, Object>> pageByProductDetailReport(String projectCode, PageRequest pageable) {
		return deliveryOrderDetailDao.pageByProductDetailReport(pageable);
	}
	@Override
	public List<Map<String, Object>> listByProductReport(String projectCode, PageRequest pageable) {
		return deliveryOrderDetailDao.listByProductReport(pageable);
	}
	
	@Override
	public List<DeliveryOrderDetail> listByIsPass(String projectCode, int isPass){
		return deliveryOrderDetailDao.listByIsPass(isPass);
	}

	@Override
	public DataGrid<DeliveryOrderDetail> queryByCode(String projectCode, PageRequest pageable) {
		return deliveryOrderDetailDao.queryByCode(pageable);
	}

	@Override
	public List<DeliveryOrderDetail> listByDeliveryOrderId(String projectCode, Long deliveryOrderId) {
		return deliveryOrderDetailDao.listByDeliveryOrderId(deliveryOrderId);
	}
}
