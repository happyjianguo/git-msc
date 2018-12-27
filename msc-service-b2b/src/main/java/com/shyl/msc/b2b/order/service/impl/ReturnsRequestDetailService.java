package com.shyl.msc.b2b.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shyl.common.framework.service.BaseService;
import com.shyl.msc.b2b.order.dao.IReturnsRequestDetailDao;
import com.shyl.msc.b2b.order.entity.ReturnsRequestDetail;
import com.shyl.msc.b2b.order.service.IReturnsRequestDetailService;
/**
 * 退货申请单明细Service实现类
 * 
 * @author a_Q
 *
 */
@Service
@Transactional(readOnly = true)
public class ReturnsRequestDetailService extends BaseService<ReturnsRequestDetail, Long> implements IReturnsRequestDetailService {
	private IReturnsRequestDetailDao returnsRequestDetailDao;

	public IReturnsRequestDetailDao getReturnsRequestDetailDao() {
		return returnsRequestDetailDao;
	}

	@Resource
	public void setReturnsRequestDetailDao(
			IReturnsRequestDetailDao returnsRequestDetailDao) {
		this.returnsRequestDetailDao = returnsRequestDetailDao;
		super.setBaseDao(returnsRequestDetailDao);
	}

	@Override
	public List<ReturnsRequestDetail> listByPid(String projectCode, Long id) {
		return returnsRequestDetailDao.listByPid(id);
	}

	@Override
	public ReturnsRequestDetail findByCode(String projectCode, String code) {
		return returnsRequestDetailDao.findByCode(code);
	}
	
}
